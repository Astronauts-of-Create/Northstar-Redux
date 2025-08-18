package com.lightning.northstar.entity;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class MarsCobraEntity extends Monster implements GeoAnimatable {

    private static final ResourceLocation SPEED_MODIFIER_ATTACKING_ID = Northstar.asResource("attacking");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_ID, 0.1, AttributeModifier.Operation.ADD_VALUE);

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    private int attackTick;
    private int lookedAt;

    public MarsCobraEntity(EntityType<? extends MarsCobraEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1);
    }

    // region GeoAnimatable

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableCache;
    }

    @Override
    public double getTick(Object object) {
        return tickCount;
    }

    private PlayState predicate(AnimationState<MarsCobraEntity> event) {
        if (this.attackTick > 0) {
            this.attackTick--;
            event.getController()
                    .setAnimationSpeed(1)
                    .setAnimation(RawAnimation.begin().then("bite", Animation.LoopType.PLAY_ONCE));
        } else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
            event.getController()
                    .setAnimationSpeed(event.getLimbSwingAmount())
                    .setAnimation(RawAnimation.begin().thenLoop("walk"));
        } else {
            event.getController()
                    .setAnimationSpeed(1)
                    .setAnimation(RawAnimation.begin().thenLoop("idle"));
        }

        return PlayState.CONTINUE;
    }

    // endregion

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 5).add(Attributes.MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return NorthstarSounds.MARS_COBRA_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return NorthstarSounds.MARS_COBRA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return NorthstarSounds.MARS_COBRA_DEATH.get();
    }

    //this handles client side stuff, and creates parity between server and client
    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.attackTick = 40;
        }
        super.handleEntityEvent(pId);
    }

    public static boolean cobraSpawnRules(EntityType<MarsCobraEntity> cobra, LevelAccessor level, MobSpawnType spawntype, BlockPos pos, RandomSource rando) {
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) pos.getX(), (int) pos.getZ());
        BlockState state = level.getBlockState(pos.below());
        if (pos.getY() >= surfaceY) {
            return false;
        } else if (pos.getY() > (surfaceY / 1.5)) {
            int light = level.getMaxLocalRawBrightness(pos);
            return light != 0 ? false : checkMobSpawnRules(cobra, level, spawntype, pos, rando) && state.is(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag);
        } else
            return false;
    }

    @Override
    public void tick() {

        super.tick();
    }

    @Override
    protected void customServerAiStep() {
        AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (this.getTarget() != null) {
            if (!attributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING_ID)) {
                attributeinstance.addTransientModifier(SPEED_MODIFIER_ATTACKING);
            }
        } else if (attributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING_ID)) {
            attributeinstance.removeModifier(SPEED_MODIFIER_ATTACKING);
        }

        super.customServerAiStep();
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new MarsCobraEntity.CobraLookForPlayerGoal(this, this::canTarget));
        this.targetSelector.addGoal(1, new MarsCobraEntity.CobraAttackWhenStaredAt(this));
        super.registerGoals();
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.level().broadcastEntityEvent(this, (byte) 4);
        this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
        return super.doHurtTarget(pEntity);
    }

    boolean isLookingAtMe(Player pPlayer) {
        ItemStack itemstack = pPlayer.getInventory().armor.get(3);
        if (itemstack.is(Blocks.CARVED_PUMPKIN.asItem())) {
            return false;
        } else {
            Vec3 vec3 = pPlayer.getViewVector(1.0F).normalize();
            Vec3 vec31 = new Vec3(this.getX() - pPlayer.getX(), this.getEyeY() - pPlayer.getEyeY(), this.getZ() - pPlayer.getZ());
            double d0 = vec31.length();
            vec31 = vec31.normalize();
            double d1 = vec3.dot(vec31);
//             System.out.println(d1);
//             System.out.println("comparer: " + String.valueOf(1.0D - 0.35D / (d0 / 8)));
            return d1 > 1.0D - 0.35D / (d0 / 8) ? pPlayer.hasLineOfSight(this) : false;
        }
    }

    static class CobraAttackWhenStaredAt extends Goal {
        private final MarsCobraEntity cobra;
        @Nullable
        private LivingEntity target;

        public CobraAttackWhenStaredAt(MarsCobraEntity coberuh) {
            this.cobra = coberuh;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            this.target = this.cobra.getTarget();
            if (!(this.target instanceof Player)) {
                return false;
            } else {
                double d0 = this.target.distanceToSqr(this.cobra);
                if (d0 > 256.0D ? false : this.cobra.isLookingAtMe((Player) this.target)) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        public void start() {
            this.cobra.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.cobra.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        }
    }

    boolean canTarget(LivingEntity target) {
        if (!this.canAttack(target)) {
            return false;
        } else {
            return target.getType() == EntityType.PLAYER;
        }
    }

    static class CobraLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final MarsCobraEntity cobra;
        @Nullable
        private Player pendingTarget;
        private int aggroTime;
        private int stareTimer;
        private final TargetingConditions startAggroTargetConditions;
        private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();

        public CobraLookForPlayerGoal(MarsCobraEntity coberuh, @Nullable Predicate<LivingEntity> pSelectionPredicate) {
            super(coberuh, Player.class, 10, false, false, pSelectionPredicate);
            this.cobra = coberuh;
            this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector((p_32578_) -> coberuh.isLookingAtMe((Player) p_32578_));
        }

        @Override
        public boolean canUse() {
            this.pendingTarget = this.cobra.level().getNearestPlayer(this.startAggroTargetConditions, this.cobra);
            return this.pendingTarget != null;
        }

        @Override
        public void start() {
            this.aggroTime = this.adjustedTickDelay(5);
            this.cobra.lookedAt++;
            ;
        }

        @Override
        public void stop() {
            this.pendingTarget = null;
            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.pendingTarget != null) {
                if (!this.cobra.isLookingAtMe(this.pendingTarget)) {

                    return false;
                } else {
                    stareTimer = Mth.clamp(stareTimer + 1, 0, 120);
                    System.out.println(stareTimer);
                    if (stareTimer >= 32) {
                        this.cobra.lookAt(this.pendingTarget, 10.0F, 10.0F);
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return this.target != null && this.continueAggroTargetConditions.test(this.cobra, this.target) ? true : super.canContinueToUse();
            }
        }

        @Override
        public void tick() {
            if (this.cobra.getTarget() == null) {
                super.setTarget((LivingEntity) null);
            }
            if (this.pendingTarget != null) {
                if (!this.cobra.isLookingAtMe(this.pendingTarget)) {
                    stareTimer = Mth.clamp(stareTimer, 0, stareTimer - 1);
                    System.out.println(stareTimer);
                }
                if (--this.aggroTime <= 0) {
                    this.target = this.pendingTarget;
                    this.pendingTarget = null;
                    super.start();
                }
            }
            super.tick();

        }
    }
}
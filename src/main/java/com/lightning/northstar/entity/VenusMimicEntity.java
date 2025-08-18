package com.lightning.northstar.entity;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class VenusMimicEntity extends Monster implements GeoAnimatable {

    private static final ResourceLocation SPEED_MODIFIER_ATTACKING_ID = Northstar.asResource("attacking");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_ID, 0.2D, AttributeModifier.Operation.ADD_VALUE);

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    private int attackTick;
    private int hideTick;
    private int ignoreHideTimer = 0;
    private int aggroTimer = 0;
    private boolean hiding = false;
    private boolean attacking = false;
    public boolean isDeep;
    public boolean deepCheck;

    public VenusMimicEntity(EntityType<? extends VenusMimicEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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

    private PlayState predicate(AnimationState<VenusMimicEntity> event) {
        AnimationController<VenusMimicEntity> controller = event.getController();

        if (this.attackTick > 0) {
            this.attackTick--;
            controller.setAnimation(RawAnimation.begin().then("mimic_bite", Animation.LoopType.PLAY_ONCE));
        } else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F) && !attacking) {
            controller.setAnimation(RawAnimation.begin().thenLoop("mimic_walk"));
        } else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F) && attacking) {
            controller.setAnimation(RawAnimation.begin().thenLoop("mimic_run"));
        } else if (hiding && hideTick > 0) {
            controller.setAnimation(RawAnimation.begin().thenLoop("mimic_hide"));
        } else if (hiding) {
            controller.setAnimation(RawAnimation.begin().thenLoop("mimic_hide_idle"));
        } else {
            controller.setAnimation(RawAnimation.begin().thenLoop("mimic_idle"));
        }

        return PlayState.CONTINUE;
    }

    // endregion

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 5).add(Attributes.MOVEMENT_SPEED, 0.2f);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    public static boolean mimicSpawnRules(EntityType<VenusMimicEntity> cobra, LevelAccessor level, MobSpawnType spawntype, BlockPos pos, RandomSource rando) {
        BlockState state = level.getBlockState(pos.below());
        return state.is(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag) && !level.canSeeSky(pos);
    }

    //this handles client side stuff, and creates parity between server and client
    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.attackTick = 40;
        } else if (pId == 8) {
            this.hiding = true;
            hideTick = 12;
//            this.setXRot(Mth.roundToward((int)this.getXRot(), 90));
        } else if (pId == 9) {
            this.hiding = false;
            this.ignoreHideTimer = 1200;
        } else if (pId == 12) {
            this.attacking = true;
        } else if (pId == 13) {
            this.attacking = false;
        } else {
            super.handleEntityEvent(pId);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (!deepCheck) {
            if (level().getBlockState(blockPosition().below()).is(NorthstarBlocks.VENUS_DEEP_STONE.get()) || level().getBlockState(blockPosition().below()).is(AllPaletteStoneTypes.SCORCHIA.getBaseBlock().get())) {
                isDeep = true;
            }
            deepCheck = true;
        }
        if (this.getTarget() != null) {
            if (this.distanceTo(this.getTarget()) < 5) {
                if (aggroTimer > 100) {
                    this.hiding = false;
                    this.ignoreHideTimer = 500;
                    this.level().broadcastEntityEvent(this, (byte) 9);
                    this.targetSelector.enableControlFlag(Goal.Flag.MOVE);
                    this.targetSelector.enableControlFlag(Goal.Flag.LOOK);
                    this.targetSelector.enableControlFlag(Goal.Flag.JUMP);
                }
                aggroTimer++;

            } else {
                aggroTimer--;
            }
        }

        if (this.level().random.nextInt(250) == 0 && this.getTarget() == null && !hiding && !this.level().isClientSide) {
            if (ignoreHideTimer <= 0) {
                this.level().broadcastEntityEvent(this, (byte) 8);
                hiding = true;
                hideTick = 12;
                this.setXRot(Mth.roundToward((int) this.getXRot(), 90));
            }
        }
        if (hiding && this.level().random.nextInt(2000) == 0 && !this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 9);
            this.ignoreHideTimer = 100;
            hiding = false;
        }
        if (hiding && !this.level().isClientSide && this.getTarget() != null) {
            this.targetSelector.disableControlFlag(Goal.Flag.MOVE);
            this.targetSelector.disableControlFlag(Goal.Flag.LOOK);
            this.targetSelector.disableControlFlag(Goal.Flag.JUMP);
            this.getNavigation().stop();
            if (hideTick == 12) {
//                if(Math.abs(this.position().x - Math.round(this.position().x)) > 0.1)
//                {double dif = this.position().x - Math.round(this.position().x);
//                this.setDeltaMovement(this.getDeltaMovement().add(dif, 0, 0));}

//                if(Math.abs(this.position().z - Math.round(this.position().z)) > 0.1)
//                {double dif = this.position().z - Math.round(this.position().z);
//                this.setDeltaMovement(this.getDeltaMovement().add(dif, 0, 0));}
            }

            this.setYBodyRot(0);
            this.setYHeadRot(0);
            hideTick--;
        } else if (hiding && this.level().isClientSide) {
            this.targetSelector.disableControlFlag(Goal.Flag.MOVE);
            this.targetSelector.disableControlFlag(Goal.Flag.LOOK);
            this.targetSelector.disableControlFlag(Goal.Flag.JUMP);
            this.getNavigation().stop();
            this.setYBodyRot(0);
            this.setYHeadRot(0);
            hideTick--;
        }
        if (!hiding) {

            if (ignoreHideTimer > 0) {
                ignoreHideTimer = Mth.clamp(ignoreHideTimer, 0, ignoreHideTimer - 1);
            }
            this.targetSelector.enableControlFlag(Goal.Flag.MOVE);
            this.targetSelector.enableControlFlag(Goal.Flag.LOOK);
            this.targetSelector.enableControlFlag(Goal.Flag.JUMP);
        }
    }

    @Override
    protected void customServerAiStep() {
        AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (this.getTarget() != null && !this.hiding) {
            if (!attributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING_ID)) {
                attributeinstance.addTransientModifier(SPEED_MODIFIER_ATTACKING);
                attacking = true;
                this.level().broadcastEntityEvent(this, (byte) 12);
            }

        } else if (attributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING_ID)) {
            attacking = false;
            this.level().broadcastEntityEvent(this, (byte) 13);
            attributeinstance.removeModifier(SPEED_MODIFIER_ATTACKING);
        }

        super.customServerAiStep();
    }


    @Override
    protected void registerGoals() {
//        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
//        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
//        this.targetSelector.addGoal(1, new VenusMimicEntity.MimicLookForPlayerGoal(this, this::canTarget));
//        this.targetSelector.addGoal(1, new VenusMimicEntity.HideWhenStaredAt(this));
        super.registerGoals();
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.level().broadcastEntityEvent(this, (byte) 4);
        this.hiding = false;
        this.ignoreHideTimer = 1200;
        this.level().broadcastEntityEvent(this, (byte) 9);
        this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
        return super.doHurtTarget(pEntity);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("isDeep", this.isDeep);
        pCompound.putBoolean("deepCheck", this.deepCheck);
        pCompound.putBoolean("hiding", this.hiding);
        pCompound.putInt("aggroTimer", this.aggroTimer);
        pCompound.putInt("ignoreHideTimer", this.ignoreHideTimer);
        pCompound.putInt("hideTick", this.hideTick);
        pCompound.putInt("attackTick", this.attackTick);

        pCompound.putInt("tickCount", this.tickCount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("isDeep")) {
            this.isDeep = (pCompound.getBoolean("isDeep"));
        }
        if (pCompound.contains("deepCheck")) {
            this.deepCheck = (pCompound.getBoolean("deepCheck"));
        }
        if (pCompound.contains("hiding")) {
            this.hiding = (pCompound.getBoolean("hiding"));
        }
        if (pCompound.contains("aggroTimer")) {
            this.aggroTimer = (pCompound.getInt("aggroTimer"));
        }
        if (pCompound.contains("ignoreHideTimer")) {
            this.ignoreHideTimer = (pCompound.getInt("ignoreHideTimer"));
        }
        if (pCompound.contains("hideTick")) {
            this.hideTick = (pCompound.getInt("hideTick"));
        }
        if (pCompound.contains("attackTick")) {
            this.attackTick = (pCompound.getInt("attackTick"));
        }

        if (pCompound.contains("tickCount")) {
            this.tickCount = (pCompound.getInt("tickCount"));
        }
    }


    @Override
    protected SoundEvent getAmbientSound() {
        if (!hiding)
            return NorthstarSounds.VENUS_MIMIC_IDLE.get();
        else
            return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        super.getHurtSound(pDamageSource);
        return NorthstarSounds.VENUS_MIMIC_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return NorthstarSounds.VENUS_MIMIC_DEATH.get();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            if (!this.level().isClientSide && this.hiding) {
                this.hiding = false;
                this.ignoreHideTimer = 1200;
                this.level().broadcastEntityEvent(this, (byte) 9);
            }

            return super.hurt(pSource, pAmount);
        }
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

    static class HideWhenStaredAt extends Goal {
        private final VenusMimicEntity mimic;
        @Nullable
        private LivingEntity target;

        public HideWhenStaredAt(VenusMimicEntity mimiece) {
            this.mimic = mimiece;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            this.target = this.mimic.getTarget();
            if (!(this.target instanceof Player)) {
                return false;
            } else {
                double d0 = this.target.distanceToSqr(this.mimic);
                if (d0 > 256.0D ? false : this.mimic.isLookingAtMe((Player) this.target) && d0 > 5) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        public void start() {
            this.mimic.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.mimic.getLookControl().setLookAt(mimic.getX() + 20, mimic.getY(), mimic.getZ() + 20);
        }
    }

    boolean canTarget(LivingEntity target) {
        if (!this.canAttack(target)) {
            return false;
        } else {
            return target.getType() == EntityType.PLAYER;
        }
    }

    static class MimicLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
        private final VenusMimicEntity mimic;
        @Nullable
        private Player pendingTarget;
        private int aggroTime;
        private int stareTimer;
        private final TargetingConditions startAggroTargetConditions;
        private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();

        public MimicLookForPlayerGoal(VenusMimicEntity coberuh, @Nullable Predicate<LivingEntity> pSelectionPredicate) {
            super(coberuh, Player.class, 10, false, false, pSelectionPredicate);
            this.mimic = coberuh;
            this.startAggroTargetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector((p_32578_) -> coberuh.isLookingAtMe((Player) p_32578_));
        }

        @Override
        public boolean canUse() {
            this.pendingTarget = this.mimic.level().getNearestPlayer(this.startAggroTargetConditions, this.mimic);
            return this.pendingTarget != null;
        }

        @Override
        public void start() {
            this.aggroTime = this.adjustedTickDelay(5);
        }

        @Override
        public void stop() {
            this.pendingTarget = null;
            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.pendingTarget != null) {
                if (!this.mimic.isLookingAtMe(this.pendingTarget)) {

                    return false;
                } else {
                    stareTimer = Mth.clamp(stareTimer + 1, 0, 120);
                    System.out.println(stareTimer);
                    if (stareTimer >= 32) {
                        this.mimic.lookAt(this.pendingTarget, 10.0F, 10.0F);
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return this.target != null && this.continueAggroTargetConditions.test(this.mimic, this.target) ? true : super.canContinueToUse();
            }
        }

        @Override
        public void tick() {
            if (this.mimic.getTarget() == null) {
                super.setTarget((LivingEntity) null);
            }
            if (this.pendingTarget != null) {
                if (!this.mimic.isLookingAtMe(this.pendingTarget)) {
                    stareTimer = Mth.clamp(stareTimer, 0, stareTimer - 1);
                    System.out.println(stareTimer);
                }
                if (--this.aggroTime <= 0) {
                    if (!this.mimic.hiding) {
                        this.mimic.hiding = true;
                        this.mimic.hideTick = 12;
                        this.mimic.level().broadcastEntityEvent(mimic, EntityEvent.SHAKE_WETNESS);
                    }
                    this.pendingTarget = null;
                    super.start();
                }
            }
            super.tick();

        }
    }

}

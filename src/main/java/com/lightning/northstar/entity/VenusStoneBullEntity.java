package com.lightning.northstar.entity;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.entity.goals.ChargeAtTargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class VenusStoneBullEntity extends Monster implements GeoAnimatable {

    private static final ResourceLocation SPEED_MODIFIER_ATTACKING_ID = Northstar.asResource("attacking");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_ID, 0.2D, AttributeModifier.Operation.ADD_VALUE);

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    public boolean charging = false;
    public boolean passedTarget = false;
    public int stopChargeTimer = 0;
    public int chargeTimer = 0;
    public int chargeCooldown = 0;
    public BlockPos targetPos;
    public int ticksSpentCharging = 0;
    public Vec3 moveDirection;

    public VenusStoneBullEntity(EntityType<? extends VenusStoneBullEntity> pEntityType, Level pLevel) {
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

    private PlayState predicate(AnimationState<VenusStoneBullEntity> event) {
        AnimationController<VenusStoneBullEntity> controller = event.getController();

        if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F) && !charging) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("walk"));
        } else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F) && charging && !passedTarget) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("charge"));
        } else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F) && charging && passedTarget) {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.venus_stone_bull.stop_charge"));
        } else {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
        }

        return PlayState.CONTINUE;
    }

    // endregion

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 60.0D).add(Attributes.ATTACK_DAMAGE, 10).add(Attributes.MOVEMENT_SPEED, 0.2f);
    }

    public static boolean stoneBullSpawnRules(EntityType<VenusStoneBullEntity> moth, LevelAccessor level, MobSpawnType spawntype, BlockPos pos, RandomSource rando) {
        BlockState state = level.getBlockState(pos.below());
        return state.is(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag);
    }

    //this handles client side stuff, and creates parity between server and client
    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
        }
        if (pId == 63) {
            charging = true;
        }
        if (pId == 65) {
            charging = false;
        }
        if (pId == 66) {
            passedTarget = true;
        }
        if (pId == 67) {
            charging = false;
            passedTarget = false;
        }
        super.handleEntityEvent(pId);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && charging && passedTarget && !level().getBlockState(blockPosition().below()).isAir()) {
            level().addParticle(ParticleTypes.CLOUD, getX() + random.nextFloat() * (random.nextBoolean() ? -1 : 1),
                    getY(), getZ() + random.nextFloat() * (random.nextBoolean() ? -1 : 1), 0, 0, 0);
            level().addParticle(ParticleTypes.CLOUD, getX() + random.nextFloat() * (random.nextBoolean() ? -1 : 1),
                    getY(), getZ() + random.nextFloat() * (random.nextBoolean() ? -1 : 1), 0, 0, 0);
            level().addParticle(ParticleTypes.CLOUD, getX() + random.nextFloat() * (random.nextBoolean() ? -1 : 1),
                    getY(), getZ() + random.nextFloat() * (random.nextBoolean() ? -1 : 1), 0, 0, 0);
            level().addParticle(ParticleTypes.CLOUD, getX() + random.nextFloat() * (random.nextBoolean() ? -1 : 1),
                    getY(), getZ() + random.nextFloat() * (random.nextBoolean() ? -1 : 1), 0, 0, 0);
        }

        if (!this.level().isClientSide && this.getTarget() != null) {
//            System.out.println(this.getTarget());
//            System.out.println("charging: " + charging);
//            System.out.println("chargeTimer: " + chargeTimer);
        }
    }

    @Override
    protected void customServerAiStep() {
        if (chargeTimer > 0)
            chargeTimer = Mth.clamp(chargeTimer, 0, chargeTimer - 1);
        if (stopChargeTimer > 0)
            stopChargeTimer = Mth.clamp(stopChargeTimer, 0, stopChargeTimer - 1);
        if (chargeCooldown > 0)
            chargeCooldown = Mth.clamp(chargeCooldown, 0, chargeCooldown - 1);

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
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return NorthstarSounds.VENUS_STONE_BULL_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return NorthstarSounds.VENUS_STONE_BULL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return NorthstarSounds.VENUS_STONE_BULL_DEATH.get();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new ChargeAtTargetGoal(this, 1.5, 16));
        this.goalSelector.addGoal(9, new VenusStoneBullEntity.StareAtTargetGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true));
        super.registerGoals();
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.level().broadcastEntityEvent(this, (byte) 4);
        this.playSound(NorthstarSounds.VENUS_STONE_BULL_ATTACK.get(), 1.0F, 1.0F);
        pEntity.setDeltaMovement(pEntity.getDeltaMovement().x + (this.getDeltaMovement().x / 4), pEntity.getDeltaMovement().y + 1, pEntity.getDeltaMovement().z + (this.getDeltaMovement().z / 4));
        return super.doHurtTarget(pEntity);
    }

    static class StareAtTargetGoal extends Goal {
        private final VenusStoneBullEntity starer;

        public StareAtTargetGoal(VenusStoneBullEntity pShooter) {
            this.starer = pShooter;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.starer.charging)
                return;

            if (this.starer.getTarget() == null) {
                Vec3 vec3 = this.starer.getDeltaMovement();
                this.starer.setYRot(-((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float) Math.PI));
                this.starer.yBodyRot = this.starer.getYRot();
            } else {
                LivingEntity livingentity = this.starer.getTarget();
                if (livingentity.distanceToSqr(this.starer) < 4096.0D) {
                    double d1 = livingentity.getX() - this.starer.getX();
                    double d2 = livingentity.getZ() - this.starer.getZ();
                    this.starer.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
                    this.starer.yBodyRot = this.starer.getYRot();
                }
            }

        }
    }
}

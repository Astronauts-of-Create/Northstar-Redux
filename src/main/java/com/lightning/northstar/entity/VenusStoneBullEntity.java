package com.lightning.northstar.entity;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.entity.goals.ChargeAtTargetGoal;
import net.minecraft.MethodsReturnNonnullByDefault;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class VenusStoneBullEntity extends Monster implements GeoAnimatable {

    private static final ResourceLocation SPEED_MODIFIER_ATTACKING_ID = Northstar.asResource("attacking");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_ID, 0.2D, AttributeModifier.Operation.ADD_VALUE);

    public static final byte EVENT_START_CHARGING = (byte) 254;
    public static final byte EVENT_STOP_CHARGING = (byte) 253;
    public static final byte EVENT_PASSED_TARGET = (byte) 252;
    public static final byte EVENT_RESET_CHARGE = (byte) 251;

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.MAX_HEALTH, 60)
                .add(Attributes.ATTACK_DAMAGE, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    public static boolean stoneBullSpawnRules(EntityType<VenusStoneBullEntity> entityType, LevelAccessor level,
                                              MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        BlockState state = level.getBlockState(pos.below());
        return state.is(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag);
    }

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    public boolean charging = false;
    public boolean passedTarget = false;
    public int stopChargeTimer = 0;
    public int chargeTimer = 0;
    public int chargeCooldown = 0;
    public BlockPos targetPos;
    public int ticksSpentCharging = 0;
    public Vec3 moveDirection;

    public VenusStoneBullEntity(EntityType<? extends VenusStoneBullEntity> entityType, Level level) {
        super(entityType, level);

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

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case EVENT_START_CHARGING -> charging = true;
            case EVENT_STOP_CHARGING -> charging = false;
            case EVENT_PASSED_TARGET -> passedTarget = true;
            case EVENT_RESET_CHARGE -> {
                charging = false;
                passedTarget = false;
            }
            default -> super.handleEntityEvent(id);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && charging && passedTarget && !level().getBlockState(blockPosition().below()).isAir()) {
            level().addParticle(ParticleTypes.CLOUD, getX() + random.nextFloat() * (random.nextBoolean() ? -1 : 1),
                    getY(), getZ() + random.nextFloat() * (random.nextBoolean() ? -1 : 1), 0, 0, 0);
            level().addParticle(ParticleTypes.CLOUD, getX() + random.nextFloat() * (random.nextBoolean() ? -1 : 1),
                    getY(), getZ() + random.nextFloat() * (random.nextBoolean() ? -1 : 1), 0, 0, 0);
            level().addParticle(ParticleTypes.CLOUD, getX() + random.nextFloat() * (random.nextBoolean() ? -1 : 1),
                    getY(), getZ() + random.nextFloat() * (random.nextBoolean() ? -1 : 1), 0, 0, 0);
            level().addParticle(ParticleTypes.CLOUD, getX() + random.nextFloat() * (random.nextBoolean() ? -1 : 1),
                    getY(), getZ() + random.nextFloat() * (random.nextBoolean() ? -1 : 1), 0, 0, 0);
        }

        if (!level().isClientSide && getTarget() != null) {
            Northstar.LOGGER.debug("target: {}, charging: {}, chargeTime: {}", getTarget(), charging, chargeTimer);
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

        AttributeInstance attributeinstance = getAttribute(Attributes.MOVEMENT_SPEED);
        if (getTarget() != null) {
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
        return NorthstarSounds.VENUS_STONE_BULL_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return NorthstarSounds.VENUS_STONE_BULL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return NorthstarSounds.VENUS_STONE_BULL_DEATH.get();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(9, new VenusStoneBullEntity.StareAtTargetGoal(this));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        goalSelector.addGoal(7, new ChargeAtTargetGoal(this, 1.5, 16));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        super.registerGoals();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        playSound(NorthstarSounds.VENUS_STONE_BULL_ATTACK.get(), 1.0F, 1.0F);
        target.setDeltaMovement(target.getDeltaMovement().add(getDeltaMovement().x / 4, 1, getDeltaMovement().z / 4));
        return super.doHurtTarget(target);
    }

    static class StareAtTargetGoal extends Goal {
        private final VenusStoneBullEntity starer;

        public StareAtTargetGoal(VenusStoneBullEntity pShooter) {
            starer = pShooter;
            setFlags(EnumSet.of(Goal.Flag.LOOK));
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
            if (starer.charging)
                return;

            if (starer.getTarget() == null) {
                Vec3 vec3 = starer.getDeltaMovement();
                starer.setYRot(-((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float) Math.PI));
                starer.yBodyRot = starer.getYRot();
            } else {
                LivingEntity livingentity = starer.getTarget();
                if (livingentity.distanceToSqr(starer) < 4096) {
                    double d1 = livingentity.getX() - starer.getX();
                    double d2 = livingentity.getZ() - starer.getZ();
                    starer.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
                    starer.yBodyRot = starer.getYRot();
                }
            }
        }
    }

}

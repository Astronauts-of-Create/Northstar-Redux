package com.lightning.northstar.entity;

import com.lightning.northstar.content.NorthstarTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;


public class AegaeonCrawler extends Monster implements GeoAnimatable {
    private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.1D, AttributeModifier.Operation.ADDITION);

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    private int attackTick;
    private int lookedAt;

    public AegaeonCrawler(EntityType<? extends AegaeonCrawler> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setMaxUpStep(1f);
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
    private boolean isMoving() {
        // horizontal speed
        double speed = Math.sqrt(this.getDeltaMovement().x * this.getDeltaMovement().x +
                this.getDeltaMovement().z * this.getDeltaMovement().z);
        return speed > 0.01; // only trigger walk if speed is noticeable
    }

    private PlayState predicate(AnimationState<? extends GeoAnimatable> event) {
        if (this.attackTick > 0) {
            this.attackTick--;
            event.getController()
                    .setAnimation(RawAnimation.begin().then("aegaeon_crawler_attack", Animation.LoopType.PLAY_ONCE));
        } else if (isMoving()) {
            event.getController()
                    .setAnimation(RawAnimation.begin().thenLoop("aegaeon_crawler_walk"));
        }
        return PlayState.CONTINUE;
    }
    @Override
    public boolean onClimbable() {
        // Spiders can climb if they are touching a wall
        return this.horizontalCollision;
    }

    @Override
    protected void registerGoals() {

        // Attack the nearest player
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));

        // Attack the player if close
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));

        // Wander around randomly
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D));

        // Look at nearby players
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));

        // Optional: look around randomly when idle
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }


    // --- Climbing logic ---
    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.horizontalCollision && this.random.nextInt(6) == 0) {
            this.level().addParticle(
                    ParticleTypes.CLOUD,
                    this.getX() + (this.random.nextDouble() - 0.5D) * 0.6D,
                    this.getY() + 0.5D,
                    this.getZ() + (this.random.nextDouble() - 0.5D) * 0.6D,
                    0.0D, 0.02D, 0.0D
            );
        }
    }


    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(SoundEvents.STONE_STEP, 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }
    public static boolean crawlerSpawnRules(EntityType<AegaeonCrawler> cobra, LevelAccessor level, MobSpawnType spawntype, BlockPos pos, RandomSource rando) {
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) pos.getX(), (int) pos.getZ());
        BlockState state = level.getBlockState(pos.below());
        if (pos.getY() >= surfaceY) {
            return false;
        } else if (pos.getY() > (surfaceY / 1.5)) {
            int light = level.getMaxLocalRawBrightness(pos);
            return light != 0 ? false : checkMobSpawnRules(cobra, level, spawntype, pos, rando) && state.is(NorthstarTags.NorthstarBlockTags.NATURAL_AEGAEON_BLOCKS.tag);
        } else
            return false;
    }
}

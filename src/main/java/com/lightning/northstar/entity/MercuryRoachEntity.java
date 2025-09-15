package com.lightning.northstar.entity;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.entity.goals.RunToGroupGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MercuryRoachEntity extends Monster implements GeoAnimatable {

    private static final ResourceLocation SPEED_MODIFIER_ATTACKING_ID = Northstar.asResource("attacking");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_ID, 0.2D, AttributeModifier.Operation.ADD_VALUE);

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    public int eating = 0;
    public int stealTimer = 0;

    public MercuryRoachEntity(EntityType<? extends MercuryRoachEntity> pEntityType, Level pLevel) {
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

    private PlayState predicate(AnimationState<MercuryRoachEntity> event) {
        if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
            event.getController()
                    .setAnimationSpeed(event.getLimbSwingAmount())
                    .setAnimation(RawAnimation.begin().thenLoop("walk"));
        } else if (eating > 0) {
            eating--;
            event.getController()
                    .setAnimationSpeed(1)
                    .setAnimation(RawAnimation.begin().then("munch", Animation.LoopType.PLAY_ONCE));
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

    public static boolean roachSpawnRules(EntityType<MercuryRoachEntity> lunargrade, LevelAccessor level, MobSpawnType spawntype, BlockPos pos, RandomSource rando) {
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) pos.getX(), (int) pos.getZ());
        BlockState state = level.getBlockState(pos.below());
        if (pos.getY() >= surfaceY) {
            return false;
        } else {
            return state.is(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag);
        }
    }

    //this handles client side stuff, and creates parity between server and client
    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 10) {
            eating = 40;
        }
        super.handleEntityEvent(pId);
    }

    @Override
    public void tick() {
        if (stealTimer > 0) {
            stealTimer = Mth.clamp(stealTimer, 0, stealTimer - 1);
        }
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
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return NorthstarSounds.MERCURY_ROACH_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return NorthstarSounds.MERCURY_ROACH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return NorthstarSounds.MERCURY_ROACH_DIE.get();
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new RunToGroupGoal<>(this, Player.class, 7, 1, 1.2));
        this.goalSelector.addGoal(7, new MeleeAttackGoal(this, 1.0D, false));
    // this.goalSelector.addGoal(7, new ScourChestGoal(this, 1.0D, 16));
        super.registerGoals();
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        this.level().broadcastEntityEvent(this, (byte) 4);
        this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 1.0F);
        return super.doHurtTarget(pEntity);
    }

}

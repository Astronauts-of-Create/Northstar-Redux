package com.lightning.northstar.entity;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.entity.goals.LayEggInNestGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.warden.AngerManagement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.UUID;
import java.util.function.BiConsumer;

public class MarsWormEntity extends Monster implements GeoAnimatable, VibrationSystem {

    private static final ResourceLocation SPEED_MODIFIER_ATTACKING_ID = Northstar.asResource("attacking");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(SPEED_MODIFIER_ATTACKING_ID, 0.2D, AttributeModifier.Operation.ADD_VALUE);
    private static final EntityDataAccessor<Integer> CLIENT_ANGER_LEVEL = SynchedEntityData.defineId(MarsWormEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    private final DynamicGameEventListener<VibrationSystem.Listener> dynamicGameEventListener;
    private final VibrationSystem.User vibrationUser;
    private VibrationSystem.Data vibrationData;

    private boolean aggro;
    private int angerTimer = 0;
    public int eggTimer = 0;
    private LivingEntity notTarget;
    private UUID notTargetUUID;

    private int attackTick;
    private AngerManagement angerManagement = new AngerManagement(this::canTargetEntity, Collections.emptyList());

    public MarsWormEntity(EntityType<? extends MarsWormEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1);

        dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
        vibrationUser = new VibrationUser();
        vibrationData = new VibrationSystem.Data();

        //dynamicGameEventListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(, 16, this, null, 0.0F, 0));
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

    private PlayState predicate(AnimationState<MarsWormEntity> event) {
        AnimationController<MarsWormEntity> controller = event.getController();

        if (this.attackTick > 0) {
            this.attackTick--;
            controller.setAnimationSpeed(1)
                    .setAnimation(RawAnimation.begin().then("bite", Animation.LoopType.PLAY_ONCE));
        } else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
            controller.setAnimationSpeed(event.getLimbSwingAmount())
                    .setAnimation(RawAnimation.begin().thenLoop("walk"));
        } else {
            controller.setAnimationSpeed(1)
                    .setAnimation(RawAnimation.begin().thenLoop("idle"));
        }

        return PlayState.CONTINUE;
    }

    // endregion
    // region VibrationSystem

    @Override
    @NotNull
    public Data getVibrationData() {
        return vibrationData;
    }

    @Override
    @NotNull
    public User getVibrationUser() {
        return vibrationUser;
    }

    private class VibrationUser implements User {

        @Override
        public int getListenerRadius() {
            return 16;
        }

        @Override
        @NotNull
        public PositionSource getPositionSource() {
            return new EntityPositionSource(MarsWormEntity.this, getEyeHeight());
        }

        @Override
        public boolean canReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> event, Context context) {
            if (!isNoAi() && !isDeadOrDying() && !getBrain().hasMemoryValue(MemoryModuleType.VIBRATION_COOLDOWN) && level.getWorldBorder().isWithinBounds(pos) && !isRemoved() && level() == level) {
                Entity entity = context.sourceEntity();
                if (entity instanceof LivingEntity livingentity) {
                    return canTargetEntity(livingentity);
                }

                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> event, @Nullable Entity entity, @Nullable Entity player, float distance) {
            //System.out.println("Big Bazinga 24");
            if (!isDeadOrDying()) {
                brain.setMemoryWithExpiry(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
                level.broadcastEntityEvent(MarsWormEntity.this, EntityEvent.TENDRILS_SHIVER);
                if (!aggro) {
                    playSound(NorthstarSounds.MARS_WORM_CLICK_NOTICE.get(), 5.0F, getVoicePitch());
                }
                MarsWormAi.setDisturbanceLocation(MarsWormEntity.this, pos);
                if (notTarget != null && !aggro && (notTarget == entity || notTarget == player)) {
                    aggro = true;
                }
            }
        }

    }

    // endregion

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 5).add(Attributes.MOVEMENT_SPEED, 0.2f);
    }

    //this handles client side stuff, and creates parity between server and client
    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 4) {
            this.attackTick = 40;
        } else {
            super.handleEntityEvent(pId);
        }

    }

    public static boolean wormSpawnRules(EntityType<MarsWormEntity> cobra, LevelAccessor level, MobSpawnType spawntype, BlockPos pos, RandomSource rando) {
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) pos.getX(), (int) pos.getZ());
        BlockState state = level.getBlockState(pos.below());

        if (pos.getY() >= surfaceY) {
            return false;
        } else if (pos.getY() < surfaceY / 2.5) {
            int light = level.getMaxLocalRawBrightness(pos);
            return light != 0 ? false : checkMobSpawnRules(cobra, level, spawntype, pos, rando) && state.is(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag);
        } else
            return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLIENT_ANGER_LEVEL, 0);
    }

    public int getClientAngerLevel() {
        return this.entityData.get(CLIENT_ANGER_LEVEL);
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> pListenerConsumer) {
        Level level = this.level();
        if (level instanceof ServerLevel serverlevel) {
            pListenerConsumer.accept(this.dynamicGameEventListener, serverlevel);
        }
    }

    private void syncClientAngerLevel() {
        this.entityData.set(CLIENT_ANGER_LEVEL, this.getActiveAnger());
    }

    private int getActiveAnger() {
        return this.angerManagement.getActiveAnger(this.getTarget());
    }


    @Override
    public void tick() {
        Level level = this.level();

        if (eggTimer > 0) {
            eggTimer--;
        }

        if (level instanceof ServerLevel serverlevel) {
            if (notTargetUUID != null) {
                notTarget = (LivingEntity) serverlevel.getEntity(notTargetUUID);
                notTargetUUID = null;
            }


            VibrationSystem.Ticker.tick(serverlevel, this.vibrationData, this.vibrationUser);

            if (getTarget() != null) {
                if (angerTimer > 0 && this.getTarget().distanceTo(this) > this.getAttributeValue(Attributes.FOLLOW_RANGE)) {
                    angerTimer--;
                }
            }
            if (this.getTarget() != null) {
                if (this.getTarget().isSpectator()) {
                    this.setTarget(null);
                    this.notTarget = null;
                }
                if (this.getTarget() instanceof Player plyr) {
                    if (plyr.isCreative()) {
                        this.setTarget(null);
                        this.notTarget = null;
                    }
                }
            }


            if (tickCount % 40 == 0) {

            }

            if (angerTimer <= 0) {
                this.setTarget(null);
                notTarget = null;
                aggro = false;
            }
            for (Entity ent : level.getEntities(this, getBoundingBox())) {
                if (ent instanceof LivingEntity lEnt) {
                    if (canTargetEntity(lEnt)) {
                        this.notTarget = lEnt;
                        aggro = true;
                    }
                }
            }


            if (this.notTarget != null) {
                if (this.distanceTo(notTarget) < 2) {
                    aggro = true;
                }
                if (this.notTarget.getHealth() <= 0) {
                    this.notTarget = null;
                    this.setTarget(null);
                    this.aggro = false;
                }
            }

            if (aggro && this.notTarget != null) {
                this.setTarget(notTarget);
            } else {
                this.setTarget(null);
                aggro = false;
            }
            if (aggro && this.getTarget() == null) {
                aggro = false;
            }
        }
        super.tick();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("aggro", aggro);
        if (this.notTarget != null) {
            pCompound.putUUID("notTarget", notTarget.getUUID());
        }
        pCompound.putInt("eggTimer", eggTimer);
        pCompound.putInt("angerTimer", angerTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("aggro")) {
            aggro = pCompound.getBoolean("aggro");
        }
        if (pCompound.contains("notTarget")) {
            notTargetUUID = pCompound.getUUID("notTarget");
        }
        eggTimer = pCompound.getInt("eggTimer");
        angerTimer = pCompound.getInt("angerTimer");
    }


    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        boolean flag = super.hurt(pSource, pAmount);
        if (!this.level().isClientSide && !this.isNoAi()) {
            Entity entity = pSource.getEntity();
            if (entity instanceof LivingEntity living) {
                if (canTargetEntity(entity) && this.notTarget == null) {
                    this.notTarget = living;
                    this.aggro = true;

                }
            }
        }

        return flag;
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
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(2, new LayEggInNestGoal(this, 1.0, 10));
        super.registerGoals();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return NorthstarSounds.MARS_WORM_CLICK.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        super.getHurtSound(pDamageSource);
        return NorthstarSounds.MARS_WORM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return NorthstarSounds.MARS_WORM_DEATH.get();
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = super.doHurtTarget(pEntity);
        this.level().broadcastEntityEvent(this, EntityEvent.START_ATTACKING);
        this.playSound(NorthstarSounds.MARS_WORM_ATTACK.get(), 1.0F, 1.0F);
        if (pEntity instanceof LivingEntity liv) {
            if (liv.getHealth() <= 0) {
                this.setTarget(null);
                aggro = false;
                this.notTarget = null;
            }
        }
        return flag;
    }

    @Contract("null->false")
    public boolean canTargetEntity(@Nullable Entity ent) {
        if (ent instanceof LivingEntity livingentity) {
            if (this.level() == ent.level() && (ent instanceof Player || ent instanceof ZombifiedPiglin || ent instanceof MarsToadEntity)) {
                if (ent instanceof Player player) {
                    if (player.isCreative()) {
                        return false;
                    }
                }
                if (ent.isSpectator()) {
                    return false;
                }
                if (notTarget == null) {
                    notTarget = livingentity;
                    angerTimer = 2400;
                }
                return true;
            }
        }
        return false;
    }


}

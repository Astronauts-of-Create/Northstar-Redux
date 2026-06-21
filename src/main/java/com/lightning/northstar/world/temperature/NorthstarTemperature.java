package com.lightning.northstar.world.temperature;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.temperature_regulator.TemperatureRegulatorBlockEntity;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.world.SealingProvider;
import com.lightning.northstar.world.sealer.ProgressiveBlockUpdater;
import com.lightning.northstar.world.sealer.SealingMode;
import com.lightning.northstar.world.sealer.transform.TransformProviders;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllFluids;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = Northstar.MOD_ID, value = Dist.CLIENT)
public class NorthstarTemperature {

    public static final int MINIMUM_TEMPERATURE = -273;
    public static final int MAXIMUM_TEMPERATURE = 1500;
    public static final int SPACE = -270;
    public static final int DEFAULT = 20;

    private final Level level;
    private final Set<Provider> providers;
    private final ProgressiveBlockUpdater updater;

    public NorthstarTemperature(Level level) {
        this.level = level;
        this.providers = new HashSet<>();
        this.updater = new ProgressiveBlockUpdater(SealingMode.TEMPERATURE);
    }

    public float getTemperatureAt(Vec3 pos) {
        return getTemperatureDirect(TransformProviders.getToWorld().applyTransformOrIdentity(level, pos));
    }

    private float getTemperatureDirect(Vec3 pos) {
        float temperature = 0;
        int count = 0;
        for (Provider provider : providers) {
            if (provider.isSealed(pos)) {
                temperature += provider.getTemperature();
                count++;
            }
        }

        return count == 0 ? getBaseTemperature(level, BlockPos.containing(pos)) : temperature / count;
    }

    public float getTemperatureAt(Vec3i pos) {
        Vec3 transformed = TransformProviders.getToWorld().applyTransform(level, Vec3.atCenterOf(pos));
        if (transformed != null) {
            return getTemperatureDirect(transformed);
        }

        float temperature = 0;
        int count = 0;

        for (Provider provider : providers) {
            if (provider.isSealed(pos)) {
                temperature += provider.getTemperature();
                count++;
            }
        }

        return count == 0 ? getBaseTemperature(level, pos instanceof BlockPos bp ? bp : new BlockPos(pos)) : temperature / count;
    }

    public void registerSealer(Provider provider) {
        providers.add(provider);
    }

    public void unregisterSealer(Provider provider) {
        providers.remove(provider);
    }

    public void enqueueUpdates(LongCollection positions) {
        updater.queueUpdates(positions);
    }

    @ApiStatus.Internal
    public void processUpdates(ServerLevel level) {
        updater.processUpdates(level);
    }

    public interface Provider extends SealingProvider {
        float getTemperature();
    }

    public static float getTemperatureAt(Level level, Vec3 pos) {
        return level.northstar$temperature().getTemperatureAt(pos);
    }

    public static float getTemperatureAt(Level level, Vec3i pos) {
        return level.northstar$temperature().getTemperatureAt(pos);
    }

    public static NorthstarTemperature getDimension(Level level) {
        return level.northstar$temperature();
    }

    public static float getBaseTemperature(Level level, BlockPos pos) {
        return level.northstar$dimension().temperature().get(level, pos);
    }

    @ApiStatus.Internal
    public static void tickEntity(LivingEntity entity) {
        if (entity.level().isClientSide())
            return;

        float temp = NorthstarTemperature.getTemperatureAt(entity.level(), entity.getEyePosition());
        boolean hasInsulation = NorthstarTemperature.hasInsulation(entity);
        boolean hasHeatProtection = NorthstarTemperature.hasHeatProtection(entity);

        if (entity instanceof Player player && (player.isCreative() || player.isSpectator()))
            return;
        if (temp > -32 && temp < 300)
            return;

        if (temp < -32 && !hasInsulation && !NorthstarEntityTags.CAN_SURVIVE_COLD.matches(entity)) {
            // +3 instead of +1 because it's decreased by -2 each tick, but we still want it to increase by 1
            int ticksFrozen = Math.min(entity.getTicksRequiredToFreeze(), entity.getTicksFrozen()) + 3;
            entity.setTicksFrozen(ticksFrozen);
            if (ticksFrozen >= entity.getTicksRequiredToFreeze() / 2) {
                int damage = entity.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES) ? 7 : 2;
                entity.hurt(entity.level().damageSources().freeze(), damage);
            }
        }
        if (temp > 300 && !entity.fireImmune() && !hasHeatProtection && !NorthstarEntityTags.CAN_SURVIVE_HEAT.matches(entity)) {
            entity.setRemainingFireTicks(5 * 20);
        }
    }

    public static boolean isCombustible(FluidState state) {
        if (state.is(NorthstarFluids.HYDROCARBON.getSource().getSource())) return true;
        return false;
    }

    public static int combustionTemp(FluidState state) {
        if (state.is(NorthstarFluids.HYDROCARBON.getSource().getSource()) || state.is(NorthstarFluids.HYDROCARBON.get()))
            return 300;
        return 1000;
    }

    public static int getBoilingPoint(FluidState state) {
        if (state.is(Fluids.WATER) || state.is(Fluids.FLOWING_WATER))
            return 100;
        if (state.is(Fluids.LAVA) || state.is(Fluids.FLOWING_LAVA))
            return 1200;
        if (state.is(AllFluids.CHOCOLATE.get()) || state.is(AllFluids.HONEY.get()))
            return 70;
        if (state.is(NorthstarFluids.METHANE.get()) || state.is(NorthstarFluids.METHANE.getSource().getSource()))
            return -80;
        if (state.is(NorthstarFluids.HYDROCARBON.get()) || state.is(NorthstarFluids.HYDROCARBON.getSource().getSource()))
            return 500;
        if (state.is(NorthstarFluids.LIQUID_OXYGEN.get()) || state.is(NorthstarFluids.LIQUID_OXYGEN.getSource().getSource()))
            return -180;
        if (state.is(NorthstarFluids.LIQUID_HYDROGEN.get()) || state.is(NorthstarFluids.LIQUID_HYDROGEN.getSource().getSource()))
            return -253;
        if (state.is(NorthstarFluids.SULFURIC_ACID.get()) || state.is(NorthstarFluids.SULFURIC_ACID.getSource().getSource()))
            return 1200;
        return Integer.MAX_VALUE;
    }

    public static int getFreezingPoint(FluidState state) {
        if (state.is(Fluids.WATER) || state.is(Fluids.FLOWING_WATER)) return 0;
        if (state.is(Fluids.LAVA) || state.is(Fluids.FLOWING_LAVA)) return -200;
        if (state.is(AllFluids.CHOCOLATE.get()) || state.is(AllFluids.HONEY.get())) return 20;
        if (state.is(NorthstarFluids.METHANE.get()) || state.is(NorthstarFluids.METHANE.getSource().getSource()))
            return -200;
        if (state.is(NorthstarFluids.LIQUID_OXYGEN.get()) || state.is(NorthstarFluids.LIQUID_OXYGEN.getSource().getSource()))
            return -220;
        if (state.is(NorthstarFluids.HYDROCARBON.get()) || state.is(NorthstarFluids.HYDROCARBON.getSource().getSource()))
            return -60;
        if (state.is(NorthstarFluids.LIQUID_HYDROGEN.get()) || state.is(NorthstarFluids.LIQUID_HYDROGEN.getSource().getSource()))
            return -259;
        if (state.is(NorthstarFluids.SULFURIC_ACID.get()) || state.is(NorthstarFluids.SULFURIC_ACID.getSource().getSource()))
            return -200;
        return Integer.MIN_VALUE;
    }

    public static boolean hasInsulation(LivingEntity entity) {
        return (entity.getItemBySlot(EquipmentSlot.HEAD).is(NorthstarItemTags.INSULATING.tag) &&
                entity.getItemBySlot(EquipmentSlot.CHEST).is(NorthstarItemTags.INSULATING.tag) &&
                entity.getItemBySlot(EquipmentSlot.LEGS).is(NorthstarItemTags.INSULATING.tag) &&
                entity.getItemBySlot(EquipmentSlot.FEET).is(NorthstarItemTags.INSULATING.tag)) ||
               NorthstarEntityTags.CAN_SURVIVE_COLD.matches(entity);
    }

    public static boolean hasHeatProtection(LivingEntity entity) {
        return (entity.getItemBySlot(EquipmentSlot.HEAD).is(NorthstarItemTags.HEAT_RESISTANT.tag) &&
                entity.getItemBySlot(EquipmentSlot.CHEST).is(NorthstarItemTags.HEAT_RESISTANT.tag) &&
                entity.getItemBySlot(EquipmentSlot.LEGS).is(NorthstarItemTags.HEAT_RESISTANT.tag) &&
                entity.getItemBySlot(EquipmentSlot.FEET).is(NorthstarItemTags.HEAT_RESISTANT.tag)) ||
               NorthstarEntityTags.CAN_SURVIVE_HEAT.matches(entity);
    }

    public static void evaporate(Level level, BlockPos pos) {
        RandomSource random = level.random;
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
        for (int i = 0; i < 8; i++) {
            level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0D, 0.0D, 0.0D);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onPostRender(RenderLevelStageEvent event) {
        if (!NorthstarConfigs.client().debugSealerBounds.get())
            return;

        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS)) {
            PoseStack pose = event.getPoseStack();
            Vec3 pos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

            pose.pushPose();
            pose.translate(-pos.x, -pos.y, -pos.z);
            for (Provider provider : Minecraft.getInstance().level.northstar$temperature().providers) {
                if (provider instanceof TemperatureRegulatorBlockEntity regulator)
                    regulator.getSealer().getVisualizer().render(pose, Minecraft.getInstance().renderBuffers().bufferSource());
            }
            pose.popPose();
        }
    }

}

package com.lightning.northstar.world;

import com.lightning.northstar.block.tech.temperature_regulator.TemperatureRegulatorBlockEntity;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTags;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.simibubi.create.AllFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class NorthstarTemperature {

    public static final int MINIMUM_TEMPERATURE = -273;
    public static final int MAXIMUM_TEMPERATURE = 1500;

    private final Level level;
    private final Set<TemperatureRegulatorBlockEntity> regulators;

    public NorthstarTemperature(Level level) {
        this.level = level;
        this.regulators = new HashSet<>();
    }

    public float getTemperatureAt(Vec3 pos) {
        return getTemperatureAt(BlockPos.containing(pos));
    }

    public float getTemperatureAt(Vec3i pos) {
        long packedPos = BlockPos.asLong(pos.getX(), pos.getY(), pos.getZ());
        float temperature = 0;
        int count = 0;

        for (TemperatureRegulatorBlockEntity regulator : regulators) {
            if (regulator.isActive() && regulator.getSealer().getSealedBlocks().contains(packedPos)) {
                temperature += regulator.getTemperature();
                count++;
            }
        }

        return count == 0 ? getBaseTemperature(level, pos instanceof BlockPos bp ? bp : new BlockPos(pos)) : temperature / count;
    }

    public Set<TemperatureRegulatorBlockEntity> getRegulators() {
        return regulators;
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
        if (level.dimension() == NorthstarDimensions.MERCURY_DIM_KEY) {
            return level.canSeeSky(pos) && !level.isNight() ? 434 : -200;
        }

        return NorthstarPlanets.getPlanetTemp(level.dimension());
    }

    public static boolean combustable(FluidState state) {
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
        return 100;
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
        return 0;
    }

    public static boolean hasInsulation(LivingEntity entity) {
        return (entity.getItemBySlot(EquipmentSlot.HEAD).is(NorthstarTags.NorthstarItemTags.INSULATING.tag) &&
                entity.getItemBySlot(EquipmentSlot.CHEST).is(NorthstarTags.NorthstarItemTags.INSULATING.tag) &&
                entity.getItemBySlot(EquipmentSlot.LEGS).is(NorthstarTags.NorthstarItemTags.INSULATING.tag) &&
                entity.getItemBySlot(EquipmentSlot.FEET).is(NorthstarTags.NorthstarItemTags.INSULATING.tag))
                || NorthstarTags.NorthstarEntityTags.CAN_SURVIVE_COLD.matches(entity);

    }

    public static boolean hasHeatProtection(LivingEntity entity) {
        return (entity.getItemBySlot(EquipmentSlot.HEAD).is(NorthstarTags.NorthstarItemTags.HEAT_RESISTANT.tag) &&
                entity.getItemBySlot(EquipmentSlot.CHEST).is(NorthstarTags.NorthstarItemTags.HEAT_RESISTANT.tag) &&
                entity.getItemBySlot(EquipmentSlot.LEGS).is(NorthstarTags.NorthstarItemTags.HEAT_RESISTANT.tag) &&
                entity.getItemBySlot(EquipmentSlot.FEET).is(NorthstarTags.NorthstarItemTags.HEAT_RESISTANT.tag));
    }

    public static double getHeatRating(ResourceKey<Level> level) {
        // I love spaghetti (2)
        if (level == NorthstarDimensions.MOON_DIM_KEY) return 0;
        if (level == NorthstarDimensions.MARS_DIM_KEY) return 0.05;
        if (level == NorthstarDimensions.MERCURY_DIM_KEY) return 0;
        if (level == NorthstarDimensions.VENUS_DIM_KEY) return 5;
        if (level == Level.OVERWORLD) return 0.4;
        return 1;
    }

    public static double getHeatConstant(ResourceKey<Level> level) {
        // I love spaghetti (2)
        if (level == NorthstarDimensions.MOON_DIM_KEY) return 0;
        if (level == NorthstarDimensions.MARS_DIM_KEY) return 50;
        if (level == NorthstarDimensions.MERCURY_DIM_KEY) return 0;
        if (level == NorthstarDimensions.VENUS_DIM_KEY) return 1000;
        if (level == Level.OVERWORLD) return 100;
        return 1;
    }

}

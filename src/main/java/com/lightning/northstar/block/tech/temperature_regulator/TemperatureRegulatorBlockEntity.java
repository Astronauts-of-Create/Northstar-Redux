package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.particle.SnowflakeParticleData;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.util.TemperatureUnit;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.lightning.northstar.world.NorthstarTemperature;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TemperatureRegulatorBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation, NorthstarTemperature.Provider {

    public static final int MAX_LIMIT_SIZE = 5;

    protected final BaseTemperatureRegulator regulator = new BaseTemperatureRegulator();

    protected int sealCooldown;
    protected boolean active;

    public TemperatureRegulatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();

        level.northstar$temperature().registerSealer(this);
    }

    @Override
    public void invalidate() {
        super.invalidate();

        level.northstar$temperature().unregisterSealer(this);
    }

    @Override
    public void tick() {
        super.tick();

        var sealer = regulator.sealer;
        if (sealer.isSealInProgress()) {
            if (sealer.updateSeal(level, getMaximumSealedBlocks())) {
                sealCooldown = NorthstarConfigs.server().sealerCheckDelay.get();
            }
        } else if (sealCooldown-- <= 0) {
            sealer.beginSeal(level, worldPosition, null);
        }

        active = Math.abs(speed) > 0 && !overStressed && !sealer.hasLeak();
        if (active && level.isClientSide) {
            addParticles(isCurrentlyWarm(), speed / 64f);
        }
    }

    public int getMaximumSealedBlocks() {
        return (int) (NorthstarConfigs.server().temperatureRegulatorBlocksPerRpm.get() * Math.abs(speed));
    }

    public boolean isCurrentlyWarm() {
        return regulator.temperature >= NorthstarTemperature.getBaseTemperature(level, worldPosition);
    }

    @Override
    public boolean isSealed(Vec3 pos) {
        return isSealed(Mth.floor(pos.x), Mth.floor(pos.y), Mth.floor(pos.z));
    }

    @Override
    public boolean isSealed(Vec3i pos) {
        return isSealed(pos.getX(), pos.getY(), pos.getZ());
    }

    private boolean isSealed(int x, int y, int z) {
        return active && regulator.sealer.getSealedBlocks().contains(BlockPos.asLong(x, y, z));
    }

    @Override
    public float getTemperature() {
        return regulator.temperature;
    }

    public void addParticles(boolean isWarm, float spinMod) {
        RandomSource random = level.getRandom();
        if (isWarm) {
            spinMod = 1;
        }

        for (int i = 0, j = random.nextInt(isWarm ? 5 : 4); i < j; i++) {
            ParticleOptions particle = isWarm ? ParticleTypes.FLAME : new SnowflakeParticleData();
            double posX = worldPosition.getX() + random.nextDouble();
            double posY = worldPosition.getY() + 0.7 + random.nextDouble();
            double posZ = worldPosition.getZ() + random.nextDouble();
            double velX = random.nextFloat() * 0.05 * (random.nextBoolean() ? -1 : 1) * spinMod;
            double velY = random.nextFloat() * (isWarm ? 0.05 : -0.08);
            double velZ = random.nextFloat() * 0.05 * (random.nextBoolean() ? -1 : 1) * spinMod;
            level.addParticle(particle, posX, posY, posZ, velX, velY, velZ);
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (super.addToGoggleTooltip(tooltip, isPlayerSneaking)) {
            tooltip.add(Component.empty());
        }

        NorthstarLang.translate("gui.goggles.temperature_regulator")
                .forGoggles(tooltip);

        NorthstarLang.translate("gui.goggles.temperature")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);

        TemperatureUnit unit = NorthstarConfigs.client().temperatureUnit.get();
        Lang.number(unit.fromCelsius(regulator.temperature))
                .style(ChatFormatting.AQUA)
                .text(ChatFormatting.GRAY, unit.symbol)
                .forGoggles(tooltip, 1);

        regulator.sealer.addToGoggleTooltip(tooltip, getMaximumSealedBlocks());
        if (isPlayerSneaking)
            regulator.sealer.addCooldownTooltip(tooltip, sealCooldown, getMaximumSealedBlocks());

        return true;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        regulator.write(compound);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        regulator.read(compound, worldPosition);
    }

    public ProgressiveBlockSealer getSealer() {
        return regulator.sealer;
    }

}

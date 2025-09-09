package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.particle.SnowflakeParticleData;
import com.lightning.northstar.util.MutableAABB;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.util.TemperatureUnit;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.lightning.northstar.world.NorthstarTemperature;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TemperatureRegulatorBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation {

    public static final int MAX_LIMIT_SIZE = 5;

    protected final MutableAABB bounds = new MutableAABB();
    protected final ProgressiveBlockSealer sealer = new ProgressiveBlockSealer() {
        @Override
        protected boolean isAirOccluded(BlockGetter level, BlockPos from, BlockPos to, Direction direction) {
            if (!bounds.contains(to))
                return true;
            return super.isAirOccluded(level, from, to, direction);
        }
    };

    protected boolean limit;
    protected int sizeX, sizeY, sizeZ;
    protected float targetTemperature;

    protected int sealCooldown;
    protected boolean active;

    public TemperatureRegulatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        limit = false;
        sizeX = sizeY = sizeZ = 2;

        bounds.inf();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    @Override
    public void initialize() {
        super.initialize();

        NorthstarTemperature.getDimension(level).getRegulators().add(this);
    }

    @Override
    public void destroy() {
        super.destroy();

        NorthstarTemperature.getDimension(level).getRegulators().remove(this);
    }

    @Override
    public void tick() {
        super.tick();

        if (sealer.isSealInProgress()) {
            if (sealer.updateSeal(level, getMaximumSealedBlocks())) {
                sealCooldown = NorthstarConfigs.server().sealerCheckDelay.get();
            }
        } else if (sealCooldown-- <= 0) {
            sealer.beginSeal(level, worldPosition, Direction.UP);
        }

        active = Math.abs(speed) >= 0 && !overStressed;
        if (active && level.isClientSide) {
            addParticles(isCurrentlyWarm(), speed / 64f);
        }
    }

    public int getMaximumSealedBlocks() {
        return (int) (NorthstarConfigs.server().temperatureRegulatorBlocksPerRpm.get() * Math.abs(speed));
    }

    public boolean isCurrentlyWarm() {
        return targetTemperature >= NorthstarTemperature.getBaseTemperature(level, worldPosition);
    }

    public void addParticles(boolean isWarm, float spinMod) {
        RandomSource random = level.getRandom();
        if (isWarm) {
            spinMod = 1;
        }

        for (int i = 0, j = random.nextInt(isWarm ? 6 : 3); i < j; i++) {
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
        CreateLang.number(unit.fromCelsius(targetTemperature))
                .style(ChatFormatting.AQUA)
                .text(ChatFormatting.GRAY, unit.symbol)
                .forGoggles(tooltip, 1);

        sealer.addToGoggleTooltip(tooltip, getMaximumSealedBlocks());
        if (isPlayerSneaking)
            sealer.addCooldownTooltip(tooltip, sealCooldown, getMaximumSealedBlocks());

        return true;
    }

    public void setBounds(boolean limit, int sizeX, int sizeY, int sizeZ) {
        this.limit = limit;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        if (limit) {
            bounds.setCentered(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), sizeX, sizeY, sizeZ);
        } else {
            bounds.inf();
        }
    }

    public void setUnbounded() {
        bounds.inf();
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        compound.putFloat("temperature", targetTemperature);
        compound.putBoolean("limit", limit);
        compound.putInt("sizeX", sizeX);
        compound.putInt("sizeY", sizeY);
        compound.putInt("sizeZ", sizeZ);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        targetTemperature = compound.getFloat("temperature");

        setBounds(compound.getBoolean("limit"), compound.getInt("sizeX"), compound.getInt("sizeY"), compound.getInt("sizeZ"));

        if (compound.contains("temp", Tag.TAG_INT)) {
            targetTemperature = compound.getInt("temp");
        }
    }

    public ProgressiveBlockSealer getSealer() {
        return sealer;
    }

    public boolean isActive() {
        return active;
    }

    public float getTemperature() {
        return targetTemperature;
    }

}

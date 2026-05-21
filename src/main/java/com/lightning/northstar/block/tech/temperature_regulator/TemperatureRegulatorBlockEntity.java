package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.particle.NorthstarParticles;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.kinetics.base.IRotate.StressImpact;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TemperatureRegulatorBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation, ClipboardCloneable, NorthstarTemperature.Provider {

    public static final int MAX_LIMIT_SIZE = 5;

    protected final BaseTemperatureRegulator regulator = new BaseTemperatureRegulator();

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
    public void destroy() {
        super.destroy();

        level.northstar$temperature().enqueueUpdates(regulator.sealer.getSealedBlocks());
    }

    @Override
    public void tick() {
        super.tick();

        ProgressiveBlockSealer sealer = regulator.sealer;
        if (sealer.processSeal(level, worldPosition, null, getMaximumSealedBlocks())) {
            level.northstar$temperature().enqueueUpdates(sealer.getUpdatedBlocks());
        }

        sealer.renderLeakPath(level);

        active = Math.abs(speed) > 0 && !overStressed && !sealer.hasLeak();
        if (active && level.isClientSide) {
            addParticles(isCurrentlyWarm(), speed / 64f);
        }
    }

    public void onTemperatureChanged() {
        if (level != null)
            level.northstar$temperature().enqueueUpdates(regulator.sealer.getSealedBlocks());
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
            ParticleOptions particle = isWarm ? ParticleTypes.FLAME : NorthstarParticles.SNOWFLAKE.get();
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
        NorthstarLang.translate("gui.goggles.temperature_regulator")
                .forGoggles(tooltip);

        if (StressImpact.isEnabled())
            addStressImpactStats(tooltip, calculateStressApplied());

        NorthstarLang.translate("gui.goggles.generic.temperature")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        NorthstarLang.temperature(regulator.temperature)
                .forGoggles(tooltip, 1);

        regulator.sealer.addToGoggleTooltip(tooltip, getMaximumSealedBlocks(), isPlayerSneaking);
        if (isPlayerSneaking)
            regulator.sealer.addCooldownTooltip(tooltip, getMaximumSealedBlocks());

        return true;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        regulator.write(compound);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        float previousTemperature = regulator.temperature;

        super.read(compound, clientPacket);
        regulator.read(compound, worldPosition);

        if (!Mth.equal(previousTemperature, regulator.temperature))
            onTemperatureChanged(); // in case of /data merge, update blocks, if it was just loaded in there is no effect
    }

    @Override
    public String getClipboardKey() {
        return "Block";
    }

    @Override
    public boolean writeToClipboard(CompoundTag tag, Direction side) {
        regulator.write(tag);
        return true;
    }

    @Override
    public boolean readFromClipboard(CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (!tag.contains("temperature")) {
            return false;
        }
        if (!simulate) {
            regulator.read(tag, worldPosition);
        }
        return true;
    }

    public ProgressiveBlockSealer getSealer() {
        return regulator.sealer;
    }

}

package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.contraption.ActorConfigPacket.ITakeConfig;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MovingTemperatureRegulator implements NorthstarTemperature.Provider, ITakeConfig {

    public final MovementContext context;
    public final Contraption contraption;
    public final BaseTemperatureRegulator regulator;
    public boolean active;

    MovingTemperatureRegulator(MovementContext context) {
        this.context = context;
        this.contraption = context.contraption;
        this.regulator = new BaseTemperatureRegulator();

        regulator.read(context.blockEntityData, context.localPos);
    }

    void tick(MovementContext context) {
        ProgressiveBlockSealer sealer = regulator.sealer;
        if (sealer.isSealInProgress()) {
            sealer.updateSeal(context.contraption.getContraptionWorld(), NorthstarConfigs.server().temperatureRegulatorMaxContraptionSealed.get());
        } else {
            sealer.beginSeal(context.contraption.getContraptionWorld(), context.localPos, null);
        }

        if (sealer.hasLeak() && regulator.showLeak)
            sealer.renderLeakPath(context.contraption.entity.level(), context.contraption.entity);

        active = !sealer.hasLeak();
    }

    @Override
    public boolean isSealed(Vec3 pos) {
        if (!active)
            return false;
        Vec3 local = contraption.entity.toLocalVector(pos, 0);
        return regulator.sealer.getSealedBlocks().contains(BlockPos.asLong(Mth.floor(local.x), Mth.floor(local.y), Mth.floor(local.z)));
    }

    @Override
    public boolean isSealed(Vec3i pos) {
        return isSealed(Vec3.atLowerCornerOf(pos));
    }

    @Override
    public float getTemperature() {
        return regulator.temperature;
    }

    @Override
    public void handleServerConfig(CompoundTag nbt) {
        regulator.read(nbt, context.localPos);
    }

}

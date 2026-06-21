package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;

public record RocketStationHolder(
        SimpleContainer container,
        BlockPos pos,
        RocketContraption contraption,
        RocketStationBlockEntity be,
        RocketContraptionEntity entity
) {
}

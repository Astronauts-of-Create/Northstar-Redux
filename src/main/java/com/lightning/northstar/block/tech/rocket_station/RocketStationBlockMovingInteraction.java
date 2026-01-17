package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.content.NorthstarStats;
import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RocketStationBlockMovingInteraction extends MovingInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        if (!(contraptionEntity.getContraption() instanceof RocketContraption)) {
            return false;
        }
        if (!(contraptionEntity instanceof RocketContraptionEntity)) {
            return false;
        }
        if (!contraptionEntity.level().isInWorldBounds(localPos)) {
            return false;

        }

        if (!player.level().isClientSide())
            player.awardStat(NorthstarStats.INTERACT_WITH_ROCKET_STATION);
        AllSoundEvents.CONTROLLER_CLICK.play(player.level(), null, BlockPos.containing(contraptionEntity.toGlobalVector(Vec3.atCenterOf(localPos), 1)), 1, 1.2f);
        if (!contraptionEntity.level().isClientSide && contraptionEntity.level().isInWorldBounds(contraptionEntity.blockPosition()) && !((RocketContraptionEntity) contraptionEntity).landingMode)
            contraptionEntity.disassemble();

        return true;
    }

}

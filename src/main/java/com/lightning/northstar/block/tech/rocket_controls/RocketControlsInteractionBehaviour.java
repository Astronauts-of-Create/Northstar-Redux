package com.lightning.northstar.block.tech.rocket_controls;

import com.lightning.northstar.content.NorthstarStats;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;

import java.util.UUID;

public class RocketControlsInteractionBehaviour extends MovingInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        if (!(contraptionEntity instanceof RocketContraptionEntity rce))
            return false;

        UUID currentlyControlling = rce.getControllingPlayer().orElse(null);

        if (currentlyControlling != null) {
            rce.stopControlling(localPos);
            if (player.getUUID().equals(currentlyControlling))
                return true;
        }

        if (!contraptionEntity.startControlling(localPos, player))
            return false;

        if (!player.level().isClientSide())
            player.awardStat(NorthstarStats.INTERACT_WITH_ROCKET_CONTROLS);

        rce.setControllingPlayer(player.getUUID());
        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RocketControlsClientHandler.startControlling(rce, localPos));
        return true;
    }

}

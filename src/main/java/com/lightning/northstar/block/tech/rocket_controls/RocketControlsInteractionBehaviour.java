package com.lightning.northstar.block.tech.rocket_controls;

import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

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

        rce.setControllingPlayer(player.getUUID());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RocketControlsClientHandler.startControlling(rce, localPos));
        return true;
    }

}

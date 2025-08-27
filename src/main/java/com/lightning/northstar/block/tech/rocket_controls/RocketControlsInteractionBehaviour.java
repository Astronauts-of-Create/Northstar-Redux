package com.lightning.northstar.block.tech.rocket_controls;

import com.google.common.base.Objects;
import com.lightning.northstar.contraptions.RocketContraptionEntity;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.client.Minecraft;
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
            if (Objects.equal(currentlyControlling, player.getUUID()))
                return true;
        }

        if (!contraptionEntity.startControlling(localPos, player))
            return false;

        if (rce.isAllPlayersSeated()) {
            rce.setControllingPlayer(player.getUUID());
            if (player.level().isClientSide)
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RocketControlsClientHandler.startControlling(rce, localPos));
            return true;
        } else {//If all players are not seated,return false
            Minecraft.getInstance().player.displayClientMessage(
                    CreateLang.translateDirect("contraption.controls.sit_down", rce.getContraptionName()), true);
        }
        return false;
    }

}

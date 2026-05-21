package com.lightning.northstar.block.tech.rocket_controls;

import com.lightning.northstar.content.NorthstarStats;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsInteractionBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RocketControlsInteractionBehaviour extends ControlsInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraption) {
        if (!(contraption instanceof RocketContraptionEntity rocket)) {
            return false;
        }
        if (!super.handlePlayerInteraction(player, activeHand, localPos, contraption)) {
            return false;
        }
        player.awardStat(NorthstarStats.INTERACT_WITH_ROCKET_CONTROLS);

        // Override the "Started controlling Rocket"
        if (player.getUUID().equals(contraption.getControllingPlayer().orElse(null)) && player.level().isClientSide()) {
            Component jump = Component.keybind("key.jump");
            Component message = switch (rocket.getStatus()) {
                case WAITING -> Component.translatable("northstar.contraption.rocket.jump_to_launch", jump);
                case COUNTDOWN -> Component.translatable("northstar.contraption.rocket.jump_to_cancel", jump);
                case DESCENDING -> rocket.getContraption().hasAutoLander ?
                        null :
                        Component.translatable("northstar.contraption.rocket.hold_to_slow_down", jump);
                default -> null;
            };
            if (message != null) {
                player.displayClientMessage(message, true);
            }
        }

        return true;
    }

}

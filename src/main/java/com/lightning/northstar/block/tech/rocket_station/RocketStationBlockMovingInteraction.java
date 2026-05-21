package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.content.NorthstarStats;
import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;

public class RocketStationBlockMovingInteraction extends MovingInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity entity) {
        if (!(entity instanceof RocketContraptionEntity rocket)) {
            return false;
        }
        if (!localPos.equals(BlockPos.ZERO)) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("northstar.gui.rocket_station.different_station").withStyle(ChatFormatting.RED), true);
            }
            return true;
        }

        player.awardStat(NorthstarStats.INTERACT_WITH_ROCKET_STATION);

        RocketContraption contraption = rocket.getContraption();
        MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> pair = contraption.getActorAt(localPos);

        if (player instanceof ServerPlayer serverPlayer && pair != null) {
            RocketStationActor actor = RocketStationActor.get(pair.right);

            ItemStack returnTicket = actor.container.getItem(1);
            if (!returnTicket.isEmpty()) {
                entity.level().addFreshEntity(new ItemEntity(entity.level(), player.getX(), player.getY(), player.getZ(), returnTicket, 0, 0, 0));
                actor.container.setItem(1, ItemStack.EMPTY);
                return true;
            }

            RocketStationMenu.open(serverPlayer, actor.container, localPos, contraption, null, rocket);
        }

        return true;
    }

}

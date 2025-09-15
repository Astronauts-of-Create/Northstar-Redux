package com.lightning.northstar.block.tech.temperature_regulator;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.apache.commons.lang3.tuple.MutablePair;

public class TemperatureRegulatorMovingInteractionBehaviour extends MovingInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor = contraptionEntity.getContraption().getActorAt(localPos);
        if (actor == null)
            return false;

        MovementContext ctx = actor.right;
        if (ctx == null || !ctx.world.isClientSide || !(ctx.temporaryData instanceof MovingTemperatureRegulator regulator))
            return false;

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ScreenOpener.open(new TemperatureRegulatorScreen(regulator.regulator, ctx.contraption.entity.getId(), ctx.localPos)));
        return true;
    }

}

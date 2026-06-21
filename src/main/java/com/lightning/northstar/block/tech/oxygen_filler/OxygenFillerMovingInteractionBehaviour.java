package com.lightning.northstar.block.tech.oxygen_filler;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.MutablePair;

public class OxygenFillerMovingInteractionBehaviour extends MovingInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor = contraptionEntity.getContraption().getActorAt(localPos);
        if (actor == null) {
            return false;
        }
        OxygenFillerActor filler = OxygenFillerActor.get(actor.right);
        BlockPos pos = BlockPos.containing(contraptionEntity.toGlobalVector(Vec3.atCenterOf(localPos), 0));
        OxygenFillerBlock.handlePlayerInteraction(player.level(), pos, player, activeHand, filler.container);
        return true;
    }

}

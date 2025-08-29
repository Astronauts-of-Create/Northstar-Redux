package com.lightning.northstar.block.tech.computer_rack;

import com.lightning.northstar.block.tech.NorthstarPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class TargetingComputerRackRenderer extends SmartBlockEntityRenderer<TargetingComputerRackBlockEntity> {

    private static final PartialModel[] MODELS = {
            NorthstarPartialModels.CPU1, NorthstarPartialModels.CPU2, NorthstarPartialModels.CPU3,
            NorthstarPartialModels.CPU4, NorthstarPartialModels.CPU5, NorthstarPartialModels.CPU6
    };

    public TargetingComputerRackRenderer(Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TargetingComputerRackBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        Optional<Direction> optionalValue = blockState.getOptionalValue(
                TargetingComputerRackBlock.HORIZONTAL_FACING);
        if(optionalValue.isEmpty()) return;

        Direction facing = optionalValue.get().getOpposite();
        ms.pushPose();
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        VertexConsumer vc = buffer.getBuffer(RenderType.solid());
        ms.popPose();

        for (int i = 0; i < 6; i++) {
            if (!be.container.getItem(i).isEmpty()) {
                CachedBuffers.partialFacing(MODELS[i], blockState, facing)
                        .light(light)
                        .renderInto(ms, vc);
            }
        }
    }

}

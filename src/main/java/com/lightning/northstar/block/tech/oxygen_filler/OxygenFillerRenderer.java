package com.lightning.northstar.block.tech.oxygen_filler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class OxygenFillerRenderer extends SafeBlockEntityRenderer<OxygenFillerBlockEntity> {

    public OxygenFillerRenderer(Context context) {
    }

    @Override
    protected void renderSafe(OxygenFillerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ItemStack item = be.container.getItem(0);
        if (!item.isEmpty()) {
            ms.pushPose();
            Direction direction = be.getBlockState().getValue(OxygenFillerBlock.HORIZONTAL_FACING);

            switch (direction) {
                case NORTH -> ms.translate(0.5f, 0.35f, 0.25f);
                case SOUTH -> ms.translate(0.5f, 0.35f, 0.75f);
                case EAST -> ms.translate(0.75f, 0.35f, 0.5f);
                case WEST -> ms.translate(0.25f, 0.35f, 0.5f);
            }

            TransformStack.of(ms).rotateY(AngleHelper.horizontalAngle(direction) * Mth.DEG_TO_RAD);

            Minecraft.getInstance()
                    .getItemRenderer()
                    .renderStatic(item, ItemDisplayContext.GROUND, light, overlay, ms, buffer, null, 0);

            ms.popPose();
        }
    }

}

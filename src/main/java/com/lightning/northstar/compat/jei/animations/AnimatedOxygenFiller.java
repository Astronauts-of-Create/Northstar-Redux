package com.lightning.northstar.compat.jei.animations;

import com.lightning.northstar.block.tech.oxygen_filler.OxygenFillerRenderer;
import com.lightning.northstar.content.NorthstarBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class AnimatedOxygenFiller extends AnimatedKinetics {

    @Nullable
    private ItemStack item;

    @Override
    public void draw(GuiGraphics graphics, int x, int y) {
        ItemStack item = Objects.requireNonNullElse(this.item, ItemStack.EMPTY);

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 200);
        pose.mulPose(Axis.XP.rotationDegrees(-15.5f));
        pose.mulPose(Axis.YP.rotationDegrees(22.5f + 180));
        pose.scale(40, 40, 40);
        blockElement(NorthstarBlocks.OXYGEN_FILLER.getDefaultState()).render(graphics);

        pose.scale(1, -1, 1); // ???
        OxygenFillerRenderer.render(item, pose, Direction.NORTH, graphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        pose.popPose();
    }

    public AnimatedOxygenFiller withItem(@Nullable ItemStack item) {
        this.item = item;
        return this;
    }

}

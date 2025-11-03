package com.lightning.northstar.block.tech.rocket_controls;

import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RocketControlsMovementBehaviour implements MovementBehaviour {

    @Override
    public ItemStack canBeDisabledVia(MovementContext context) {
        return null;
    }

    @Override
    public void stopMoving(MovementContext context) {
        context.contraption.entity.stopControlling(context.localPos);
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world.isClientSide) {
            if (!(context.temporaryData instanceof LerpedFloat))
                context.temporaryData = LerpedFloat.linear();

            ((LerpedFloat) context.temporaryData).tickChaser();
        }
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!(context.temporaryData instanceof LerpedFloat angle))
            return;

        if (context.contraption.entity instanceof RocketContraptionEntity rocket && rocket.isInFlight() &&
                rocket.getLaunchTime() <= 2 && angle.getChaseTarget() == 0) {
            angle.chase(1.0, 0.2, LerpedFloat.Chaser.EXP);
        }

        RocketControlsRenderer.render(context, renderWorld, matrices, buffer, angle.getValue(AnimationTickHolder.getPartialTicks(renderWorld)));
    }

}

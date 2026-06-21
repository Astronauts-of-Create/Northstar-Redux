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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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
        if (context.world.isClientSide()) {
            get(context).tickChaser();
        }
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        LerpedFloat angle = get(context);

        if (context.contraption.entity instanceof RocketContraptionEntity rocket) {
            double targetAngle = switch (rocket.getStatus()) {
                case WAITING -> 0f;
                case COUNTDOWN -> rocket.getCountdown() <= 2 ? 1 : 0.5;
                case ASCENDING -> 1;
                case DESCENDING -> rocket.areThrustersEnabled() ? 1 : 0;
            };
            angle.chase(targetAngle, 0.2, LerpedFloat.Chaser.EXP);
        }

        RocketControlsRenderer.render(context, renderWorld, matrices, buffer, angle.getValue(AnimationTickHolder.getPartialTicks(renderWorld)));
    }

    private static LerpedFloat get(MovementContext context) {
        if (context.temporaryData instanceof LerpedFloat f) {
            return f;
        }

        LerpedFloat f = LerpedFloat.linear();
        context.temporaryData = f;
        return f;
    }

}

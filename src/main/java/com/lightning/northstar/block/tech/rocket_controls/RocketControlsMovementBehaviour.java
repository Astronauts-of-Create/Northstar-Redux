package com.lightning.northstar.block.tech.rocket_controls;

import com.lightning.northstar.contraptions.RocketContraptionEntity;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;

public class RocketControlsMovementBehaviour  implements MovementBehaviour {

    static class LeverAngles {
        LerpedFloat steering = LerpedFloat.linear();
        LerpedFloat speed = LerpedFloat.linear();
        LerpedFloat equipAnimation = LerpedFloat.linear();
    }

    @Override
    public ItemStack canBeDisabledVia(MovementContext context) {
        return null;
    }

    @Override
    public void stopMoving(MovementContext context) {
        context.contraption.entity.stopControlling(context.localPos);
        MovementBehaviour.super.stopMoving(context);
    }

    @Override
    public void tick(MovementContext context) {
        MovementBehaviour.super.tick(context);
        if (!context.world.isClientSide)
            return;
        if (!(context.temporaryData instanceof LeverAngles))
            context.temporaryData = new LeverAngles();
        LeverAngles angles = (LeverAngles) context.temporaryData;
        angles.steering.tickChaser();
        angles.speed.tickChaser();
        angles.equipAnimation.tickChaser();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
        ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!(context.temporaryData instanceof LeverAngles angles))
            return;

        float pt = AnimationTickHolder.getPartialTicks(context.world);
        RocketControlsRenderer.render(context, renderWorld, matrices, buffer, angles.equipAnimation.getValue(pt),
                angles.speed.getValue(pt), angles.steering.getValue(pt));

        AbstractContraptionEntity entity = context.contraption.entity;
        if (!(entity instanceof RocketContraptionEntity cce))
            return;

        if (RocketControlsClientHandler.getContraption() == entity && RocketControlsClientHandler.getControlsPos() != null
            && RocketControlsClientHandler.getControlsPos().equals(context.localPos)) {
            Collection<Integer> pressed = RocketControlsClientHandler.currentlyPressed;
            angles.equipAnimation.chase(1, .2f, LerpedFloat.Chaser.EXP);
            angles.steering.chase((pressed.contains(3) ? 0.5 : 0) + (pressed.contains(2) ? -0.5 : 0), 0.2f, LerpedFloat.Chaser.EXP);
            float f = pressed.contains(4) ? -1f : 1f;
//            System.out.println("LENGTH: " + context.motion.length());
            angles.speed.chase(Mth.clamp((Math.min(context.motion.length(), 0.5f) + 1) / 2 * f, 0, 0.4), 0.6f, LerpedFloat.Chaser.EXP);

        } else {
            angles.equipAnimation.chase(0, .2f, LerpedFloat.Chaser.EXP);
            angles.steering.chase(0, 0, LerpedFloat.Chaser.EXP);
            angles.speed.chase(0, 0, LerpedFloat.Chaser.EXP);
        }

    }
}

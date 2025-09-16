package com.lightning.northstar.block.tech.oxygen_sealer;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.lightning.northstar.client.renderer.block.SpinningBlockRenderer;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OxygenSealerMovementBehaviour implements MovementBehaviour {

    @Override
    public void stopMoving(MovementContext context) {
        if (context.temporaryData instanceof MovingOxygenSealer sealer) {
            context.world.northstar$oxygen().unregisterSealer(sealer);
            context.temporaryData = null;
        }
    }

    @Override
    public void tick(MovementContext context) {
        if (context.temporaryData instanceof MovingOxygenSealer sealer) {
            sealer.tick(context);
        } else {
            // we cannot rely on startMoving() because it is called unreliably by trains due to dimensional carriages
            MovingOxygenSealer sealer = new MovingOxygenSealer(context.contraption);
            context.temporaryData = sealer;
            context.world.northstar$oxygen().registerSealer(sealer);
            sealer.tick(context);
        }
    }

    @Override
    public boolean hasSpecialInstancedRendering() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        float rpm = 0;
        if (context.temporaryData instanceof MovingOxygenSealer sealer) {
            if (sealer.active)
                rpm = 128;

            sealer.sealer.getVisualizer().render(matrices.getModelViewProjection(), buffer);
        }

        SpinningBlockRenderer.renderInContraption(context, renderWorld, matrices, buffer, NorthstarPartialModels.OXYGEN_SEALER_FAN, rpm);
    }

}

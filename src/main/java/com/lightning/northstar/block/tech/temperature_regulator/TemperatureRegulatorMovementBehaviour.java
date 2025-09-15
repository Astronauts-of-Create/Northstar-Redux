package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.client.renderer.block.SpinningBlockRenderer;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.lightning.northstar.world.NorthstarTemperature;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TemperatureRegulatorMovementBehaviour implements MovementBehaviour {

    @Override
    public void stopMoving(MovementContext context) {
        if (context.temporaryData instanceof MovingTemperatureRegulator regulator) {
            context.world.northstar$temperature().unregisterSealer(regulator);
            context.temporaryData = null;
        }
    }

    @Override
    public void tick(MovementContext context) {
        if (context.temporaryData instanceof MovingTemperatureRegulator regulator) {
            regulator.tick(context);
        } else {
            // we cannot rely on startMoving() because it is called unreliably by trains

            MovingTemperatureRegulator regulator = new MovingTemperatureRegulator(context);
            context.temporaryData = regulator;
            context.world.northstar$temperature().registerSealer(regulator);
            regulator.tick(context);
        }
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        float rpm = 0;
        boolean warm = false;
        if (context.temporaryData instanceof MovingTemperatureRegulator regulator) {
            if (regulator.active)
                rpm = 128;
            warm = regulator.regulator.temperature >= NorthstarTemperature.getBaseTemperature(context.world, context.contraption.entity.blockPosition());

            regulator.regulator.sealer.getVisualizer().render(matrices.getModelViewProjection(), buffer);
        }

        PartialModel model = warm ? NorthstarPartialModels.WARM_SPINNY : NorthstarPartialModels.COLD_SPINNY;

        SpinningBlockRenderer.renderInContraption(context, renderWorld, matrices, buffer, model, rpm);
    }

}

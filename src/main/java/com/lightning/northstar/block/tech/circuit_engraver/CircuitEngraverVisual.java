package com.lightning.northstar.block.tech.circuit_engraver;

import com.lightning.northstar.content.NorthstarPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction;

public class CircuitEngraverVisual extends SingleAxisRotatingVisual<CircuitEngraverBlockEntity> implements SimpleDynamicVisual {

    private final RotatingInstance crystalHead;
    private final RotatingInstance crystalLaser;

    public CircuitEngraverVisual(VisualizationContext context, CircuitEngraverBlockEntity entity, float partialTick) {
        super(context, entity, partialTick, Models.partial(AllPartialModels.SHAFT));

        crystalHead = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(NorthstarPartialModels.CIRCUIT_ENGRAVER_HEAD))
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);

        crystalLaser = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(NorthstarPartialModels.CIRCUIT_ENGRAVER_LASER))
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);
    }

    @Override
    public void beginFrame(Context ctx) {
        boolean running = blockEntity.engravingBehaviour.running;
        float speed = blockEntity.getRenderedHeadRotationSpeed(ctx.partialTick());

        crystalHead.setPosition(getVisualPosition())
                .setRotationalSpeed(speed * 0.5f)
                .setChanged();

        crystalLaser.setVisible(running);
        crystalLaser.setPosition(getVisualPosition())
                .nudge(0, -0.16f, 0)
                .setRotationalSpeed(speed / 1.5f)
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(crystalHead, crystalLaser);
    }

    @Override
    protected void _delete() {
        super._delete();
        crystalHead.delete();
        crystalLaser.delete();
    }

}

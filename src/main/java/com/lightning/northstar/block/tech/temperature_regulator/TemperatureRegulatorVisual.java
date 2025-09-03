package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.block.tech.NorthstarPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

public class TemperatureRegulatorVisual extends SingleAxisRotatingVisual<TemperatureRegulatorBlockEntity> implements SimpleDynamicVisual {

    private final RotatingInstance warmSpinner;
    private final RotatingInstance coldSpinner;

    public TemperatureRegulatorVisual(VisualizationContext context, TemperatureRegulatorBlockEntity entity, float partialTick) {
        super(context, entity, partialTick, Models.partial(AllPartialModels.SHAFT_HALF));

        rotatingModel.rotateToFace(Direction.NORTH, Direction.Axis.Y);

        warmSpinner = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(NorthstarPartialModels.WARM_SPINNY))
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);

        coldSpinner = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(NorthstarPartialModels.COLD_SPINNY))
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);
    }

    @Override
    public void beginFrame(Context context) {
        float speed = blockEntity.getSpeed();
        boolean warm = blockEntity.isCurrentlyWarm();

        warmSpinner.setVisible(warm);
        warmSpinner.setPosition(getVisualPosition())
                .setRotationalSpeed(speed / 2)
                .setChanged();

        coldSpinner.setVisible(!warm);
        coldSpinner.setPosition(getVisualPosition())
                .setRotationalSpeed(speed / 2)
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(warmSpinner, coldSpinner);
    }

    @Override
    protected void _delete() {
        super._delete();
        warmSpinner.delete();
        coldSpinner.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(warmSpinner);
        consumer.accept(coldSpinner);
    }

}

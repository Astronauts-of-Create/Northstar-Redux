package com.lightning.northstar.block.tech.rocket_controls;

import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import net.createmod.catnip.math.AngleHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.function.Consumer;

public class RocketControlsVisual extends AbstractBlockEntityVisual<RocketControlsBlockEntity> {

    private final OrientedInstance lever;

    public RocketControlsVisual(VisualizationContext context, RocketControlsBlockEntity entity, float partialTick) {
        super(context, entity, partialTick);

        Quaternionf rotation = Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(RocketControlsBlock.FACING)));

        lever = instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(NorthstarPartialModels.CONTROL_LEVER_BLOCK))
                .createInstance()
                .position(getVisualPosition())
                .rotate(rotation);
        lever.setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(lever);
    }

    @Override
    protected void _delete() {
        lever.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(lever);
    }

}

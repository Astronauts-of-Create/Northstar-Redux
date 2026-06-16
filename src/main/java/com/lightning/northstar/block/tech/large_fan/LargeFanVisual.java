package com.lightning.northstar.block.tech.large_fan;

import com.lightning.northstar.content.NorthstarPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class LargeFanVisual extends KineticBlockEntityVisual<LargeFanBlockEntity> {

    private final OrientedInstance casing;

    public LargeFanVisual(VisualizationContext context, LargeFanBlockEntity entity, float partialTick) {
        super(context, entity, partialTick);

        BlockState state = entity.getBlockState();
        Direction.Axis axis = state.getValue(LargeFanBlock.AXIS);
        Direction dir = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        TenPatch patch = state.getValue(LargeFanBlock.PATCH);
        PartialModel model = switch (patch.type) {
            case SINGLE -> NorthstarPartialModels.LARGE_FAN_SINGLE;
            case CENTER -> NorthstarPartialModels.LARGE_FAN_CENTER;
            case CORNER -> NorthstarPartialModels.LARGE_FAN_CORNER;
            case SIDE -> NorthstarPartialModels.LARGE_FAN_SIDE;
        };

        casing = instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(model, dir))
                .createInstance()
                .position(entity.getBlockPos())
                .rotateDegrees(patch.rotation, axis);
        casing.setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(casing);
    }

    @Override
    protected void _delete() {
        casing.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(casing);
    }

}

package com.lightning.northstar.block.tech.oxygen_sealer;

import com.lightning.northstar.content.NorthstarPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

public class OxygenSealerVisual extends KineticBlockEntityVisual<OxygenSealerBlockEntity> implements SimpleTickableVisual {

    private final RotatingInstance shaft;
    private final RotatingInstance propeller;

    public OxygenSealerVisual(VisualizationContext context, OxygenSealerBlockEntity entity, float partialTick) {
        super(context, entity, partialTick);

        shaft = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF, Direction.DOWN))
                .createInstance()
                .setPosition(getVisualPosition());

        propeller = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(NorthstarPartialModels.OXYGEN_SEALER_FAN))
                .createInstance()
                .setPosition(getVisualPosition());
    }

    @Override
    public void tick(Context context) {
        shaft.setup(blockEntity).setChanged();
        propeller.setup(blockEntity).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(shaft, propeller);
    }

    @Override
    protected void _delete() {
        shaft.delete();
        propeller.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(shaft);
        consumer.accept(propeller);
    }

}

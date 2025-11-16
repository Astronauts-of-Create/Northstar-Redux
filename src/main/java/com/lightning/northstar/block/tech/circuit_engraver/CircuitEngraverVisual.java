package com.lightning.northstar.block.tech.circuit_engraver;

import com.lightning.northstar.content.NorthstarPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

public class CircuitEngraverVisual extends SingleAxisRotatingVisual<CircuitEngraverBlockEntity> {

    private final RotatingInstance head;
    private final RotatingInstance laser;

    public CircuitEngraverVisual(VisualizationContext context, CircuitEngraverBlockEntity entity, float partialTick) {
        super(context, entity, partialTick, Models.partial(AllPartialModels.SHAFT));

        head = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(NorthstarPartialModels.CIRCUIT_ENGRAVER_HEAD))
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);

        laser = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(NorthstarPartialModels.CIRCUIT_ENGRAVER_LASER))
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);
        laser.light(LightTexture.FULL_BRIGHT);
    }

    @Override
    public void tick(Context context) {
        super.tick(context);

        boolean running = blockEntity.isRunning();
        float speed = running ? blockEntity.getSpeed() : 0;

        head.setPosition(getVisualPosition())
                .setRotationalSpeed(speed)
                .setChanged();

        laser.setVisible(running);
        laser.setPosition(getVisualPosition())
                .nudge(0, -0.16f, 0)
                .setRotationalSpeed(speed * 2)
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(head);
    }

    @Override
    protected void _delete() {
        super._delete();
        head.delete();
        laser.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(head);
    }

}

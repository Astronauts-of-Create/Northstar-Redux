package com.lightning.northstar.block.tech.combustion_engine;

import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;

import java.util.function.Consumer;

public class CombustionEngineVisual extends SingleAxisRotatingVisual<CombustionEngineBlockEntity> implements SimpleDynamicVisual {

    private final OrientedInstance piston1;
    private final OrientedInstance piston2;
    private final OrientedInstance piston3;
    private final OrientedInstance piston4;
    private final OrientedInstance piston5;
    private final OrientedInstance piston6;

    public CombustionEngineVisual(VisualizationContext context, CombustionEngineBlockEntity entity, float partialTick) {
        super(context, entity, partialTick, Direction.SOUTH, Models.partial(AllPartialModels.SHAFT_HALF));

        Quaternionf rotation = Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(CombustionEngineBlock.HORIZONTAL_FACING)));

        piston1 = instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(NorthstarPartialModels.PISTON1))
                .createInstance()
                .rotate(rotation);
        piston2 = instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(NorthstarPartialModels.PISTON2))
                .createInstance()
                .rotate(rotation);
        piston3 = instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(NorthstarPartialModels.PISTON3))
                .createInstance()
                .rotate(rotation);
        piston4 = instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(NorthstarPartialModels.PISTON4))
                .createInstance()
                .rotate(rotation);
        piston5 = instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(NorthstarPartialModels.PISTON5))
                .createInstance()
                .rotate(rotation);
        piston6 = instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(NorthstarPartialModels.PISTON6))
                .createInstance()
                .rotate(rotation);

        beginFrame(null);
    }

    @Override
    public void beginFrame(Context ctx) {
        float time = AnimationTickHolder.getRenderTime(level) * Math.signum(blockEntity.getSpeed()) * 2f;

        piston1.position(getVisualPosition()).translatePosition(0, getPistonOffset(time), 0).setChanged();
        piston2.position(getVisualPosition()).translatePosition(0, getPistonOffset(time + 2), 0).setChanged();
        piston3.position(getVisualPosition()).translatePosition(0, getPistonOffset(time + 4), 0).setChanged();
        piston4.position(getVisualPosition()).translatePosition(0, getPistonOffset(time + 8), 0).setChanged();
        piston5.position(getVisualPosition()).translatePosition(0, getPistonOffset(time + 10), 0).setChanged();
        piston6.position(getVisualPosition()).translatePosition(0, getPistonOffset(time + 12), 0).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(piston1, piston2, piston3, piston4, piston5, piston6);
    }

    @Override
    protected void _delete() {
        super._delete();
        piston1.delete();
        piston2.delete();
        piston3.delete();
        piston4.delete();
        piston5.delete();
        piston6.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(piston1);
        consumer.accept(piston2);
        consumer.accept(piston3);
        consumer.accept(piston4);
        consumer.accept(piston5);
        consumer.accept(piston6);
    }

    public static float getPistonOffset(double time) {
        return (float) (Math.sin(time) * 0.05f);
    }

}

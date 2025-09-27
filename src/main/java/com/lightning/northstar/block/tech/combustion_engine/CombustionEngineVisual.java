package com.lightning.northstar.block.tech.combustion_engine;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import org.joml.Quaternionf;

public class CombustionEngineVisual extends ShaftInstance<CombustionEngineBlockEntity> implements DynamicInstance {

    private final OrientedData piston1;
    private final OrientedData piston2;
    private final OrientedData piston3;
    private final OrientedData piston4;
    private final OrientedData piston5;
    private final OrientedData piston6;

    public CombustionEngineVisual(MaterialManager materialManager, CombustionEngineBlockEntity entity) {
        super(materialManager, entity);

        Quaternionf rotation = Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(CombustionEngineBlock.HORIZONTAL_FACING)));

        piston1 = materialManager
                .defaultSolid()
                .material(Materials.ORIENTED)
                .getModel(NorthstarPartialModels.PISTON1)
                .createInstance()
                .setRotation(rotation);
        piston2 = materialManager
                .defaultSolid()
                .material(Materials.ORIENTED)
                .getModel(NorthstarPartialModels.PISTON2)
                .createInstance()
                .setRotation(rotation);
        piston3 = materialManager
                .defaultSolid()
                .material(Materials.ORIENTED)
                .getModel(NorthstarPartialModels.PISTON3)
                .createInstance()
                .setRotation(rotation);
        piston4 = materialManager
                .defaultSolid()
                .material(Materials.ORIENTED)
                .getModel(NorthstarPartialModels.PISTON4)
                .createInstance()
                .setRotation(rotation);
        piston5 = materialManager
                .defaultSolid()
                .material(Materials.ORIENTED)
                .getModel(NorthstarPartialModels.PISTON5)
                .createInstance()
                .setRotation(rotation);
        piston6 = materialManager
                .defaultSolid()
                .material(Materials.ORIENTED)
                .getModel(NorthstarPartialModels.PISTON6)
                .createInstance()
                .setRotation(rotation);

        beginFrame();
    }

    @Override
    public void beginFrame() {
        float time = AnimationTickHolder.getRenderTime() * Math.signum(blockEntity.getSpeed()) * 2f;

        piston1.setPosition(getInstancePosition()).nudge(0, getPistonOffset(time), 0);
        piston2.setPosition(getInstancePosition()).nudge(0, getPistonOffset(time + 2), 0);
        piston3.setPosition(getInstancePosition()).nudge(0, getPistonOffset(time + 4), 0);
        piston4.setPosition(getInstancePosition()).nudge(0, getPistonOffset(time + 8), 0);
        piston5.setPosition(getInstancePosition()).nudge(0, getPistonOffset(time + 10), 0);
        piston6.setPosition(getInstancePosition()).nudge(0, getPistonOffset(time + 12), 0);
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, piston1, piston2, piston3, piston4, piston5, piston6);
    }

    @Override
    public void remove() {
        super.remove();
        piston1.delete();
        piston2.delete();
        piston3.delete();
        piston4.delete();
        piston5.delete();
        piston6.delete();
    }

    public static float getPistonOffset(double time) {
        return (float) (Math.sin(time) * 0.05f);
    }

}

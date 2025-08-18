package com.lightning.northstar.block.tech.oxygen_concentrator;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.lightning.northstar.block.tech.NorthstarPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.Direction;

public class OxygenConcentratorVisual extends KineticBlockEntityInstance<OxygenConcentratorBlockEntity> implements DynamicInstance {

    private final RotatingData shaft;
    private final RotatingData propeller;

    public OxygenConcentratorVisual(MaterialManager materialManager, OxygenConcentratorBlockEntity entity) {
        super(materialManager, entity);

        shaft = (RotatingData) materialManager
                .defaultSolid()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(AllPartialModels.SHAFT_HALF)
                .createInstance()
                .setRotationAxis(Direction.Axis.Y)
                .setPosition(getInstancePosition());
                //.rotateToFace(Direction.SOUTH, Direction.DOWN);

        propeller = (RotatingData) materialManager
                .defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(NorthstarPartialModels.OXYGEN_CONCENTATOR_FAN)
                .createInstance()
                .setRotationAxis(Direction.Axis.Y)
                .setPosition(getInstancePosition());
                //.setRotationAxis(Direction.Axis.Y);
    }

    @Override
    public void beginFrame() {
        shaft.setRotationalSpeed(blockEntity.getSpeed());
        propeller.setRotationalSpeed(blockEntity.getSpeed() * 0.25f);
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, shaft, propeller);
    }

    @Override
    protected void remove() {
        shaft.delete();
        propeller.delete();
    }

}

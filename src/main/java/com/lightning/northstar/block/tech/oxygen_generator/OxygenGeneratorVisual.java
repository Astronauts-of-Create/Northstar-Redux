package com.lightning.northstar.block.tech.oxygen_generator;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.lightning.northstar.block.tech.NorthstarPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.Direction;

public class OxygenGeneratorVisual extends KineticBlockEntityInstance<OxygenGeneratorBlockEntity> implements DynamicInstance {

    private final RotatingData shaft;
    private final RotatingData propeller;

    public OxygenGeneratorVisual(MaterialManager materialManager, OxygenGeneratorBlockEntity entity) {
        super(materialManager, entity);

        shaft = (RotatingData) materialManager
                .defaultSolid()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(AllPartialModels.SHAFT_HALF, blockState, Direction.DOWN)
                .createInstance()
                .setRotationAxis(Direction.Axis.Y)
                .setPosition(getInstancePosition());

        propeller = (RotatingData) materialManager
                .defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(NorthstarPartialModels.OXYGEN_FAN)
                .createInstance()
                .setRotationAxis(Direction.Axis.Y)
                .setPosition(getInstancePosition());
    }

    @Override
    public void beginFrame() {
        shaft.setRotationalSpeed(blockEntity.getSpeed());
        propeller.setRotationalSpeed(blockEntity.getSpeed());
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

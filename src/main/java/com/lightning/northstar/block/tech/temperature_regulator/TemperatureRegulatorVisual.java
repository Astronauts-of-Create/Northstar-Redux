package com.lightning.northstar.block.tech.temperature_regulator;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.Direction;

public class TemperatureRegulatorVisual extends HalfShaftInstance<TemperatureRegulatorBlockEntity> implements DynamicInstance {

    private final RotatingData warmSpinner;
    private final RotatingData coldSpinner;

    public TemperatureRegulatorVisual(MaterialManager materialManager, TemperatureRegulatorBlockEntity entity) {
        super(materialManager, entity);

        warmSpinner = materialManager
                .defaultSolid()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(NorthstarPartialModels.WARM_SPINNY)
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);

        coldSpinner = materialManager
                .defaultSolid()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(NorthstarPartialModels.COLD_SPINNY)
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);
    }

    @Override
    protected Direction getShaftDirection() {
        return Direction.DOWN;
    }

    @Override
    public void beginFrame() {
        float speed = blockEntity.getSpeed();
        boolean warm = blockEntity.isCurrentlyWarm();

        warmSpinner.setPosition(getInstancePosition())
                .nudge(0, warm ? 0 : 1e8f, 0)
                .setRotationalSpeed(speed / 2);

        coldSpinner.setPosition(getInstancePosition())
                .nudge(0, warm ? 1e8f : 0, 0)
                .setRotationalSpeed(speed / 2);
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, warmSpinner, coldSpinner);
    }

    @Override
    public void remove() {
        super.remove();
        warmSpinner.delete();
        coldSpinner.delete();
    }

}

package com.lightning.northstar.block.tech.rocket_controls;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.lightning.northstar.block.tech.NorthstarPartialModels;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.utility.AngleHelper;
import org.joml.Quaternionf;

public class RocketControlsVisual extends BlockEntityInstance<RocketControlsBlockEntity> {

    private final OrientedData lever;

    public RocketControlsVisual(MaterialManager materialManager, RocketControlsBlockEntity entity) {
        super(materialManager, entity);

        Quaternionf rotation = Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(blockState.getValue(RocketControlsBlock.FACING)));

        lever = materialManager
                .defaultSolid()
                .material(Materials.ORIENTED)
                .getModel(NorthstarPartialModels.CONTROL_LEVER_BLOCK)
                .createInstance()
                .setPosition(getInstancePosition())
                .setRotation(rotation);
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, lever);
    }

    @Override
    protected void remove() {
        lever.delete();
    }

}

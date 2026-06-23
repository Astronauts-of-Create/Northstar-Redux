package com.lightning.northstar.block.tech.large_fan;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class LargeFanVisual extends KineticBlockEntityInstance<LargeFanBlockEntity> {

    private final OrientedData casing;

    public LargeFanVisual(MaterialManager materialManager, LargeFanBlockEntity entity) {
        super(materialManager, entity);

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

        casing = materialManager
                .defaultCutout()
                .material(Materials.ORIENTED)
                .getModel(model, entity.getBlockState(), dir)
                .createInstance()
                .setPosition(getInstancePosition())
                .setRotation(Axis.of(dir.step()).rotationDegrees(patch.rotation));
        casing.markDirty();
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, casing);
    }

    @Override
    protected void remove() {
        casing.delete();
    }

}

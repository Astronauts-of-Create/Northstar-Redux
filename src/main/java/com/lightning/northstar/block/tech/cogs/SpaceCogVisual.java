package com.lightning.northstar.block.tech.cogs;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;

public record SpaceCogVisual(PartialModel small, PartialModel big) {

    public ShaftInstance<BracketedKineticBlockEntity> create(MaterialManager materialManager, BracketedKineticBlockEntity blockEntity) {
        if (ICogWheel.isLargeCog(blockEntity.getBlockState())) {
            return new ShaftInstance<>(materialManager, blockEntity) {
                @Override
                public void update() {
                    rotatingModel.setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos));
                    super.update();
                }
                @Override
                protected Instancer<RotatingData> getModel() {
                    return getRotatingMaterial().getModel(big);
                }
            };
        }
        return new ShaftInstance<>(materialManager, blockEntity) {
            @Override
            protected Instancer<RotatingData> getModel() {
                return getRotatingMaterial().getModel(small);
            }
        };
    }

}

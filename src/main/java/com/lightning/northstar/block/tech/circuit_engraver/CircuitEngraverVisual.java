package com.lightning.northstar.block.tech.circuit_engraver;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.lightning.northstar.block.tech.NorthstarPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.core.Direction;

public class CircuitEngraverVisual extends ShaftInstance<CircuitEngraverBlockEntity> implements DynamicInstance {

    private final RotatingData crystalHead;
    private final RotatingData crystalLaser;

    public CircuitEngraverVisual(MaterialManager materialManager, CircuitEngraverBlockEntity entity) {
        super(materialManager, entity);

        crystalHead = materialManager
                .defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(NorthstarPartialModels.CIRCUIT_ENGRAVER_HEAD, getRenderedBlockState())
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);

        crystalLaser =  materialManager
                .defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(NorthstarPartialModels.CIRCUIT_ENGRAVER_LASER)
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);
    }

    @Override
    public void beginFrame() {
        boolean running = blockEntity.engravingBehaviour.running;
        float speed = blockEntity.getRenderedHeadRotationSpeed(AnimationTickHolder.getPartialTicks());

        crystalHead.setPosition(getInstancePosition())
                .setRotationalSpeed(speed * 0.5f);

        crystalLaser.setPosition(getInstancePosition())
                .nudge(0, running ? -0.16f : 1e8f, 0)
                .setRotationalSpeed(speed / 1.5f);
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, crystalHead, crystalLaser);
    }

    @Override
    public void remove() {
        super.remove();
        crystalHead.delete();
        crystalLaser.delete();
    }

}

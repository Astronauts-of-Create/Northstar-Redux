package com.lightning.northstar.block.tech.circuit_engraver;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Direction;

public class CircuitEngraverVisual extends ShaftInstance<CircuitEngraverBlockEntity> implements DynamicInstance {

    private final RotatingData head;
    private final RotatingData laser;

    public CircuitEngraverVisual(MaterialManager materialManager, CircuitEngraverBlockEntity entity) {
        super(materialManager, entity);

        head = materialManager
                .defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(NorthstarPartialModels.CIRCUIT_ENGRAVER_HEAD, getRenderedBlockState())
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);

        laser = materialManager
                .defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(NorthstarPartialModels.CIRCUIT_ENGRAVER_LASER)
                .createInstance()
                .setRotationAxis(Direction.Axis.Y);
        laser.light(LightTexture.FULL_BRIGHT);
    }

    @Override
    public void beginFrame() {
        boolean running = blockEntity.isRunning();
        float speed = running ? blockEntity.getSpeed() : 0;

        head.setPosition(getInstancePosition())
                .setRotationalSpeed(speed);

        laser.setPosition(getInstancePosition())
                .nudge(0, running ? -0.16f : 1e8f, 0)
                .setRotationalSpeed(speed * 2);
    }

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos, head);
    }

    @Override
    public void remove() {
        super.remove();
        head.delete();
        laser.delete();
    }

}

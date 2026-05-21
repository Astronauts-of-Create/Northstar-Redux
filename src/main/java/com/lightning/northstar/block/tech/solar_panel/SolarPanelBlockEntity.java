package com.lightning.northstar.block.tech.solar_panel;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SolarPanelBlockEntity extends GeneratingKineticBlockEntity {

    public static final int MAXIMUM_SPEED = 16;

    public final LerpedFloat targetAngle = LerpedFloat.angular();

    private float generatedSpeed;

    public SolarPanelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide()) {
            targetAngle.tickChaser();
        } else {
            int rpm = (int) (MAXIMUM_SPEED * level.northstar$dimension().sun().get(level, worldPosition));
            if (rpm != generatedSpeed && getFlickerScore() <= 64) {
                generatedSpeed = rpm;
                updateGeneratedRotation();
            }
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        generatedSpeed = compound.getFloat("GeneratorSpeed");
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("GeneratorSpeed", generatedSpeed);
    }

    @Override
    public float getGeneratedSpeed() {
        return generatedSpeed;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).inflate(1);
    }

}

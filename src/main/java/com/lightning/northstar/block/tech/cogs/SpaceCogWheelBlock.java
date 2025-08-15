package com.lightning.northstar.block.tech.cogs;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SpaceCogWheelBlock extends CogWheelBlock {

    protected SpaceCogWheelBlock(boolean large, Properties properties) {
        super(large, properties);
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.BRACKETED_KINETIC.get();
    }

    public static SpaceCogWheelBlock small(Properties properties) {
        return new SpaceCogWheelBlock(false, properties);
    }

    public static SpaceCogWheelBlock large(Properties properties) {
        return new SpaceCogWheelBlock(true, properties);
    }

}

package com.lightning.northstar.block.simple;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class SpaceDoorBlock extends SlidingDoorBlock {

    public SpaceDoorBlock(Properties properties, BlockSetType type, boolean folds) {
        super(properties, type, folds);
    }

    @Override
    public BlockEntityType<? extends SlidingDoorBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.SPACE_DOORS.get();
    }

}

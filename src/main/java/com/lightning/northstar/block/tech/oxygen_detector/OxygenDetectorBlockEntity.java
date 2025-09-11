package com.lightning.northstar.block.tech.oxygen_detector;

import com.lightning.northstar.world.NorthstarOxygen;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class OxygenDetectorBlockEntity extends SmartBlockEntity {

    public OxygenDetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void tick() {
        super.tick();

        BlockState state = getBlockState();
        BlockPos observed = worldPosition.relative(state.getValue(OxygenDetectorBlock.FACING));

        boolean oxygenated = NorthstarOxygen.hasOxygen(level, observed);
        if (oxygenated != state.getValue(OxygenDetectorBlock.POWERED)) {
            level.setBlock(worldPosition, state.setValue(OxygenDetectorBlock.POWERED, oxygenated), Block.UPDATE_ALL);
        }
    }

}

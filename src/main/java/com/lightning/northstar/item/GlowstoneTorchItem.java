package com.lightning.northstar.item;

import com.lightning.northstar.block.GlowstoneTorchWallBlock;
import com.lightning.northstar.content.NorthstarTechBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GlowstoneTorchItem extends BlockItem {

    private final Block wallBlock;

    public GlowstoneTorchItem(GlowstoneTorchWallBlock pStandingBlock, Properties pProperties) {
        super(pStandingBlock, pProperties);
        this.wallBlock = NorthstarTechBlocks.GLOWSTONE_TORCH_WALL.get();
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        BlockState blockstate = this.wallBlock.getStateForPlacement(context);
        BlockState blockstate1 = null;
        LevelReader levelreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();

        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction != Direction.UP) {
                BlockState blockstate2 = direction == Direction.DOWN ? this.getBlock().getStateForPlacement(context) : blockstate;
                if (blockstate2 != null && blockstate2.canSurvive(levelreader, blockpos)) {
                    blockstate1 = blockstate2;
                    break;
                }
            }
        }

        return blockstate1 != null && levelreader.isUnobstructed(blockstate1, blockpos, CollisionContext.empty()) ? blockstate1 : null;
    }

    @Override
    public void registerBlocks(Map<Block, Item> pBlockToItemMap, Item pItem) {
        super.registerBlocks(pBlockToItemMap, pItem);
        pBlockToItemMap.put(this.wallBlock, pItem);
    }

    @Override
    public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
        super.removeFromBlockToItemMap(blockToItemMap, itemIn);
        blockToItemMap.remove(this.wallBlock);
    }

}

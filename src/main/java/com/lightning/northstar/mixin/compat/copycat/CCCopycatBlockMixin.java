package com.lightning.northstar.mixin.compat.copycat;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import com.lightning.northstar.content.NorthstarTags;
import com.lightning.northstar.world.sealer.SealableBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import com.simibubi.create.AllBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(CCCopycatBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CCCopycatBlockMixin implements SealableBlock {

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        if (!(level.getBlockEntity(pos) instanceof CCCopycatBlockEntity be))
            return false;
        BlockState material = be.getMaterial();
        return material != AllBlocks.COPYCAT_BASE.getDefaultState() &&
                !NorthstarTags.NorthstarBlockTags.AIR_PASSES_THROUGH.matches(material) &&
                Block.isFaceFull(state.getShape(level, pos), direction);
    }

}

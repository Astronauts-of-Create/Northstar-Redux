package com.lightning.northstar.mixin.compat.copycat;

import com.copycatsplus.copycats.content.copycat.half_layer.CopycatHalfLayerBlock;
import com.copycatsplus.copycats.content.copycat.vertical_half_layer.CopycatVerticalHalfLayerBlock;
import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.compat.copycats.CopycatSealHelper;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.world.sealer.SealableBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@WhenModLoaded(ModCompat.COPYCATS)
@Mixin(CopycatVerticalHalfLayerBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CopycatVerticalHalfLayerBlockMixin implements SealableBlock {

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        if (!Block.isFaceFull(state.getShape(level, pos), direction))
            return false;

        Direction facing = state.getValue(CopycatVerticalHalfLayerBlock.FACING);

        SlabType testType = direction.getAxis() == Axis.Y || direction.getAxis() == facing.getAxis() ?
                SlabType.DOUBLE :
                (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE) == (direction.getAxis() == Axis.X) ?
                SlabType.BOTTOM :
                SlabType.TOP;

        return CopycatSealHelper.isSlabSealed(level, pos, testType, CopycatHalfLayerBlock.NEGATIVE_LAYERS.getName(), CopycatHalfLayerBlock.POSITIVE_LAYERS.getName());
    }

}

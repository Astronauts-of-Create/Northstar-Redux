package com.lightning.northstar.mixin.compat.copycat;

import com.copycatsplus.copycats.content.copycat.half_layer.CopycatHalfLayerBlock;
import com.copycatsplus.copycats.content.copycat.stacked_half_layer.CopycatStackedHalfLayerBlock;
import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.compat.copycats.CopycatSealHelper;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.world.sealer.SealableBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@WhenModLoaded(ModCompat.COPYCATS)
@Mixin(CopycatStackedHalfLayerBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CopycatStackedHalfLayerBlockMixin implements SealableBlock {

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        if (!Block.isFaceFull(state.getShape(level, pos), direction))
            return false;

        SlabType testType = direction.getAxis() != Direction.Axis.Y ?
                SlabType.DOUBLE :
                direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE ?
                        SlabType.BOTTOM :
                        SlabType.TOP;

        return CopycatSealHelper.isSlabSealed(level, pos, testType, CopycatHalfLayerBlock.NEGATIVE_LAYERS.getName(), CopycatHalfLayerBlock.POSITIVE_LAYERS.getName());
    }

}

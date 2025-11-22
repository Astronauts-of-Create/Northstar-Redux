package com.lightning.northstar.mixin.compat.copycat;

import com.copycatsplus.copycats.content.copycat.slab.CopycatSlabBlock;
import com.lightning.northstar.compat.copycats.CopycatSealHelper;
import com.lightning.northstar.world.sealer.SealableBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(CopycatSlabBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CopycatSlabBlockMixin implements SealableBlock {

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        Axis axis = state.getValue(CopycatSlabBlock.AXIS);

        SlabType testType = axis != direction.getAxis() ?
                SlabType.DOUBLE :
                direction.getAxisDirection() == AxisDirection.NEGATIVE ? SlabType.BOTTOM : SlabType.TOP;

        return CopycatSealHelper.isSlabSealed(level, pos, testType, SlabType.BOTTOM.getSerializedName(), SlabType.TOP.getSerializedName());
    }

}

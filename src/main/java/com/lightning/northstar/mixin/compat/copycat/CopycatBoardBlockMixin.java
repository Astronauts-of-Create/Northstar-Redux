package com.lightning.northstar.mixin.compat.copycat;

import com.copycatsplus.copycats.content.copycat.board.CopycatBoardBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.MaterialItemStorage;
import com.lightning.northstar.compat.copycats.CopycatSealHelper;
import com.lightning.northstar.mixin.accessor.NorthstarContraptionWorld;
import com.lightning.northstar.world.sealer.SealableBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(CopycatBoardBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CopycatBoardBlockMixin implements SealableBlock {

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        BooleanProperty property = CopycatBoardBlock.byDirection(direction);

        if (!state.getValue(property))
            return false;

        if (level instanceof NorthstarContraptionWorld cw) {
            CompoundTag compound = CopycatSealHelper.getBlockNbt(cw, pos);
            if (compound == null)
                return true;
            return CopycatSealHelper.isMaterialSealed(CopycatSealHelper.getMultistateMaterial(compound, property));
        }

        if (level.getBlockEntity(pos) instanceof IMultiStateCopycatBlockEntity be) {
            MaterialItemStorage storage = be.getMaterialItemStorage();
            return CopycatSealHelper.isMaterialSealed(CopycatSealHelper.getMultistateMaterial(storage, property));
        }

        return false;
    }

}

package com.lightning.northstar.mixin.compat.copycat;

import com.copycatsplus.copycats.content.copycat.byte_panel.CopycatBytePanelBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.MaterialItemStorage;
import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.compat.copycats.CopycatSealHelper;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.mixin.accessor.NorthstarContraptionWorld;
import com.lightning.northstar.world.sealer.SealableBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import com.simibubi.create.AllBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.ParametersAreNonnullByDefault;

@WhenModLoaded(ModCompat.COPYCATS)
@Mixin(CopycatBytePanelBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CopycatBytePanelBlockMixin implements SealableBlock {

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        if (direction != state.getValue(CopycatBytePanelBlock.FACING) ||
                !state.getValue(CopycatBytePanelBlock.BOTTOM_LEFT) ||
                !state.getValue(CopycatBytePanelBlock.BOTTOM_RIGHT) ||
                !state.getValue(CopycatBytePanelBlock.TOP_LEFT) ||
                !state.getValue(CopycatBytePanelBlock.TOP_RIGHT))
            return false;

        if (level instanceof NorthstarContraptionWorld cw) {
            CompoundTag compound = CopycatSealHelper.getBlockNbt(cw, pos);
            if (compound == null)
                return true;
            return northstar$isMaterialSealed(CopycatSealHelper.getMultistateMaterial(compound, CopycatBytePanelBlock.BOTTOM_LEFT)) &&
                    northstar$isMaterialSealed(CopycatSealHelper.getMultistateMaterial(compound, CopycatBytePanelBlock.BOTTOM_RIGHT)) &&
                    northstar$isMaterialSealed(CopycatSealHelper.getMultistateMaterial(compound, CopycatBytePanelBlock.TOP_LEFT)) &&
                    northstar$isMaterialSealed(CopycatSealHelper.getMultistateMaterial(compound, CopycatBytePanelBlock.TOP_RIGHT));
        }

        if (level.getBlockEntity(pos) instanceof IMultiStateCopycatBlockEntity be) {
            MaterialItemStorage storage = be.getMaterialItemStorage();
            return northstar$isMaterialSealed(CopycatSealHelper.getMultistateMaterial(storage, CopycatBytePanelBlock.BOTTOM_LEFT)) &&
                    northstar$isMaterialSealed(CopycatSealHelper.getMultistateMaterial(storage, CopycatBytePanelBlock.BOTTOM_RIGHT)) &&
                    northstar$isMaterialSealed(CopycatSealHelper.getMultistateMaterial(storage, CopycatBytePanelBlock.TOP_LEFT)) &&
                    northstar$isMaterialSealed(CopycatSealHelper.getMultistateMaterial(storage, CopycatBytePanelBlock.TOP_RIGHT));
        }

        return false;
    }

    @Unique
    private static boolean northstar$isMaterialSealed(BlockState state) {
        // special case for bytes and byte panels, default material is filled
        return AllBlocks.COPYCAT_BASE.is(state.getBlock()) || !NorthstarBlockTags.AIR_PASSES_THROUGH.matches(state);
    }

}

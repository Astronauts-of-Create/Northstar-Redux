package com.lightning.northstar.mixin.compat.copycat;

import com.copycatsplus.copycats.content.copycat.bytes.CopycatByteBlock;
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
import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.ParametersAreNonnullByDefault;

@WhenModLoaded(ModCompat.COPYCATS)
@Mixin(CopycatByteBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CopycatByteBlockMixin implements SealableBlock {

    @Unique
    private static final BooleanProperty[] northstar$propertiesByDirection;

    static {
        northstar$propertiesByDirection = new BooleanProperty[6 * 4];

        int i = 0;
        for (Direction direction : Iterate.directions) {
            boolean positive = direction.getAxisDirection() == AxisDirection.POSITIVE;
            Axis axis = direction.getAxis();
            CopycatByteBlock.Byte bite = CopycatByteBlock.bite(axis == Axis.X && positive, axis == Axis.Y && positive, axis == Axis.Z && positive);

            for (int j = 0; j < 4; j++) {
                northstar$propertiesByDirection[i++] = CopycatByteBlock.byByte(bite);
                bite = switch (axis) {
                    case X -> bite.rotateX(Rotation.CLOCKWISE_90);
                    case Y -> bite.rotateY(Rotation.CLOCKWISE_90);
                    case Z -> bite.rotateZ(Rotation.CLOCKWISE_90);
                };
            }
        }
    }

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        int i = direction.ordinal() * 4;
        for (int j = 0; j < 4; j++) {
            if (!state.getValue(northstar$propertiesByDirection[i + j]))
                return false;
        }

        if (level instanceof NorthstarContraptionWorld cw) {
            CompoundTag compound = CopycatSealHelper.getBlockNbt(cw, pos);
            if (compound == null)
                return true;
            for (int j = 0; j < 4; j++) {
                if (northstar$isMaterialUnsealed(CopycatSealHelper.getMultistateMaterial(compound, northstar$propertiesByDirection[i + j])))
                    return false;
            }
            return true;
        }

        if (level.getBlockEntity(pos) instanceof IMultiStateCopycatBlockEntity be) {
            MaterialItemStorage storage = be.getMaterialItemStorage();
            for (int j = 0; j < 4; j++) {
                if (northstar$isMaterialUnsealed(CopycatSealHelper.getMultistateMaterial(storage, northstar$propertiesByDirection[i + j])))
                    return false;
            }
            return true;
        }

        return false;
    }

    @Unique
    private static boolean northstar$isMaterialUnsealed(BlockState state) {
        // special case for bytes and byte panels, default material is filled
        return !AllBlocks.COPYCAT_BASE.is(state.getBlock()) && NorthstarBlockTags.AIR_PASSES_THROUGH.matches(state);
    }

}

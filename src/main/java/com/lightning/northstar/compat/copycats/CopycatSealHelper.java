package com.lightning.northstar.compat.copycats;

import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.MaterialItemStorage;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.mixin.accessor.NorthstarContraptionWorld;
import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class CopycatSealHelper {

    public static boolean isMaterialSealed(BlockState state) {
        return !NorthstarBlockTags.AIR_PASSES_THROUGH.matches(state);
    }

    public static CompoundTag getBlockNbt(NorthstarContraptionWorld cw, BlockPos pos) {
        StructureTemplate.StructureBlockInfo block = cw.northstar$getContraption().getBlocks().get(pos);
        return block == null ? null : block.nbt();
    }

    public static BlockState getMultistateMaterial(CompoundTag tag, Property<?> property) {
        return getMultistateMaterial(tag, property.getName());
    }

    public static BlockState getMultistateMaterial(CompoundTag tag, String property) {
        if (tag == null)
            return AllBlocks.COPYCAT_BASE.getDefaultState();
        return NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("material_data")
                .getCompound(property)
                .getCompound("material"));
    }

    public static BlockState getMultistateMaterial(MaterialItemStorage storage, Property<?> property) {
        MaterialItemStorage.MaterialItem item = storage.getMaterialItem(property.getName());
        return item == null ? AllBlocks.COPYCAT_BASE.getDefaultState() : item.material();
    }

    public static BlockState getMultistateMaterial(MaterialItemStorage storage, String property) {
        MaterialItemStorage.MaterialItem item = storage.getMaterialItem(property);
        return item == null ? AllBlocks.COPYCAT_BASE.getDefaultState() : item.material();
    }

    public static boolean isSlabSealed(BlockGetter level, BlockPos pos, SlabType testType, String negativeProperty, String positiveProperty) {
        if (level instanceof NorthstarContraptionWorld cw) {
            CompoundTag compound = getBlockNbt(cw, pos);
            if (compound == null)
                return true;

            if (testType != SlabType.DOUBLE) {
                String property = testType == SlabType.BOTTOM ? negativeProperty : positiveProperty;
                return isMaterialSealed(getMultistateMaterial(compound, property));
            }

            return isMaterialSealed(getMultistateMaterial(compound, negativeProperty)) &&
                   isMaterialSealed(getMultistateMaterial(compound, positiveProperty));
        }

        if (level.getBlockEntity(pos) instanceof IMultiStateCopycatBlockEntity be) {
            MaterialItemStorage storage = be.getMaterialItemStorage();

            if (testType != SlabType.DOUBLE) {
                String property = testType == SlabType.BOTTOM ? negativeProperty : positiveProperty;
                return isMaterialSealed(getMultistateMaterial(storage, property));
            }

            return isMaterialSealed(getMultistateMaterial(storage, negativeProperty)) &&
                   isMaterialSealed(getMultistateMaterial(storage, positiveProperty));
        }

        return false;
    }

}

package com.lightning.northstar.mixin.compat.copycat;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.mixin.accessor.NorthstarContraptionWorld;
import com.lightning.northstar.world.sealer.SealableBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(CCCopycatBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CCCopycatBlockMixin implements SealableBlock {

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        BlockState material = null;
        if (level.getBlockEntity(pos) instanceof ICopycatBlockEntity be) {
            material = be.getMaterial();
        } else if (level instanceof NorthstarContraptionWorld cw) {
            StructureBlockInfo block = cw.northstar$getContraption().getBlocks().get(pos);
            if (block != null && block.nbt() != null)
                material = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), block.nbt().getCompound("Material"));
        }
        return material != null &&
                !NorthstarBlockTags.AIR_PASSES_THROUGH.matches(material) &&
                Block.isFaceFull(state.getShape(level, pos), direction);
    }

}

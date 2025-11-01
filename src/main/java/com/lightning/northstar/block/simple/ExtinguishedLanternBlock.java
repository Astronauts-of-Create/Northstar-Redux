package com.lightning.northstar.block.simple;

import com.lightning.northstar.content.NorthstarTags;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ExtinguishedLanternBlock extends LanternBlock {

    public ExtinguishedLanternBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.getAbilities().mayBuild ||
                !stack.is(NorthstarTags.NorthstarItemTags.IGNITION_SOURCE.tag) ||
                !NorthstarOxygen.hasOxygen(level, pos)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        level.setBlock(pos, Blocks.LANTERN.defaultBlockState()
                .setValue(HANGING, state.getValue(HANGING))
                .setValue(WATERLOGGED, state.getValue(WATERLOGGED)), UPDATE_ALL);
        level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

}

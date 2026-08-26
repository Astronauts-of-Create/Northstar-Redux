package com.lightning.northstar.block.simple;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarBlockStateProperties;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ExtinguishedLanternBlock extends LanternBlock implements SealReactiveBlock {

    public static final BooleanProperty OXYGEN_DEPRIVED = NorthstarBlockStateProperties.OXYGEN_DEPRIVED;

    public ExtinguishedLanternBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(OXYGEN_DEPRIVED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(OXYGEN_DEPRIVED));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.getAbilities().mayBuild ||
            !stack.is(NorthstarItemTags.IGNITION_SOURCE.tag) ||
            !NorthstarOxygen.hasOxygen(level, pos)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        relight(level, pos, state);
        level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode != SealingMode.OXYGEN) {
            return;
        }

        if (state.getValue(OXYGEN_DEPRIVED) &&
            NorthstarConfigs.server().relitExtinguishedBlocks.get() &&
            NorthstarOxygen.hasOxygen(level, pos)) {
            relight(level, pos, state);
        }
    }

    private void relight(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, Blocks.LANTERN.defaultBlockState()
                .setValue(HANGING, state.getValue(HANGING))
                .setValue(WATERLOGGED, state.getValue(WATERLOGGED)), UPDATE_ALL);
    }

}

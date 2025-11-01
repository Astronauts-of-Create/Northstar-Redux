package com.lightning.northstar.block.simple;

import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CustomIceBlock extends HalfTransparentBlock implements SealReactiveBlock {

    public final Fluid fluid;

    public CustomIceBlock(BlockBehaviour.Properties properties, Fluid fluid) {
        super(properties);
        this.fluid = fluid;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (EnchantmentHelper.getItemEnchantmentLevel(pLevel.holderOrThrow(Enchantments.SILK_TOUCH), tool) == 0) {
            BlockState blockState = level.getBlockState(pos.below());
            if (blockState.blocksMotion() || blockState.liquid()) {
                level.setBlockAndUpdate(pos, fluid.defaultFluidState().createLegacyBlock());
            }
        }
    }

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode != SealingMode.TEMPERATURE)
            return;

        float temperature = NorthstarTemperature.getTemperatureAt(level, pos);
        FluidState fluidstate = fluid.defaultFluidState();

        if (temperature >= NorthstarTemperature.getBoilingPoint(fluidstate)) {
            NorthstarTemperature.evaporate(level, pos);
        } else if (temperature >= NorthstarTemperature.getFreezingPoint(fluidstate)) {
            level.setBlockAndUpdate(pos, fluidstate.createLegacyBlock());
        }
    }

}

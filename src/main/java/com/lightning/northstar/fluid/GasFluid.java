package com.lightning.northstar.fluid;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GasFluid extends ForgeFlowingFluid {

    private final boolean source;

    public GasFluid(Properties properties, boolean source) {
        super(properties);
        this.source = source;
    }

    protected BlockState createLegacyBlock(FluidState state) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState state) {
        return source;
    }

    @Override
    public int getAmount(FluidState state) {
        return 0;
    }

}

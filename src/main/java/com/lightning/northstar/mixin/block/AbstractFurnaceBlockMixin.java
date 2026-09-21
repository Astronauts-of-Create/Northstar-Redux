package com.lightning.northstar.mixin.block;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.sealer.SealReactiveBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(AbstractFurnaceBlock.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class AbstractFurnaceBlockMixin implements SealReactiveBlock {

    @Override
    public void northstar$onSealUpdated(Level level, BlockPos pos, BlockState state, SealingMode mode) {
        if (mode == SealingMode.OXYGEN &&
            level.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity furnace &&
            !NorthstarOxygen.hasOxygen(level, pos)) {
            furnace.litTime = 0;

            state = state.setValue(AbstractFurnaceBlock.LIT, false);
            level.setBlock(pos, state, Block.UPDATE_ALL);
            furnace.setChanged();
        }
    }

}

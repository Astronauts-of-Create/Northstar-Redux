package com.lightning.northstar.block.tech.astronomy_table;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AstronomyTableBlockEntity extends BlockEntity implements MenuProvider  {

    public AstronomyTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return AstronomyTableMenu.create(id, inv, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Astronomy Table");
    }

}

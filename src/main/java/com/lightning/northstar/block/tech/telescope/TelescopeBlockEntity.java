package com.lightning.northstar.block.tech.telescope;

import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TelescopeBlockEntity extends SmartBlockEntity implements MenuProvider {

    public TelescopeBlockEntity(BlockEntityType<TelescopeBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return TelescopeMenu.create(id, inventory, this);
    }

    public void print(String name, ServerPlayer player) {
        boolean foundPaper = false;

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack item = inventory.items.get(i);
            if (item.is(Items.PAPER)) {
                item.setCount(item.getCount() - 1);
                foundPaper = true;
                break;
            }
        }

        if (!foundPaper && !player.isCreative()) {
            return;
        }

        player.level().playSound(player, getBlockPos(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);

        int x = (int) NorthstarPlanets.getPlanetX(name);
        int y = (int) NorthstarPlanets.getPlanetY(name);

        ItemStack reading = new ItemStack(NorthstarItems.ASTRONOMICAL_READING.get(), 1);

        reading.set(DataComponents.CUSTOM_NAME, Component.translatable("item.northstar.reading_" + name).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE).withItalic(false)));
        reading.set(DataComponents.LORE, new ItemLore(List.of(
                Component.literal("X: " + x).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false)),
                Component.literal("Y: " + y).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false))
        )));

        reading.set(NorthstarDataComponents.PLANET, name);
        reading.set(NorthstarDataComponents.PLANET_X, x);
        reading.set(NorthstarDataComponents.PLANET_Y, y);

        level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), reading));
    }

    @Override
    public Component getDisplayName() {
        return NorthstarBlocks.TELESCOPE.get().getName();
    }

}

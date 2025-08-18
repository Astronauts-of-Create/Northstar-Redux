package com.lightning.northstar.block.tech.telescope;

import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarMenuTypes;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class TelescopeBlockEntity extends SmartBlockEntity implements MenuProvider {

    public String SelectedPlanet = null;

    public TelescopeBlockEntity(BlockEntityType<TelescopeBlockEntity> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new TelescopeMenu(NorthstarMenuTypes.TELESCOPE_MENU.get(), id, inventory, this);
    }

    public void print(String name, ServerPlayer player) {
        SelectedPlanet = name;
        boolean flag = false;
        int paperslot = 0;
        Inventory inv = player.getInventory();
        if (name == null) {
            return;
        }
        for (int p = 0; p < 36; p++) {
            ItemStack items = inv.getItem(p);
            Item item = items.getItem();
            if (item == Items.PAPER) {
                flag = true;
                paperslot = p;
            }
        }
        System.out.println(flag);
        if (!flag) {
            return;
        }
        ItemStack paper = inv.getItem(paperslot);
        paper.setCount(paper.getCount() - 1);
        inv.player.level().playSound(null, inv.player.blockPosition(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);

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
        return Component.literal("Telescope");
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}

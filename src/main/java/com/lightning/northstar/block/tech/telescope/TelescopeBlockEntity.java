package com.lightning.northstar.block.tech.telescope;

import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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

        ItemStack reading = new ItemStack(NorthstarItems.ASTRONOMICAL_READING.get(), 1);

        int x = (int) NorthstarPlanets.getPlanetX(name);
        int y = (int) NorthstarPlanets.getPlanetY(name);
        reading.getOrCreateTagElement("Planet").putString("name", name);
        reading.getOrCreateTagElement("planetX").putInt("value", x);
        reading.getOrCreateTagElement("planetY").putInt("value", y);

        reading.setHoverName(Component.translatable("item.northstar.reading_" + name).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE).withItalic(false)));
        ListTag lore = new ListTag();
        lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("X: " + x).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false)))));
        lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Y: " + y).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false)))));
        reading.getOrCreateTagElement("display").put("Lore", lore);

        level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), reading));
    }

    @Override
    public Component getDisplayName() {
        return NorthstarBlocks.TELESCOPE.get().getName();
    }

}

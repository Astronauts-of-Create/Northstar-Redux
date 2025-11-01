package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.function.Consumer;

import static com.lightning.northstar.Northstar.REGISTRATE;

public class NorthstarCreativeModeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Northstar.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEMS = CREATIVE_TABS
            .register("items", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.northstar.items"))
                    .icon(() -> new ItemStack(NorthstarItems.MARTIAN_STEEL.get()))
                    .displayItems(createItemDisplay(NorthstarCreativeModeTab.ITEMS))
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCKS = CREATIVE_TABS
            .register("blocks", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.northstar.blocks"))
                    .icon(() -> new ItemStack(NorthstarBlocks.MARTIAN_STEEL_BLOCK.get()))
                    .displayItems(createItemDisplay(NorthstarCreativeModeTab.BLOCKS))
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TECH = CREATIVE_TABS
            .register("tech", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.northstar.tech"))
                    .icon(() -> new ItemStack(NorthstarBlocks.TELESCOPE.get()))
                    .displayItems(createItemDisplay(NorthstarCreativeModeTab.TECH))
                    .build());

    private static void registerItem(CreativeModeTab.Output event, String planet) {
        ItemStack stack = new ItemStack(NorthstarItems.STAR_MAP.get());
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.northstar.star_map_" + planet).setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA).withItalic(false)));
        stack.set(NorthstarDataComponents.PLANET, planet);
        event.accept(stack);
    }

    private static void registerSpaceSuit(CreativeModeTab.Output event, Item item) {
        ItemStack stack = new ItemStack(item);
        stack.set(NorthstarDataComponents.OXYGEN, NorthstarOxygen.MAXIMUM_OXYGEN);
        event.accept(stack);
    }

    private static CreativeModeTab.DisplayItemsGenerator createItemDisplay(DeferredHolder<CreativeModeTab, CreativeModeTab> tab) {
        return (parameters, output) -> {
            Map<Item, Consumer<CreativeModeTab.Output>> builders = Map.of(
                    NorthstarItems.STAR_MAP.get(), out -> {
                        registerItem(out, "earth");
                        registerItem(out, "moon");
                        registerItem(out, "mars");
                        registerItem(out, "mercury");
                        registerItem(out, "venus");
                    },
                    NorthstarItems.IRON_SPACE_SUIT_CHESTPIECE.get(), out -> registerSpaceSuit(out, NorthstarItems.IRON_SPACE_SUIT_CHESTPIECE.get()),
                    NorthstarItems.MARTIAN_STEEL_SPACE_SUIT_CHESTPIECE.get(), out -> registerSpaceSuit(out, NorthstarItems.MARTIAN_STEEL_SPACE_SUIT_CHESTPIECE.get())
            );

            for (RegistryEntry<Item, Item> item : REGISTRATE.getAll(Registries.ITEM)) {
                if (CreateRegistrate.isInCreativeTab(item, tab)) {
                    output.accept(item.get(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);

                    Consumer<CreativeModeTab.Output> factory = builders.get(item.get());
                    if (factory != null) {
                        factory.accept(output);
                    }
                }
            }
        };
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }

}

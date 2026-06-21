package com.lightning.northstar.block.tech.astronomy_table;

import com.lightning.northstar.block.tech.telescope.AstronomicalReadingData;
import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarMenuTypes;
import com.lightning.northstar.item.atlas.SpaceAtlasContent;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.PlanetTracker;
import com.lightning.northstar.util.NorthstarLang;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AstronomyTableMenu extends MenuBase<AstronomyTableBlockEntity> {

    public boolean hasError = false;
    public List<Component> messages = List.of();

    protected SimpleContainer inputSlots;
    protected ResultContainer resultSlots;

    public AstronomyTableMenu(MenuType<AstronomyTableMenu> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public AstronomyTableMenu(MenuType<?> type, int id, Inventory inv, AstronomyTableBlockEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    public static AstronomyTableMenu create(int id, Inventory inv, AstronomyTableBlockEntity be) {
        return new AstronomyTableMenu(NorthstarMenuTypes.ASTRONOMY_TABLE_MENU.get(), id, inv, be);
    }

    @Override
    @Nullable
    protected AstronomyTableBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        return Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos()) instanceof AstronomyTableBlockEntity be ? be : null;
    }

    @Override
    protected void initAndReadInventory(AstronomyTableBlockEntity contentHolder) {
        inputSlots = new SimpleContainer(2);
        resultSlots = new ResultContainer();

        inputSlots.addListener(container -> updateResult());
    }

    @Override
    protected void addSlots() {
        class FilteredSlot extends Slot {
            private final ItemLike allowed;

            public FilteredSlot(Container container, int slot, int x, int y, ItemLike allowed) {
                super(container, slot, x, y);
                this.allowed = allowed;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(allowed.asItem());
            }
        }
        class ResultSlot extends Slot {
            public ResultSlot(Container container, int slot, int x, int y) {
                super(container, slot, x, y);
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                for (int i = 0; i < 2; i++) {
                    inputSlots.getItem(i).shrink(1);
                }
                player.level().playSound(player, contentHolder.getBlockPos(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                updateResult();
            }
        }

        addSlot(new FilteredSlot(inputSlots, 0, 26, 56, NorthstarItems.SPACE_ATLAS));
        addSlot(new FilteredSlot(inputSlots, 1, 80, 56, NorthstarItems.ASTRONOMICAL_READING));
        addSlot(new ResultSlot(resultSlots, 0, 134, 56));

        addPlayerSlots(8, 84);
    }

    @Override
    protected void saveData(AstronomyTableBlockEntity contentHolder) {
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        clearContainer(player, inputSlots);
        resultSlots.clearContent();
    }

    public void updateResult() {
        hasError = true;
        messages = List.of();
        resultSlots.clearContent();

        ItemStack atlasItem = inputSlots.getItem(0);
        ItemStack readingItem = inputSlots.getItem(1);

        boolean hasAtlas = NorthstarItems.SPACE_ATLAS.isIn(atlasItem);
        boolean hasReading = readingItem.has(NorthstarDataComponents.ASTRONOMICAL_READING_DATA);
        if (!hasAtlas || !hasReading) {
            List<Component> messages = new ArrayList<>();
            if (!hasAtlas)
                messages.add(Component.translatable("northstar.gui.astronomy_table.missing_atlas").withStyle(ChatFormatting.RED));
            if (!hasReading)
                messages.add(Component.translatable("northstar.gui.astronomy_table.missing_reading").withStyle(ChatFormatting.RED));
            this.messages = messages;
            return;
        }

        PlanetTracker tracker = contentHolder.getLevel().northstar$getPlanetTracker();
        AstronomicalReadingData readingData = readingItem.get(NorthstarDataComponents.ASTRONOMICAL_READING_DATA);
        Planet originPlanet = tracker.getPlanetById(readingData.originPlanet());
        Planet targetPlanet = tracker.getPlanetById(readingData.targetPlanet());

        if (originPlanet == null || targetPlanet == null) {
            messages = List.of(
                    Component.translatable("northstar.gui.astronomy_table.invalid_reading").withStyle(ChatFormatting.RED)
            );
            return;
        }

        SpaceAtlasContent atlas = atlasItem.getOrDefault(NorthstarDataComponents.SPACE_ATLAS_CONTENT, SpaceAtlasContent.EMPTY);
        SpaceAtlasContent.Planet planet = atlas.planets().get(targetPlanet.key.location());

        if (planet != null && planet.readings().stream().anyMatch(r -> r.origin().equals(originPlanet.key.location()) && r.day() == readingData.day())) {
            messages = List.of(
                    Component.translatable("northstar.gui.astronomy_table.duplicate_reading").withStyle(ChatFormatting.RED),
                    Component.translatable("northstar.gui.astronomy_table.duplicate_reading.tip")
            );
            return;
        }

        SpaceAtlasContent.Planet updatedPlanet = (planet == null ?
                SpaceAtlasContent.Planet.builder().planetId(targetPlanet.key.location()) :
                planet.toBuilder())
                .addReading(new SpaceAtlasContent.AtlasReading(originPlanet.key.location(), readingData.science(), readingData.day()))
                .calculateScience(targetPlanet.properties.scienceWeightExp())
                .build();

        SpaceAtlasContent updatedAtlas = atlas.asBuilder()
                .addPlanet(updatedPlanet)
                .build();

        ItemStack result = atlasItem.copy();
        result.set(NorthstarDataComponents.SPACE_ATLAS_CONTENT, updatedAtlas);
        resultSlots.setItem(0, result);

        float oldScience = planet == null ? 0 : planet.science();
        float newScience = updatedPlanet.science();
        float addedScience = newScience - oldScience;

        List<Component> message = new ArrayList<>();

        Component lowScienceTip = Component.literal(" (?)")
                .withStyle(ChatFormatting.GRAY)
                .northstar$onHover(HoverEvent.Action.SHOW_TEXT, Component.translatable("northstar.gui.astronomy_table.low_science_tip"));

        message.add(Component.translatable("northstar.gui.astronomy_table.planet", targetPlanet.getName().withStyle(ChatFormatting.AQUA)));
        message.add(Component.translatable("northstar.gui.astronomy_table.added_science", NorthstarLang.number(addedScience)
                .style(ChatFormatting.AQUA)
                .add(addedScience <= 0.8 ? lowScienceTip : Component.empty())
                .component()));

        if (targetPlanet.properties.requiredScience() >= 0) {
            message.add(Component.translatable("northstar.gui.astronomy_table.total_science", NorthstarLang.builder()
                    .add(NorthstarLang.number(newScience).style(ChatFormatting.AQUA))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(NorthstarLang.number(targetPlanet.properties.requiredScience()).style(ChatFormatting.AQUA))
                    .component()
            ));

            if (newScience >= targetPlanet.properties.requiredScience()) {
                message.add(Component.translatable("northstar.gui.astronomy_table.planet_unlocked").withStyle(ChatFormatting.GREEN));
            }
        } else {
            message.add(Component.translatable("northstar.gui.astronomy_table.total_science", NorthstarLang.numberDirect(newScience).withStyle(ChatFormatting.AQUA)));
        }

        hasError = false;
        messages = message;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack movedItem = slot.getItem();

        // from container to inventory
        if (index <= 2) {
            moveItemStackTo(movedItem, 4, slots.size(), true);
            slot.onTake(player, movedItem);
            return ItemStack.EMPTY;
        }

        // from inventory to container
        moveItemStackTo(movedItem, 0, 3, false);
        return ItemStack.EMPTY;
    }

}

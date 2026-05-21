package com.lightning.northstar.item.atlas;

import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.lightning.northstar.network.packet.SpaceAtlasEditPacket;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpaceAtlasScreen extends AbstractSimiContainerScreen<SpaceAtlasMenu> {

    private List<EditableDestination> destinations = new ArrayList<>();
    private List<RocketDestination> removed = new ArrayList<>();
    private int page;

    public SpaceAtlasScreen(SpaceAtlasMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        setWindowSize(294, 190);

        SpaceAtlasContent content = SpaceAtlasContent.fromTag(menu.contentHolder.getOrCreateTag());

        if (content.destinations.isEmpty()) {
            Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("northstar.item.space_atlas.empty").withStyle(ChatFormatting.RED));
            onClose();
            return;
        }

        for (Map.Entry<RocketDestination, Component> entry : content.destinations.entrySet()) {
            destinations.add(new EditableDestination(entry.getKey(), entry.getValue().getString()));
        }

        updatePage(0);
    }

    private void updatePage(int delta) {
        page = Mth.clamp(page + delta, 0, destinations.size() / 14);

        clearWidgets();
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> saveAndClose()).bounds(width / 2 - 100, 196, 200, 20).build());

        addRenderableWidget(new PageButton(leftPos + 116, 159, true, button -> updatePage(1), true));
        addRenderableWidget(new PageButton(leftPos + 23, 159, false, button -> updatePage(-1), true));

        for (int i = page * 14, j = Math.min(destinations.size(), i + 14); i < j; i++) {
            int x = leftPos + 26;
            int y = 17 + (i - page * 14) * 11;

            EditableDestination dest = destinations.get(i);
            PlanetDimension dimension = NorthstarLevel.CLIENT_TRACKER.getDimensionByLevel(dest.destination.dimKey());
            Planet planet = dimension == null ? null : NorthstarLevel.CLIENT_TRACKER.getPlanetById(dimension.planet());

            Component tooltip = Component.translatable(
                    "northstar.gui.space_atlas.waypoint_tooltip",
                    SpaceAtlasContent.getDefaultLabel(dest.destination.pos(), dest.destination.dir()),
                    planet == null ? Component.translatable("northstar.gui.space_atlas.unknown_dimension") : planet.getDimensionName(dimension)
            );

            EditBox editBox = new EditBox(new NoShadowFontWrapper(font), x, y, 104, 9, Component.literal(dest.text));
            editBox.setValue(dest.text);
            editBox.setTooltip(Tooltip.create(tooltip));
            editBox.setTextColor(0);
            editBox.setBordered(false);
            editBox.setMaxLength(256);
            editBox.setResponder(value -> {
                dest.text = value;
                dest.edited = true;
            });
            addRenderableWidget(editBox);

            Button remove = Button.builder(Component.literal("X"), b -> {
                destinations.remove(dest);
                removed.add(dest.destination);
                updatePage(0);
            }).bounds(x + 104 + 2, y, 10, 10).build();
            addRenderableWidget(remove);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BookViewScreen.BOOK_LOCATION, (width - 192) / 2, 2, 0, 0, 192, 192);
    }

    private void saveAndClose() {
        Map<RocketDestination, String> edited = destinations
                .stream()
                .filter(dest -> dest.edited)
                .collect(Collectors.toMap(d -> d.destination, d -> d.text));

        if (!edited.isEmpty() || !removed.isEmpty()) {
            NorthstarPackets.getChannel().sendToServer(new SpaceAtlasEditPacket(edited, removed));
        }

        onClose();
    }

    private static class EditableDestination {
        private RocketDestination destination;
        private String text;
        private boolean edited;

        public EditableDestination(RocketDestination destination, String text) {
            this.destination = destination;
            this.text = text;
            this.edited = false;
        }
    }

}

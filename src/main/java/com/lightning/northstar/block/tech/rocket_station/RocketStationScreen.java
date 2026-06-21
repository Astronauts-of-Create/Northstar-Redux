package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.block.tech.rocket_station.RocketStationMenu.DimensionEntry;
import com.lightning.northstar.client.gui.ListScrollInput;
import com.lightning.northstar.client.gui.ScrollingLabel;
import com.lightning.northstar.compat.jei.NorthstarJEI;
import com.lightning.northstar.compat.jei.category.FuelTypeCategory;
import com.lightning.northstar.compat.jei.category.HeatShieldingCategory;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.contraption.FuelType;
import com.lightning.northstar.contraption.rocket.*;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.data.PlanetProperties;
import com.lightning.northstar.util.NorthstarLang;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.lang.LangNumberFormat;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@OnlyIn(Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RocketStationScreen extends AbstractSimiContainerScreen<RocketStationMenu> {

    public static final ResourceLocation TEXTURE = Northstar.asResource("textures/gui/rocket_station.png");

    private IconButton button1;
    private IconButton button2;
    private ListScrollInput<DimensionEntry> dimensionSelection;
    private ListScrollInput<Pair<RocketDestination, Component>> destinationSelection;
    private ContainerListener slotChangeListener;
    private List<Component> messages;
    private int ticks;

    public RocketStationScreen(RocketStationMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);

        setWindowSize(176, 210);
    }

    @Override
    protected void init() {
        super.init();

        RocketStationHolder holder = menu.contentHolder;
        if (holder == null) {
            onClose();
            return;
        }

        holder.contraption().getStorage().initialize();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        Label dimensionLabel = new ScrollingLabel(x + 30, y + 11, 110, 10, Component.empty())
                .withShadow();

        dimensionSelection = new ListScrollInput<>(x + 30, y + 8, 115, 18);
        dimensionSelection.formatter(DimensionEntry::text)
                .calling(i -> {
                    destinationSelection.options(dimensionSelection.get().destinations());
                    updateInfo();
                })
                .writingTo(dimensionLabel);
        addRenderableWidget(dimensionSelection);
        addRenderableWidget(dimensionLabel);

        Label destionationLabel = new ScrollingLabel(x + 30, y + 30, 110, 10, Component.empty())
                .withShadow();

        destinationSelection = new ListScrollInput<>(x + 30, y + 27, 115, 18);
        destinationSelection.formatter(Pair::second)
                .writingTo(destionationLabel);
        addRenderableWidget(destinationSelection);
        addRenderableWidget(destionationLabel);

        button1 = new IconButton(x + 151, y + 7, AllIcons.I_NONE);
        addRenderableWidget(button1);

        button2 = new IconButton(x + 151, y + 7 + 18 + 1, AllIcons.I_NONE);
        addRenderableWidget(button2);

        if (holder.entity() == null) {
            button1.setIcon(AllIcons.I_CONFIRM);
            button1.setToolTip(Component.translatable("northstar.gui.rocket_station.assemble"));
        } else {
            button1.setIcon(AllIcons.I_DISABLE);
            if (holder.entity().isOutOfWorld()) {
                button1.active = false;
                button1.setToolTip(Component.translatable("northstar.gui.rocket_station.cannot_disassemble"));
            } else {
                button1.setToolTip(Component.translatable("northstar.gui.rocket_station.disassemble"));
            }
        }
        button1.withCallback(() -> {
            if (button1.active) {
                saveSettings(true);
                removed();
                onClose();
            }
        });

        button2.setIcon(AllIcons.I_CONFIG_SAVE);
        button2.setToolTip(Component.translatable("northstar.gui.rocket_station.save"));
        button2.withCallback(() -> {
            saveSettings(false);
            removed();
            onClose();
        });

        slotChangeListener = container -> {
            List<DimensionEntry> options = new ArrayList<>(RocketStationMenu.getPossibleDestinations(NorthstarLevel.CLIENT_TRACKER, container.getItem(0)));
            options.sort(Comparator.comparing(dim -> dim.text().getString()));

            RocketDestination destination = holder.be() != null ? holder.be().destination : holder.contraption().destination;

            if (options.isEmpty()) {
                dimensionSelection.options(List.of(new DimensionEntry(null, null, List.of(), Component.empty())));
                dimensionSelection.onChanged();
                destinationSelection.onChanged();
            } else {
                DimensionEntry dim = options.stream()
                        .filter(entry -> destination != null && Objects.equals(entry.dimensionId(), destination.dim()))
                        .findFirst()
                        .orElse(null);

                dimensionSelection.options(options);
                dimensionSelection.setState(dim);
                dimensionSelection.onChanged();
                if (dim != null) {
                    Pair<RocketDestination, Component> pos = dim.destinations()
                            .stream()
                            .filter(pair -> Objects.equals(pair.first(), destination))
                            .findFirst()
                            .orElse(null);
                    destinationSelection.setState(pos);
                }
                destinationSelection.onChanged();
            }

            dimensionSelection.active = !options.isEmpty();
            destinationSelection.active = !options.isEmpty();
        };
        holder.container().addListener(slotChangeListener);

        // initially update the container to display everything properly
        slotChangeListener.containerChanged(holder.container());
    }

    @Override
    public void onClose() {
        super.onClose();

        menu.contentHolder.container().removeListener(slotChangeListener);
    }

    private void saveSettings(boolean toggleAssembly) {
        CatnipServices.NETWORK.sendToServer(new RocketStationEditPacket(
                menu.contentHolder.pos(),
                menu.contentHolder.entity() == null ? -1 : menu.contentHolder.entity().getId(),
                toggleAssembly,
                destinationSelection.get() == null ? null : destinationSelection.get().first()
        ));
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (ticks++ >= 20) {
            ticks = 0;
            updateInfo();
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        GuiGameElement.of(NorthstarBlocks.ROCKET_WAYPOINT)
                .at(x + 8, y + 27)
                .render(graphics);

        if (messages == null) {
            updateInfo();
        }

        int labelY = y + 48;
        for (Component line : messages) {
            graphics.drawString(font, line, x + 8, labelY, 0xFFFFFF, true);
            labelY += 12;
        }

        RocketContraptionEntity rocket = menu.contentHolder.entity();
        if (rocket != null) {
            boolean active = rocket.getStatus() == LaunchStatus.WAITING;
            button1.setActive(active);
            button2.setActive(active);
        }
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);

        graphics.renderComponentHoverEffect(font, getComponentStyleAt(mouseX, mouseY), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && handleComponentClicked(getComponentStyleAt((int) mouseX, (int) mouseY))) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style style) {
        if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.CHANGE_PAGE) {
            ModCompat.JEI.executeIfLoaded(() -> () -> {
                switch (style.getClickEvent().getValue()) {
                    case "heat_shielding" ->
                            NorthstarJEI.getRuntime().getRecipesGui().showTypes(List.of(HeatShieldingCategory.RECIPE_TYPE));
                    case "fuel" ->
                            NorthstarJEI.getRuntime().getRecipesGui().showTypes(List.of(FuelTypeCategory.RECIPE_TYPE));
                }
            });
            return true;
        }

        return super.handleComponentClicked(style);
    }

    @Nullable
    private Style getComponentStyleAt(int mouseX, int mouseY) {
        int x = mouseX - leftPos - 8;
        int y = (mouseY - topPos - 48) / 12;
        return mouseY <= topPos + 46 || y < 0 || y >= messages.size() || x < 0 ? null : font.getSplitter().componentStyleAtWidth(messages.get(y), x);
    }

    private void updateInfo() {
        ClientLevel level = Minecraft.getInstance().level;
        RocketContraption contraption = menu.contentHolder.contraption();
        RocketContraptionEntity rocket = menu.contentHolder.entity();
        DimensionEntry target = dimensionSelection.get();

        // Fuel: <amount> / <required>
        // Computers: <amount> / <maximum> (-N% fuel)
        // Engines: <count> / <required>
        // Shielding: <amount> / <required>
        // Navigator: Available / Optional / Required
        // Status: Disassembled / Landed / Launching / Ascending / Descending

        Component infinite = Component.translatable("northstar.gui.rocket_station.infinite")
                .withStyle(ChatFormatting.LIGHT_PURPLE);

        BiFunction<Float, Float, Component> valueFormat = (value, required) -> NorthstarLang.numberDirect(value)
                .withStyle(Float.isInfinite(required) ? ChatFormatting.GOLD : value >= required ? ChatFormatting.GREEN : ChatFormatting.RED);
        Float2ObjectFunction<Component> requirementFormat = value -> Float.isInfinite(value) ?
                Component.literal("?").withStyle(ChatFormatting.GOLD) :
                NorthstarLang.numberDirect(value).withStyle(ChatFormatting.AQUA);


        Planet targetPlanet = target != null && target.dimension() != null ?
                NorthstarLevel.CLIENT_TRACKER.getPlanetById(target.dimension().planet()) :
                null;
        FuelCost requiredFuel = targetPlanet != null ? contraption.calculateRequiredFuel(
                level.northstar$planet(), level.northstar$dimension(),
                targetPlanet, target.dimension()
        ) : null;

        MutableComponent fuelTip = Component.empty()
                .append(Component.translatable("northstar.gui.rocket.station.fuel.tip.info"))
                .append("\n\n");

        if (requiredFuel != null) {
            fuelTip = fuelTip
                    .append(Component.translatable(
                            "northstar.gui.rocket_station.fuel.tip.takeoff",
                            NorthstarLang.numberDirect((int) requiredFuel.takeoff()).withStyle(ChatFormatting.AQUA)
                    ))
                    .append("\n")
                    .append(Component.translatable(
                            "northstar.gui.rocket_station.fuel.tip.travel",
                            NorthstarLang.numberDirect((int) requiredFuel.travel()).withStyle(ChatFormatting.AQUA)
                    ))
                    .append("\n")
                    .append(Component.translatable(
                            "northstar.gui.rocket_station.fuel.tip.landing",
                            NorthstarLang.numberDirect((int) requiredFuel.landing()).withStyle(ChatFormatting.AQUA)
                    ))
                    .append("\n\n");
        }

        float availableFuel = contraption.calculateAvailableFuel();
        if (availableFuel == 0) {
            fuelTip = fuelTip.append(Component.translatable("northstar.gui.rocket_station.fuel.tip.no_fuel"));
        } else {
            Object2IntMap<Fluid> amounts = new Object2IntOpenHashMap<>();

            IFluidHandler fluids = contraption.getStorage().getFluids();
            for (int i = 0, j = fluids.getTanks(); i < j; i++) {
                FluidStack stack = fluids.getFluidInTank(i);
                FuelType fuel = FuelType.getFuelType(stack.getFluid());
                if (fuel != null && fuel.gjPerMb() != 0) {
                    amounts.computeInt(stack.getFluid(), (f, a) -> a == null ? stack.getAmount() : a + stack.getAmount());
                }
            }

            fuelTip = fuelTip.append(Component.translatable("northstar.gui.rocket_station.fuel.tip.stored"));

            for (Object2IntMap.Entry<Fluid> entry : amounts.object2IntEntrySet()) {
                float energy = FuelType.getFuelType(entry.getKey()).gjPerMb() * entry.getIntValue();

                fuelTip = fuelTip
                        .append("\n")
                        .append(new FluidStack(entry.getKey(), 1).getHoverName())
                        .append(" ")
                        .append(NorthstarLang.numberDirect(entry.getIntValue())
                                .append(NorthstarLang.MB.component())
                                .withStyle(ChatFormatting.AQUA))
                        .append(" -> ")
                        .append(NorthstarLang.numberDirect(energy)
                                .append(NorthstarLang.GJ.component())
                                .withStyle(ChatFormatting.AQUA));
            }
        }

        Component fuel = Component.translatable(
                        "northstar.gui.rocket_station.fuel",
                        Component.empty()
                                .append(contraption.infiniteFuel ? infinite : valueFormat.apply(availableFuel, requiredFuel == null ? Float.POSITIVE_INFINITY : requiredFuel.total()))
                                .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                                .append(requirementFormat.apply(requiredFuel == null ? Float.POSITIVE_INFINITY : (int) requiredFuel.total()))
                ).northstar$onHover(HoverEvent.Action.SHOW_TEXT, fuelTip)
                .northstar$onClick(ClickEvent.Action.CHANGE_PAGE, "fuel");

        Component computers = Component.translatable(
                "northstar.gui.rocket_station.targeting_computers",
                Component.empty()
                        .append(NorthstarLang.numberDirect(contraption.computerCount).withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                        .append(NorthstarLang.numberDirect(NorthstarConfigs.server().targetingComputersNeeded.get()).withStyle(ChatFormatting.AQUA))
                        .append(" ")
                        .append(Component.translatable("northstar.gui.rocket_station.fuel_reduction", LangNumberFormat.format(Mth.floor(contraption.getTargetingComputerReduction() * 100))))
        ).northstar$onHover(HoverEvent.Action.SHOW_TEXT, Component.translatable("northstar.gui.rocket_station.targeting_computers.tip"));

        float requiredHeatShielding = target != null && target.dimension() != null ?
                contraption.calculateRequiredHeatShielding(level.northstar$dimension(), target.dimension()) :
                Float.POSITIVE_INFINITY;
        Component heatShielding = Component.translatable(
                        "northstar.gui.rocket_station.heat_shielding",
                        Component.empty()
                                .append(Float.isInfinite(contraption.heatShielding) ? infinite : valueFormat.apply((float) Mth.floor(contraption.heatShielding), requiredHeatShielding))
                                .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                                .append(requirementFormat.apply(Math.ceil(requiredHeatShielding)))
                ).northstar$onHover(HoverEvent.Action.SHOW_TEXT, Component.translatable("northstar.gui.rocket_station.heat_shielding.tip"))
                .northstar$onClick(ClickEvent.Action.CHANGE_PAGE, "heat_shielding");

        float requiredThrusters = target != null && target.dimension() != null ?
                contraption.calculateRequiredThrusters(Math.max(level.northstar$gravity(), target.dimension().gravity())) :
                Float.POSITIVE_INFINITY;
        Component thrusters = Component.translatable(
                "northstar.gui.rocket_station.thrusters",
                Component.empty()
                        .append(contraption.thrusterCount == RocketContraption.INFINITE_THRUSTERS ? infinite : valueFormat.apply((float) contraption.thrusterCount, requiredThrusters))
                        .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                        .append(requirementFormat.apply(requiredThrusters))
        ).northstar$onHover(HoverEvent.Action.SHOW_TEXT, Component.translatable("northstar.gui.rocket_station.thrusters.tip"));

        Planet currentPlanet = level.northstar$planet();
        Component navigatorStatus;
        if (contraption.hasInterplanetaryNavigator) {
            navigatorStatus = Component.translatable("northstar.gui.rocket_station.navigator.available").withStyle(ChatFormatting.GREEN);
        } else if (currentPlanet != null && target != null && target.planet() != null && PlanetProperties.isInterplanetary(currentPlanet, target.planet())) {
            navigatorStatus = Component.translatable("northstar.gui.rocket_station.navigator.required").withStyle(ChatFormatting.RED);
        } else {
            navigatorStatus = Component.translatable("northstar.gui.rocket_station.navigator.optional").withStyle(ChatFormatting.YELLOW);
        }
        Component navigator = Component.translatable("northstar.gui.rocket_station.navigator", navigatorStatus);

        MutableComponent baseStatus;
        if (rocket != null) {
            baseStatus = switch (rocket.getStatus()) {
                case WAITING -> Component.translatable("northstar.gui.rocket_station.status.landed");
                case COUNTDOWN -> Component.translatable("northstar.gui.rocket_station.status.launching");
                case ASCENDING -> Component.translatable("northstar.gui.rocket_station.status.ascending");
                case DESCENDING -> Component.translatable("northstar.gui.rocket_station.status.descending");
            };
        } else {
            baseStatus = Component.translatable("northstar.gui.rocket_station.status.disassembled");
        }
        Component status = Component.translatable("northstar.gui.rocket_station.status", baseStatus.withStyle(ChatFormatting.GREEN));

        messages = List.of(fuel, computers, heatShielding, thrusters, navigator, status);
    }

}

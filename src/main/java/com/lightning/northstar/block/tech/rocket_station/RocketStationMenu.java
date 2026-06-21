package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarMenuTypes;
import com.lightning.northstar.contraption.rocket.LaunchStatus;
import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.lightning.northstar.item.atlas.SpaceAtlasContent;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.PlanetTracker;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RocketStationMenu extends MenuBase<RocketStationHolder> {

    @Contract("_, _, _, _, null, null -> fail")
    public static void open(ServerPlayer player, SimpleContainer container, BlockPos pos, RocketContraption contraption,
                            @Nullable RocketStationBlockEntity be, @Nullable RocketContraptionEntity entity) {
        if (be == null && entity == null) {
            throw new IllegalArgumentException("Either a rocket station or rocket contraption is required");
        }
        MenuProvider provider = new SimpleMenuProvider((id, inventory, p) -> new RocketStationMenu(
                NorthstarMenuTypes.ROCKET_STATION.get(),
                id,
                inventory,
                new RocketStationHolder(container, pos, contraption, be, entity)
        ), Component.translatable("block.northstar.rocket_station"));

        player.openMenu(provider, buffer -> {
            buffer.writeBlockPos(pos);
            buffer.writeInt(entity == null ? -1 : entity.getId());
        });
    }

    private boolean isClient;

    public RocketStationMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public RocketStationMenu(MenuType<?> type, int id, Inventory inv, RocketStationHolder holder) {
        super(type, id, inv, holder);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    @Override
    protected RocketStationHolder createOnClient(RegistryFriendlyByteBuf extraData) {
        isClient = true;

        ClientLevel level = Minecraft.getInstance().level;

        BlockPos pos = extraData.readBlockPos();
        int entityId = extraData.readInt();

        if (entityId == -1) {
            if (!(level.getBlockEntity(pos) instanceof RocketStationBlockEntity station)) {
                return null;
            }
            RocketContraption contraption = station.assembleContraption();
            if (contraption == null) {
                return null;
            }
            return new RocketStationHolder(station.container, pos, contraption, station, null);
        }

        if (!(level.getEntity(entityId) instanceof RocketContraptionEntity rocket)) {
            return null;
        }
        MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> pair = rocket.getContraption().getActorAt(pos);
        if (pair == null) {
            return null;
        }
        return new RocketStationHolder(RocketStationActor.get(pair.right).container, pos, rocket.getContraption(), null, rocket);
    }

    @Override
    protected void initAndReadInventory(RocketStationHolder holder) {
    }

    @Override
    protected void addSlots() {
        addSlot(new Slot(contentHolder.container(), 0, 8, 8) {
            private boolean mayInteract() {
                return contentHolder.entity() == null || contentHolder.entity().getStatus() == LaunchStatus.WAITING;
            }

            @Override
            public boolean mayPickup(Player player) {
                return mayInteract() && super.mayPickup(player);
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return mayInteract() && (NorthstarItems.SPACE_ATLAS.isIn(stack) || NorthstarItems.RETURN_TICKET.isIn(stack)) && super.mayPlace(stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                PlanetTracker planets = isClient ? NorthstarLevel.CLIENT_TRACKER : NorthstarLevel.SERVER_TRACKER;
                if (contentHolder.be() != null) {
                    if (!validateDestination(planets, container.getItem(0), contentHolder.be().destination)) {
                        contentHolder.be().destination = null;
                    }
                } else {
                    if (!validateDestination(planets, container.getItem(0), contentHolder.contraption().destination)) {
                        contentHolder.contraption().destination = null;
                    }
                }
            }
        });

        addPlayerSlots(8, 128);
    }

    @Override
    protected void saveData(RocketStationHolder holder) {
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack movedItem = slot.getItem();

        // from container to inventory
        if (index == 0) {
            moveItemStackTo(movedItem, 1, slots.size(), true);
            slot.onTake(player, movedItem);
            return ItemStack.EMPTY;
        }

        // from inventory to container
        moveItemStackTo(movedItem, 0, 1, false);
        return ItemStack.EMPTY;
    }

    public static List<DimensionEntry> getPossibleDestinations(PlanetTracker planets, ItemStack item) {
        if (item.has(NorthstarDataComponents.SPACE_ATLAS_CONTENT)) {
            List<DimensionEntry> dimensions = new ArrayList<>();
            SpaceAtlasContent atlas = item.getOrDefault(NorthstarDataComponents.SPACE_ATLAS_CONTENT, SpaceAtlasContent.EMPTY);

            for (var entry : atlas.planets().entrySet()) {
                Planet planet = planets.getPlanetById(entry.getKey());
                if (planet == null || entry.getValue().science() < planet.properties.requiredScience()) {
                    continue;
                }
                for (PlanetDimension dimension : planet.dimensions) {
                    MutableComponent text = planet.getDimensionName(dimension);
                    ResourceLocation dimensionId = dimension.dimensionId().location();

                    List<Pair<RocketDestination, Component>> destinations = new ArrayList<>();
                    destinations.add(Pair.of(new RocketDestination(dimensionId, null, null), Component.translatable("northstar.gui.rocket_station.hold_position")));
                    atlas.destinations()
                            .entrySet()
                            .stream()
                            .filter(e -> e.getKey().dim().equals(dimensionId))
                            .forEach(e -> destinations.add(Pair.of(e.getKey(), e.getValue())));

                    dimensions.add(new DimensionEntry(planet, dimension, destinations, text));
                }
            }

            return dimensions;
        }

        RocketDestination destination;
        PlanetDimension dimension;
        Planet planet;
        if ((destination = item.get(NorthstarDataComponents.RETURN_DESTINATION)) != null &&
            (dimension = planets.getDimensionByLevel(destination.dim())) != null &&
            (planet = planets.getPlanetById(dimension.planet())) != null) {
            return List.of(
                    new DimensionEntry(
                            planet,
                            dimension,
                            List.of(
                                    Pair.of(destination, Component.translatable("northstar.gui.rocket_station.takeoff_origin")),
                                    Pair.of(new RocketDestination(destination.dim(), null, null), Component.translatable("northstar.gui.rocket_station.hold_position"))
                            ),
                            planet.getDimensionName(dimension)
                    )
            );
        }

        return List.of();
    }

    public static boolean validateDestination(PlanetTracker planets, ItemStack item, @Nullable RocketDestination destination) {
        return destination == null ||
               getPossibleDestinations(planets, item)
                       .stream()
                       .anyMatch(entry -> entry.destinations()
                               .stream()
                               .anyMatch(pair -> destination.equals(pair.first())));
    }

    public record DimensionEntry(
            @Nullable Planet planet,
            @Nullable PlanetDimension dimension,
            List<Pair<RocketDestination, Component>> destinations,
            Component text
    ) {
        @Nullable
        public ResourceLocation dimensionId() {
            return dimension == null ? null : dimension.dimensionId().location();
        }
    }

}

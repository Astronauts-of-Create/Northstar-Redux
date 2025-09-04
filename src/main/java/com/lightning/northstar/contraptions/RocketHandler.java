package com.lightning.northstar.contraptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.rocket_station.RocketStationBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Northstar.MOD_ID, bus = Bus.FORGE)
public class RocketHandler {

    public static final List<RocketContraptionEntity> ROCKETS = new ArrayList<>();
    public static List<Pair<Level, BlockPos>> TICKET_QUEUE = new ArrayList<>();
    public static HashMap<Pair<UUID, BlockPos>, Integer> CONTROL_QUEUE = new HashMap<>();
    public static long eventTickNumber;
    public static long eventTickNumberCheck;
    private static UUID pilotID;
    static int pp = 0;
    public static final int DIMENSION_CHANGE_HEIGHT = 1750;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        eventTickNumber = event.level.getGameTime();


        // this is pretty much only clientside
        if (!CONTROL_QUEUE.isEmpty() && eventTickNumber > eventTickNumberCheck) {
            HashMap<Pair<UUID, BlockPos>, Integer> destroy = new HashMap<>();
            for (Entry<Pair<UUID, BlockPos>, Integer> entries : CONTROL_QUEUE.entrySet()) {
                if (entries.getKey().getFirst() != null) {
                    Player player = event.level.getPlayerByUUID(entries.getKey().getFirst());
                    if (player != null) {
                        RocketContraptionEntity rocket = ((RocketContraptionEntity) event.level.getEntity(entries.getValue()));
                        if (rocket != null) {
                            if (rocket.getControllingPlayer().isPresent()) {
                                if (rocket.getControllingPlayer().get() == player.getUUID()) {
                                    destroy.put(entries.getKey(), entries.getValue());
                                    continue;
                                }
                            }

                            rocket.handlePlayerInteraction(player, entries.getKey().getSecond(), Direction.NORTH, InteractionHand.MAIN_HAND);
                            destroy.put(entries.getKey(), entries.getValue());
                        }
                    }
                }
            }
            for (Entry<Pair<UUID, BlockPos>, Integer> entries : destroy.entrySet()) {
                CONTROL_QUEUE.remove(entries.getKey(), entries.getValue());
            }
        }


        /**
         * All server side code
         */
        if (event.level.isClientSide)
            return;

        if (eventTickNumber > eventTickNumberCheck) {
            //deleting contents of the queue
            if (!TICKET_QUEUE.isEmpty()) {
                List<Pair<Level, BlockPos>> DELETE_QUEUE = new ArrayList<>();
                for (Pair<Level, BlockPos> entries : TICKET_QUEUE) {
                    if (event.level.dimension() == entries.getFirst().dimension()) {
                        if (entries.getFirst().getBlockEntity(entries.getSecond()) instanceof RocketStationBlockEntity rsbe) {
                            rsbe.container.setItem(0, new ItemStack(Blocks.AIR.asItem()));
                            DELETE_QUEUE.add(entries);
                        }
                    }
                }
                for (Pair<Level, BlockPos> entries : DELETE_QUEUE) {
                    TICKET_QUEUE.remove(entries);
                }
            }
        }
    }

    private static CompoundTag saveEntityData(Entity passenger) {
        CompoundTag nbt = new CompoundTag(); //Save the NBT data
        return passenger.saveWithoutId(nbt); // contains "UUID"
    }

    private static void loadEntityData(Entity passenger, CompoundTag nbt, Map<UUID, Vec3> shipOffsetMap) {
        passenger.load(nbt); // restores same UUID + your custom data
        if (shipOffsetMap == null ||!shipOffsetMap.containsKey(passenger.getUUID())) {
            Northstar.LOGGER.warn("Passenger {} DOES NOT has a ship offset", passenger);
        }
    }

    public static void deleteTicket(Level level, BlockPos pos) {
        TICKET_QUEUE.add(Pair.of(level, pos.above()));
        eventTickNumberCheck = eventTickNumber + 3;
    }


    public static boolean isInRocket(Entity entity) {
        return getRocketThatContainsEntity(entity) != null;
    }

    public static RocketContraptionEntity getRocketThatContainsEntity(Entity entity) {
        for (RocketContraptionEntity rocket : ROCKETS) {
            if (rocket.getEntitiesWithinContraption().contains(entity)) {
                return rocket;
            }
        }
        return null;
    }

    public static void register() {
    }

}

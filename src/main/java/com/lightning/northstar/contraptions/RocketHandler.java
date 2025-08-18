package com.lightning.northstar.contraptions;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.rocket_station.RocketStationBlockEntity;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.mojang.datafixers.util.Pair;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.LevelData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;
import java.util.Map.Entry;

@EventBusSubscriber(modid = Northstar.MOD_ID)
public class RocketHandler {
    public static List<RocketContraptionEntity> ROCKETS = new ArrayList<>();
    public static List<Pair<Level, BlockPos>> TICKET_QUEUE = new ArrayList<>();
    public static HashMap<Pair<UUID, BlockPos>, Integer> CONTROL_QUEUE = new HashMap<>();
    public static long eventTickNumber;
    public static long eventTickNumberCheck;
    private static UUID pilotID;
    static int pp = 0;

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Pre event) {
        eventTickNumber = event.getLevel().getGameTime();


        // this is pretty much only clientside
        if (!CONTROL_QUEUE.isEmpty() && eventTickNumber > eventTickNumberCheck) {
            HashMap<Pair<UUID, BlockPos>, Integer> destroy = new HashMap<>();
            for (Entry<Pair<UUID, BlockPos>, Integer> entries : CONTROL_QUEUE.entrySet()) {
                if (entries.getKey().getFirst() != null) {
                    Player player = event.getLevel().getPlayerByUUID(entries.getKey().getFirst());
                    if (player != null) {
                        RocketContraptionEntity rocket = ((RocketContraptionEntity) event.getLevel().getEntity(entries.getValue()));
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
        if (event.getLevel().isClientSide)
            return;

        if (ROCKETS.size() != 0) {
            pp++;
            for (int p = 0; p < ROCKETS.size(); p++) {
                if (pp % 800 == 0) {
                    if (ROCKETS.get(p).level().dimension() != ROCKETS.get(p).destination && ROCKETS.get(p).getY() > 1750) {
                        changeDim(ROCKETS.get(p), event.getLevel());
                        ROCKETS.remove(ROCKETS.get(p));
                    }
                }
            }
        }

        if (eventTickNumber > eventTickNumberCheck) {
            //deleting contents of the queue
            if (!TICKET_QUEUE.isEmpty()) {
                List<Pair<Level, BlockPos>> DELETE_QUEUE = new ArrayList<>();
                for (Pair<Level, BlockPos> entries : TICKET_QUEUE) {
                    if (event.getLevel().dimension() == entries.getFirst().dimension()) {
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

    public static void changeDim(RocketContraptionEntity entity, Level level) {
        if (entity == null)
            return;
        entity.startLanding();
        ResourceKey<Level> dest = entity.destination == null ? NorthstarDimensions.MOON_DIM_KEY : entity.destination;
        ServerLevel destLevel = entity.level().getServer().getLevel(dest);
        HashMap<Entity, Integer> seatMap = new HashMap<>();
        UUID controller = null;
        Map<Entity, MutableInt> colliders = new HashMap<>();
        for (Entity passengers : entity.entitiesInContraption) {
            if (passengers.level().getServer().getLevel(passengers.level().dimension()) != destLevel && !passengers.level().isClientSide) {
                if (passengers instanceof ServerPlayer) {
                    if (!entity.getControllingPlayer().isEmpty()) {
                        if (entity.getControllingPlayer().get() == passengers.getUUID())
                            pilotID = passengers.getUUID();

                    }
                    changePlayerDimension(destLevel, (ServerPlayer) passengers, seatMap, entity.getContraption(), entity, controller);
                    continue;
                }
                changeDimensionCustom(destLevel, passengers, seatMap, colliders, entity.getContraption(), entity, controller);
            }
        }
        changeDimensionCustom(destLevel, entity, seatMap, colliders, entity.getContraption(), entity, controller);
        eventTickNumberCheck = eventTickNumber + 70;

    }

    public static Entity changeDimensionCustom(ServerLevel pDestination, Entity entity,
                                               HashMap<Entity, Integer> seatMap, Map<Entity, MutableInt> colliders, RocketContraption contrap, RocketContraptionEntity contrapEnt, UUID controller) {
        if (!CommonHooks.onTravelToDimension(entity, pDestination.dimension())) return null;
        if (entity.level() instanceof ServerLevel && !entity.isRemoved()) {
            entity.level().getProfiler().push("changeDimension");
            int seatNumber = -12345;
            if (contrap.getSeatOf(entity.getUUID()) != null) {
                Map<UUID, Integer> seatMapping = contrap.getSeatMapping();
                for (Map.Entry<UUID, Integer> entry : seatMapping.entrySet()) {
                    if (entry.getKey() == entity.getUUID()) {
                        seatNumber = entry.getValue();
                    }
                }
            }
            entity.level().getProfiler().push("reposition");

            System.out.println(entity);

            Entity transportedEntity = entity.getType().create(pDestination);
            System.out.println(transportedEntity);
            System.out.println(entity.getType());
            if (transportedEntity != null) {
                transportedEntity.restoreFrom(entity);
                transportedEntity.moveTo(entity.position().x, entity.position().y, entity.position().z, entity.getYRot(), entity.getXRot());
                transportedEntity.setDeltaMovement(entity.getDeltaMovement());
                pDestination.addDuringTeleport(transportedEntity);
            }

            if (entity instanceof RocketContraptionEntity rce) {

                for (Integer entint : seatMap.values()) {
                    for (Entity ents : seatMap.keySet()) {
                        if (seatMap.get(ents) == entint)
                            ((RocketContraptionEntity) transportedEntity).addSittingPassenger(ents, entint);
                        ;
                    }
                }
                for (MutableInt entint : colliders.values()) {
                    for (Entity ents : colliders.keySet()) {
                        if (colliders.get(ents) == entint)
                            ((RocketContraptionEntity) transportedEntity).registerColliding(ents);
                    }
                }
                if (controller == null) {
                    ((RocketContraptionEntity) transportedEntity).setControllingPlayer(controller);
                }
                ((RocketContraptionEntity) transportedEntity).owner = rce.owner;
                ((RocketContraptionEntity) transportedEntity).isUsingTicket = rce.isUsingTicket;
                ((RocketContraptionEntity) transportedEntity).visualEngineCount = rce.visualEngineCount;
                ((RocketContraptionEntity) transportedEntity).localControlsPos = rce.localControlsPos;
                ((RocketContraptionEntity) transportedEntity).setControllingPlayer(controller);
                if (pilotID != null)
                    CatnipServices.NETWORK.sendToAllClients(new RocketControlPacket(transportedEntity.getId(), pilotID, rce.localControlsPos));


            }
            if (seatNumber != -12345) {
                seatMap.put(transportedEntity, seatNumber);
            }
            if (contrapEnt.collidingEntities.containsKey(entity)) {
                colliders.put(transportedEntity, contrapEnt.collidingEntities.get(entity));
                // TRUCK NUTS!!!!!!
            }

            entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
            entity.level().getProfiler().pop();
            ((ServerLevel) entity.level()).resetEmptyTime();
            pDestination.resetEmptyTime();
            entity.level().getProfiler().pop();
            return transportedEntity;
        } else {
            return null;
        }
    }

    public static Entity changePlayerDimension(ServerLevel pDestination, ServerPlayer entity, HashMap<Entity, Integer> seatMap, RocketContraption contrap, RocketContraptionEntity contrapEnt, UUID controller) {
        if (!CommonHooks.onTravelToDimension(entity, pDestination.dimension())) return null;
        entity.isChangingDimension();

        ServerLevel serverlevel = (ServerLevel) entity.level();
        LevelData leveldata = pDestination.getLevelData();

        entity.connection.send(new ClientboundRespawnPacket(new CommonPlayerSpawnInfo(pDestination.dimensionTypeRegistration(), pDestination.dimension(), BiomeManager.obfuscateSeed(pDestination.getSeed()), entity.gameMode.getGameModeForPlayer(), entity.gameMode.getPreviousGameModeForPlayer(), pDestination.isDebug(), pDestination.isFlat(), entity.getLastDeathLocation(), entity.getPortalCooldown()), (byte) 3));
        entity.connection.send(new ClientboundChangeDifficultyPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));

        PlayerList playerlist = entity.server.getPlayerList();
        int seatNumber = -12345;
        if (contrap.getSeatOf(entity.getUUID()) != null) {
            Map<UUID, Integer> seatMapping = contrap.getSeatMapping();
            for (Map.Entry<UUID, Integer> entry : seatMapping.entrySet()) {
                if (entry.getKey() == entity.getUUID()) {
                    seatNumber = entry.getValue();
                }
            }
        }
        if (contrapEnt.hasControllingPassenger() && contrapEnt.getControllingPlayer().isPresent()) {
            controller = contrapEnt.getControllingPlayer().get();
        }
        playerlist.sendPlayerPermissionLevel(entity);
        pDestination.removePlayerImmediately(entity, Entity.RemovalReason.CHANGED_DIMENSION);
        entity.revive();
        entity.setServerLevel(pDestination);
        CriteriaTriggers.CHANGED_DIMENSION.trigger(entity, entity.level().dimension(), pDestination.dimension());
        entity.connection.send(new ClientboundPlayerAbilitiesPacket(entity.getAbilities()));
        playerlist.sendLevelInfo(entity, pDestination);
        playerlist.sendAllPlayerInfo(entity);

        for (MobEffectInstance mobeffectinstance : entity.getActiveEffects()) {
            entity.connection.send(new ClientboundUpdateMobEffectPacket(entity.getId(), mobeffectinstance, false));

        }
        if (seatNumber != -12345) {
            seatMap.put(entity, seatNumber);
        }


        return entity;

    }

    public static void deleteTicket(Level level, BlockPos pos) {
        TICKET_QUEUE.add(Pair.of(level, pos.above()));
        eventTickNumberCheck = eventTickNumber + 3;
    }

    public static boolean isInRocket(Entity entity) {
        for (RocketContraptionEntity rockets : ROCKETS) {
            if (rockets.entitiesInContraption.contains(entity)) {
                return true;
            }
        }
        return false;
    }

    public static void register() {
    }

}

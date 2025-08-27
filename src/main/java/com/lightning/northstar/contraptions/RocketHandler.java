package com.lightning.northstar.contraptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraptions.packets.RocketControlPacket;
import com.lightning.northstar.mixinInterfaces.EntityMixin_I;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.mutable.MutableInt;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.rocket_station.RocketStationBlockEntity;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
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
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.storage.LevelData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(modid = Northstar.MOD_ID, bus = Bus.FORGE)
public class RocketHandler {
    public static List<RocketContraptionEntity> ROCKETS = new ArrayList<>();
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
                            if (!rocket.getControllingPlayer().isEmpty()) {
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

        if (!ROCKETS.isEmpty()) {
            pp++;
            if (pp % 800 == 0) {
                for (int p = 0; p < ROCKETS.size(); p++) {
                    if (ROCKETS.get(p).level().dimension() != ROCKETS.get(p).destination
                            && ROCKETS.get(p).getY() > DIMENSION_CHANGE_HEIGHT) { //If we are above the dimension change height and we are in the wrong dimension
                        changeDim(ROCKETS.get(p), event.level); //Change rocket dimension
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


    public static void changeDim(RocketContraptionEntity rocket, Level level) {
        //Note that this method only runs on the server
        if (rocket == null) return;

        rocket.startLanding();
        ResourceKey<Level> dest = rocket.destination == null ? NorthstarDimensions.MOON_DIM_KEY : rocket.destination;
        Map<UUID, Vec3> shipOffsetMap = rocket.getPassengerOffsets();
        ServerLevel destLevel = rocket.level().getServer().getLevel(dest);

        HashMap<Entity, Integer> seatMap = new HashMap<>();
        UUID controller = null;
        Map<Entity, MutableInt> colliders = new HashMap<>();

        //Change dimension of all passengers
        for (Entity passenger : rocket.getEntitiesWithinContraption()) {
            if (passenger.level().getServer().getLevel(passenger.level().dimension()) != destLevel && !passenger.level().isClientSide) {
                System.out.println("Changing dimension of (passenger): " + passenger);
                if (passenger instanceof ServerPlayer) {
                    if (!rocket.getControllingPlayer().isEmpty()) {
                        if (rocket.getControllingPlayer().get() == passenger.getUUID())
                            pilotID = passenger.getUUID();

                    }
                    if (destLevel == null) {
                        destLevel = passenger.level().getServer().getLevel(passenger.level().dimension());
                    }

                    CompoundTag nbt = saveEntityData(passenger);
                    Entity newEntity = changePlayerDimension(destLevel, (ServerPlayer) passenger, new PortalForcer(destLevel), seatMap, rocket.getContraption(), rocket, controller);
                    loadEntityData(newEntity, nbt, shipOffsetMap);

                } else {
                    CompoundTag nbt = saveEntityData(passenger);
                    Entity newEntity = changeDimensionCustom(destLevel, passenger, new PortalForcer(destLevel), seatMap, colliders, rocket.getContraption(), rocket, controller);
                    loadEntityData(newEntity, nbt, shipOffsetMap);
                }
            }
        }

        //Change rocket dimension
        System.out.println("Changing dimension of (ship): " + rocket);
        Entity newRocket = changeDimensionCustom(destLevel, rocket, new PortalForcer(destLevel), seatMap, colliders, rocket.getContraption(), rocket, controller);
        ((EntityMixin_I) newRocket).setRocketPassengerOffsets(shipOffsetMap);  //Make sure we dont lose the offset map
        eventTickNumberCheck = eventTickNumber + 70;
    }

    private static CompoundTag saveEntityData(Entity passenger) {
        CompoundTag nbt = new CompoundTag(); //Save the NBT data
        return passenger.saveWithoutId(nbt); // contains "UUID"
    }

    private static void loadEntityData(Entity passenger, CompoundTag nbt, Map<UUID, Vec3> shipOffsetMap) {
        passenger.load(nbt); // restores same UUID + your custom data
        if (shipOffsetMap == null ||!shipOffsetMap.containsKey(passenger.getUUID())) {
            Northstar.LOGGER.warn("Passenger " + passenger + " DOES NOT has a ship offset");
        }
    }

    public static Entity changeDimensionCustom(ServerLevel pDestination, Entity entity, net.minecraftforge.common.util.ITeleporter teleporter,
                                               HashMap<Entity, Integer> seatMap, Map<Entity, MutableInt> colliders, RocketContraption contrap, RocketContraptionEntity contrapEnt, UUID controller) {
        if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entity, pDestination.dimension())) return null;
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

            Entity transportedEntity = teleporter.placeEntity(entity, (ServerLevel) entity.level(), pDestination, entity.getYRot(), spawnPortal -> {

                entity.level().getProfiler().popPush("reloading");
                Entity newentity = entity.getType().create(pDestination);
                System.out.println(newentity);
                System.out.println(entity.getType());

                if (newentity != null) {
                    newentity.restoreFrom(entity);
                    newentity.moveTo(entity.position().x, entity.position().y, entity.position().z, entity.getYRot(), entity.getXRot());
                    newentity.setDeltaMovement(entity.getDeltaMovement());
                    pDestination.addDuringTeleport(newentity);
                }

                return newentity;
            });
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
                    NorthstarPackets.getChannel().send(PacketDistributor.ALL.noArg(),
                            new RocketControlPacket(pilotID, ((RocketContraptionEntity) transportedEntity).getId(), rce.localControlsPos));


            }
            if (seatNumber != -12345) {
                seatMap.put(transportedEntity, seatNumber);
            }
            if (contrapEnt.collidingEntities.containsKey(entity)) {
                colliders.put(transportedEntity, contrapEnt.collidingEntities.get(entity));
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

    public static Entity changePlayerDimension(ServerLevel pDestination, ServerPlayer entity, net.minecraftforge.common.util.ITeleporter teleporter, HashMap<Entity, Integer> seatMap, RocketContraption contrap, RocketContraptionEntity contrapEnt, UUID controller) {
        if (!ForgeHooks.onTravelToDimension(entity, pDestination.dimension())) return null;
        entity.isChangingDimension();

        ServerLevel serverlevel = (ServerLevel) entity.level();
        LevelData leveldata = pDestination.getLevelData();
        entity.connection.send(new ClientboundRespawnPacket(pDestination.dimensionTypeId(), pDestination.dimension(), BiomeManager.obfuscateSeed(pDestination.getSeed()), entity.gameMode.getGameModeForPlayer(), entity.gameMode.getPreviousGameModeForPlayer(), pDestination.isDebug(), pDestination.isFlat(), (byte) 3, entity.getLastDeathLocation(), entity.getPortalCooldown()));
        entity.connection.send(new ClientboundChangeDifficultyPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));

        PlayerList playerlist = entity.server.getPlayerList();
        int seatNumber = Integer.MIN_VALUE;
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
        //We have to detach from the rocket in order to change dimensions
        pDestination.removePlayerImmediately(entity, Entity.RemovalReason.CHANGED_DIMENSION);
        entity.revive();
        PortalInfo portalinfo = new PortalInfo(entity.position(), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
        Entity e = teleporter.placeEntity(entity, serverlevel, pDestination, entity.getYRot(), spawnPortal -> {//Forge: Start vanilla logic
            serverlevel.getProfiler().push("moving");
            serverlevel.getProfiler().pop();
            serverlevel.getProfiler().push("placing");
            entity.setServerLevel(pDestination);
            pDestination.addDuringPortalTeleport(entity);
            entity.setXRot(portalinfo.xRot);
            entity.setYRot(portalinfo.yRot);
            entity.moveTo(portalinfo.pos.x, portalinfo.pos.y + 1, portalinfo.pos.z);
            serverlevel.getProfiler().pop();
            CriteriaTriggers.CHANGED_DIMENSION.trigger(entity, entity.level().dimension(), pDestination.dimension());
            return entity;//forge: this is part of the ITeleporter patch
        });//Forge: End vanilla logic
        if (e != entity)
            throw new java.lang.IllegalArgumentException(String.format(java.util.Locale.ENGLISH, "Teleporter %s returned not the player entity but instead %s, expected PlayerEntity %s", teleporter, e, entity));
        entity.connection.send(new ClientboundPlayerAbilitiesPacket(entity.getAbilities()));
        playerlist.sendLevelInfo(entity, pDestination);
        playerlist.sendAllPlayerInfo(entity);

        for (MobEffectInstance mobeffectinstance : entity.getActiveEffects()) {
            entity.connection.send(new ClientboundUpdateMobEffectPacket(entity.getId(), mobeffectinstance));

        }
        if (seatNumber != Integer.MIN_VALUE) {
            seatMap.put(entity, seatNumber);
        }


        return entity;

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

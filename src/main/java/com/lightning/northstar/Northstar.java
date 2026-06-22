package com.lightning.northstar;

import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.advancements.NorthstarAdvancements;
import com.lightning.northstar.compat.sable.NorthstarSable;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.*;
import com.lightning.northstar.content.world.NorthstarFeatures;
import com.lightning.northstar.contraption.FuelType;
import com.lightning.northstar.data.ModCompat;
import com.lightning.northstar.entity.*;
import com.lightning.northstar.particle.NorthstarParticles;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.planet.data.PlanetProperties;
import com.lightning.northstar.planet.data.func.LevelFunction;
import com.lightning.northstar.planet.data.orbit.OrbitProvider;
import com.lightning.northstar.planet.data.render.PlanetSpriteRenderer;
import com.lightning.northstar.world.gen.feature.trunkplacers.NorthstarTrunkPlacerTypes;
import com.lightning.northstar.world.oxygen.DefaultOxygenConsumers;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.util.Iterator;

@Mod(Northstar.MOD_ID)
public class Northstar {

    public static final String MOD_ID = "northstar";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final FontHelper.Palette PALETTE = new FontHelper.Palette(
            TooltipHelper.styleFromColor(0x80AFD2),
            TooltipHelper.styleFromColor(0x4D98FA)
    );
    public static final NorthstarRegistrate REGISTRATE = new NorthstarRegistrate(MOD_ID);

    static {
        REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
                .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, PALETTE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    public Northstar(IEventBus eventBus, ModContainer container) {
        REGISTRATE.registerEventListeners(eventBus);

        NorthstarArmorMaterials.register(eventBus);
        NorthstarBlockEntityTypes.register();
        NorthstarBlocks.register();
        NorthstarContraptionTypes.register(eventBus);
        NorthstarCreativeModeTab.register(eventBus);
        NorthstarDataComponents.register(eventBus);
        NorthstarEnchantments.register(eventBus);
        NorthstarEntitySubPredicates.register(eventBus);
        NorthstarEntityTypes.register();
        NorthstarFluids.register();
        NorthstarItems.register();
        NorthstarMenuTypes.register();
        NorthstarPartialModels.register();
        NorthstarParticles.register(eventBus);
        NorthstarPois.register(eventBus);
        NorthstarPotatoProjectileEntityHitActions.register(eventBus);
        NorthstarPotions.register(eventBus);
        NorthstarRecipeTypes.register(eventBus);
        NorthstarSounds.register(eventBus);
        NorthstarStats.register(eventBus);
        NorthstarTags.register();

        NorthstarFeatures.register(eventBus);
        NorthstarTrunkPlacerTypes.register(eventBus);

        DefaultOxygenConsumers.register();

        LevelFunction.register();
        OrbitProvider.register();
        PlanetSpriteRenderer.register();

        NorthstarConfigs.register(container::registerConfig);

        ModCompat.SABLE.executeIfLoaded(() -> NorthstarSable::init);

        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> NorthstarClient.clientInit(eventBus));
    }

    @EventBusSubscriber(modid = MOD_ID)
    public static class EventListeners {
        @SubscribeEvent
        public static void init(FMLCommonSetupEvent event) {
            NorthstarPackets.registerPackets();

            event.enqueueWork(() -> {
                NorthstarAdvancements.register();
            });
        }

        @SubscribeEvent
        public static void onRegister(RegisterEvent event) {
            if (event.getRegistryKey() == Registries.CUSTOM_STAT) {
                NorthstarStats.registerFormatters();
            }
            if (event.getRegistryKey() == Registries.TRIGGER_TYPE) {
                NorthstarAdvancements.register();
            }
        }

        @SubscribeEvent
        public static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(NorthstarRegistries.FUEL, FuelType.CODEC, FuelType.CODEC);
            event.dataPackRegistry(NorthstarRegistries.PLANET, PlanetProperties.CODEC, PlanetProperties.CODEC);
            event.dataPackRegistry(NorthstarRegistries.PLANET_DIMENSION, PlanetDimension.CODEC, PlanetDimension.CODEC);
        }

        @SubscribeEvent
        public static void onLoadConfig(ModConfigEvent.Loading event) {
            NorthstarConfigs.onLoad(event.getConfig());
        }

        @SubscribeEvent
        public static void onReloadConfig(ModConfigEvent.Reloading event) {
            NorthstarConfigs.onReload(event.getConfig());
        }

        @SubscribeEvent
        public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
            event.register(NorthstarEntityTypes.MARS_WORM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MarsWormEntity::wormSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MARS_TOAD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MarsToadEntity::toadSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MARS_COBRA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MarsCobraEntity::cobraSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MARS_MOTH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MarsMothEntity::mothSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

            event.register(NorthstarEntityTypes.VENUS_MIMIC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    VenusMimicEntity::mimicSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.VENUS_SCORPION.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    VenusScorpionEntity::scorpionSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.VENUS_STONE_BULL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    VenusStoneBullEntity::stoneBullSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.VENUS_VULTURE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    VenusVultureEntity::vultureSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

            event.register(NorthstarEntityTypes.MOON_SNAIL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MoonSnailEntity::snailSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MOON_LUNARGRADE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MoonLunargradeEntity::lunargradeSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MOON_EEL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MoonEelEntity::eelSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

            event.register(NorthstarEntityTypes.MERCURY_RAPTOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MercuryRaptorEntity::raptorSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MERCURY_ROACH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MercuryRoachEntity::roachSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MERCURY_TORTOISE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MercuryTortoiseEntity::tortoiseSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        @SubscribeEvent
        public static void onRegistrySync(TagsUpdatedEvent event) {
            FuelType.recacheFuels(event.getRegistryAccess());

            if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
                NorthstarLevel.SERVER_TRACKER.reloadPlanets(event.getRegistryAccess());
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server != null)
                    server.getAllLevels().forEach(Level::northstar$onResourceReload);
            } else if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
                NorthstarLevel.CLIENT_TRACKER.reloadPlanets(event.getRegistryAccess());
                RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    ClientLevel level = Minecraft.getInstance().level;
                    if (level != null)
                        level.northstar$onResourceReload();
                });
            } else {
                throw new RuntimeException("Unknown update cause " + event.getUpdateCause());
            }
        }

        @SubscribeEvent
        public static void onServerTick(ServerTickEvent.Pre event) {
            Iterator<ServerLevel> levels = event.getServer().getAllLevels().iterator();
            if (levels.hasNext()) {
                NorthstarLevel.SERVER_TRACKER.tick(levels.next(), 0);
            }
        }
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

}

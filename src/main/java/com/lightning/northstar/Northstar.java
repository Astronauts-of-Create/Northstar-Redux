package com.lightning.northstar;

import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.advancements.NorthstarAdvancements;
import com.lightning.northstar.advancements.NorthstarTriggers;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.*;
import com.lightning.northstar.content.world.NorthstarFeatures;
import com.lightning.northstar.contraption.FuelType;
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
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.Iterator;

@Mod(Northstar.MOD_ID)
public class Northstar {

    public static final String MOD_ID = "northstar";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final FontHelper.Palette PALETTE = new FontHelper.Palette(
            TooltipHelper.styleFromColor(0x80AFD2),
            TooltipHelper.styleFromColor(0x4D98FA)
    );
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, PALETTE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))));

    public Northstar(FMLJavaModLoadingContext modContext) {
        IEventBus modEventBus = modContext.getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        REGISTRATE.registerEventListeners(modEventBus);
        GeckoLib.initialize();

        NorthstarConfigs.register(modContext::registerConfig);

        NorthstarBlockEntityTypes.register();
        NorthstarBlocks.register();
        NorthstarContraptionTypes.register(modEventBus);
        NorthstarCreativeModeTab.register(modEventBus);
        NorthstarEnchantments.register();
        NorthstarEntityTypes.register();
        NorthstarFluids.register();
        NorthstarItems.register();
        NorthstarMenuTypes.register();
        NorthstarPartialModels.register();
        NorthstarParticles.register(modEventBus);
        NorthstarPois.register(modEventBus);
        NorthstarPotatoProjectileEntityHitActions.register(modEventBus);
        NorthstarPotions.register(modEventBus);
        NorthstarRecipeTypes.register(modEventBus);
        NorthstarSounds.register(modEventBus);
        NorthstarStats.register(modEventBus);
        NorthstarTags.register();

        NorthstarFeatures.register(modEventBus);
        NorthstarTrunkPlacerTypes.register(modEventBus);

        DefaultOxygenConsumers.register();

        LevelFunction.register();
        OrbitProvider.register();
        PlanetSpriteRenderer.register();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> NorthstarClient.clientInit(modEventBus, forgeEventBus));
    }

    @EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD)
    public static class ModEventListeners {
        @SubscribeEvent
        public static void init(FMLCommonSetupEvent event) {
            NorthstarPackets.registerPackets();

            event.enqueueWork(() -> {
                NorthstarAdvancements.register();
                NorthstarTriggers.register();
            });
        }

        @SubscribeEvent
        public static void onRegister(RegisterEvent event) {
            if (event.getRegistryKey() == Registries.CUSTOM_STAT) {
                NorthstarStats.registerFormatters();
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
        public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
            event.register(NorthstarEntityTypes.MARS_WORM.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MarsWormEntity::wormSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MARS_TOAD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MarsToadEntity::toadSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MARS_COBRA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MarsCobraEntity::cobraSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MARS_MOTH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MarsMothEntity::mothSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);

            event.register(NorthstarEntityTypes.VENUS_MIMIC.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    VenusMimicEntity::mimicSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.VENUS_SCORPION.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    VenusScorpionEntity::scorpionSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.VENUS_STONE_BULL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    VenusStoneBullEntity::stoneBullSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.VENUS_VULTURE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    VenusVultureEntity::vultureSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);

            event.register(NorthstarEntityTypes.MOON_SNAIL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MoonSnailEntity::snailSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MOON_LUNARGRADE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MoonLunargradeEntity::lunargradeSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MOON_EEL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MoonEelEntity::eelSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);

            event.register(NorthstarEntityTypes.MERCURY_RAPTOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MercuryRaptorEntity::raptorSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MERCURY_ROACH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MercuryRoachEntity::roachSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(NorthstarEntityTypes.MERCURY_TORTOISE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    MercuryTortoiseEntity::tortoiseSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        }
    }

    @EventBusSubscriber(modid = MOD_ID, bus = Bus.FORGE)
    public static class ForgeEventListeners {
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
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    ClientLevel level = Minecraft.getInstance().level;
                    if (level != null)
                        level.northstar$onResourceReload();
                });
            } else {
                throw new RuntimeException("Unknown update cause " + event.getUpdateCause());
            }
        }

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
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

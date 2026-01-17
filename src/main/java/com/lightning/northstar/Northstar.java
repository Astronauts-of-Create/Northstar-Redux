package com.lightning.northstar;

import com.lightning.northstar.advancements.NorthstarAdvancements;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.*;
import com.lightning.northstar.contraption.FuelType;
import com.lightning.northstar.contraption.rocket.RocketHandler;
import com.lightning.northstar.entity.*;
import com.lightning.northstar.item.NorthstarEnchantments;
import com.lightning.northstar.particle.NorthstarParticles;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.lightning.northstar.world.features.NorthstarFeatures;
import com.lightning.northstar.world.features.trunkplacers.NorthstarTrunkPlacerTypes;
import com.lightning.northstar.world.oxygen.DefaultOxygenConsumers;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.item.CreativeModeTab;
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
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(Northstar.MOD_ID)
public class Northstar {

    // Define mod id in a common place for everything to reference
    public static final double GRAV_CONSTANT = 0.08;
    public static final double EARTH_GRAV = 1;
    public static final double MARS_GRAV = 0.37;
    public static final double VENUS_GRAV = 0.89;

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

    public Northstar(IEventBus modEventBus, ModContainer container) {
        REGISTRATE.registerEventListeners(modEventBus);

        NorthstarTags.register();
        NorthstarCreativeModeTab.register(modEventBus);
        NorthstarDataComponents.register(modEventBus);
        NorthstarItems.register();
        NorthstarBlocks.register();
        NorthstarBlockEntityTypes.register();
        NorthstarPotions.register(modEventBus);
        NorthstarBlocks.register();
        NorthstarFeatures.register(modEventBus);
        NorthstarRecipeTypes.register(modEventBus);
        NorthstarParticles.register(modEventBus);
        NorthstarSounds.register(modEventBus);
        NorthstarStats.register(modEventBus);
        NorthstarMenuTypes.register();
        NorthstarPlanets.register();
        NorthstarDimensions.register();
        NorthstarEntityTypes.register();
        NorthstarFluids.register();
        NorthstarArmorMaterials.register(modEventBus);
        NorthstarEnchantments.register(modEventBus);
        DefaultOxygenConsumers.register();
        NorthstarPotatoProjectileEntityHitActions.register(modEventBus);

        NorthstarTrunkPlacerTypes.register(modEventBus);
        NorthstarPartialModels.register();

        RocketHandler.register();

        NorthstarConfigs.register(container::registerConfig);

        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> NorthstarClient.onCtorClient(modEventBus));
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

            NorthstarContraptionTypes.register();
            if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
                NorthstarAdvancements.register();
            }
        }

        @SubscribeEvent
        public static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(NorthstarRegistries.FUEL, FuelType.CODEC, FuelType.CODEC);
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
        }
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

}

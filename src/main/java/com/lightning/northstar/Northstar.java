package com.lightning.northstar;

import com.lightning.northstar.advancements.NorthstarAdvancements;
import com.lightning.northstar.advancements.NorthstarTriggers;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.*;
import com.lightning.northstar.contraption.rocket.RocketHandler;
import com.lightning.northstar.data.FuelType;
import com.lightning.northstar.entity.*;
import com.lightning.northstar.item.NorthstarEnchantments;
import com.lightning.northstar.item.NorthstarPotions;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.lightning.northstar.item.NorthstarTooltipModifier;
import com.lightning.northstar.particle.NorthstarParticles;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.lightning.northstar.world.features.NorthstarFeatures;
import com.lightning.northstar.world.features.trunkplacers.NorthstarTrunkPlacerTypes;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

@Mod(Northstar.MOD_ID)
public class Northstar {

    // Define mod id in a common place for everything to reference
    public static final double GRAV_CONSTANT = 0.08;
    public static final double EARTH_GRAV = 1;
    public static final double MARS_GRAV = 0.37;
    public static final double VENUS_GRAV = 0.89;

    public static final String MOD_ID = "northstar";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, new FontHelper.Palette(TooltipHelper.styleFromColor(0x9ba4ae), TooltipHelper.styleFromColor(0x80afd2)))
                .andThen(new NorthstarTooltipModifier())
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    public Northstar(FMLJavaModLoadingContext modContext) {
        IEventBus modEventBus = modContext.getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        GeckoLib.initialize();

        NorthstarTags.register();
        NorthstarCreativeModeTab.register(modEventBus);
        NorthstarItems.register();
        NorthstarBlocks.register();
        NorthstarBlockEntityTypes.register();
        NorthstarPotions.register(modEventBus);
        NorthstarEnchantments.register();
        NorthstarTechBlocks.register();
        NorthstarFeatures.register(modEventBus);
        NorthstarRecipeTypes.register(modEventBus);
        NorthstarParticles.register(modEventBus);
        NorthstarSounds.register(modEventBus);
        NorthstarMenuTypes.register();
        NorthstarPlanets.register();
        NorthstarDimensions.register();
        NorthstarEntityTypes.register(modEventBus);
        NorthstarFluids.register();

        NorthstarTrunkPlacerTypes.register(modEventBus);
        NorthstarPartialModels.register();

        RocketHandler.register();

        REGISTRATE.registerEventListeners(modEventBus);
        modEventBus.addListener(this::onRegisterRegistries);
        modEventBus.addListener(this::onRegister);

        NorthstarConfigs.register(modContext::registerConfig);
        modEventBus.addListener(this::onLoadConfig);
        modEventBus.addListener(this::onReloadConfig);

        modEventBus.addListener(this::init);
        modEventBus.addListener(this::registerSpawnPlacements);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> NorthstarClient.onCtorClient(modEventBus, forgeEventBus));
    }

    private void onRegister(RegisterEvent event) {
        NorthstarContraptionTypes.register();
    }

    private void onRegisterRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(NorthstarRegistries.FUEL, FuelType.CODEC, FuelType.CODEC);
    }

    private void init(FMLCommonSetupEvent event) {
        NorthstarPackets.registerPackets();

        event.enqueueWork(() -> {
            NorthstarAdvancements.register();
            NorthstarTriggers.register();
        });
    }

    private void onLoadConfig(ModConfigEvent.Loading event) {
        NorthstarConfigs.onLoad(event.getConfig());
    }

    private void onReloadConfig(ModConfigEvent.Reloading event) {
        NorthstarConfigs.onReload(event.getConfig());
    }

    private void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
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

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

}

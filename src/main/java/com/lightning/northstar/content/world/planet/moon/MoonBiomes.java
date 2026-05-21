package com.lightning.northstar.content.world.planet.moon;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarEntityTypes;
import com.lightning.northstar.content.world.planet.core.NorthstarPlacedFeatures;
import com.lightning.northstar.particle.NorthstarParticles;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.registries.ForgeRegistries;

public class MoonBiomes {

    public static final ResourceKey<Biome>
            ASURINE_CAVES = key("lunar_asurine_caves"),
            COOLED_LAVA_CAVE = key("lunar_cooled_lava_cave"),
            CRATER_FIELDS = key("lunar_crater_fields"),
            GLOWSTONE_CAVERN = key("lunar_glowstone_cavern"),
            HILLS = key("lunar_hills"),
            ICE_CAVES = key("lunar_ice_caves"),
            PLAINS = key("lunar_plains");

    private static ResourceKey<Biome> key(String path) {
        return ResourceKey.create(Registries.BIOME, Northstar.asResource(path));
    }

    public static void bootstrap(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> worldCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(
                ASURINE_CAVES,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0)
                                .fogColor(0)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MoonBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, NorthstarPlacedFeatures.RARE_LAVA_LAKE)
                                .apply(MoonBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_ZINC_LARGE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_GLOWSTONE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ANDESITE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ASURINE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ASURINE_LARGE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_BASALT)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.ASURINE_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.ASURINE_COLUMN)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MoonPlacedFeatures.LUNAR_SAPPHIRE_GEODE)
                                .build())
                        .build()
        );

        context.register(
                COOLED_LAVA_CAVE,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0)
                                .fogColor(0)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MoonBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, NorthstarPlacedFeatures.RARE_LAVA_LAKE)
                                .apply(MoonBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_GLOWSTONE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ANDESITE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ASURINE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_BASALT)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_OBSIDIAN)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.OBSIDIAN_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MoonPlacedFeatures.LUNAR_SAPPHIRE_GEODE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.FROST_SPARSE)
                                .build())
                        .build()
        );

        context.register(
                CRATER_FIELDS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0)
                                .fogColor(0)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MoonBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.RAW_GENERATION, MoonPlacedFeatures.CRATER_BIG)
                                .addFeature(Decoration.RAW_GENERATION, MoonPlacedFeatures.CRATER)
                                .addFeature(Decoration.LAKES, NorthstarPlacedFeatures.RARE_LAVA_LAKE)
                                .apply(MoonBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_GLOWSTONE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ANDESITE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ASURINE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_BASALT)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_BLACKSTONE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ICE_SMALL)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MoonPlacedFeatures.LUNAR_SAPPHIRE_GEODE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.FROST_SPARSE)
                                .build())
                        .build()
        );

        context.register(
                GLOWSTONE_CAVERN,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0)
                                .fogColor(0)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .ambientMoodSound(new AmbientMoodSettings(
                                        ForgeRegistries.SOUND_EVENTS.getDelegateOrThrow(SoundEvents.AMETHYST_BLOCK_CHIME),
                                        6000,
                                        8,
                                        2
                                ))
                                .ambientParticle(new AmbientParticleSettings(
                                        NorthstarParticles.GLOWSTONE.get(),
                                        1
                                ))
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MoonBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.LAKES, NorthstarPlacedFeatures.RARE_LAVA_LAKE)
                                .apply(MoonBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_GLOWSTONE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ANDESITE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ASURINE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_BASALT)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.GLOWSTONE_BRANCH)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.GLOWSTONE_UPSIDE_DOWN_BRANCH)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.GLOWSTONE_COLUMN)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MoonPlacedFeatures.LUNAR_SAPPHIRE_GEODE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.FROST_SPARSE)
                                .build())
                        .build()
        );

        context.register(
                HILLS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0)
                                .fogColor(0)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MoonBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .apply(MoonBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_GLOWSTONE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ANDESITE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ASURINE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_BASALT)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_BLACKSTONE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MoonPlacedFeatures.LUNAR_SAPPHIRE_GEODE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.FROST_SPARSE)
                                .build())
                        .build()
        );

        context.register(
                ICE_CAVES,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0)
                                .fogColor(0)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MoonBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .apply(MoonBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_GLOWSTONE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ANDESITE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ASURINE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_BASALT)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ICE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_BLUE_ICE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.ICE_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.BLUE_ICE_CLUSTER)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.ICE_COLUMN)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.ICICLES)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.FROST)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MoonPlacedFeatures.LUNAR_SAPPHIRE_GEODE)
                                .build())
                        .build()
        );

        context.register(
                PLAINS,
                new Biome.BiomeBuilder()
                        .hasPrecipitation(false)
                        .temperature(0.8f)
                        .downfall(0.4f)
                        .specialEffects(new BiomeSpecialEffects.Builder()
                                .skyColor(0)
                                .fogColor(0)
                                .waterColor(0x3f76e4)
                                .waterFogColor(0x050533)
                                .build())
                        .mobSpawnSettings(new MobSpawnSettings.Builder()
                                .apply(MoonBiomes::commonMonsters)
                                .build())
                        .generationSettings(new BiomeGenerationSettings.Builder(placedFeatures, worldCarvers)
                                .addCarver(Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                                .addCarver(Carving.AIR, Carvers.CAVE)
                                .addFeature(Decoration.RAW_GENERATION, MoonPlacedFeatures.CRATER)
                                .addFeature(Decoration.LAKES, NorthstarPlacedFeatures.RARE_LAVA_LAKE)
                                .apply(MoonBiomes::commonOres)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_GLOWSTONE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ANDESITE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_ASURINE)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_BASALT)
                                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.BLOB_BLACKSTONE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, MoonPlacedFeatures.LUNAR_SAPPHIRE_GEODE)
                                .addFeature(Decoration.UNDERGROUND_DECORATION, NorthstarPlacedFeatures.FROST_SPARSE)
                                .build())
                        .build()
        );
    }

    private static void commonMonsters(MobSpawnSettings.Builder builder) {
        builder.addSpawn(
                        MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(
                                NorthstarEntityTypes.MOON_LUNARGRADE.get(),
                                40,
                                1,
                                4
                        )
                )
                .addSpawn(
                        MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(
                                NorthstarEntityTypes.MOON_SNAIL.get(),
                                20,
                                1,
                                3
                        )
                )
                .addSpawn(
                        MobCategory.MONSTER,
                        new MobSpawnSettings.SpawnerData(
                                NorthstarEntityTypes.MOON_EEL.get(),
                                20,
                                2,
                                5
                        )
                );
    }

    private static void commonOres(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_TITANIUM)
                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_COPPER)
                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_DIAMOND)
                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_GOLD)
                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_IRON)
                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_IRON_LARGE)
                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_LAPIS)
                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_REDSTONE)
                .addFeature(Decoration.UNDERGROUND_ORES, MoonPlacedFeatures.ORE_ZINC);
    }

}

package com.lightning.northstar.content;

import com.lightning.northstar.block.simple.*;
import com.lightning.northstar.block.crops.*;
import com.lightning.northstar.block.tech.astronomy_table.AstronomyTableBlock;
import com.lightning.northstar.block.tech.auto_lander.AutoLanderBlock;
import com.lightning.northstar.block.tech.circuit_engraver.CircuitEngraverBlock;
import com.lightning.northstar.block.tech.cogs.SpaceCogWheelBlock;
import com.lightning.northstar.block.tech.combustion_engine.CombustionEngineBlock;
import com.lightning.northstar.block.tech.computer_rack.TargetingComputerRackBlock;
import com.lightning.northstar.block.tech.electrolysis_machine.ElectrolysisMachineBlock;
import com.lightning.northstar.block.tech.ice_box.IceBoxBlock;
import com.lightning.northstar.block.tech.jet_engine.JetEngineBlock;
import com.lightning.northstar.block.tech.jet_engine.JetEngineMovementBehaviour;
import com.lightning.northstar.block.tech.oxygen_concentrator.OxygenConcentratorBlock;
import com.lightning.northstar.block.tech.oxygen_detector.OxygenDetectorBlock;
import com.lightning.northstar.block.tech.oxygen_filler.OxygenFillerBlock;
import com.lightning.northstar.block.tech.oxygen_sealer.OxygenSealerBlock;
import com.lightning.northstar.block.tech.oxygen_sealer.OxygenSealerMovementBehaviour;
import com.lightning.northstar.block.tech.oxygen_sealer.OxygenSealerMovingInteractionBehaviour;
import com.lightning.northstar.block.tech.rocket_controls.RocketControlsBlock;
import com.lightning.northstar.block.tech.rocket_controls.RocketControlsInteractionBehaviour;
import com.lightning.northstar.block.tech.rocket_controls.RocketControlsMovementBehaviour;
import com.lightning.northstar.block.tech.rocket_station.RocketStationBlock;
import com.lightning.northstar.block.tech.rocket_station.RocketStationBlockMovingInteraction;
import com.lightning.northstar.block.tech.solar_panel.SolarPanelBlock;
import com.lightning.northstar.block.tech.solar_panel.SolarPanelBlockEntity;
import com.lightning.northstar.block.tech.telescope.TelescopeBlock;
import com.lightning.northstar.block.tech.temperature_regulator.TemperatureRegulatorBlock;
import com.lightning.northstar.block.tech.temperature_regulator.TemperatureRegulatorMovementBehaviour;
import com.lightning.northstar.block.tech.temperature_regulator.TemperatureRegulatorMovingInteractionBehaviour;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.data.NorthstarConfiguredFeatures;
import com.lightning.northstar.util.NorthstarDataGenHelper;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.content.processing.basin.BasinMovementBehaviour;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

import static com.lightning.northstar.Northstar.REGISTRATE;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.*;
import static net.minecraft.world.level.block.Blocks.*;

public class NorthstarBlocks {

    // region Martian Steel

    public static final BlockEntry<Block> MARTIAN_STEEL_BLOCK = REGISTRATE
            .block("martian_steel_block", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(30f, 15f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MARTIAN_STEEL_SHEETMETAL = REGISTRATE
            .block("martian_steel_sheetmetal", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(5f, 15f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.TIER_2_HEAT_RESISTANCE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.sheetmetalRecipe(NorthstarItemTags.COMMON_SHEETS_MARTIAN_STEEL))
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> MARTIAN_STEEL_SHEETMETAL_SLAB = REGISTRATE
            .block("martian_steel_sheetmetal_slab", SlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(5f, 15f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleSlab())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(MARTIAN_STEEL_SHEETMETAL))
            .simpleItem()
            .register();

    public static final BlockEntry<VerticalSlabBlock> MARTIAN_STEEL_SHEETMETAL_VERTICAL_SLAB = REGISTRATE
            .block("martian_steel_sheetmetal_vertical_slab", VerticalSlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(5f, 15f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleVerticalSlab())
            .loot(NorthstarDataGenHelper.verticalSlabLoot())
            .recipe(NorthstarDataGenHelper.verticalSlabRecipe(MARTIAN_STEEL_SHEETMETAL))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MARTIAN_STEEL_PLATING = REGISTRATE
            .block("martian_steel_plating", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(5f, 15f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.TIER_2_HEAT_RESISTANCE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.platingRecipe(NorthstarItemTags.COMMON_INGOTS_MARTIAN_STEEL))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MARTIAN_STEEL_LARGE_PLATING = REGISTRATE
            .block("martian_steel_large_plating", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(5f, 15f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> MARTIAN_STEEL_PLATING_SLAB = REGISTRATE
            .block("martian_steel_plating_slab", SlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(5f, 15f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleSlab())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(MARTIAN_STEEL_PLATING))
            .simpleItem()
            .register();

    public static final BlockEntry<VerticalSlabBlock> MARTIAN_STEEL_PLATING_VERTICAL_SLAB = REGISTRATE
            .block("martian_steel_plating_vertical_slab", VerticalSlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(5f, 15f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleVerticalSlab())
            .loot(NorthstarDataGenHelper.verticalSlabLoot())
            .recipe(NorthstarDataGenHelper.verticalSlabRecipe(MARTIAN_STEEL_PLATING))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> MARTIAN_STEEL_PLATING_STAIRS = REGISTRATE
            .block("martian_steel_plating_stairs", p -> new StairBlock(MARTIAN_STEEL_PLATING.get().defaultBlockState(), p))
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(5f, 15f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.stairRecipe(MARTIAN_STEEL_PLATING))
            .simpleItem()
            .register();

    public static final BlockEntry<RotatedPillarBlock> MARTIAN_STEEL_PILLAR = REGISTRATE
            .block("martian_steel_pillar", RotatedPillarBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(5f, 15f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.TIER_2_HEAT_RESISTANCE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.pillarRecipe(NorthstarItemTags.COMMON_INGOTS_MARTIAN_STEEL))
            .simpleItem()
            .register();

    public static final BlockEntry<GrateBlock> MARTIAN_STEEL_GRATE = REGISTRATE
            .block("martian_steel_grate", GrateBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 12f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.AIR_PASSES_THROUGH.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.grateRecipe(NorthstarItemTags.COMMON_SHEETS_MARTIAN_STEEL))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MARTIAN_STEEL_LAMP = REGISTRATE
            .block("martian_steel_lamp", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .lightLevel(pState -> 15)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MARTIAN_STEEL_BLUE_LAMP = REGISTRATE
            .block("martian_steel_blue_lamp", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .lightLevel(pState -> 15)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.TIER_2_HEAT_RESISTANCE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    // endregion
    // region Titanium

    public static final BlockEntry<Block> TITANIUM_BLOCK = REGISTRATE.block("titanium_block", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(10f, 15f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> TITANIUM_SHEETMETAL = REGISTRATE
            .block("titanium_sheetmetal", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.TIER_1_HEAT_RESISTANCE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.platingRecipe(NorthstarItemTags.COMMON_SHEETS_TITANIUM))
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> TITANIUM_SHEETMETAL_SLAB = REGISTRATE
            .block("titanium_sheetmetal_slab", SlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleSlab())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(TITANIUM_SHEETMETAL))
            .simpleItem()
            .register();

    public static final BlockEntry<VerticalSlabBlock> TITANIUM_SHEETMETAL_VERTICAL_SLAB = REGISTRATE
            .block("titanium_sheetmetal_vertical_slab", VerticalSlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleVerticalSlab())
            .loot(NorthstarDataGenHelper.verticalSlabLoot())
            .recipe(NorthstarDataGenHelper.verticalSlabRecipe(TITANIUM_SHEETMETAL))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> TITANIUM_PLATING = REGISTRATE
            .block("titanium_plating", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.TIER_1_HEAT_RESISTANCE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.platingRecipe(NorthstarItemTags.COMMON_INGOTS_TITANIUM))
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> TITANIUM_PLATING_SLAB = REGISTRATE
            .block("titanium_plating_slab", SlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleSlab())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(TITANIUM_PLATING))
            .simpleItem()
            .register();

    public static final BlockEntry<VerticalSlabBlock> TITANIUM_PLATING_VERTICAL_SLAB = REGISTRATE
            .block("titanium_plating_vertical_slab", VerticalSlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleVerticalSlab())
            .loot(NorthstarDataGenHelper.verticalSlabLoot())
            .recipe(NorthstarDataGenHelper.verticalSlabRecipe(TITANIUM_PLATING))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> TITANIUM_PLATING_STAIRS = REGISTRATE
            .block("titanium_plating_stairs", p -> new StairBlock(TITANIUM_PLATING.get().defaultBlockState(), p))
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.stairRecipe(TITANIUM_PLATING))
            .simpleItem()
            .register();

    public static final BlockEntry<RotatedPillarBlock> TITANIUM_PILLAR = REGISTRATE
            .block("titanium_pillar", RotatedPillarBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.TIER_1_HEAT_RESISTANCE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.pillarRecipe(NorthstarItemTags.COMMON_INGOTS_TITANIUM))
            .simpleItem()
            .register();

    public static final BlockEntry<GrateBlock> TITANIUM_GRATE = REGISTRATE
            .block("titanium_grate", GrateBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 8f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.AIR_PASSES_THROUGH.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.grateRecipe(NorthstarItemTags.COMMON_SHEETS_TITANIUM))
            .simpleItem()
            .register();

    // endregion
    // region Tungsten

    public static final BlockEntry<Block> TUNGSTEN_BLOCK = REGISTRATE
            .block("tungsten_block", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(30f, 16f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.SUPER_HEAVY_BLOCKS.tag)
            .tag(NorthstarBlockTags.TIER_3_HEAT_RESISTANCE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> TUNGSTEN_SHEETMETAL = REGISTRATE
            .block("tungsten_sheetmetal", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(6f, 16f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.HEAVY_BLOCKS.tag)
            .tag(NorthstarBlockTags.TIER_3_HEAT_RESISTANCE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> TUNGSTEN_SHEETMETAL_SLAB = REGISTRATE
            .block("tungsten_sheetmetal_slab", SlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(6f, 16f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleSlab())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(TUNGSTEN_SHEETMETAL))
            .simpleItem()
            .register();

    public static final BlockEntry<VerticalSlabBlock> TUNGSTEN_SHEETMETAL_VERTICAL_SLAB = REGISTRATE
            .block("tungsten_sheetmetal_vertical_slab", VerticalSlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(6f, 16f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleVerticalSlab())
            .loot(NorthstarDataGenHelper.verticalSlabLoot())
            .recipe(NorthstarDataGenHelper.verticalSlabRecipe(TUNGSTEN_SHEETMETAL))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> TUNGSTEN_PLATING = REGISTRATE
            .block("tungsten_plating", Block::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(6f, 16f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.HEAVY_BLOCKS.tag)
            .tag(NorthstarBlockTags.TIER_3_HEAT_RESISTANCE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> TUNGSTEN_PLATING_SLAB = REGISTRATE
            .block("tungsten_plating_slab", SlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(6f, 16f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleSlab())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(TUNGSTEN_PLATING))
            .simpleItem()
            .register();

    public static final BlockEntry<VerticalSlabBlock> TUNGSTEN_PLATING_VERTICAL_SLAB = REGISTRATE
            .block("tungsten_plating_vertical_slab", VerticalSlabBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(6f, 16f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.simpleVerticalSlab())
            .loot(NorthstarDataGenHelper.verticalSlabLoot())
            .recipe(NorthstarDataGenHelper.verticalSlabRecipe(TUNGSTEN_PLATING))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> TUNGSTEN_PLATING_STAIRS = REGISTRATE
            .block("tungsten_plating_stairs", p -> new StairBlock(TUNGSTEN_PLATING.get().defaultBlockState(), p))
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(6f, 16f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.stairRecipe(TUNGSTEN_PLATING))
            .simpleItem()
            .register();

    public static final BlockEntry<RotatedPillarBlock> TUNGSTEN_PILLAR = REGISTRATE
            .block("tungsten_pillar", RotatedPillarBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(6f, 16f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.HEAVY_BLOCKS.tag)
            .tag(NorthstarBlockTags.TIER_3_HEAT_RESISTANCE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.pillarRecipe(NorthstarItemTags.COMMON_INGOTS_TUNGSTEN))
            .simpleItem()
            .register();

    public static final BlockEntry<GrateBlock> TUNGSTEN_GRATE = REGISTRATE
            .block("tungsten_grate", GrateBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(5f, 16f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.AIR_PASSES_THROUGH.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.grateRecipe(NorthstarItemTags.COMMON_SHEETS_TUNGSTEN))
            .simpleItem()
            .register();

    // endregion
    // region Mars

    // TODO: color
    public static final BlockEntry<ColoredFallingBlock> MARS_SAND = REGISTRATE
            .block("mars_sand", p -> new ColoredFallingBlock(new ColorRGBA(0), p))
            .initialProperties(() -> SAND)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.GRAVEL)
                    .strength(0.5f, 1.6f))
            .tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .tag(BlockTags.SAND)
            .tag(NorthstarBlockTags.ARGYRE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.BASE_SURFACE_BLOCKS_MARS.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    // TODO: color
    public static final BlockEntry<ColoredFallingBlock> MARS_GRAVEL = REGISTRATE
            .block("mars_gravel", p -> new ColoredFallingBlock(new ColorRGBA(0), p))
            .initialProperties(() -> SAND)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.GRAVEL)
                    .strength(0.65f, 2.0f))
            .tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            // TODO: Flint drops?
            .simpleItem()
            .register();

    public static final BlockEntry<MarsSoilBlock> MARS_SOIL = REGISTRATE
            .block("mars_soil", MarsSoilBlock::new)
            .initialProperties(() -> DIRT)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.GRAVEL)
                    .strength(0.5f, 8f))
            .tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .tag(NorthstarBlockTags.ARGYRE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.BASE_SURFACE_BLOCKS_MARS.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<MartianGrassBlock> MARTIAN_GRASS = REGISTRATE
            .block("martian_grass", MartianGrassBlock::new)
            .initialProperties(() -> DIRT)
            .properties(p -> p.mapColor(MapColor.COLOR_PURPLE)
                    .sound(SoundType.GRASS)
                    .strength(0.65f, 8f)
                    .randomTicks())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<MartianTallGrassBlock> MARTIAN_TALL_GRASS = REGISTRATE
            .block("martian_tall_grass", MartianTallGrassBlock::new)
            /*.initialProperties(SharedProperties::REPLACEABLE_PLANT)*/
            .properties(p -> p.mapColor(MapColor.COLOR_PURPLE)
                    .sound(SoundType.GRASS)
                    .noCollission()
                    .instabreak()
                    .randomTicks()
                    .offsetType(BlockBehaviour.OffsetType.XYZ))
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, RegistrateBlockLootTables.createShearsOnlyDrop(b)))
            .simpleItem()
            .register();

    public static final BlockEntry<MarsFarmlandBlock> MARS_FARMLAND = REGISTRATE
            .block("mars_farmland", MarsFarmlandBlock::new)
            .initialProperties(() -> DIRT)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .randomTicks()
                    .sound(SoundType.GRAVEL)
                    .strength(0.5f, 8f))
            .tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.dropOther(b, MARS_SOIL))
            .simpleItem()
            .register();

    public static final BlockEntry<MarsWormNestBlock> MARS_WORM_NEST = REGISTRATE
            .block("mars_worm_nest", MarsWormNestBlock::new)
            /*.initialProperties(SharedProperties::LEAVES)*/
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .randomTicks()
                    .sound(SoundType.GRASS)
                    .strength(0.2f, 0.2f))
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
            .lang("Mars Echo Worm Nest")
            .item()
            .model((c, p) -> p.withExistingParent(p.name(c), p.modLoc("block/worm_nest_full")))
            .build()
            .register();

    public static final BlockEntry<MarsRootBlock> MARS_ROOTS = REGISTRATE
            .block("mars_roots", MarsRootBlock::new)
            /*.initialProperties(SharedProperties::LEAVES)*/
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_GRAY)
                    .sound(SoundType.VINE)
                    .noOcclusion()
                    .noCollission()
                    .strength(0.2f)
                    .randomTicks()
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .tag(NorthstarBlockTags.ARGYRE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createMultifaceBlockDrops(b, RegistrateBlockLootTables.HAS_SHEARS)))
            .simpleItem()
            .register();

    public static final BlockEntry<MarsRootBlock> GLOWING_MARS_ROOTS = REGISTRATE
            .block("glowing_mars_roots", MarsRootBlock::new)
            /*.initialProperties(SharedProperties::LEAVES)*/
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_GRAY)
                    .lightLevel(pState -> 10)
                    .sound(SoundType.VINE)
                    .noOcclusion()
                    .noCollission()
                    .strength(0.2f)
                    .randomTicks()
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .tag(NorthstarBlockTags.ARGYRE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createMultifaceBlockDrops(b, c.hasSilkTouch())))
            .item()
            .model(NorthstarDataGenHelper.itemGeneratedBlock())
            .build()
            .register();

    public static final BlockEntry<MartianStrawberryBushBlock> MARTIAN_STRAWBERRY_BUSH = REGISTRATE
            .block("martian_strawberry_bush", MartianStrawberryBushBlock::new)
            /*.initialProperties(SharedProperties::PLANT)*/
            .properties(p -> p.sound(SoundType.GRASS)
                    .randomTicks()
                    .instabreak()
                    .noCollission())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.applyExplosionDecay(b, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .add(LootItem.lootTableItem(NorthstarItems.MARTIAN_STRAWBERRY)))
                    .withPool(LootPool.lootPool()
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)
                                    .setProperties(StatePropertiesPredicate.Builder.properties()
                                            .hasProperty(MartianStrawberryBushBlock.AGE, 5)))
                            .add(LootItem.lootTableItem(NorthstarItems.MARTIAN_STRAWBERRY))
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                            .apply(ApplyBonusCount.addUniformBonusCount(c.getRegistries().holderOrThrow(Enchantments.FORTUNE)))))))
            .item()
            .model((c, p) -> p.generated(c::get, p.modLoc("block/strawberry_bush_stage5")))
            .build()
            .register();

    public static final BlockEntry<MarsTulipBlock> MARS_TULIP = REGISTRATE
            .block("mars_tulip", MarsTulipBlock::new)
            /*.initialProperties(SharedProperties::PLANT)*/
            .properties(p -> p.sound(SoundType.GRASS)
                    .mapColor(MapColor.COLOR_ORANGE)
                    .randomTicks()
                    .instabreak()
                    .noCollission()
                    .offsetType(BlockBehaviour.OffsetType.XZ))
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot(NorthstarDataGenHelper.cropLoot(NorthstarItems.MARS_TULIP_FLOWER, NorthstarItems.MARS_TULIP_SEEDS, 2))
            .simpleItem()
            .register();

    public static final BlockEntry<MarsPalmBlock> MARS_PALM = REGISTRATE
            .block("mars_palm", MarsPalmBlock::new)
            /*.initialProperties(SharedProperties::PLANT)*/
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.GRASS)
                    .randomTicks()
                    .instabreak()
                    .noCollission()
                    .offsetType(BlockBehaviour.OffsetType.XZ))
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot(NorthstarDataGenHelper.cropLoot(NorthstarItems.MARS_PALM_FLOWER, NorthstarItems.MARS_PALM_SEEDS, 2))
            .simpleItem()
            .register();

    public static final BlockEntry<MarsSproutBlock> MARS_SPROUT = REGISTRATE
            .block("mars_sprout", MarsSproutBlock::new)
            /*.initialProperties(SharedProperties::PLANT)*/
            .properties(p -> p.sound(SoundType.GRASS)
                    .mapColor(MapColor.COLOR_PINK)
                    .randomTicks()
                    .instabreak()
                    .noCollission()
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .lightLevel(pState -> 7))
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot(NorthstarDataGenHelper.cropLoot(NorthstarItems.MARS_SPROUT_FLOWER, NorthstarItems.MARS_SPROUT_SEEDS, 2))
            .simpleItem()
            .register();

    public static final BlockEntry<MartianTallFlowerBlock> MARS_SPROUT_BIG = REGISTRATE
            .block("mars_sprout_big", MartianTallFlowerBlock::new)
            /*.initialProperties(SharedProperties::PLANT)*/
            .properties(p -> p.sound(SoundType.GRASS)
                    .randomTicks()
                    .instabreak()
                    .noCollission()
                    .lightLevel(pState -> 14))
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createSinglePropConditionTable(b, MartianTallFlowerBlock.HALF, DoubleBlockHalf.LOWER)))
            .lang("Tall Mars Sprout")
            .item()
            .model(NorthstarDataGenHelper.itemGeneratedBlock("_top"))
            .build()
            .register();

    public static final BlockEntry<PointedCrimsiteBlock> POINTED_CRIMSITE = REGISTRATE
            .block("pointed_crimsite", PointedCrimsiteBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_RED)
                    .sound(SoundType.DEEPSLATE)
                    .strength(3.5f, 12f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .dynamicShape()
                    .offsetType(BlockBehaviour.OffsetType.XZ))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model(NorthstarDataGenHelper.itemGeneratedBlock("_up_tip"))
            .build()
            .register();

    public static final BlockEntry<Block> MARS_STONE = REGISTRATE
            .block("mars_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(3.5f, 8f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.ARGYRE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.BASE_STONE_MARS.tag)
            .tag(NorthstarBlockTags.MARS_STONE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MARS_DEEP_STONE = REGISTRATE
            .block("mars_deep_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(4.5f, 8f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.ARGYRE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.BASE_STONE_MARS.tag)
            .tag(NorthstarBlockTags.MARS_DEEP_STONE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .lang("Deep Mars Stone")
            .simpleItem()
            .register();

    //mars deco blocks
    public static final BlockEntry<Block> MARS_STONE_BRICKS = REGISTRATE
            .block("mars_stone_bricks", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> MARS_STONE_BRICK_SLAB = REGISTRATE
            .block("mars_stone_brick_slab", SlabBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(MARS_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<VerticalSlabBlock> MARS_STONE_BRICK_SLAB_VERTICAL = REGISTRATE
            .block("mars_stone_brick_slab_vertical", VerticalSlabBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .lang("Mars Stone Brick Vertical Slab")
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.verticalSlabRecipe(MARS_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> MARS_STONE_BRICK_STAIRS = REGISTRATE
            .block("mars_stone_brick_stairs", p -> new StairBlock(STONE.defaultBlockState(), p))
            /*.initalProperties(STONE)*/
            .properties(p -> p.sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.stairRecipe(MARS_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<WallBlock> MARS_STONE_BRICK_WALL = REGISTRATE
            .block("mars_stone_brick_wall", WallBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(BlockTags.WALLS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.wallRecipe(MARS_STONE_BRICKS))
            .item()
            .model((c, p) -> p.blockWithInventoryModel(c::get))
            .build()
            .register();

    public static final BlockEntry<RotatedPillarBlock> MARS_STONE_PILLAR = REGISTRATE
            .block("mars_stone_pillar", RotatedPillarBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.pillarRecipe(MARS_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> CHISELED_MARS_STONE_BRICKS = REGISTRATE
            .block("chiseled_mars_stone", Block::new)
            .lang("Chieseled Mars Stone Bricks")
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.chiseledRecipe(MARS_STONE_BRICK_SLAB))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> POLISHED_MARS_STONE = REGISTRATE
            .block("polished_mars_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MARS_STONE_LAMP = REGISTRATE
            .block("mars_stone_lamp", Block::new)
            /*.initialProperties(SharedProperties::DECORATION)*/
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .lightLevel(pState -> 15)
                    .sound(SoundType.GLASS)
                    .strength(3f, 6.5f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    //mars ores
    public static final BlockEntry<Block> MARS_TITANIUM_ORE = REGISTRATE
            .block("mars_titanium_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_TITANIUM.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_TITANIUM.get())))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MARS_IRON_ORE = REGISTRATE
            .block("mars_iron_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_IRON.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_IRON)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_IRON.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MARS_COPPER_ORE = REGISTRATE
            .block("mars_copper_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_COPPER.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createCopperOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_COPPER.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MARS_GOLD_ORE = REGISTRATE
            .block("mars_gold_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GOLD.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_GOLD)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GOLD.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MARS_DIAMOND_ORE = REGISTRATE
            .block("mars_diamond_ore", p -> new DropExperienceBlock(UniformInt.of(3, 7), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_DIAMOND.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.DIAMOND)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_DIAMOND.tag)
            .build()
            .register();

    public static final BlockEntry<RedStoneOreBlock> MARS_REDSTONE_ORE = REGISTRATE
            .block("mars_redstone_ore", RedStoneOreBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .lightLevel(litBlockEmission(9)))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_REDSTONE.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createRedstoneOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_REDSTONE.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MARS_QUARTZ_ORE = REGISTRATE
            .block("mars_quartz_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_QUARTZ.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.QUARTZ)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_QUARTZ.tag)
            .build()
            .register();

    //mars deep ores
    public static final BlockEntry<Block> MARS_DEEP_TITANIUM_ORE = REGISTRATE
            .block("mars_deep_titanium_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_TITANIUM.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_TITANIUM.get())))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MARS_DEEP_IRON_ORE = REGISTRATE
            .block("mars_deep_iron_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_IRON.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_IRON)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_IRON.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MARS_DEEP_COPPER_ORE = REGISTRATE
            .block("mars_deep_copper_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_COPPER.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createCopperOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_COPPER.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MARS_DEEP_GOLD_ORE = REGISTRATE
            .block("mars_deep_gold_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GOLD.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_GOLD)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GOLD.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MARS_DEEP_DIAMOND_ORE = REGISTRATE
            .block("mars_deep_diamond_ore", p -> new DropExperienceBlock(UniformInt.of(3, 7), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(7f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_DIAMOND.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.DIAMOND)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_DIAMOND.tag)
            .build()
            .register();

    public static final BlockEntry<RedStoneOreBlock> MARS_DEEP_REDSTONE_ORE = REGISTRATE
            .block("mars_deep_redstone_ore", RedStoneOreBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .lightLevel(litBlockEmission(9)))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_REDSTONE.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createRedstoneOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_REDSTONE.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MARS_DEEP_QUARTZ_ORE = REGISTRATE
            .block("mars_deep_quartz_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_QUARTZ.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.QUARTZ)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_QUARTZ.tag)
            .build()
            .register();

    public static final BlockEntry<Block> VOLCANIC_ASH = REGISTRATE
            .block("volcanic_ash", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.TUFF)
                    .strength(0.4f, 2f))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.BASE_STONE_MARS.tag)
            .tag(NorthstarBlockTags.MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> VOLCANIC_ROCK = REGISTRATE
            .block("volcanic_rock", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_BROWN)
                    .sound(SoundType.TUFF)
                    .strength(2f, 4f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    // endregion
    // region Wilter wood

    public static final BlockEntry<RotatedPillarBlock> STRIPPED_WILTER_LOG = REGISTRATE
            .block("stripped_wilter_log", RotatedPillarBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.WOOD)
                    .strength(2f))
            .transform(axeOnly())
            .tag(BlockTags.LOGS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.WILTER_LOGS.tag)
            .build()
            .register();

    public static final BlockEntry<LogBlock> WILTER_LOG = REGISTRATE
            .block("wilter_log", p -> new LogBlock(p, STRIPPED_WILTER_LOG.get()))
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.WOOD)
                    .strength(2f))
            .transform(axeOnly())
            .tag(BlockTags.LOGS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.WILTER_LOGS.tag)
            .build()
            .register();

    public static final BlockEntry<Block> WILTER_PLANKS = REGISTRATE
            .block("wilter_planks", Block::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.WOOD)
                    .strength(2f))
            .transform(axeOnly())
            .tag(BlockTags.PLANKS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.plankRecipe(NorthstarItemTags.WILTER_LOGS))
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> WILTER_SLAB = REGISTRATE
            .block("wilter_slab", SlabBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.WOOD)
                    .strength(2f, 3f))
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(WILTER_PLANKS))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> WILTER_STAIRS = REGISTRATE
            .block("wilter_stairs", p -> new StairBlock(OAK_PLANKS.defaultBlockState(), p))
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.WOOD)
                    .strength(2f, 3f))
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.stairRecipe(WILTER_PLANKS))
            .simpleItem()
            .register();

    public static final BlockEntry<SaplingBlock> WILTER_SAPLING = REGISTRATE
            .block("wilter_sapling", p -> new SaplingBlock(NorthstarTreeGrowers.WILTER, p))
            /*BlockBehaviour.Properties.of(Material.PLANT)*/
            .properties(p -> p.sound(SoundType.GRASS)
                    .randomTicks()
                    .instabreak()
                    .noCollission())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<LeavesBlock> WILTER_LEAVES = REGISTRATE
            .block("wilter_leaves", LeavesBlock::new)
            /*.initialProperties(SharedProperties::LEAVES)*/
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.GRASS)
                    .noOcclusion()
                    .strength(0.5f)
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createLeavesDrops(b, WILTER_SAPLING.get(), 1f / 20f, 1f / 16f, 1f / 12f, 1f / 10f)))
            .simpleItem()
            .register();

    // endregion
    // region Argyre wood

    public static final BlockEntry<RotatedPillarBlock> STRIPPED_ARGYRE_LOG = REGISTRATE
            .block("stripped_argyre_log", RotatedPillarBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_RED)
                    .sound(SoundType.WOOD)
                    .strength(2f))
            .transform(axeOnly())
            .tag(BlockTags.LOGS, BlockTags.MINEABLE_WITH_AXE)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.ARGYRE_LOGS.tag)
            .build()
            .register();

    public static final BlockEntry<LogBlock> ARGYRE_LOG = REGISTRATE
            .block("argyre_log", p -> new LogBlock(p, STRIPPED_ARGYRE_LOG.get()))
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_RED)
                    .sound(SoundType.WOOD)
                    .strength(2f))
            .transform(axeOnly())
            .tag(BlockTags.LOGS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.ARGYRE_LOGS.tag)
            .build()
            .register();

    public static final BlockEntry<SaplingBlock> ARGYRE_SAPLING = REGISTRATE
            .block("argyre_sapling", p -> new SaplingBlock(NorthstarTreeGrowers.ARGYRE_SAPLING, p))
            /*BlockBehaviour.Properties.of(Material.PLANT)*/
            .properties(p -> p.sound(SoundType.GRASS)
                    .randomTicks()
                    .instabreak()
                    .noCollission())
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model(NorthstarDataGenHelper.itemGeneratedBlock())
            .build()
            .register();

    public static final BlockEntry<LeavesBlock> ARGYRE_LEAVES = REGISTRATE
            .block("argyre_leaves", LeavesBlock::new)
            /*.initialProperties(SharedProperties::LEAVES)*/
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.GRASS)
                    .noOcclusion()
                    .strength(0.5f)
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createLeavesDrops(b, ARGYRE_SAPLING.get(), 1f / 20f, 1f / 16f, 1f / 12f, 1f / 10f)))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> ARGYRE_PLANKS = REGISTRATE
            .block("argyre_planks", Block::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_RED)
                    .sound(SoundType.WOOD)
                    .strength(2f, 3f))
            .transform(axeOnly())
            .tag(BlockTags.PLANKS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.plankRecipe(NorthstarItemTags.ARGYRE_LOGS))
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> ARGYRE_SLAB = REGISTRATE
            .block("argyre_slab", SlabBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_RED)
                    .sound(SoundType.WOOD)
                    .strength(2f, 3f))
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(ARGYRE_PLANKS))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> ARGYRE_STAIRS = REGISTRATE
            .block("argyre_stairs", p -> new StairBlock(OAK_PLANKS.defaultBlockState(), p))
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_RED)
                    .sound(SoundType.WOOD)
                    .strength(2f, 3f))
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.stairRecipe(ARGYRE_PLANKS))
            .simpleItem()
            .register();

    // endregion
    // region Coiler wood

    public static final BlockEntry<RotatedPillarBlock> STRIPPED_COILER_LOG = REGISTRATE
            .block("stripped_coiler_log", RotatedPillarBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .sound(SoundType.WOOD)
                    .strength(2f))
            .transform(axeOnly())
            .tag(BlockTags.LOGS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.COILER_LOGS.tag)
            .build()
            .register();

    public static final BlockEntry<LogBlock> COILER_LOG = REGISTRATE
            .block("coiler_log", p -> new LogBlock(p, STRIPPED_COILER_LOG.get()))
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .sound(SoundType.WOOD)
                    .strength(2f))
            .transform(axeOnly())
            .tag(BlockTags.LOGS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.COILER_LOGS.tag)
            .build()
            .register();

    public static final BlockEntry<Block> COILER_PLANKS = REGISTRATE
            .block("coiler_planks", Block::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .sound(SoundType.WOOD)
                    .strength(2f, 3f))
            .transform(axeOnly())
            .tag(BlockTags.PLANKS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.plankRecipe(NorthstarItemTags.COILER_LOGS))
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> COILER_SLAB = REGISTRATE
            .block("coiler_slab", SlabBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .sound(SoundType.WOOD)
                    .strength(2f, 3f))
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> COILER_STAIRS = REGISTRATE
            .block("coiler_stairs", p -> new StairBlock(OAK_PLANKS.defaultBlockState(), p))
            /*/BlockBehaviour.Properties.of(Material.WOOD*/
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .sound(SoundType.WOOD)
                    .strength(2f, 3f))
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<SaplingBlock> COILER_SAPLING = REGISTRATE
            .block("coiler_sapling", p -> new SaplingBlock(NorthstarTreeGrowers.COILER, p))
            /*BlockBehaviour.Properties.of(Material.PLANT)*/
            .properties(p -> p.sound(SoundType.GRASS)
                    .randomTicks()
                    .instabreak()
                    .noCollission())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model(NorthstarDataGenHelper.itemGeneratedBlock())
            .build()
            .register();

    public static final BlockEntry<LeavesBlock> COILER_LEAVES = REGISTRATE
            .block("coiler_leaves", LeavesBlock::new)
            /*.initialProperties(SharedProperties::LEAVES)*/
            .properties(p -> p.mapColor(MapColor.COLOR_MAGENTA)
                    .sound(SoundType.GRASS)
                    .noOcclusion()
                    .strength(0.5f)
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .tag(BlockTags.MINEABLE_WITH_HOE)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createLeavesDrops(b, COILER_SAPLING.get(), 1f / 20f, 1f / 16f, 1f / 12f, 1f / 10f)))
            .simpleItem()
            .register();

    public static final BlockEntry<VineBlock> COILER_VINES = REGISTRATE
            .block("coiler_vines", VineBlock::new)
            /*.initialProperties(SharedProperties::LEAVES)*/
            .properties(p -> p.mapColor(MapColor.COLOR_MAGENTA)
                    .sound(SoundType.VINE)
                    .noOcclusion()
                    .noCollission()
                    .randomTicks()
                    .strength(0.2f)
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .tag(BlockTags.CLIMBABLE)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, RegistrateBlockLootTables.createShearsOnlyDrop(b)))
            .item()
            .model(NorthstarDataGenHelper.itemGeneratedBlock())
            .build()
            .register();

    // endregion
    // region Moon

    // TODO: color
    public static final BlockEntry<ColoredFallingBlock> MOON_SAND = REGISTRATE
            .block("moon_sand", p -> new ColoredFallingBlock(new ColorRGBA(0), p))
            .initialProperties(() -> SAND)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.SAND)
                    .strength(0.5f, 8.0f))
            .tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .tag(BlockTags.SAND)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.MOON_SURFACE_REPLACEABLE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MOON_STONE = REGISTRATE
            .block("moon_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(3.5f, 8f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.BASE_STONE_MOON.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.MOON_STONE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.MOON_SURFACE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MOON_DEEP_STONE = REGISTRATE
            .block("moon_deep_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(4.5f, 8f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.BASE_STONE_MOON.tag)
            .tag(NorthstarBlockTags.MOON_DEEP_STONE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .lang("Deep Moon Stone")
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<FrostBlock> FROST = REGISTRATE
            .block("frost", FrostBlock::new)
            .initialProperties(() -> ICE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .sound(SoundType.CALCITE)
                    .friction(0.989F)
                    .noOcclusion()
                    .noCollission()
                    .strength(0.2f)
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createMultifaceBlockDrops(b, c.hasSilkTouch())))
            .simpleItem()
            .register();

    //moon deco stuff ayy
    public static final BlockEntry<Block> MOON_STONE_BRICKS = REGISTRATE
            .block("moon_stone_bricks", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> MOON_STONE_BRICK_SLAB = REGISTRATE
            .block("moon_stone_brick_slab", SlabBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(MOON_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<VerticalSlabBlock> MOON_STONE_BRICK_SLAB_VERTICAL = REGISTRATE
            .block("moon_stone_brick_slab_vertical", VerticalSlabBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .lang("Moon Stone Brick Vertical Slab")
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.verticalSlabRecipe(MOON_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> MOON_STONE_BRICK_STAIRS = REGISTRATE
            .block("moon_stone_brick_stairs", p -> new StairBlock(STONE.defaultBlockState(), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.stairRecipe(MOON_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<WallBlock> MOON_STONE_BRICK_WALL = REGISTRATE
            .block("moon_stone_brick_wall", WallBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(BlockTags.WALLS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.wallRecipe(MOON_STONE_BRICKS))
            .item()
            .model((c, p) -> p.blockWithInventoryModel(c::get))
            .build()
            .register();

    public static final BlockEntry<RotatedPillarBlock> MOON_STONE_PILLAR = REGISTRATE
            .block("moon_stone_pillar", RotatedPillarBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.pillarRecipe(MOON_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MOON_STONE_LAMP = REGISTRATE
            .block("moon_stone_lamp", Block::new)
            /*.initialProperties(SharedProperties::DECORATION)*/
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .lightLevel(pState -> 15)
                    .sound(SoundType.GLASS)
                    .strength(3f, 6.5f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> CHISELED_MOON_STONE_BRICKS = REGISTRATE
            .block("chiseled_moon_stone", Block::new)
            .lang("Chiseled Moon Stone Bricks")
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.chiseledRecipe(MOON_STONE_BRICK_SLAB))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> POLISHED_MOON_STONE = REGISTRATE
            .block("polished_moon_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    //moon ores
    public static final BlockEntry<Block> MOON_TITANIUM_ORE = REGISTRATE
            .block("moon_titanium_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_TITANIUM.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_TITANIUM.get())))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MOON_IRON_ORE = REGISTRATE
            .block("moon_iron_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_IRON.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_IRON)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_IRON.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MOON_COPPER_ORE = REGISTRATE
            .block("moon_copper_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_COPPER.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createCopperOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_COPPER.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MOON_GOLD_ORE = REGISTRATE
            .block("moon_gold_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GOLD.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_GOLD)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GOLD.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MOON_DIAMOND_ORE = REGISTRATE
            .block("moon_diamond_ore", p -> new DropExperienceBlock(UniformInt.of(3, 7), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_DIAMOND.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.DIAMOND)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_DIAMOND.tag)
            .build()
            .register();

    public static final BlockEntry<RedStoneOreBlock> MOON_REDSTONE_ORE = REGISTRATE
            .block("moon_redstone_ore", RedStoneOreBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .lightLevel(litBlockEmission(9)))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_REDSTONE.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createRedstoneOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_REDSTONE.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MOON_LAPIS_ORE = REGISTRATE
            .block("moon_lapis_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_LAPIS.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_LAPIS.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MOON_ZINC_ORE = REGISTRATE
            .block("moon_zinc_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_ZINC.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_ZINC.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MOON_GLOWSTONE_ORE = REGISTRATE
            .block("moon_glowstone_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(pState -> 15))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GLOWSTONE.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_GLOWSTONE_ORE.get())))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GLOWSTONE.tag)
            .build()
            .register();

    //moon deep ores
    public static final BlockEntry<Block> MOON_DEEP_TITANIUM_ORE = REGISTRATE
            .block("moon_deep_titanium_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_TITANIUM.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_TITANIUM.get())))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MOON_DEEP_IRON_ORE = REGISTRATE
            .block("moon_deep_iron_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_IRON.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_IRON)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_IRON.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MOON_DEEP_COPPER_ORE = REGISTRATE
            .block("moon_deep_copper_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_COPPER.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createCopperOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_COPPER.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MOON_DEEP_GOLD_ORE = REGISTRATE
            .block("moon_deep_gold_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GOLD.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_GOLD)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GOLD.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MOON_DEEP_DIAMOND_ORE = REGISTRATE
            .block("moon_deep_diamond_ore", p -> new DropExperienceBlock(UniformInt.of(3, 7), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(7f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_DIAMOND.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.DIAMOND)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_DIAMOND.tag)
            .build()
            .register();

    public static final BlockEntry<RedStoneOreBlock> MOON_DEEP_REDSTONE_ORE = REGISTRATE
            .block("moon_deep_redstone_ore", RedStoneOreBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .lightLevel(litBlockEmission(9)))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_REDSTONE.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createRedstoneOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_REDSTONE.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MOON_DEEP_LAPIS_ORE = REGISTRATE
            .block("moon_deep_lapis_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_LAPIS.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_LAPIS.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MOON_DEEP_ZINC_ORE = REGISTRATE
            .block("moon_deep_zinc_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_ZINC.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_ZINC.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MOON_DEEP_GLOWSTONE_ORE = REGISTRATE
            .block("moon_deep_glowstone_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(pState -> 15))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GLOWSTONE.tag)
            .tag(NorthstarBlockTags.MOON_BLOCKS.tag)
            .tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_GLOWSTONE_ORE.get())))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GLOWSTONE.tag)
            .build()
            .register();

    public static final BlockEntry<Block> LUNAR_SAPPHIRE_BLOCK = REGISTRATE
            .block("lunar_sapphire_block", Block::new)
            .initialProperties(() -> AMETHYST_BLOCK)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.AMETHYST)
                    .strength(2f, 5f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<ClusterBlock> LUNAR_SAPPHIRE_CLUSTER = REGISTRATE
            .block("lunar_sapphire_cluster", p -> new ClusterBlock(7, 3, p))
            .initialProperties(() -> AMETHYST_BLOCK)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.AMETHYST)
                    .strength(2f, 5f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
            .simpleItem()
            .register();

    public static final BlockEntry<ClusterBlock> SMALL_LUNAR_SAPPHIRE_BUD = REGISTRATE
            .block("small_lunar_sapphire_bud", p -> new ClusterBlock(3, 4, p))
            .initialProperties(() -> AMETHYST_BLOCK)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.AMETHYST)
                    .strength(1.5f, 5f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
            .simpleItem()
            .register();

    public static final BlockEntry<ClusterBlock> MEDIUM_LUNAR_SAPPHIRE_BUD = REGISTRATE
            .block("medium_lunar_sapphire_bud", p -> new ClusterBlock(4, 3, p))
            .initialProperties(() -> AMETHYST_BLOCK)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.AMETHYST)
                    .strength(1.5f, 5f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
            .simpleItem()
            .register();

    public static final BlockEntry<ClusterBlock> LARGE_LUNAR_SAPPHIRE_BUD = REGISTRATE
            .block("large_lunar_sapphire_bud", p -> new ClusterBlock(5, 3, p))
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.AMETHYST)
                    .strength(1.5f, 5f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
            .simpleItem()
            .register();

    public static final BlockEntry<BuddingCrystalBlock> BUDDING_LUNAR_SAPPHIRE_BLOCK = REGISTRATE
            .block("budding_lunar_sapphire_block", p -> new BuddingCrystalBlock(p, SMALL_LUNAR_SAPPHIRE_BUD.get(), MEDIUM_LUNAR_SAPPHIRE_BUD.get(), LARGE_LUNAR_SAPPHIRE_BUD.get(), LUNAR_SAPPHIRE_CLUSTER.get()))
            .initialProperties(() -> AMETHYST_BLOCK)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.AMETHYST)
                    .strength(2f, 5f)
                    .randomTicks()
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    // endregion
    // region Venus

    public static final BlockEntry<Block> VENUS_STONE = REGISTRATE
            .block("venus_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(3.5f, 8f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.BASE_STONE_VENUS.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .tag(NorthstarBlockTags.VENUS_STONE.tag)
            .tag(NorthstarBlockTags.VENUS_STONE_REPLACEABLE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> VENUS_DEEP_STONE = REGISTRATE
            .block("venus_deep_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(4.5f, 8f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .tag(NorthstarBlockTags.BASE_STONE_VENUS.tag)
            .tag(NorthstarBlockTags.VENUS_DEEP_STONE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.VENUS_STONE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    // TODO: color
    public static final BlockEntry<ColoredFallingBlock> VENUS_GRAVEL = REGISTRATE
            .block("venus_gravel", p -> new ColoredFallingBlock(new ColorRGBA(0), p))
            .initialProperties(() -> SAND)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.GRAVEL)
                    .strength(1.1f, 2f))
            .tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .tag(NorthstarBlockTags.VENUS_STONE_REPLACEABLE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            // TODO: Flint drops?
            .simpleItem()
            .register();

    public static final BlockEntry<VenusExhaustBlock> VENUS_PLUME = REGISTRATE
            .block("venus_plume", VenusExhaustBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(3.5f, 8f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<VenusMushroomBlock> SPIKE_FUNGUS = REGISTRATE
            .block("spike_fungus", p -> new VenusMushroomBlock(p, NorthstarConfiguredFeatures.SPIKE_FUNGUS, null))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.FUNGUS)
                    .strength(0f, 0.5f)
                    .noCollission()
                    .instabreak())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<VenusMushroomBlock> BLOOM_FUNGUS = REGISTRATE
            .block("bloom_fungus", p -> new VenusMushroomBlock(p, NorthstarConfiguredFeatures.BLOOM_FUNGUS, NorthstarConfiguredFeatures.ROOF_BLOOM_FUNGUS))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.FUNGUS)
                    .strength(0f, 0.5f)
                    .noCollission()
                    .instabreak()
                    .lightLevel(pState -> 7))
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model(NorthstarDataGenHelper.itemGeneratedBlock())
            .build()
            .register();

    public static final BlockEntry<VenusMushroomBlock> PLATE_FUNGUS = REGISTRATE
            .block("plate_fungus", p -> new VenusMushroomBlock(p, NorthstarConfiguredFeatures.PLATE_FUNGUS, NorthstarConfiguredFeatures.ROOF_PLATE_FUNGUS))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GREEN)
                    .sound(SoundType.FUNGUS)
                    .strength(0f, 0.5f)
                    .noCollission()
                    .instabreak())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<HugeMushroomBlock> SPIKE_FUNGUS_BLOCK = REGISTRATE
            .block("spike_fungus_block", HugeMushroomBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.FUNGUS)
                    .strength(3f, 6f)
                    .requiresCorrectToolForDrops())
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model((c, p) -> p.blockWithInventoryModel(c::get))
            .build()
            .register();

    public static final BlockEntry<HugeMushroomBlock> BLOOM_FUNGUS_BLOCK = REGISTRATE
            .block("bloom_fungus_block", HugeMushroomBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.FUNGUS)
                    .strength(3f, 6f)
                    .requiresCorrectToolForDrops())
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model((c, p) -> p.blockWithInventoryModel(c::get))
            .build()
            .register();

    public static final BlockEntry<HugeMushroomBlock> BLOOM_FUNGUS_STEM_BLOCK = REGISTRATE
            .block("bloom_fungus_stem_block", HugeMushroomBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_ORANGE)
                    .sound(SoundType.FUNGUS)
                    .strength(3f, 6f)
                    .requiresCorrectToolForDrops())
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<HugeMushroomBlock> PLATE_FUNGUS_STEM_BLOCK = REGISTRATE
            .block("plate_fungus_stem_block", HugeMushroomBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_LIGHT_GREEN)
                    .sound(SoundType.FUNGUS)
                    .strength(3f, 6f)
                    .requiresCorrectToolForDrops())
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model((c, p) -> p.blockWithInventoryModel(c::get))
            .build()
            .register();

    public static final BlockEntry<HugeMushroomBlock> PLATE_FUNGUS_CAP_BLOCK = REGISTRATE
            .block("plate_fungus_cap_block", HugeMushroomBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GREEN)
                    .sound(SoundType.FUNGUS)
                    .strength(4f, 6f)
                    .requiresCorrectToolForDrops())
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model((c, p) -> p.blockWithInventoryModel(c::get))
            .build()
            .register();

    public static final BlockEntry<TallFungusBlock> TOWER_FUNGUS = REGISTRATE
            .block("tower_fungus", p -> new TallFungusBlock(p, NorthstarConfiguredFeatures.TOWER_FUNGUS, NorthstarConfiguredFeatures.ROOF_TOWER_FUNGUS))
            /*.initialProperties(SharedProperties::PLANT)*/
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.FUNGUS)
                    .randomTicks()
                    .instabreak()
                    .noCollission())
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model(NorthstarDataGenHelper.itemGeneratedBlock("_top"))
            .build()
            .register();

    public static final BlockEntry<HugeMushroomBlock> TOWER_FUNGUS_STEM_BLOCK = REGISTRATE
            .block("tower_fungus_stem_block", HugeMushroomBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.FUNGUS)
                    .strength(3f, 6f)
                    .requiresCorrectToolForDrops())
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model((c, p) -> p.blockWithInventoryModel(c::get))
            .build()
            .register();

    public static final BlockEntry<HugeMushroomBlock> TOWER_FUNGUS_CAP_BLOCK = REGISTRATE
            .block("tower_fungus_cap_block", HugeMushroomBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .sound(SoundType.FUNGUS)
                    .strength(4f, 6f)
                    .requiresCorrectToolForDrops())
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model((c, p) -> p.blockWithInventoryModel(c::get))
            .build()
            .register();

    public static final BlockEntry<VenusVinesBlock> VENUS_VINES = REGISTRATE
            .block("venus_vines", VenusVinesBlock::new)
            /*.initialProperties(SharedProperties::LEAVES)*/
            .properties(p -> p.mapColor(MapColor.COLOR_GREEN)
                    .sound(SoundType.GRASS)
                    .strength(0.5f, 0.5f)
                    .randomTicks()
                    .noCollission()
                    .noOcclusion())
            .tag(BlockTags.CLIMBABLE)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, RegistrateBlockLootTables.createShearsOnlyDrop(b)))
            .simpleItem()
            .register();

    public static final BlockEntry<VenusVinesBlock> GLOWING_VENUS_VINES = REGISTRATE
            .block("glowing_venus_vines", VenusVinesBlock::new)
            /*.initialProperties(SharedProperties::LEAVES)*/
            .properties(p -> p.mapColor(MapColor.COLOR_GREEN)
                    .sound(SoundType.GRASS)
                    .strength(0.5f, 0.5f)
                    .randomTicks()
                    .noCollission()
                    .noOcclusion()
                    .lightLevel(pState -> 11))
            .tag(BlockTags.CLIMBABLE)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, RegistrateBlockLootTables.createShearsOnlyDrop(b)))
            .item()
            .model((c, p) -> p.generated(c::get, p.modLoc("block/glowing_venus_vine")))
            .build()
            .register();

    public static final BlockEntry<VenusTallMyceliumBlock> TALL_VENUS_MYCELIUM = REGISTRATE
            .block("tall_venus_mycelium", VenusTallMyceliumBlock::new)
            /*.initialProperties(SharedProperties::REPLACEABLE_PLANT)*/
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.GRASS)
                    .noCollission()
                    .instabreak()
                    .randomTicks()
                    .offsetType(BlockBehaviour.OffsetType.XZ))
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    //venus deco blocks
    public static final BlockEntry<Block> VENUS_STONE_BRICKS = REGISTRATE
            .block("venus_stone_bricks", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> VENUS_STONE_BRICK_SLAB = REGISTRATE
            .block("venus_stone_brick_slab", SlabBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(VENUS_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<VerticalSlabBlock> VENUS_STONE_BRICK_SLAB_VERTICAL = REGISTRATE
            .block("venus_stone_brick_slab_vertical", VerticalSlabBlock::new)
            .lang("Venus Stone Brick Vertical Slab")
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.verticalSlabRecipe(VENUS_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> VENUS_STONE_BRICK_STAIRS = REGISTRATE
            .block("venus_stone_brick_stairs", p -> new StairBlock(STONE.defaultBlockState(), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.stairRecipe(VENUS_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<WallBlock> VENUS_STONE_BRICK_WALL = REGISTRATE
            .block("venus_stone_brick_wall", WallBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(BlockTags.WALLS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.wallRecipe(VENUS_STONE_BRICKS))
            .item()
            .model((c, p) -> p.blockWithInventoryModel(c::get))
            .build()
            .register();

    public static final BlockEntry<RotatedPillarBlock> VENUS_STONE_PILLAR = REGISTRATE
            .block("venus_stone_pillar", RotatedPillarBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.pillarRecipe(VENUS_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> CHISELED_VENUS_STONE = REGISTRATE
            .block("chiseled_venus_stone", Block::new)
            .lang("Chiseled Venus Stone Bricks")
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.chiseledRecipe(VENUS_STONE_BRICK_SLAB))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> POLISHED_VENUS_STONE = REGISTRATE
            .block("polished_venus_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> VENUS_STONE_LAMP = REGISTRATE
            .block("venus_stone_lamp", Block::new)
            /*.initialProperties(SharedProperties::DECORATION)*/
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .lightLevel(pState -> 15)
                    .sound(SoundType.GLASS)
                    .strength(3f, 6.5f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    //venus ores
    public static final BlockEntry<Block> VENUS_TITANIUM_ORE = REGISTRATE
            .block("venus_titanium_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_TITANIUM.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_TITANIUM.get())))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> VENUS_COAL_ORE = REGISTRATE
            .block("venus_coal_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> VENUS_IRON_ORE = REGISTRATE
            .block("venus_iron_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_IRON.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_IRON)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_IRON.tag)
            .build()
            .register();

    public static final BlockEntry<Block> VENUS_COPPER_ORE = REGISTRATE
            .block("venus_copper_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_COPPER.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createCopperOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_COPPER.tag)
            .build()
            .register();

    public static final BlockEntry<Block> VENUS_GOLD_ORE = REGISTRATE
            .block("venus_gold_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GOLD.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_GOLD)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GOLD.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> VENUS_DIAMOND_ORE = REGISTRATE
            .block("venus_diamond_ore", p -> new DropExperienceBlock(UniformInt.of(3, 7), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_DIAMOND.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.DIAMOND)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_DIAMOND.tag)
            .build()
            .register();

    public static final BlockEntry<RedStoneOreBlock> VENUS_REDSTONE_ORE = REGISTRATE
            .block("venus_redstone_ore", RedStoneOreBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .lightLevel(litBlockEmission(9)))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_REDSTONE.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createRedstoneOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_REDSTONE.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> VENUS_QUARTZ_ORE = REGISTRATE
            .block("venus_quartz_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_QUARTZ.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.QUARTZ)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_QUARTZ.tag)
            .build()
            .register();

    public static final BlockEntry<Block> VENUS_GLOWSTONE_ORE = REGISTRATE
            .block("venus_glowstone_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(pState -> 6))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GLOWSTONE.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_GLOWSTONE_ORE.get())))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GLOWSTONE.tag)
            .build()
            .register();

    //venus deep ores
    public static final BlockEntry<Block> VENUS_DEEP_TITANIUM_ORE = REGISTRATE
            .block("venus_deep_titanium_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_TITANIUM.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_TITANIUM.get())))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> VENUS_DEEP_IRON_ORE = REGISTRATE
            .block("venus_deep_iron_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_IRON.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_IRON)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_IRON.tag)
            .build()
            .register();

    public static final BlockEntry<Block> VENUS_DEEP_COPPER_ORE = REGISTRATE
            .block("venus_deep_copper_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_COPPER.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createCopperOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_COPPER.tag)
            .build()
            .register();

    public static final BlockEntry<Block> VENUS_DEEP_GOLD_ORE = REGISTRATE
            .block("venus_deep_gold_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GOLD.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_GOLD)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GOLD.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> VENUS_DEEP_DIAMOND_ORE = REGISTRATE
            .block("venus_deep_diamond_ore", p -> new DropExperienceBlock(UniformInt.of(3, 7), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(7f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_DIAMOND.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.DIAMOND)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_DIAMOND.tag)
            .build()
            .register();

    public static final BlockEntry<RedStoneOreBlock> VENUS_DEEP_REDSTONE_ORE = REGISTRATE
            .block("venus_deep_redstone_ore", RedStoneOreBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .lightLevel(litBlockEmission(9)))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_REDSTONE.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createRedstoneOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_REDSTONE.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> VENUS_DEEP_QUARTZ_ORE = REGISTRATE
            .block("venus_deep_quartz_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_QUARTZ.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.QUARTZ)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_QUARTZ.tag)
            .build()
            .register();

    public static final BlockEntry<Block> VENUS_DEEP_GLOWSTONE_ORE = REGISTRATE
            .block("venus_deep_glowstone_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(pState -> 6))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GLOWSTONE.tag)
            .tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_GLOWSTONE_ORE.get())))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GLOWSTONE.tag)
            .build()
            .register();

    // endregion
    // region Mercury

    public static final BlockEntry<Block> MERCURY_STONE = REGISTRATE
            .block("mercury_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(3.5f, 8f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.BASE_STONE_MERCURY.tag)
            .tag(NorthstarBlockTags.MERCURY_STONE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MERCURY_DEEP_STONE = REGISTRATE
            .block("mercury_deep_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE)
                    .strength(4.5f, 8f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.BASE_STONE_MERCURY.tag)
            .tag(NorthstarBlockTags.MERCURY_DEEP_STONE_REPLACEABLE.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .lang("Deep Mercury Stone")
            .simpleItem()
            .register();

    //mercury deco blocks
    public static final BlockEntry<Block> MERCURY_STONE_BRICKS = REGISTRATE
            .block("mercury_stone_bricks", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> MERCURY_STONE_BRICK_SLAB = REGISTRATE
            .block("mercury_stone_brick_slab", SlabBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(MERCURY_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<VerticalSlabBlock> MERCURY_STONE_BRICK_SLAB_VERTICAL = REGISTRATE
            .block("mercury_stone_brick_slab_vertical", VerticalSlabBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.verticalSlabRecipe(MERCURY_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> MERCURY_STONE_BRICK_STAIRS = REGISTRATE
            .block("mercury_stone_brick_stairs", p -> new StairBlock(STONE.defaultBlockState(), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.stairRecipe(MERCURY_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<WallBlock> MERCURY_STONE_BRICK_WALL = REGISTRATE
            .block("mercury_stone_brick_wall", WallBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(BlockTags.WALLS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.wallRecipe(MERCURY_STONE_BRICKS))
            .item()
            .model((c, p) -> p.blockWithInventoryModel(c::get))
            .build()
            .register();

    public static final BlockEntry<RotatedPillarBlock> MERCURY_STONE_PILLAR = REGISTRATE
            .block("mercury_stone_pillar", RotatedPillarBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.pillarRecipe(MERCURY_STONE_BRICKS))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> CHISELED_MERCURY_STONE = REGISTRATE
            .block("chiseled_mercury_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> POLISHED_MERCURY_STONE = REGISTRATE
            .block("polished_mercury_stone", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(3.5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MERCURY_STONE_LAMP = REGISTRATE
            .block("mercury_stone_lamp", Block::new)
            /*.initialProperties(SharedProperties::DECORATION)*/
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .lightLevel(pState -> 15)
                    .sound(SoundType.GLASS)
                    .strength(3f, 6.5f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    //mercury ores
    public static final BlockEntry<Block> MERCURY_TITANIUM_ORE = REGISTRATE
            .block("mercury_titanium_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_TITANIUM.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_TITANIUM.get())))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MERCURY_IRON_ORE = REGISTRATE
            .block("mercury_iron_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_IRON.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_IRON)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_IRON.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MERCURY_COPPER_ORE = REGISTRATE
            .block("mercury_copper_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_COPPER.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createCopperOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_COPPER.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MERCURY_GOLD_ORE = REGISTRATE
            .block("mercury_gold_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GOLD.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_GOLD)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GOLD.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MERCURY_DIAMOND_ORE = REGISTRATE
            .block("mercury_diamond_ore", p -> new DropExperienceBlock(UniformInt.of(3, 7), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_DIAMOND.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.DIAMOND)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_DIAMOND.tag)
            .build()
            .register();

    public static final BlockEntry<RedStoneOreBlock> MERCURY_REDSTONE_ORE = REGISTRATE
            .block("mercury_redstone_ore", RedStoneOreBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .lightLevel(litBlockEmission(9)))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_REDSTONE.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createRedstoneOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_REDSTONE.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MERCURY_LAPIS_ORE = REGISTRATE
            .block("mercury_lapis_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_LAPIS.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_LAPIS.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MERCURY_ZINC_ORE = REGISTRATE
            .block("mercury_zinc_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_ZINC.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_ZINC.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MERCURY_GLOWSTONE_ORE = REGISTRATE
            .block("mercury_glowstone_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(5f, 12f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(pState -> 15))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GLOWSTONE.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_GLOWSTONE_ORE.get())))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GLOWSTONE.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MERCURY_TUNGSTEN_ORE = REGISTRATE
            .block("mercury_tungsten_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 20f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_TUNGSTEN.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_TUNGSTEN.tag)
            .build()
            .register();

    //mercury deep ores
    public static final BlockEntry<Block> MERCURY_DEEP_TITANIUM_ORE = REGISTRATE
            .block("mercury_deep_titanium_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_TITANIUM.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_TITANIUM.get())))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MERCURY_DEEP_IRON_ORE = REGISTRATE
            .block("mercury_deep_iron_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_IRON.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_IRON)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_IRON.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MERCURY_DEEP_COPPER_ORE = REGISTRATE
            .block("mercury_deep_copper_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_COPPER.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createCopperOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_COPPER.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MERCURY_DEEP_GOLD_ORE = REGISTRATE
            .block("mercury_deep_gold_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GOLD.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.RAW_GOLD)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GOLD.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MERCURY_DEEP_DIAMOND_ORE = REGISTRATE
            .block("mercury_deep_diamond_ore", p -> new DropExperienceBlock(UniformInt.of(3, 7), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(7f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_DIAMOND.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, Items.DIAMOND)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_DIAMOND.tag)
            .build()
            .register();

    public static final BlockEntry<RedStoneOreBlock> MERCURY_DEEP_REDSTONE_ORE = REGISTRATE
            .block("mercury_deep_redstone_ore", RedStoneOreBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops()
                    .randomTicks()
                    .lightLevel(litBlockEmission(9)))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_REDSTONE.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createRedstoneOreDrops(b)))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_REDSTONE.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MERCURY_DEEP_LAPIS_ORE = REGISTRATE
            .block("mercury_deep_lapis_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_LAPIS.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_LAPIS.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MERCURY_DEEP_ZINC_ORE = REGISTRATE
            .block("mercury_deep_zinc_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_ZINC.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_ZINC.tag)
            .build()
            .register();

    public static final BlockEntry<Block> MERCURY_DEEP_GLOWSTONE_ORE = REGISTRATE
            .block("mercury_deep_glowstone_ore", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(6f, 12f)
                    .requiresCorrectToolForDrops()
                    .lightLevel(pState -> 15))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_GLOWSTONE.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createOreDrop(b, NorthstarItems.RAW_GLOWSTONE_ORE.get())))
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_GLOWSTONE.tag)
            .build()
            .register();

    public static final BlockEntry<DropExperienceBlock> MERCURY_DEEP_TUNGSTEN_ORE = REGISTRATE
            .block("mercury_deep_tungsten_ore", p -> new DropExperienceBlock(UniformInt.of(2, 5), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.DEEPSLATE)
                    .strength(7f, 20f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.COMMON_ORES_TUNGSTEN.tag)
            .tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .tag(NorthstarItemTags.SPACE_ORE_TUNGSTEN.tag)
            .build()
            .register();

    public static final BlockEntry<RotatedPillarBlock> CALORIAN_LOG = REGISTRATE
            .block("calorian_log", RotatedPillarBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.BASALT)
                    .strength(4f, 4f))
            .transform(axeOnly())
            .tag(BlockTags.LOGS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> CALORIAN_PLANKS = REGISTRATE
            .block("calorian_planks", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.BASALT)
                    .strength(4f, 4f))
            .transform(axeOnly())
            .tag(BlockTags.PLANKS)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> CALORIAN_SLAB = REGISTRATE
            .block("calorian_slab", SlabBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.BASALT)
                    .strength(4f, 4f))
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createSlabItemTable(b)))
            .recipe(NorthstarDataGenHelper.slabRecipe(CALORIAN_PLANKS))
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> CALORIAN_STAIRS = REGISTRATE
            .block("calorian_stairs", p -> new StairBlock(CALORIAN_PLANKS.get().defaultBlockState(), p))
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.BASALT)
                    .strength(4f, 4f))
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .recipe(NorthstarDataGenHelper.stairRecipe(CALORIAN_PLANKS))
            .simpleItem()
            .register();

    public static final BlockEntry<MercuryShelfFungusBlock> MERCURY_SHELF_FUNGUS = REGISTRATE
            .block("mercury_shelf_fungus", MercuryShelfFungusBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.BASALT)
                    .strength(1f, 1f)
                    .noCollission()
                    .noOcclusion()
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.applyExplosionDecay(b, LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(b)
                                    .apply(IntStream.rangeClosed(MercuryShelfFungusBlock.MIN_SHELVES, MercuryShelfFungusBlock.MAX_SHELVES)
                                            .boxed()
                                            .toList(), i ->
                                            SetItemCountFunction.setCount(ConstantValue.exactly(i))
                                                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)
                                                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                                                    .hasProperty(MercuryShelfFungusBlock.SHELVES, i)))))))))
            .item()
            .model((c, p) -> p.blockWithInventoryModel(c::get))
            .build()
            .register();

    public static final BlockEntry<HugeMushroomBlock> MERCURY_SHELF_FUNGUS_BLOCK = REGISTRATE
            .block("mercury_shelf_fungus_block", HugeMushroomBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.BASALT)
                    .strength(4f, 6f)
                    .requiresCorrectToolForDrops())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<MercuryCactusBlock> MERCURY_CACTUS = REGISTRATE
            .block("mercury_cactus", MercuryCactusBlock::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.BASALT)
                    .strength(4f, 6f)
                    .requiresCorrectToolForDrops())
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .lang("Mercury Petrified Cactus")
            .item()
            .model((c, p) -> p.blockItem(c::get, "_item"))
            .build()
            .register();

    // endregion

    public static final BlockEntry<GrateBlock> VENT_BLOCK = REGISTRATE
            .block("vent_block", GrateBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(4f, 8f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isSuffocating(NorthstarBlocks::never)
                    .isViewBlocking(NorthstarBlocks::never))
            .transform(pickaxeOnly())
            .tag(NorthstarBlockTags.AIR_PASSES_THROUGH.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> GLOWSTONE_LAMP = REGISTRATE
            .block("glowstone_lamp", Block::new)
            /*.initialProperties(SharedProperties::decoration)*/
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW)
                    .lightLevel(pState -> 15)
                    .sound(SoundType.GLASS)
                    .strength(2f, 5f))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<Block> MONOLITHITE = REGISTRATE
            .block("monolithite", Block::new)
            .initialProperties(() -> STONE)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.DEEPSLATE_BRICKS)
                    .strength(100f, 100f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<MethaneIceBlock> METHANE_ICE = REGISTRATE
            .block("methane_ice", MethaneIceBlock::new)
            .initialProperties(() -> ICE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .sound(SoundType.GLASS)
                    .friction(0.989F)
                    .randomTicks()
                    .strength(0.5F)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
            .simpleItem()
            .register();

    public static final BlockEntry<IcicleBlock> ICICLE = REGISTRATE
            .block("icicle", IcicleBlock::new)
            .initialProperties(() -> ICE)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .sound(SoundType.GLASS)
                    .strength(3.5f, 12f)
                    .noOcclusion()
                    .dynamicShape()
                    .offsetType(BlockBehaviour.OffsetType.XZ))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot(RegistrateBlockLootTables::dropWhenSilkTouch)
            .item()
            .model((c, p) -> p.generated(c::get, p.modLoc("block/icicle_up_tip")))
            .build()
            .register();

    public static final BlockEntry<CrystalBlock> AMETHYST_CRYSTAL = REGISTRATE
            .block("amethyst_crystal", CrystalBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_PURPLE)
                    .lightLevel(value -> 5)
                    .sound(SoundType.AMETHYST_CLUSTER))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model((c, p) -> p.withExistingParent(p.name(c), "minecraft:item/amethyst_bud")
                    .texture("layer0", p.modLoc("block/amethyst_crystal_block"))
                    .transforms()
                    .transform(ItemDisplayContext.FIXED)
                    .translation(0, 4, 0)
                    .end()
                    .end())
            .build()
            .register();

    public static final BlockEntry<CrystalBlock> LUNAR_SAPPHIRE_CRYSTAL = REGISTRATE
            .block("lunar_sapphire_crystal", CrystalBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLUE)
                    .lightLevel(value -> 7)
                    .sound(SoundType.AMETHYST_CLUSTER))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<ExtinguishedTorchBlock> EXTINGUISHED_TORCH = REGISTRATE
            .block("extinguished_torch", ExtinguishedTorchBlock::new)
            .properties(p -> p.sound(SoundType.WOOD)
                    .noCollission()
                    .instabreak())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item((b, p) -> new StandingAndWallBlockItem(NorthstarBlocks.EXTINGUISHED_TORCH.get(), NorthstarBlocks.EXTINGUISHED_TORCH_WALL.get(), p, Direction.DOWN))
            .model(NorthstarDataGenHelper.itemGeneratedBlock())
            .build()
            .register();

    public static final BlockEntry<ExtinguishedTorchWallBlock> EXTINGUISHED_TORCH_WALL = REGISTRATE
            .block("extinguished_torch_wall", ExtinguishedTorchWallBlock::new)
            .properties(p -> p.sound(SoundType.WOOD)
                    .noCollission()
                    .instabreak())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .register();

    public static final BlockEntry<ExtinguishedLanternBlock> EXTINGUISHED_LANTERN = REGISTRATE
            .block("extinguished_lantern", ExtinguishedLanternBlock::new)
            .initialProperties(() -> Blocks.LANTERN)
            .properties(p -> p.sound(SoundType.LANTERN))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model(NorthstarDataGenHelper.itemGeneratedItem())
            .build()
            .register();

    public static final BlockEntry<GlowstoneTorchBlock> GLOWSTONE_TORCH = REGISTRATE
            .block("glowstone_torch", GlowstoneTorchBlock::new)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .lightLevel(value -> 15)
                    .sound(SoundType.METAL)
                    .noCollission()
                    .instabreak())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item((b, p) -> new StandingAndWallBlockItem(NorthstarBlocks.GLOWSTONE_TORCH.get(), NorthstarBlocks.GLOWSTONE_TORCH_WALL.get(), p, Direction.DOWN))
            .model(NorthstarDataGenHelper.itemGeneratedBlock())
            .build()
            .register();

    public static final BlockEntry<GlowstoneTorchWallBlock> GLOWSTONE_TORCH_WALL = REGISTRATE
            .block("glowstone_torch_wall", GlowstoneTorchWallBlock::new)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .lightLevel(value -> 15)
                    .sound(SoundType.METAL)
                    .noCollission()
                    .instabreak())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .register();

    public static final BlockEntry<LanternBlock> GLOWSTONE_LANTERN = REGISTRATE
            .block("glowstone_lantern", LanternBlock::new)
            .initialProperties(() -> Blocks.LANTERN)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .lightLevel(value -> 15)
                    .sound(SoundType.LANTERN))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model(NorthstarDataGenHelper.itemGeneratedItem())
            .build()
            .register();

    // region Tech

    static {
        REGISTRATE.setCreativeTab(NorthstarCreativeModeTab.TECH);
    }

    public static final BlockEntry<TelescopeBlock> TELESCOPE = REGISTRATE
            .block("telescope", TelescopeBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_BROWN)
                    .isViewBlocking(NorthstarBlocks::never)
                    .sound(SoundType.COPPER)
                    .strength(8f, 8f)
                    .noOcclusion())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .transform(customItemModel())
            .simpleItem()
            .register();

    public static final BlockEntry<AstronomyTableBlock> ASTRONOMY_TABLE = REGISTRATE
            .block("astronomy_table", AstronomyTableBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .noOcclusion()
                    .sound(SoundType.WOOD))
            .transform(axeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<SpaceCogWheelBlock> IRON_COGWHEEL = REGISTRATE
            .block("iron_cogwheel", SpaceCogWheelBlock::small)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.METAL))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> 0))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .item(CogwheelBlockItem::new)
            .build()
            .register();

    public static final BlockEntry<SpaceCogWheelBlock> IRON_LARGE_COGWHEEL = REGISTRATE
            .block("iron_large_cogwheel", SpaceCogWheelBlock::large)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.METAL))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> 0))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .lang("Large Iron Cogwheel")
            .item(CogwheelBlockItem::new)
            .build()
            .register();

    public static final BlockEntry<SolarPanelBlock> SOLAR_PANEL = REGISTRATE
            .block("solar_panel", SolarPanelBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.CLAY)
                    .noOcclusion())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(b -> BlockStressValues.CAPACITIES.register(b, () -> 128.0))
            .onRegister(BlockStressValues.setGeneratorSpeed(SolarPanelBlockEntity.MAXIMUM_SPEED, true))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<CombustionEngineBlock> COMBUSTION_ENGINE = REGISTRATE
            .block("combustion_engine", CombustionEngineBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .noOcclusion()
                    .isViewBlocking(NorthstarBlocks::never)
                    .strength(8, 8))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(b -> BlockStressValues.CAPACITIES.register(b, () -> 256))
            //.onRegister(BlockStressValues.setGeneratorSpeed(CombustionEngineBlock.getSpeedRange().getSecond(), true))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<CircuitEngraverBlock> CIRCUIT_ENGRAVER = REGISTRATE
            .block("circuit_engraver", CircuitEngraverBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK)
                    .noOcclusion()
                    .isViewBlocking(NorthstarBlocks::never))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> 8))
            .item(AssemblyOperatorBlockItem::new)
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ElectrolysisMachineBlock> ELECTROLYSIS_MACHINE = REGISTRATE
            .block("electrolysis_machine", ElectrolysisMachineBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .isViewBlocking(NorthstarBlocks::never)
                    .sound(SoundType.NETHERITE_BLOCK).noOcclusion())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> 8))
            .simpleItem()
            .register();

    public static final BlockEntry<OxygenConcentratorBlock> OXYGEN_CONCENTRATOR = REGISTRATE
            .block("oxygen_concentrator", OxygenConcentratorBlock::new)
            .lang("Atmospheric Concentrator")
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .noOcclusion()
                    .isViewBlocking(NorthstarBlocks::never)
                    .strength(6, 6))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> 16))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<OxygenFillerBlock> OXYGEN_FILLER = REGISTRATE
            .block("oxygen_filler", OxygenFillerBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .noOcclusion()
                    .isViewBlocking(NorthstarBlocks::never)
                    .strength(6, 6))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<OxygenSealerBlock> OXYGEN_SEALER = REGISTRATE
            .block("oxygen_sealer", OxygenSealerBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .noOcclusion()
                    .isViewBlocking(NorthstarBlocks::never)
                    .strength(8, 8))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(MovingInteractionBehaviour.interactionBehaviour(new OxygenSealerMovingInteractionBehaviour()))
            .onRegister(MovementBehaviour.movementBehaviour(new OxygenSealerMovementBehaviour()))
            .onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> 16))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<OxygenDetectorBlock> OXYGEN_DETECTOR = REGISTRATE
            .block("oxygen_detector", OxygenDetectorBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .noOcclusion()
                    .sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<TemperatureRegulatorBlock> TEMPERATURE_REGULATOR = REGISTRATE
            .block("temperature_regulator", TemperatureRegulatorBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .noOcclusion()
                    .isViewBlocking(NorthstarBlocks::never)
                    .strength(8, 8))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> 16))
            .onRegister(MovementBehaviour.movementBehaviour(new TemperatureRegulatorMovementBehaviour()))
            .onRegister(MovingInteractionBehaviour.interactionBehaviour(new TemperatureRegulatorMovingInteractionBehaviour()))
            .simpleItem()
            .register();

    public static final BlockEntry<IceBoxBlock> ICE_BOX = REGISTRATE
            .block("ice_box", IceBoxBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(MovementBehaviour.movementBehaviour(new BasinMovementBehaviour()))
            .simpleItem()
            .register();

    public static final BlockEntry<RocketStationBlock> ROCKET_STATION = REGISTRATE
            .block("rocket_station", RocketStationBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .onRegister(MovingInteractionBehaviour.interactionBehaviour(new RocketStationBlockMovingInteraction()))
            .register();

    public static final BlockEntry<RocketControlsBlock> ROCKET_CONTROLS = REGISTRATE
            .block("rocket_controls", RocketControlsBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .noOcclusion()
                    .sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(MovementBehaviour.movementBehaviour(new RocketControlsMovementBehaviour()))
            .onRegister(MovingInteractionBehaviour.interactionBehaviour(new RocketControlsInteractionBehaviour()))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<AutoLanderBlock> AUTO_LANDER = REGISTRATE
            .block("auto_lander", AutoLanderBlock::new)
            .lang("Auto-Landing Station")
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.METAL)
                    .strength(8f, 8f)
                    .noOcclusion()
                    .isViewBlocking(NorthstarBlocks::never))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<JetEngineBlock> JET_ENGINE = REGISTRATE
            .block("jet_engine", JetEngineBlock::new)
            .lang("Rocket Engine")
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .noOcclusion()
                    .isViewBlocking(NorthstarBlocks::never))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .onRegister(MovementBehaviour.movementBehaviour(new JetEngineMovementBehaviour()))
            .item()
            .model((c, p) -> p.withExistingParent(p.name(c), p.modLoc("block/jet_engine/jet_single")))
            .build()
            .register();

    public static final BlockEntry<TargetingComputerRackBlock> COMPUTER_RACK = REGISTRATE
            .block("computer_rack", TargetingComputerRackBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .noOcclusion()
                    .sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .item()
            .model((c, p) -> p.withExistingParent(p.name(c), p.modLoc("block/computer_rack/block")))
            .build()
            .register();

    public static final BlockEntry<SpaceDoorBlock> TITANIUM_SPACE_DOOR = REGISTRATE
            .block("titanium_space_door", p -> new SpaceDoorBlock(p, BlockSetType.IRON, false))
            .transform(BuilderTransformers.slidingDoor("titanium_space"))
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_CYAN)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .register();

    public static final BlockEntry<InterplanetaryNavigatorBlock> INTERPLANETARY_NAVIGATOR = REGISTRATE
            .block("interplanetary_navigator", InterplanetaryNavigatorBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.METAL)
                    .strength(8f, 8f)
                    .noOcclusion()
                    .isViewBlocking(NorthstarBlocks::never))
            .transform(pickaxeOnly())
            .tag(AllBlockTags.BRITTLE.tag)
            .blockstate(NorthstarDataGenHelper.manualModel())
            .loot((c, b) -> c.add(b, c.createSinglePropConditionTable(b, InterplanetaryNavigatorBlock.HALF, DoubleBlockHalf.LOWER)))
            .item()
            .model(NorthstarDataGenHelper.itemGeneratedItem())
            .build()
            .register();

    public static final BlockEntry<OxygenBubbleGeneratorBlock> OXYGEN_BUBBLE_GENERATOR = REGISTRATE
            .block("oxygen_bubble_generator", OxygenBubbleGeneratorBlock::new)
            .lang("Oxygen Bubble Generator (WIP)")
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .sound(SoundType.COPPER)
                    .strength(8f, 8f)
                    .requiresCorrectToolForDrops())
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .simpleItem()
            .register();

    public static final BlockEntry<LaserLenseBlock> LASER_LENSE = REGISTRATE
            .block("laser_lense", LaserLenseBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY)
                    .noOcclusion()
                    .sound(SoundType.COPPER))
            .transform(pickaxeOnly())
            .blockstate(NorthstarDataGenHelper.manualModel())
            .lang("Laser Lense (WIP)")
            .simpleItem()
            .register();

    public static final BlockEntry<LaserBlock> LASER = REGISTRATE
            .block("laser", LaserBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_RED)
                    .noOcclusion()
                    .noCollission()
                    .lightLevel(state -> 15))
            .blockstate(NorthstarDataGenHelper.manualModel())
            .lang("Laser (WIP)")
            .simpleItem()
            .register();

    // endregion

    private static boolean never(BlockState blockstate, BlockGetter blockgetter, BlockPos blockpos) {
        return false;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int level) {
        return state -> state.getValue(BlockStateProperties.LIT) ? level : 0;
    }

    public static void register() {
    }

}

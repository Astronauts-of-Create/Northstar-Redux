package com.lightning.northstar.data.recipe;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.data.recipe.StandardRecipeGen;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.data.Tags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public class NorthstarStandardRecipeGen extends StandardRecipeGen {

    GeneratedRecipe
            $ = null,

    // region crafting

    AMETHYST_CRYSTAL = create(NorthstarBlocks.AMETHYST_CRYSTAL)
            .unlockedBy(() -> NorthstarItems.POLISHED_AMETHYST)
            .viaShaped(b -> b.define('I', Items.IRON_INGOT)
                    .define('R', Items.REDSTONE)
                    .define('A', NorthstarItems.POLISHED_AMETHYST)
                    .pattern(" A ")
                    .pattern("RAR")
                    .pattern("IAI")),

    ASTRONOMY_TABLE = create(NorthstarBlocks.ASTRONOMY_TABLE)
            .unlockedByTag(() -> NorthstarItemTags.C_STRIPPED_LOGS.tag)
            .viaShaped(b -> b.define('#', Items.PAPER)
                    .define('X', ItemTags.WOODEN_SLABS)
                    .define('S', Items.IRON_INGOT)
                    .define('P', NorthstarItemTags.C_STRIPPED_LOGS.tag)
                    .pattern("X#X")
                    .pattern(" S ")
                    .pattern("XPX")),

    COMBUSTION_ENGINE = create(NorthstarBlocks.COMBUSTION_ENGINE)
            .unlockedBy(() -> NorthstarItems.ROCKET_COMBUSTION_CHAMBER)
            .viaShaped(b -> b.define('A', AllBlocks.INDUSTRIAL_IRON_BLOCK)
                    .define('C', NorthstarItems.ROCKET_COMBUSTION_CHAMBER)
                    .define('P', Items.PISTON)
                    .define('T', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .pattern("PTP")
                    .pattern("TCT")
                    .pattern("AAA")),

    DURABLE_FABRIC = create(NorthstarItems.DURABLE_FABRIC)
            .unlockedByTag(() -> ItemTags.WOOL)
            .viaShaped(b -> b.define('S', Items.STRING)
                    .define('W', ItemTags.WOOL)
                    .pattern("SWS")
                    .pattern("SWS")),

    FAN_BLADE = create(NorthstarItems.FAN_BLADE)
            .unlockedByTag(() -> NorthstarItemTags.C_SHEETS_TITANIUM.tag)
            .viaShaped(b -> b.define('S', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .pattern("  S")
                    .pattern(" S ")
                    .pattern("S  ")),

    GLOWSTONE_LANTERN = create(NorthstarBlocks.GLOWSTONE_LANTERN)
            .unlockedBy(() -> Items.GLOWSTONE)
            .viaShaped(b -> b.define('#', Items.IRON_NUGGET)
                    .define('G', Items.GLOWSTONE_DUST)
                    .pattern("###")
                    .pattern("#G#")
                    .pattern("###")),

    GLOWSTONE_TORCH = create(NorthstarBlocks.GLOWSTONE_TORCH)
            .returns(4)
            .unlockedBy(() -> Items.GLOWSTONE)
            .viaShaped(b -> b.define('I', Items.IRON_INGOT)
                    .define('G', Items.GLOWSTONE_DUST)
                    .pattern("G")
                    .pattern("I")),

    ICE_BOX = create(NorthstarBlocks.ICE_BOX)
            .unlockedBy(() -> AllBlocks.BASIN)
            .viaShaped(b -> b.define('B', Items.BLUE_ICE)
                    .define('I', NorthstarItemTags.C_SHEETS_IRON.tag)
                    .define('C', AllBlocks.BASIN)
                    .pattern("IBI")
                    .pattern("BCB")
                    .pattern("IBI")),

    ICE_TO_FROST = create(NorthstarBlocks.FROST)
            .unlockedBy(() -> Items.ICE)
            .viaShapeless(b -> b.requires(Items.ICE)),

    IRON_COGWHEEL = create(NorthstarBlocks.IRON_COGWHEEL)
            .unlockedBy(() -> AllBlocks.SHAFT)
            .viaShapeless(b -> b.requires(AllBlocks.SHAFT)
                    .requires(AllBlocks.INDUSTRIAL_IRON_BLOCK)),

    IRON_LARGE_COGWHEEL = create(NorthstarBlocks.IRON_LARGE_COGWHEEL)
            .unlockedBy(() -> AllBlocks.SHAFT)
            .viaShapeless(b -> b.requires(AllBlocks.SHAFT)
                    .requires(AllBlocks.INDUSTRIAL_IRON_BLOCK)
                    .requires(AllBlocks.INDUSTRIAL_IRON_BLOCK)),

    IRON_LARGE_COGWHEEL_FROM_SMALL = create(NorthstarBlocks.IRON_LARGE_COGWHEEL)
            .unlockedBy(() -> AllBlocks.SHAFT)
            .withSuffix("_from_small")
            .viaShapeless(b -> b.requires(NorthstarBlocks.IRON_COGWHEEL)
                    .requires(AllBlocks.INDUSTRIAL_IRON_BLOCK)),

    // TODO: unlock: armor, fabric or other space related item?
    //  or maybe even another space-related item
    IRON_SPACE_SUIT_BOOTS = create(NorthstarItems.IRON_SPACE_SUIT_BOOTS)
            .unlockedBy(() -> Items.IRON_BOOTS)
            .viaShaped(b -> b.define('A', Items.IRON_BOOTS)
                    .define('F', NorthstarItems.DURABLE_FABRIC)
                    .define('I', NorthstarItemTags.C_SHEETS_IRON.tag)
                    .pattern("IFI")
                    .pattern("FAF")
                    .pattern("IFI")),

    IRON_SPACE_SUIT_LEGGINGS = create(NorthstarItems.IRON_SPACE_SUIT_LEGGINGS)
            .unlockedBy(() -> Items.IRON_LEGGINGS)
            .viaShaped(b -> b.define('A', Items.IRON_LEGGINGS)
                    .define('F', NorthstarItems.DURABLE_FABRIC)
                    .define('I', NorthstarItemTags.C_SHEETS_IRON.tag)
                    .pattern("IFI")
                    .pattern("FAF")
                    .pattern("IFI")),

    IRON_SPACE_SUIT_CHESTPLATE = create(NorthstarItems.IRON_SPACE_SUIT_CHESTPIECE)
            .unlockedBy(() -> Items.IRON_CHESTPLATE)
            .viaShaped(b -> b.define('A', Items.IRON_CHESTPLATE)
                    .define('F', NorthstarItems.DURABLE_FABRIC)
                    .define('I', NorthstarItemTags.C_SHEETS_IRON.tag)
                    .define('C', AllItems.COPPER_BACKTANK)
                    .pattern("ICI")
                    .pattern("FAF")
                    .pattern("IFI")),

    IRON_SPACE_SUIT_HELMET = create(NorthstarItems.IRON_SPACE_SUIT_HELMET)
            .unlockedBy(() -> Items.IRON_HELMET)
            .viaShaped(b -> b.define('A', Items.IRON_HELMET)
                    .define('G', Items.TINTED_GLASS)
                    .define('I', NorthstarItemTags.C_SHEETS_IRON.tag)
                    .pattern("GGG")
                    .pattern("GAG")
                    .pattern("III")),

    JET_ENGINE = create(NorthstarBlocks.JET_ENGINE)
            .unlockedBy(() -> NorthstarItems.ROCKET_COMBUSTION_CHAMBER)
            .viaShaped(b -> b.define('C', NorthstarItems.ROCKET_COMBUSTION_CHAMBER)
                    .define('P', AllItems.PROPELLER)
                    .define('T', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .pattern("TCT")
                    .pattern("TPT")
                    .pattern("T T")),

    LARGE_FAN = create(NorthstarBlocks.LARGE_FAN)
            .unlockedByTag(() -> NorthstarItemTags.C_INGOTS_TITANIUM.tag)
            .viaShaped(b -> b.define('T', NorthstarItemTags.C_INGOTS_TITANIUM.tag)
                    .define('S', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .define('A', AllBlocks.SHAFT)
                    .pattern("TTT")
                    .pattern("SAS")
                    .pattern("TTT")),

    LUNAR_SAPPHIRE_BLOCK = create(NorthstarBlocks.LUNAR_SAPPHIRE_BLOCK)
            .unlockedBy(() -> NorthstarItems.LUNAR_SAPPHIRE_SHARD)
            .viaShaped(b -> b.define('#', NorthstarItems.LUNAR_SAPPHIRE_SHARD)
                    .pattern("##")
                    .pattern("##")),

    LUNAR_SAPPHIRE_CRYSTAL = create(NorthstarBlocks.LUNAR_SAPPHIRE_CRYSTAL)
            .unlockedBy(() -> NorthstarItems.POLISHED_LUNAR_SAPPHIRE)
            .viaShaped(b -> b.define('I', Items.IRON_INGOT)
                    .define('R', Items.REDSTONE)
                    .define('S', NorthstarItems.POLISHED_LUNAR_SAPPHIRE)
                    .pattern(" S ")
                    .pattern("RSR")
                    .pattern("ISI")),

    MARS_STONE_BRICKS = createFourToFour(NorthstarBlocks.MARS_STONE_BRICKS, NorthstarBlocks.MARS_STONE),

    MARS_STONE_BRICKS_DEEP = createFourToFour(NorthstarBlocks.MARS_STONE_BRICKS, NorthstarBlocks.MARS_DEEP_STONE, "_deep"),

    MARS_STONE_LAMP = createLamp(NorthstarBlocks.MARS_STONE_LAMP,
            () -> NorthstarBlocks.MARS_STONE,
            () -> Items.GLOWSTONE),

    MARTIAN_STEEL_BLUE_LAMP = createLamp(NorthstarBlocks.MARTIAN_STEEL_BLUE_LAMP,
            NorthstarItemTags.C_SHEETS_MARTIAN_STEEL,
            () -> Items.SEA_LANTERN),

    MARTIAN_STEEL_LAMP = createLamp(NorthstarBlocks.MARTIAN_STEEL_LAMP,
            NorthstarItemTags.C_SHEETS_MARTIAN_STEEL,
            () -> Items.GLOWSTONE),

    // TODO: do those need mirrored recipes or does it work out of the box?
    MARTIAN_AXE = create(NorthstarItems.MARTIAN_AXE)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL)
            .viaShaped(b -> b.define('S', NorthstarItems.MARTIAN_STEEL)
                    .define('Z', AllItems.ZINC_INGOT)
                    .pattern("SS")
                    .pattern("SZ")
                    .pattern(" Z")),

    MARTIAN_HOE = create(NorthstarItems.MARTIAN_HOE)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL)
            .viaShaped(b -> b.define('S', NorthstarItems.MARTIAN_STEEL)
                    .define('Z', AllItems.ZINC_INGOT)
                    .pattern("SS")
                    .pattern(" Z")
                    .pattern(" Z")),

    MARTIAN_PICKAXE = create(NorthstarItems.MARTIAN_PICKAXE)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL)
            .viaShaped(b -> b.define('S', NorthstarItems.MARTIAN_STEEL)
                    .define('Z', AllItems.ZINC_INGOT)
                    .pattern("SSS")
                    .pattern(" Z ")
                    .pattern(" Z ")),

    MARTIAN_SWORD = create(NorthstarItems.MARTIAN_SWORD)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL)
            .viaShaped(b -> b.define('S', NorthstarItems.MARTIAN_STEEL)
                    .define('Z', AllItems.ZINC_INGOT)
                    .pattern("S")
                    .pattern("S")
                    .pattern("Z")),

    MARTIAN_SHOVEL = create(NorthstarItems.MARTIAN_SHOVEL)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL)
            .viaShaped(b -> b.define('S', NorthstarItems.MARTIAN_STEEL)
                    .define('Z', AllItems.ZINC_INGOT)
                    .pattern("S")
                    .pattern("Z")
                    .pattern("Z")),

    MARTIAN_STEEL_HELMET = create(NorthstarItems.MARTIAN_STEEL_HELMET)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL)
            .viaShaped(b -> b.define('#', NorthstarItems.MARTIAN_STEEL)
                    .pattern("###")
                    .pattern("# #")),

    MARTIAN_STEEL_CHESTPLATE = create(NorthstarItems.MARTIAN_STEEL_CHESTPLATE)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL)
            .viaShaped(b -> b.define('#', NorthstarItems.MARTIAN_STEEL)
                    .pattern("# #")
                    .pattern("###")
                    .pattern("###")),

    MARTIAN_STEEL_LEGGINGS = create(NorthstarItems.MARTIAN_STEEL_LEGGINGS)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL)
            .viaShaped(b -> b.define('#', NorthstarItems.MARTIAN_STEEL)
                    .pattern("###")
                    .pattern("# #")
                    .pattern("# #")),

    MARTIAN_STEEL_BOOTS = create(NorthstarItems.MARTIAN_STEEL_BOOTS)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL)
            .viaShaped(b -> b.define('#', NorthstarItems.MARTIAN_STEEL)
                    .pattern("# #")
                    .pattern("# #")),

    MARTIAN_STEEL_SPACE_SUIT_BOOTS = create(NorthstarItems.MARTIAN_STEEL_SPACE_SUIT_BOOTS)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL_BOOTS)
            .viaShaped(b -> b.define('A', NorthstarItems.MARTIAN_STEEL_BOOTS)
                    .define('F', NorthstarItems.DURABLE_FABRIC)
                    .define('I', NorthstarItemTags.C_SHEETS_MARTIAN_STEEL.tag)
                    .pattern("IFI")
                    .pattern("FAF")
                    .pattern("IFI")),

    MARTIAN_STEEL_SPACE_SUIT_LEGGINGS = create(NorthstarItems.MARTIAN_STEEL_SPACE_SUIT_LEGGINGS)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL_LEGGINGS)
            .viaShaped(b -> b.define('A', NorthstarItems.MARTIAN_STEEL_LEGGINGS)
                    .define('F', NorthstarItems.DURABLE_FABRIC)
                    .define('I', NorthstarItemTags.C_SHEETS_MARTIAN_STEEL.tag)
                    .pattern("IFI")
                    .pattern("FAF")
                    .pattern("IFI")),

    MARTIAN_STEEL_SPACE_SUIT_CHESTPLATE = create(NorthstarItems.MARTIAN_STEEL_SPACE_SUIT_CHESTPIECE)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL_CHESTPLATE)
            .viaShaped(b -> b.define('A', NorthstarItems.MARTIAN_STEEL_CHESTPLATE)
                    .define('F', NorthstarItems.DURABLE_FABRIC)
                    .define('I', NorthstarItemTags.C_SHEETS_MARTIAN_STEEL.tag)
                    .define('C', AllItems.COPPER_BACKTANK)
                    .pattern("ICI")
                    .pattern("FAF")
                    .pattern("IFI")),

    MARTIAN_STEEL_SPACE_SUIT_HELMET = create(NorthstarItems.MARTIAN_STEEL_SPACE_SUIT_HELMET)
            .unlockedBy(() -> NorthstarItems.MARTIAN_STEEL_HELMET)
            .viaShaped(b -> b.define('A', NorthstarItems.MARTIAN_STEEL_HELMET)
                    .define('G', Items.TINTED_GLASS)
                    .define('I', NorthstarItemTags.C_SHEETS_MARTIAN_STEEL.tag)
                    .pattern("GGG")
                    .pattern("GAG")
                    .pattern("III")),

    MERCURY_STONE_BRICKS = createFourToFour(NorthstarBlocks.MERCURY_STONE_BRICKS, NorthstarBlocks.MERCURY_STONE),

    MERCURY_STONE_BRICKS_DEEP = createFourToFour(NorthstarBlocks.MERCURY_STONE_BRICKS, NorthstarBlocks.MERCURY_DEEP_STONE, "_deep"),

    MERCURY_STONE_LAMP = createLamp(NorthstarBlocks.MERCURY_STONE_LAMP,
            () -> NorthstarBlocks.MERCURY_STONE,
            () -> Items.GLOWSTONE),

    MOON_SAND_PAPER = create(NorthstarItems.MOON_SAND_PAPER)
            .unlockedBy(() -> NorthstarBlocks.MOON_SAND)
            .viaShapeless(b -> b.requires(NorthstarBlocks.MOON_SAND)
                    .requires(Items.PAPER)),

    MOON_STONE_BRICKS = createFourToFour(NorthstarBlocks.MOON_STONE_BRICKS, NorthstarBlocks.MOON_STONE),

    MOON_STONE_BRICKS_DEEP = createFourToFour(NorthstarBlocks.MOON_STONE_BRICKS, NorthstarBlocks.MOON_DEEP_STONE, "_deep"),

    MOON_STONE_LAMP = createLamp(NorthstarBlocks.MOON_STONE_LAMP,
            () -> NorthstarBlocks.MOON_STONE,
            () -> Items.GLOWSTONE),

    // TODO: maybe have the oxygen* recipes be unlocked after
    //  obtaining oxygen in any ways?
    OXYGEN_DETECTOR = create(NorthstarBlocks.OXYGEN_DETECTOR)
            .unlockedByTag(() -> NorthstarItemTags.C_SHEETS_MARTIAN_STEEL.tag)
            .viaShaped(b -> b.define('C', NorthstarItems.ADVANCED_CIRCUIT)
                    .define('O', Items.OBSERVER)
                    .define('S', NorthstarItemTags.C_SHEETS_MARTIAN_STEEL.tag)
                    .define('W', ItemTags.WOOL)
                    .pattern("SCS")
                    .pattern("SOS")
                    .pattern("SWS")),

    // TODO: unlock: circuit, titanium?
    OXYGEN_FILLER = create(NorthstarBlocks.OXYGEN_FILLER)
            .unlockedBy(() -> NorthstarItems.CIRCUIT)
            .viaShaped(b -> b.define('C', NorthstarItems.CIRCUIT)
                    .define('I', NorthstarItemTags.C_SHEETS_IRON.tag)
                    .define('S', NorthstarItems.OXYGEN_SEPARATOR)
                    .define('T', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .pattern("TCT")
                    .pattern("TST")
                    .pattern("III")),

    OXYGEN_SEPARATOR = create(NorthstarItems.OXYGEN_SEPARATOR)
            .returns(2)
            .unlockedBy(() -> AllBlocks.FLUID_PIPE)
            .viaShaped(b -> b.define('E', AllItems.ELECTRON_TUBE)
                    .define('I', NorthstarItemTags.C_SHEETS_IRON.tag)
                    .define('P', AllBlocks.FLUID_PIPE)
                    .pattern("III")
                    .pattern("PEP")
                    .pattern(" P ")),

    RAW_ICE_CREAM_CONE = create(NorthstarItems.RAW_ICE_CREAM_CONE)
            .unlockedBy(() -> NorthstarItems.FLATTENED_DOUGH)
            .viaShapeless(b -> b.requires(NorthstarItems.FLATTENED_DOUGH)),

    // TODO: unlock, HDP, Titanium?
    ROCKET_COMBUSTION_CHAMBER = create(NorthstarItems.ROCKET_COMBUSTION_CHAMBER)
            .unlockedBy(() -> NorthstarItems.HARDENED_PRECISION_MECHANISM)
            .viaShaped(b -> b.define('M', NorthstarItems.HARDENED_PRECISION_MECHANISM)
                    .define('P', AllItems.PROPELLER)
                    .define('S', NorthstarItems.OXYGEN_SEPARATOR)
                    .define('T', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .pattern("TST")
                    .pattern("TMT")
                    .pattern(" P ")),

    TELESCOPE = create(NorthstarBlocks.TELESCOPE)
            .unlockedBy(() -> Items.AMETHYST_SHARD)
            .viaShaped(b -> b.define('A', Items.AMETHYST_SHARD)
                    .define('B', NorthstarItemTags.C_INGOTS_BRASS.tag)
                    .define('S', Items.STICK)
                    .pattern("ABB")
                    .pattern(" S ")
                    .pattern("S S")),

    TITANIUM_SPACE_DOOR = create(NorthstarBlocks.TITANIUM_SPACE_DOOR)
            .unlockedByTag(() -> NorthstarItemTags.C_SHEETS_TITANIUM.tag)
            .viaShaped(b -> b.define('C', NorthstarItems.CIRCUIT)
                    .define('G', Items.GLASS)
                    .define('T', NorthstarItemTags.C_SHEETS_TITANIUM.tag)
                    .pattern("GG ")
                    .pattern("TTC")
                    .pattern("TT ")),

    VENUS_STONE_BRICKS = createFourToFour(NorthstarBlocks.VENUS_STONE_BRICKS, NorthstarBlocks.VENUS_STONE),

    VENUS_STONE_BRICKS_DEEP = createFourToFour(NorthstarBlocks.VENUS_STONE_BRICKS, NorthstarBlocks.VENUS_DEEP_STONE, "_deep"),

    VENUS_STONE_LAMP = createLamp(NorthstarBlocks.VENUS_STONE_LAMP,
            () -> NorthstarBlocks.VENUS_STONE,
            () -> Items.GLOWSTONE),

    // endregion
    // region smelting

    BAMBOO = create(NorthstarItems.DRY_PLANT_FIBER)
            .viaCooking(() -> Items.BAMBOO)
            .rewardXP(0.2f)
            .inSmoker(),

    ICE_CREAM_CONE = create(NorthstarItems.ICE_CREAM_CONE)
            .viaCooking(() -> NorthstarItems.RAW_ICE_CREAM_CONE)
            .rewardXP(0.4f)
            .inSmoker(),

    POLISHED_MARS_STONE = create(NorthstarBlocks.POLISHED_MARS_STONE)
            .viaCooking(() -> NorthstarBlocks.MARS_STONE)
            .rewardXP(0.1f)
            .inFurnace(),

    POLISHED_MERCURY_STONE = create(NorthstarBlocks.POLISHED_MERCURY_STONE)
            .viaCooking(() -> NorthstarBlocks.MERCURY_STONE)
            .rewardXP(0.1f)
            .inFurnace(),

    POLISHED_MOON_STONE = create(NorthstarBlocks.POLISHED_MOON_STONE)
            .viaCooking(() -> NorthstarBlocks.MOON_STONE)
            .rewardXP(0.1f)
            .inFurnace(),

    POLISHED_VENUS_STONE = create(NorthstarBlocks.POLISHED_VENUS_STONE)
            .viaCooking(() -> NorthstarBlocks.VENUS_STONE)
            .rewardXP(0.1f)
            .inFurnace(),

    MARTIAN_IRON_ORE = create(() -> Items.IRON_INGOT)
            .withSuffix("_from_martian_ore")
            .viaCooking(() -> NorthstarItems.RAW_MARTIAN_IRON_ORE)
            .rewardXP(0.7f)
            .inBlastFurnace(),

    TITANIUM = create(NorthstarItems.TITANIUM_INGOT)
            .withSuffix("_from_raw")
            .viaCooking(() -> NorthstarItems.RAW_TITANIUM_ORE)
            .rewardXP(0.8f)
            .inBlastFurnace(),

    TUNGSTEN = create(NorthstarItems.TUNGSTEN_INGOT)
            .withSuffix("_from_raw")
            .viaCooking(() -> NorthstarItems.RAW_TUNGSTEN_ORE)
            .rewardXP(0.8f)
            .inBlastFurnace(),

    TUNGSTEN_CRUSHED = create(NorthstarItems.TUNGSTEN_INGOT)
            .withSuffix("_from_crushed")
            .viaCooking(() -> NorthstarItems.CRUSHED_RAW_TUNGSTEN)
            .rewardXP(0.8f)
            .inBlastFurnace();

    // endregion

    private GeneratedRecipe createLamp(ItemProviderEntry<? extends ItemLike> lamp, Supplier<? extends ItemLike> casing, Supplier<? extends ItemLike> core) {
        return create(lamp)
                .returns(4)
                .unlockedBy(casing::get)
                .viaShaped(b -> b.define('#', casing.get())
                        .define('C', core.get())
                        .pattern(" # ")
                        .pattern("#C#")
                        .pattern(" # "));
    }

    private GeneratedRecipe createLamp(ItemProviderEntry<? extends ItemLike> lamp, Tags.Tag<Item> casing, Supplier<? extends ItemLike> core) {
        return create(lamp)
                .returns(4)
                .unlockedByTag(casing::tag)
                .viaShaped(b -> b.define('#', casing.tag())
                        .define('C', core.get())
                        .pattern(" # ")
                        .pattern("#C#")
                        .pattern(" # "));
    }

    private GeneratedRecipe createFourToFour(ItemLike result, ItemLike from) {
        return createFourToFour(result, from, "");
    }

    private GeneratedRecipe createFourToFour(ItemLike result, ItemLike from, String suffix) {
        return create(() -> result)
                .returns(4)
                .withSuffix(suffix)
                .unlockedBy(() -> from)
                .viaShaped(b -> b.define('#', from)
                        .pattern("##")
                        .pattern("##"));
    }

    public NorthstarStandardRecipeGen(PackOutput output) {
        super(output, Northstar.MOD_ID);

        interchangeable(NorthstarBlocks.MARTIAN_STEEL_PLATING, NorthstarBlocks.MARTIAN_STEEL_LARGE_PLATING);

        createMaterial(null, NorthstarItems.MARTIAN_STEEL, NorthstarBlocks.MARTIAN_STEEL_BLOCK);
        createMaterial(NorthstarItems.TITANIUM_NUGGET, NorthstarItems.TITANIUM_INGOT, NorthstarBlocks.TITANIUM_BLOCK);
        createMaterial(NorthstarItems.TUNGSTEN_NUGGET, NorthstarItems.TUNGSTEN_INGOT, NorthstarBlocks.TUNGSTEN_BLOCK);

        createOre(() -> NorthstarItemTags.SPACE_ORE_COPPER.tag, () -> Items.COPPER_INGOT, 0.7f, 200);
        createOre(() -> NorthstarItemTags.SPACE_ORE_DIAMOND.tag, () -> Items.DIAMOND, 1f, 200);
        createOre(() -> NorthstarItemTags.SPACE_ORE_GLOWSTONE.tag, () -> Items.GLOWSTONE_DUST, 0.7f, 100);
        createOre(() -> NorthstarItemTags.SPACE_ORE_GOLD.tag, () -> Items.GOLD_INGOT, 0.7f, 200);
        createOre(() -> NorthstarItemTags.SPACE_ORE_IRON.tag, () -> Items.IRON_INGOT, 0.7f, 200);
        createOre(() -> NorthstarItemTags.SPACE_ORE_LAPIS.tag, () -> Items.LAPIS_LAZULI, 0.7f, 200);
        createOre(() -> NorthstarItemTags.SPACE_ORE_QUARTZ.tag, () -> Items.QUARTZ, 0.7f, 200);
        createOre(() -> NorthstarItemTags.SPACE_ORE_REDSTONE.tag, () -> Items.REDSTONE, 0.7f, 100);
        createOre(() -> NorthstarItemTags.SPACE_ORE_TITANIUM.tag, () -> NorthstarItems.TITANIUM_INGOT, 0.3f, 500);
        createOre(() -> NorthstarItemTags.SPACE_ORE_TUNGSTEN.tag, () -> NorthstarItems.TUNGSTEN_INGOT, 0.7f, 100);
        createOre(() -> NorthstarItemTags.SPACE_ORE_ZINC.tag, () -> AllItems.ZINC_INGOT, 0.7f, 100);
    }

}

package com.lightning.northstar.content;

import com.lightning.northstar.block.crops.SeedItem;
import com.lightning.northstar.client.model.armor.IronSpaceSuitArmorModel;
import com.lightning.northstar.client.model.armor.MartianSteelSpaceSuitArmorModel;
import com.lightning.northstar.item.MartianFlowerItem;
import com.lightning.northstar.item.armor.BrokenIronSpaceSuitArmorItem;
import com.lightning.northstar.item.armor.SpaceSuitArmorItem;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;

import static com.lightning.northstar.Northstar.REGISTRATE;

public class NorthstarItems {

    static {
        REGISTRATE.setCreativeTab(NorthstarCreativeModeTab.ITEMS);
    }



    //Titanium
    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_TITANIUM_INGOT
            = sequencedIngredient("incomplete_titanium_ingot");

    public static final ItemEntry<Item> RAW_TITANIUM = REGISTRATE
            .item("raw_titanium_ore", Item::new)
           .register();

    public static final ItemEntry<Item> TITANIUM_INGOT = REGISTRATE
            .item("titanium_ingot", Item::new)
            .register();

    public static final ItemEntry<Item> TITANIUM_SHEET = REGISTRATE
            .item("titanium_sheet", Item::new)
            .register();

    public static final ItemEntry<Item> TITANIUM_NUGGET = REGISTRATE
            .item("titanium_nugget", Item::new)
            .register();


    //sand
//    public static final ItemEntry<Item> COMPRESSED_SAND_BAR = REGISTRATE
//            .item("compressed_sand_bar", Item::new)
//            .register();
//
//    public static final ItemEntry<Item> COMPRESSSED_SAND_TILE = REGISTRATE
//            .item("compressed_sand_tile", Item::new)
//            .register();


    public static final ItemEntry<Item> DURABLE_FABRIC = REGISTRATE
            .item("durable_fabric", Item::new)
            .register();

    public static final ItemEntry<Item> VOLCANIC_ASH = REGISTRATE
            .item("volcanic_ash_item", Item::new)
            .register();

    public static final ItemEntry<Item> ROCKET_COMBUSTION_CHAMBER = REGISTRATE
            .item("rocket_combustion_chamber", Item::new)
            .register();

    public static final ItemEntry<Item> OXYGEN_SEPARATOR = REGISTRATE
            .item("oxygen_separator", Item::new)
            .register();

    public static final ItemEntry<Item> HARDENED_PRECISION_MECHANISM = REGISTRATE
            .item("hardened_precision_mechanism", Item::new)
            .register();

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_HARDENED_PRECISION_MECHANISM
            = sequencedIngredient("incomplete_hardened_precision_mechanism");

    public static final ItemEntry<Item> RAW_MARTIAN_IRON_ORE = REGISTRATE
            .item("raw_martian_iron_ore", Item::new)
            .register();
    public static final ItemEntry<Item> RAW_TUNGSTEN_ORE = REGISTRATE
            .item("raw_tungsten_ore", Item::new)
            .register();
    public static final ItemEntry<Item> CRUSHED_RAW_TUNGSTEN = REGISTRATE
            .item("crushed_raw_tungsten", Item::new)
            .register();
    public static final ItemEntry<Item> TUNGSTEN_NUGGET = REGISTRATE
            .item("tungsten_nugget", Item::new)
            .register();
    public static final ItemEntry<Item> RAW_GLOWSTONE_ORE = REGISTRATE
            .item("raw_glowstone_ore", Item::new)
            .register();
    public static final ItemEntry<Item> ENRICHED_GLOWSTONE_ORE = REGISTRATE
            .item("enriched_glowstone_ore", Item::new) // TODO: tag & compatibility with Mekanism
            .register();

    public static final ItemEntry<Item> MARTIAN_STEEL = REGISTRATE
            .item("martian_steel", Item::new)
            .register();
    public static final ItemEntry<Item> MARTIAN_STEEL_SHEET = REGISTRATE
            .item("martian_steel_sheet", Item::new)
            .register();
    public static final ItemEntry<Item> TUNGSTEN_INGOT = REGISTRATE
            .item("tungsten_ingot", Item::new)
            .register();
    public static final ItemEntry<Item> TUNGSTEN_SHEET = REGISTRATE
            .item("tungsten_sheet", Item::new)
            .register();

    public static final ItemEntry<StandingAndWallBlockItem> EXTINGUISHED_TORCH = REGISTRATE
            .item("extinguished_torch", p -> new StandingAndWallBlockItem(NorthstarTechBlocks.EXTINGUISHED_TORCH.get(), NorthstarTechBlocks.EXTINGUISHED_TORCH_WALL.get(), p, Direction.DOWN))
            .register();
    public static final ItemEntry<StandingAndWallBlockItem> GLOWSTONE_TORCH = REGISTRATE
            .item("glowstone_torch", p -> new StandingAndWallBlockItem(NorthstarTechBlocks.GLOWSTONE_TORCH.get(), NorthstarTechBlocks.GLOWSTONE_TORCH_WALL.get(), p, Direction.DOWN))
            .register();

    public static final ItemEntry<Item> DORMANT_MARTIAN_SAPLING = REGISTRATE
            .item("dormant_martian_sapling", Item::new)
            .register();
    public static final ItemEntry<SequencedAssemblyItem> DORMANT_MARTIAN_SAPLING_SEQUENCED = sequencedIngredient("dormant_martian_sapling_sequenced");

    public static final ItemEntry<Item> DORMANT_MARTIAN_SEED = REGISTRATE
            .item("dormant_martian_seed", Item::new)
            .register();
    public static final ItemEntry<SequencedAssemblyItem> DORMANT_MARTIAN_SEED_SEQUENCED = sequencedIngredient("dormant_martian_seed_sequenced");

    // region armor and tools

    public static final ItemEntry<SwordItem> MARTIAN_SWORD = REGISTRATE
            .item("martian_sword", p -> new SwordItem(NorthstarToolTiers.MARTIAN_STEEL, 3, -2.4f, p))
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<PickaxeItem> MARTIAN_PICKAXE = REGISTRATE
            .item("martian_pickaxe", p -> new PickaxeItem(NorthstarToolTiers.MARTIAN_STEEL, 1, -2.8f, p))
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<ShovelItem> MARTIAN_SHOVEL = REGISTRATE
            .item("martian_shovel", p -> new ShovelItem(NorthstarToolTiers.MARTIAN_STEEL, 1.5f, -3.0f, p))
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<AxeItem> MARTIAN_AXE = REGISTRATE
            .item("martian_axe", p -> new AxeItem(NorthstarToolTiers.MARTIAN_STEEL, 5, -3.0f, p))
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<HoeItem> MARTIAN_HOE = REGISTRATE
            .item("martian_hoe", p -> new HoeItem(NorthstarToolTiers.MARTIAN_STEEL, -2, 0.0f, p))
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<ArmorItem> MARTIAN_STEEL_HELMET = REGISTRATE
            .item("martian_steel_helmet", p -> new ArmorItem(NorthstarArmorTiers.MARTIAN_STEEL_ARMOR, ArmorItem.Type.HELMET, p))
            .register();
    public static final ItemEntry<ArmorItem> MARTIAN_STEEL_CHESTPLATE = REGISTRATE
            .item("martian_steel_chestplate", p -> new ArmorItem(NorthstarArmorTiers.MARTIAN_STEEL_ARMOR, ArmorItem.Type.CHESTPLATE, p))
            .register();
    public static final ItemEntry<ArmorItem> MARTIAN_STEEL_LEGGINGS = REGISTRATE
            .item("martian_steel_leggings", p -> new ArmorItem(NorthstarArmorTiers.MARTIAN_STEEL_ARMOR, ArmorItem.Type.LEGGINGS, p))
            .register();
    public static final ItemEntry<ArmorItem> MARTIAN_STEEL_BOOTS = REGISTRATE
            .item("martian_steel_boots", p -> new ArmorItem(NorthstarArmorTiers.MARTIAN_STEEL_ARMOR, ArmorItem.Type.BOOTS, p))
            .register();

    public static final ItemEntry<SpaceSuitArmorItem> IRON_SPACE_SUIT_HELMET = REGISTRATE
            .item("iron_space_suit_helmet", p -> new SpaceSuitArmorItem(NorthstarArmorTiers.IRON_SPACE_SUIT, ArmorItem.Type.HELMET, p, IronSpaceSuitArmorModel::new))
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> IRON_SPACE_SUIT_CHESTPIECE = REGISTRATE
            .item("iron_space_suit_chestpiece", p -> new SpaceSuitArmorItem(NorthstarArmorTiers.IRON_SPACE_SUIT, ArmorItem.Type.CHESTPLATE, p, IronSpaceSuitArmorModel::new))
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> IRON_SPACE_SUIT_LEGGINGS = REGISTRATE
            .item("iron_space_suit_leggings", p -> new SpaceSuitArmorItem(NorthstarArmorTiers.IRON_SPACE_SUIT, ArmorItem.Type.LEGGINGS, p, IronSpaceSuitArmorModel::new))
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> IRON_SPACE_SUIT_BOOTS = REGISTRATE
            .item("iron_space_suit_boots", p -> new SpaceSuitArmorItem(NorthstarArmorTiers.IRON_SPACE_SUIT, ArmorItem.Type.BOOTS, p, IronSpaceSuitArmorModel::new))
            .register();

    public static final ItemEntry<SpaceSuitArmorItem> MARTIAN_STEEL_SPACE_SUIT_HELMET = REGISTRATE
            .item("martian_steel_space_suit_helmet", p -> new SpaceSuitArmorItem(NorthstarArmorTiers.MARTIAN_STEEL_SPACE_SUIT, ArmorItem.Type.HELMET, p, MartianSteelSpaceSuitArmorModel::new))
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> MARTIAN_STEEL_SPACE_SUIT_CHESTPIECE = REGISTRATE
            .item("martian_steel_space_suit_chestpiece", p -> new SpaceSuitArmorItem(NorthstarArmorTiers.MARTIAN_STEEL_SPACE_SUIT, ArmorItem.Type.CHESTPLATE, p, MartianSteelSpaceSuitArmorModel::new))
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> MARTIAN_STEEL_SPACE_SUIT_LEGGINGS = REGISTRATE
            .item("martian_steel_space_suit_leggings", p -> new SpaceSuitArmorItem(NorthstarArmorTiers.MARTIAN_STEEL_SPACE_SUIT, ArmorItem.Type.LEGGINGS, p, MartianSteelSpaceSuitArmorModel::new))
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> MARTIAN_STEEL_SPACE_SUIT_BOOTS = REGISTRATE
            .item("martian_steel_space_suit_boots", p -> new SpaceSuitArmorItem(NorthstarArmorTiers.MARTIAN_STEEL_SPACE_SUIT, ArmorItem.Type.BOOTS, p, MartianSteelSpaceSuitArmorModel::new))
            .register();

    public static final ItemEntry<BrokenIronSpaceSuitArmorItem> BROKEN_IRON_SPACE_SUIT_HELMET = REGISTRATE
            .item("broken_iron_space_suit_helmet", p -> new BrokenIronSpaceSuitArmorItem(NorthstarArmorTiers.IRON_SPACE_SUIT, ArmorItem.Type.HELMET, p))
            .register();
    public static final ItemEntry<BrokenIronSpaceSuitArmorItem> BROKEN_IRON_SPACE_SUIT_CHESTPIECE = REGISTRATE
            .item("broken_iron_space_suit_chestpiece", p -> new BrokenIronSpaceSuitArmorItem(NorthstarArmorTiers.IRON_SPACE_SUIT, ArmorItem.Type.CHESTPLATE, p))
            .register();
    public static final ItemEntry<BrokenIronSpaceSuitArmorItem> BROKEN_IRON_SPACE_SUIT_LEGGINGS = REGISTRATE
            .item("broken_iron_space_suit_leggings", p -> new BrokenIronSpaceSuitArmorItem(NorthstarArmorTiers.IRON_SPACE_SUIT, ArmorItem.Type.LEGGINGS, p))
            .register();
    public static final ItemEntry<BrokenIronSpaceSuitArmorItem> BROKEN_IRON_SPACE_SUIT_BOOTS = REGISTRATE
            .item("broken_iron_space_suit_boots", p -> new BrokenIronSpaceSuitArmorItem(NorthstarArmorTiers.IRON_SPACE_SUIT, ArmorItem.Type.BOOTS, p))
            .register();

    // endregion
    // region spawn eggs

    public static final ItemEntry<ForgeSpawnEggItem> MARS_WORM_SPAWN_EGG = REGISTRATE
            .item("mars_worm_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.MARS_WORM, 0xC3B1A9, 0x624234, p))
            .register();
    public static final ItemEntry<ForgeSpawnEggItem> MARS_TOAD_SPAWN_EGG = REGISTRATE
            .item("mars_toad_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.MARS_TOAD, 0xa3907c, 0x716252, p))
            .register();
    public static final ItemEntry<ForgeSpawnEggItem> MARS_COBRA_SPAWN_EGG = REGISTRATE
            .item("mars_cobra_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.MARS_COBRA, 0xc19c85, 0xccb086, p))
            .register();
    public static final ItemEntry<ForgeSpawnEggItem> MARS_MOTH_SPAWN_EGG = REGISTRATE
            .item("mars_moth_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.MARS_MOTH, 0xb35525, 0x493124, p))
            .register();

    public static final ItemEntry<ForgeSpawnEggItem> VENUS_MIMIC_SPAWN_EGG = REGISTRATE
            .item("venus_mimic_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.VENUS_MIMIC, 0x8e755b, 0x65553e, p))
            .register();
    public static final ItemEntry<ForgeSpawnEggItem> VENUS_SCORPION_SPAWN_EGG = REGISTRATE
            .item("venus_scorpion_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.VENUS_SCORPION, 0x8f7450, 0x6bc18d, p))
            .register();
    public static final ItemEntry<ForgeSpawnEggItem> VENUS_STONE_BULL_SPAWN_EGG = REGISTRATE
            .item("venus_stone_bull_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.VENUS_STONE_BULL, 0x79674f, 0x3a2417, p))
            .register();
    public static final ItemEntry<ForgeSpawnEggItem> VENUS_VULTURE_SPAWN_EGG = REGISTRATE
            .item("venus_vulture_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.VENUS_VULTURE, 0x99826a, 0x813024, p))
            .register();
    public static final ItemEntry<ForgeSpawnEggItem> FROZEN_ZOMBIE_SPAWN_EGG = REGISTRATE
            .item("frozen_zombie_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.FROZEN_ZOMBIE, 0x62a9bc, 0x4a695e, p))
            .register();


    public static final ItemEntry<ForgeSpawnEggItem> MOON_LUNARGRADE_SPAWN_EGG = REGISTRATE
            .item("moon_lunargrade_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.MOON_LUNARGRADE, 0xa3afb4, 0x2b424c, p))
            .register();
    public static final ItemEntry<ForgeSpawnEggItem> MOON_SNAIL_SPAWN_EGG = REGISTRATE
            .item("moon_snail_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.MOON_SNAIL, 0x7fab98, 0x3d676d, p))
            .register();
    public static final ItemEntry<ForgeSpawnEggItem> MOON_EEL_SPAWN_EGG = REGISTRATE
            .item("moon_eel_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.MOON_EEL, 0xa3afb4, 0x58223e, p))
            .register();

    public static final ItemEntry<ForgeSpawnEggItem> MERCURY_RAPTOR_SPAWN_EGG = REGISTRATE
            .item("mercury_raptor_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.MERCURY_RAPTOR, 0x88757f, 0x79636e, p))
            .register();
    public static final ItemEntry<ForgeSpawnEggItem> MERCURY_ROACH_SPAWN_EGG = REGISTRATE
            .item("mercury_roach_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.MERCURY_ROACH, 0x8f7683, 0x53424a, p))
            .register();
    public static final ItemEntry<ForgeSpawnEggItem> MERCURY_TORTOISE_SPAWN_EGG = REGISTRATE
            .item("mercury_tortoise_spawn_egg", p -> new ForgeSpawnEggItem(NorthstarEntityTypes.MERCURY_TORTOISE, 0x877b81, 0x6b5b64, p))
            .register();

    // endregion

    public static final ItemEntry<ItemNameBlockItem> MARS_TULIP_SEEDS = REGISTRATE
            .item("mars_tulip_seeds", p -> new ItemNameBlockItem(NorthstarBlocks.MARS_TULIP.get(), p))
            .register();
    public static final ItemEntry<ItemNameBlockItem> MARS_PALM_SEEDS = REGISTRATE
            .item("mars_palm_seeds", p -> new ItemNameBlockItem(NorthstarBlocks.MARS_PALM.get(), p))
            .register();
    public static final ItemEntry<ItemNameBlockItem> MARS_SPROUT_SEEDS = REGISTRATE
            .item("mars_sprout_seeds", p -> new ItemNameBlockItem(NorthstarBlocks.MARS_SPROUT.get(), p))
            .register();

    public static final ItemEntry<MartianFlowerItem> MARS_TULIP_FLOWER = REGISTRATE
            .item("mars_tulip_flower", p -> new MartianFlowerItem(NorthstarBlocks.MARS_TULIP.get(), p))
            .register();
    public static final ItemEntry<MartianFlowerItem> MARS_PALM_FLOWER = REGISTRATE
            .item("mars_palm_flower", p -> new MartianFlowerItem(NorthstarBlocks.MARS_PALM.get(), p))
            .register();
    public static final ItemEntry<MartianFlowerItem> MARS_SPROUT_FLOWER = REGISTRATE
            .item("mars_sprout_flower", p -> new MartianFlowerItem(NorthstarBlocks.MARS_SPROUT.get(), p))
            .register();

    // ice cream  :]
    public static final ItemEntry<Item> VANILLA_ICE_CREAM = REGISTRATE
            .item("vanilla_ice_cream", p -> new Item(p
                    .food((new FoodProperties.Builder()).nutrition(6).saturationMod(0.7F).build())))
            .register();
    public static final ItemEntry<Item> CHOCOLATE_ICE_CREAM = REGISTRATE
            .item("chocolate_ice_cream", Item::new)
            .properties(p -> p.food(new FoodProperties.Builder()
                    .nutrition(7)
                    .saturationMod(0.8F)
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 280, 0, false, false, true), 1.0F)
                    .build()))
            .register();
    public static final ItemEntry<Item> STRAWBERRY_ICE_CREAM = REGISTRATE
            .item("strawberry_ice_cream", Item::new)
            .properties(p -> p.food(new FoodProperties.Builder()
                    .nutrition(7)
                    .saturationMod(0.7F)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 280, 0, false, false, true), 1.0F)
                    .build()))
            .register();

    public static final ItemEntry<Item> FLATTENED_DOUGH = REGISTRATE
            .item("flattened_dough", Item::new)
            .register();

    public static final ItemEntry<Item> RAW_ICE_CREAM_CONE = REGISTRATE
            .item("raw_ice_cream_cone", Item::new)
            .register();
    public static final ItemEntry<Item> ICE_CREAM_CONE = REGISTRATE
            .item("ice_cream_cone", Item::new)
            .properties(p -> p.food(Foods.MELON_SLICE))
            .register();


    public static final ItemEntry<SeedItem> MARTIAN_STRAWBERRY = REGISTRATE
            .item("martian_strawberry", p -> new SeedItem(NorthstarBlocks.MARTIAN_STRAWBERRY_BUSH.get(), p))
            .properties(p -> p.food(Foods.APPLE))
            .register();


    public static final ItemEntry<Item> ASTRONOMICAL_READING = REGISTRATE
            .item("astronomical_reading", Item::new)
            .register();
    public static final ItemEntry<Item> STAR_MAP = REGISTRATE
            .item("star_map", Item::new)
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<Item> RETURN_TICKET = REGISTRATE
            .item("return_ticket", p -> new Item(p.stacksTo(1)))
            .register();

    public static final ItemEntry<Item> LUNAR_SAPPHIRE_SHARD = REGISTRATE
            .item("lunar_sapphire_shard", Item::new)
            .register();

    public static final ItemEntry<Item> POLISHED_LUNAR_SAPPHIRE = REGISTRATE
            .item("polished_lunar_sapphire", Item::new)
            .register();
    public static final ItemEntry<Item> POLISHED_DIAMOND = REGISTRATE
            .item("polished_diamond", Item::new)
            .register();
    public static final ItemEntry<Item> POLISHED_AMETHYST = REGISTRATE
            .item("polished_amethyst", Item::new)
            .register();

    public static final ItemEntry<Item> TARGETING_COMPUTER = REGISTRATE
            .item("targeting_computer", Item::new)
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<SequencedAssemblyItem> UNFINISHED_TARGETING_COMPUTER = sequencedIngredient("unfinished_targeting_computer");

    public static final ItemEntry<Item> CIRCUIT = REGISTRATE
            .item("circuit", Item::new)
            .register();
    public static final ItemEntry<SequencedAssemblyItem> UNFINISHED_CIRCUIT = sequencedIngredient("unfinished_circuit");

    public static final ItemEntry<Item> ADVANCED_CIRCUIT = REGISTRATE
            .item("advanced_circuit", Item::new)
            .register();
    public static final ItemEntry<SequencedAssemblyItem> UNFINISHED_ADVANCED_CIRCUIT = sequencedIngredient("unfinished_advanced_circuit");

    public static final ItemEntry<SandPaperItem> MOON_SAND_PAPER = REGISTRATE
            .item("moon_sand_paper", SandPaperItem::new)
            .tag(AllTags.AllItemTags.SANDPAPER.tag)
            .properties(p -> p.defaultDurability(512))
            .register();

    //Chlorine
    public static final ItemEntry<Item> SALT = REGISTRATE
            .item("salt", Item::new)
            .register();

    //An alternative to chlorine (pre-electrolysis)
    public static final ItemEntry<Item> SODIUM_CATALYST = REGISTRATE
            .item("sodium_catalyst", Item::new)
            .register();

    //minerals
    public static final ItemEntry<Item> RUITILE_CONCENTRATE = REGISTRATE
            .item("rutile_concentrate", Item::new)
            .register();

    public static final ItemEntry<Item> DRY_PLANT_FIBER = REGISTRATE
            .item("dry_plant_fiber", Item::new)
            .register();

    public static final RegistryEntry<BucketItem> TITANIUM_TETRACHLORIDE_BUCKET =
            REGISTRATE.item("tetrachloride_bucket",
                            props -> new BucketItem(() -> NorthstarFluids.TITANIUM_TETRACHLORIDE.getSource(),
                                    props.stacksTo(1).craftRemainder(Items.BUCKET)))
                    .register();

    public static void register() {
    }

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        try {
            REGISTRATE.setCreativeTab(null);

            return REGISTRATE
                    .item(name, SequencedAssemblyItem::new)
                    .register();
        } finally {
            REGISTRATE.setCreativeTab(NorthstarCreativeModeTab.ITEMS);
        }
    }

}

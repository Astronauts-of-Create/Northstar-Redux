package com.lightning.northstar.content;

import com.lightning.northstar.block.crops.SeedItem;
import com.lightning.northstar.client.model.armor.IronSpaceSuitArmorModel;
import com.lightning.northstar.client.model.armor.MartianSteelSpaceSuitArmorModel;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.item.MartianFlowerItem;
import com.lightning.northstar.item.armor.BrokenIronSpaceSuitArmorItem;
import com.lightning.northstar.item.armor.SpaceSuitArmorItem;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Consumer;

import static com.lightning.northstar.Northstar.REGISTRATE;

public class NorthstarItems {

    static {
        REGISTRATE.setCreativeTab(NorthstarCreativeModeTab.ITEMS);
    }

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_TITANIUM_INGOT =
            sequencedIngredient("incomplete_titanium_ingot",
                    b -> b.model((c, p) -> p.generated(c::get, p.modLoc("item/titanium_incomplete")))
                            .lang("Unforged Titanium Ingot"));

    public static final ItemEntry<Item> RAW_TITANIUM = REGISTRATE
            .item("raw_titanium_ore", Item::new)
            .model((c, p) -> p.generated(c::get, p.modLoc("item/raw_titanium")))
            .register();

    public static final ItemEntry<Item> TITANIUM_INGOT = REGISTRATE
            .item("titanium_ingot", Item::new)
            .tag(NorthstarItemTags.C_INGOTS.tag)
            .tag(NorthstarItemTags.C_INGOTS_TITANIUM.tag)
            .register();

    public static final ItemEntry<Item> TITANIUM_SHEET = REGISTRATE
            .item("titanium_sheet", Item::new)
            .tag(NorthstarItemTags.C_SHEETS.tag)
            .tag(NorthstarItemTags.C_SHEETS_TITANIUM.tag)
            .register();

    public static final ItemEntry<Item> TITANIUM_NUGGET = REGISTRATE
            .item("titanium_nugget", Item::new)
            .tag(NorthstarItemTags.C_NUGGETS.tag)
            .tag(NorthstarItemTags.C_NUGGETS_TITANIUM.tag)
            .register();

    public static final ItemEntry<Item> DURABLE_FABRIC = REGISTRATE
            .item("durable_fabric", Item::new)
            .register();

    // TODO: "dusts/ash", "dusts/volcanic_ash"?
    public static final ItemEntry<Item> VOLCANIC_ASH = REGISTRATE
            .item("volcanic_ash_item", Item::new)
            .tag(NorthstarItemTags.C_DUSTS.tag)
            .model((c, p) -> p.generated(c::get, p.modLoc("item/volcanic_ash")))
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
            .tag(NorthstarItemTags.C_NUGGETS.tag)
            .tag(NorthstarItemTags.C_NUGGETS_TUNGSTEN.tag)
            .register();

    public static final ItemEntry<Item> RAW_GLOWSTONE_ORE = REGISTRATE
            .item("raw_glowstone_ore", Item::new)
            .register();

    public static final ItemEntry<Item> ENRICHED_GLOWSTONE_ORE = REGISTRATE
            .item("enriched_glowstone_ore", Item::new)
            .register();

    // TODO: rename to ingot
    public static final ItemEntry<Item> MARTIAN_STEEL = REGISTRATE
            .item("martian_steel", Item::new)
            .lang("Martian Steel Ingot")
            .tag(NorthstarItemTags.C_INGOTS.tag)
            .tag(NorthstarItemTags.C_INGOTS_MARTIAN_STEEL.tag)
            .register();

    public static final ItemEntry<Item> MARTIAN_STEEL_SHEET = REGISTRATE
            .item("martian_steel_sheet", Item::new)
            .tag(NorthstarItemTags.C_SHEETS.tag)
            .tag(NorthstarItemTags.C_SHEETS_MARTIAN_STEEL.tag)
            .register();

    public static final ItemEntry<Item> TUNGSTEN_INGOT = REGISTRATE
            .item("tungsten_ingot", Item::new)
            .tag(NorthstarItemTags.C_INGOTS.tag)
            .tag(NorthstarItemTags.C_INGOTS_TUNGSTEN.tag)
            .register();

    public static final ItemEntry<Item> TUNGSTEN_SHEET = REGISTRATE
            .item("tungsten_sheet", Item::new)
            .tag(NorthstarItemTags.C_SHEETS.tag)
            .tag(NorthstarItemTags.C_SHEETS_TUNGSTEN.tag)
            .register();

    public static final ItemEntry<Item> DORMANT_MARTIAN_SAPLING = REGISTRATE
            .item("dormant_martian_sapling", Item::new)
            .register();

    public static final ItemEntry<SequencedAssemblyItem> DORMANT_MARTIAN_SAPLING_SEQUENCED =
            sequencedIngredient("dormant_martian_sapling_sequenced", b ->
                    b.model((c, p) -> p.generated(c::get, p.modLoc("item/dormant_martian_sapling"))));

    public static final ItemEntry<Item> DORMANT_MARTIAN_SEED = REGISTRATE
            .item("dormant_martian_seed", Item::new)
            .register();
    public static final ItemEntry<SequencedAssemblyItem> DORMANT_MARTIAN_SEED_SEQUENCED =
            sequencedIngredient("dormant_martian_seed_sequenced", b ->
                    b.model((c, p) -> p.generated(c::get, p.modLoc("item/dormant_martian_seed"))));

    // region armor and tools

    public static final ItemEntry<SwordItem> MARTIAN_SWORD = REGISTRATE
            .item("martian_sword", p -> new SwordItem(NorthstarToolTiers.MARTIAN_STEEL, p))
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<PickaxeItem> MARTIAN_PICKAXE = REGISTRATE
            .item("martian_pickaxe", p -> new PickaxeItem(NorthstarToolTiers.MARTIAN_STEEL, p))
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<ShovelItem> MARTIAN_SHOVEL = REGISTRATE
            .item("martian_shovel", p -> new ShovelItem(NorthstarToolTiers.MARTIAN_STEEL, p))
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<AxeItem> MARTIAN_AXE = REGISTRATE
            .item("martian_axe", p -> new AxeItem(NorthstarToolTiers.MARTIAN_STEEL, p))
            .properties(p -> p.stacksTo(1))
            .register();
    public static final ItemEntry<HoeItem> MARTIAN_HOE = REGISTRATE
            .item("martian_hoe", p -> new HoeItem(NorthstarToolTiers.MARTIAN_STEEL, p))
            .properties(p -> p.stacksTo(1))
            .register();

    public static final ItemEntry<ArmorItem> MARTIAN_STEEL_HELMET = REGISTRATE
            .item("martian_steel_helmet", p -> new ArmorItem(NorthstarArmorMaterials.MARTIAN_STEEL_ARMOR, ArmorItem.Type.HELMET, p))
            .register();
    public static final ItemEntry<ArmorItem> MARTIAN_STEEL_CHESTPLATE = REGISTRATE
            .item("martian_steel_chestplate", p -> new ArmorItem(NorthstarArmorMaterials.MARTIAN_STEEL_ARMOR, ArmorItem.Type.CHESTPLATE, p))
            .register();
    public static final ItemEntry<ArmorItem> MARTIAN_STEEL_LEGGINGS = REGISTRATE
            .item("martian_steel_leggings", p -> new ArmorItem(NorthstarArmorMaterials.MARTIAN_STEEL_ARMOR, ArmorItem.Type.LEGGINGS, p))
            .register();
    public static final ItemEntry<ArmorItem> MARTIAN_STEEL_BOOTS = REGISTRATE
            .item("martian_steel_boots", p -> new ArmorItem(NorthstarArmorMaterials.MARTIAN_STEEL_ARMOR, ArmorItem.Type.BOOTS, p))
            .register();

    public static final ItemEntry<SpaceSuitArmorItem> IRON_SPACE_SUIT_HELMET = REGISTRATE
            .item("iron_space_suit_helmet", p -> new SpaceSuitArmorItem(NorthstarArmorMaterials.IRON_SPACE_SUIT, ArmorItem.Type.HELMET, p, IronSpaceSuitArmorModel::new))
            .tag(NorthstarItemTags.OXYGEN_SEALING.tag)
            .tag(NorthstarItemTags.INSULATING.tag)
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> IRON_SPACE_SUIT_CHESTPIECE = REGISTRATE
            .item("iron_space_suit_chestpiece", p -> new SpaceSuitArmorItem(NorthstarArmorMaterials.IRON_SPACE_SUIT, ArmorItem.Type.CHESTPLATE, p, IronSpaceSuitArmorModel::new))
            .tag(NorthstarItemTags.OXYGEN_SEALING.tag)
            .tag(NorthstarItemTags.OXYGEN_SOURCES.tag)
            .tag(NorthstarItemTags.INSULATING.tag)
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> IRON_SPACE_SUIT_LEGGINGS = REGISTRATE
            .item("iron_space_suit_leggings", p -> new SpaceSuitArmorItem(NorthstarArmorMaterials.IRON_SPACE_SUIT, ArmorItem.Type.LEGGINGS, p, IronSpaceSuitArmorModel::new))
            .tag(NorthstarItemTags.OXYGEN_SEALING.tag)
            .tag(NorthstarItemTags.INSULATING.tag)
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> IRON_SPACE_SUIT_BOOTS = REGISTRATE
            .item("iron_space_suit_boots", p -> new SpaceSuitArmorItem(NorthstarArmorMaterials.IRON_SPACE_SUIT, ArmorItem.Type.BOOTS, p, IronSpaceSuitArmorModel::new))
            .tag(NorthstarItemTags.OXYGEN_SEALING.tag)
            .tag(NorthstarItemTags.INSULATING.tag)
            .register();

    public static final ItemEntry<SpaceSuitArmorItem> MARTIAN_STEEL_SPACE_SUIT_HELMET = REGISTRATE
            .item("martian_steel_space_suit_helmet", p -> new SpaceSuitArmorItem(NorthstarArmorMaterials.MARTIAN_STEEL_SPACE_SUIT, ArmorItem.Type.HELMET, p, MartianSteelSpaceSuitArmorModel::new))
            .tag(NorthstarItemTags.OXYGEN_SEALING.tag)
            .tag(NorthstarItemTags.HEAT_RESISTANT.tag)
            .tag(NorthstarItemTags.INSULATING.tag)
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> MARTIAN_STEEL_SPACE_SUIT_CHESTPIECE = REGISTRATE
            .item("martian_steel_space_suit_chestpiece", p -> new SpaceSuitArmorItem(NorthstarArmorMaterials.MARTIAN_STEEL_SPACE_SUIT, ArmorItem.Type.CHESTPLATE, p, MartianSteelSpaceSuitArmorModel::new))
            .tag(NorthstarItemTags.OXYGEN_SEALING.tag)
            .tag(NorthstarItemTags.OXYGEN_SOURCES.tag)
            .tag(NorthstarItemTags.HEAT_RESISTANT.tag)
            .tag(NorthstarItemTags.INSULATING.tag)
            .lang("Martian Steel Space Suit Chestplate")
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> MARTIAN_STEEL_SPACE_SUIT_LEGGINGS = REGISTRATE
            .item("martian_steel_space_suit_leggings", p -> new SpaceSuitArmorItem(NorthstarArmorMaterials.MARTIAN_STEEL_SPACE_SUIT, ArmorItem.Type.LEGGINGS, p, MartianSteelSpaceSuitArmorModel::new))
            .tag(NorthstarItemTags.OXYGEN_SEALING.tag)
            .tag(NorthstarItemTags.HEAT_RESISTANT.tag)
            .tag(NorthstarItemTags.INSULATING.tag)
            .register();
    public static final ItemEntry<SpaceSuitArmorItem> MARTIAN_STEEL_SPACE_SUIT_BOOTS = REGISTRATE
            .item("martian_steel_space_suit_boots", p -> new SpaceSuitArmorItem(NorthstarArmorMaterials.MARTIAN_STEEL_SPACE_SUIT, ArmorItem.Type.BOOTS, p, MartianSteelSpaceSuitArmorModel::new))
            .tag(NorthstarItemTags.OXYGEN_SEALING.tag)
            .tag(NorthstarItemTags.HEAT_RESISTANT.tag)
            .tag(NorthstarItemTags.INSULATING.tag)
            .register();

    public static final ItemEntry<BrokenIronSpaceSuitArmorItem> BROKEN_IRON_SPACE_SUIT_HELMET = REGISTRATE
            .item("broken_iron_space_suit_helmet", p -> new BrokenIronSpaceSuitArmorItem(NorthstarArmorMaterials.IRON_SPACE_SUIT, ArmorItem.Type.HELMET, p))
            .register();
    public static final ItemEntry<BrokenIronSpaceSuitArmorItem> BROKEN_IRON_SPACE_SUIT_CHESTPIECE = REGISTRATE
            .item("broken_iron_space_suit_chestpiece", p -> new BrokenIronSpaceSuitArmorItem(NorthstarArmorMaterials.IRON_SPACE_SUIT, ArmorItem.Type.CHESTPLATE, p))
            .register();
    public static final ItemEntry<BrokenIronSpaceSuitArmorItem> BROKEN_IRON_SPACE_SUIT_LEGGINGS = REGISTRATE
            .item("broken_iron_space_suit_leggings", p -> new BrokenIronSpaceSuitArmorItem(NorthstarArmorMaterials.IRON_SPACE_SUIT, ArmorItem.Type.LEGGINGS, p))
            .register();
    public static final ItemEntry<BrokenIronSpaceSuitArmorItem> BROKEN_IRON_SPACE_SUIT_BOOTS = REGISTRATE
            .item("broken_iron_space_suit_boots", p -> new BrokenIronSpaceSuitArmorItem(NorthstarArmorMaterials.IRON_SPACE_SUIT, ArmorItem.Type.BOOTS, p))
            .register();

    // endregion
    // region spawn eggs

    public static final ItemEntry<DeferredSpawnEggItem> MARS_WORM_SPAWN_EGG = REGISTRATE
            .item("mars_worm_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.MARS_WORM, 0xC3B1A9, 0x624234, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .lang("Mars Echo Worm Spawn Egg")
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> MARS_TOAD_SPAWN_EGG = REGISTRATE
            .item("mars_toad_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.MARS_TOAD, 0xa3907c, 0x716252, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .lang("Mars Root Toad Spawn Egg")
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> MARS_COBRA_SPAWN_EGG = REGISTRATE
            .item("mars_cobra_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.MARS_COBRA, 0xc19c85, 0xccb086, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> MARS_MOTH_SPAWN_EGG = REGISTRATE
            .item("mars_moth_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.MARS_MOTH, 0xb35525, 0x493124, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .lang("Mars Devil Moth Spawn Egg")
            .register();

    public static final ItemEntry<DeferredSpawnEggItem> VENUS_MIMIC_SPAWN_EGG = REGISTRATE
            .item("venus_mimic_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.VENUS_MIMIC, 0x8e755b, 0x65553e, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> VENUS_SCORPION_SPAWN_EGG = REGISTRATE
            .item("venus_scorpion_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.VENUS_SCORPION, 0x8f7450, 0x6bc18d, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> VENUS_STONE_BULL_SPAWN_EGG = REGISTRATE
            .item("venus_stone_bull_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.VENUS_STONE_BULL, 0x79674f, 0x3a2417, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> VENUS_VULTURE_SPAWN_EGG = REGISTRATE
            .item("venus_vulture_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.VENUS_VULTURE, 0x99826a, 0x813024, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> FROZEN_ZOMBIE_SPAWN_EGG = REGISTRATE
            .item("frozen_zombie_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.FROZEN_ZOMBIE, 0x62a9bc, 0x4a695e, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .register();


    public static final ItemEntry<DeferredSpawnEggItem> MOON_LUNARGRADE_SPAWN_EGG = REGISTRATE
            .item("moon_lunargrade_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.MOON_LUNARGRADE, 0xa3afb4, 0x2b424c, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> MOON_SNAIL_SPAWN_EGG = REGISTRATE
            .item("moon_snail_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.MOON_SNAIL, 0x7fab98, 0x3d676d, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> MOON_EEL_SPAWN_EGG = REGISTRATE
            .item("moon_eel_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.MOON_EEL, 0xa3afb4, 0x58223e, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .register();

    public static final ItemEntry<DeferredSpawnEggItem> MERCURY_RAPTOR_SPAWN_EGG = REGISTRATE
            .item("mercury_raptor_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.MERCURY_RAPTOR, 0x88757f, 0x79636e, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> MERCURY_ROACH_SPAWN_EGG = REGISTRATE
            .item("mercury_roach_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.MERCURY_ROACH, 0x8f7683, 0x53424a, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
            .register();
    public static final ItemEntry<DeferredSpawnEggItem> MERCURY_TORTOISE_SPAWN_EGG = REGISTRATE
            .item("mercury_tortoise_spawn_egg", p -> new DeferredSpawnEggItem(NorthstarEntityTypes.MERCURY_TORTOISE, 0x877b81, 0x6b5b64, p))
            .model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/template_spawn_egg")))
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
            .model((c, p) -> p.generated(c::get, p.modLoc("block/mars_tulip")))
            .register();

    public static final ItemEntry<MartianFlowerItem> MARS_PALM_FLOWER = REGISTRATE
            .item("mars_palm_flower", p -> new MartianFlowerItem(NorthstarBlocks.MARS_PALM.get(), p))
            .model((c, p) -> p.generated(c::get, p.modLoc("block/mars_palm")))
            .register();

    public static final ItemEntry<MartianFlowerItem> MARS_SPROUT_FLOWER = REGISTRATE
            .item("mars_sprout_flower", p -> new MartianFlowerItem(NorthstarBlocks.MARS_SPROUT.get(), p))
            .model((c, p) -> p.generated(c::get, p.modLoc("block/mars_sprout")))
            .register();

    // ice cream  :]
    public static final ItemEntry<Item> VANILLA_ICE_CREAM = REGISTRATE
            .item("vanilla_ice_cream", Item::new)
            .properties(p -> p.food(new FoodProperties.Builder()
                    .nutrition(6)
                    .saturationModifier(0.7F)
                    .build()))
            .model((c, p) -> p.generated(c::get, p.modLoc("item/ice_cream_vanilla")))
            .register();

    public static final ItemEntry<Item> CHOCOLATE_ICE_CREAM = REGISTRATE
            .item("chocolate_ice_cream", Item::new)
            .properties(p -> p.food(new FoodProperties.Builder()
                    .nutrition(7)
                    .saturationModifier(0.8F)
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 280, 0, false, false, true), 1.0F)
                    .build()))
            .model((c, p) -> p.generated(c::get, p.modLoc("item/ice_cream_chocolate")))
            .register();

    public static final ItemEntry<Item> STRAWBERRY_ICE_CREAM = REGISTRATE
            .item("strawberry_ice_cream", Item::new)
            .properties(p -> p.food(new FoodProperties.Builder()
                    .nutrition(7)
                    .saturationModifier(0.7F)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 280, 0, false, false, true), 1.0F)
                    .build()))
            .model((c, p) -> p.generated(c::get, p.modLoc("item/ice_cream_strawberry")))
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

    public static final ItemEntry<SequencedAssemblyItem> UNFINISHED_TARGETING_COMPUTER =
            sequencedIngredient("unfinished_targeting_computer");

    public static final ItemEntry<Item> CIRCUIT = REGISTRATE
            .item("circuit", Item::new)
            .register();

    public static final ItemEntry<SequencedAssemblyItem> UNFINISHED_CIRCUIT =
            sequencedIngredient("unfinished_circuit");

    public static final ItemEntry<Item> ADVANCED_CIRCUIT = REGISTRATE
            .item("advanced_circuit", Item::new)
            .register();

    public static final ItemEntry<SequencedAssemblyItem> UNFINISHED_ADVANCED_CIRCUIT =
            sequencedIngredient("unfinished_advanced_circuit");

    public static final ItemEntry<SandPaperItem> MOON_SAND_PAPER = REGISTRATE
            .item("moon_sand_paper", SandPaperItem::new)
            .tag(AllItemTags.SANDPAPER.tag)
            .properties(p -> p.durability(512))
            .register();

    public static final ItemEntry<Item> SALT = REGISTRATE
            .item("salt", Item::new)
            .tag(NorthstarItemTags.C_DUSTS.tag)
            .tag(NorthstarItemTags.C_DUSTS_SALT.tag)
            .register();

    public static final ItemEntry<Item> SODIUM_CATALYST = REGISTRATE
            .item("sodium_catalyst", Item::new)
            .register();

    public static final ItemEntry<Item> RUTILE_CONCENTRATE = REGISTRATE
            .item("rutile_concentrate", Item::new)
            .register();

    public static final ItemEntry<Item> DRY_PLANT_FIBER = REGISTRATE
            .item("dry_plant_fiber", Item::new)
            .register();

    public static void register() {
    }

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        return sequencedIngredient(name, null);
    }

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name, Consumer<ItemBuilder<SequencedAssemblyItem, CreateRegistrate>> builder) {
        try {
            REGISTRATE.setCreativeTab(null);

            return REGISTRATE
                    .item(name, SequencedAssemblyItem::new)
                    .transform(b -> {
                        if (builder != null)
                            builder.accept(b);
                        return b;
                    })
                    .register();
        } finally {
            REGISTRATE.setCreativeTab(NorthstarCreativeModeTab.ITEMS);
        }
    }

}

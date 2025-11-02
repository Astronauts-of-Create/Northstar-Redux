package com.lightning.northstar.data;

import com.drmangotea.tfmg.registry.TFMGFluids;
import com.jesz.createdieselgenerators.blocks.BlockRegistry;
import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarDamageTypes;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.lightning.northstar.Northstar.REGISTRATE;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NorthstarTagGen {

    public static void register() {
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, NorthstarTagGen::blocks);
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, NorthstarTagGen::items);
        REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, NorthstarTagGen::entities);
        REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, NorthstarTagGen::fluids);
    }

    private static void blocks(RegistrateTagsProvider<Block> provider) {
        Tags<Block, Block> tags = new Tags<>(provider, Block::builtInRegistryHolder, Function.identity());

        for (NorthstarBlockTags tag : NorthstarBlockTags.values()) {
            if (tag.alwaysDataGen)
                tags.tag(tag);
        }

        tags.tag(NorthstarBlockTags.AIR_PASSES_THROUGH)
                .add(NorthstarBlockTags.BLOCKS_AIR)
                .add(Blocks.OAK_DOOR, Blocks.OAK_TRAPDOOR, Blocks.JUNGLE_DOOR, Blocks.JUNGLE_TRAPDOOR,
                        Blocks.ACACIA_DOOR, Blocks.ACACIA_TRAPDOOR, Blocks.MANGROVE_TRAPDOOR,
                        Blocks.CHERRY_DOOR, Blocks.CHERRY_TRAPDOOR, Blocks.BAMBOO_DOOR, Blocks.BAMBOO_TRAPDOOR,
                        Blocks.CRIMSON_TRAPDOOR, Blocks.WARPED_TRAPDOOR, Blocks.IRON_DOOR, Blocks.IRON_TRAPDOOR,
                        Blocks.AZALEA, Blocks.FLOWERING_AZALEA, Blocks.SCULK_SHRIEKER, Blocks.COBWEB,
                        Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, Blocks.CAULDRON, Blocks.LADDER, Blocks.SCAFFOLDING,
                        Blocks.RAIL, Blocks.POWERED_RAIL, Blocks.DETECTOR_RAIL, Blocks.ACTIVATOR_RAIL,
                        Blocks.LIGHT)
                .add(BlockTags.FIRE)
                .add(BlockTags.LEAVES)

                .add(AllBlocks.BLAZE_BURNER.get(), AllBlocks.ANDESITE_LADDER.get(), AllBlocks.BRASS_LADDER.get(),
                        AllBlocks.COPPER_LADDER.get(), AllBlocks.ANDESITE_SCAFFOLD.get(), AllBlocks.BRASS_SCAFFOLD.get(),
                        AllBlocks.COPPER_SCAFFOLD.get());

        tags.tag(NorthstarBlockTags.ARGYRE_REPLACEABLE)
                .add(Blocks.AIR, Blocks.CAVE_AIR,
                        Blocks.ANDESITE, Blocks.BASALT)
                .add(AllPaletteStoneTypes.CRIMSITE.baseBlock.get());

        tags.tag(NorthstarBlockTags.BASE_STONE_MARS)
                .add(Blocks.ANDESITE, Blocks.BASALT, Blocks.DEEPSLATE, Blocks.TUFF)
                .add(AllPaletteStoneTypes.CRIMSITE.baseBlock.get());

        tags.tag(NorthstarBlockTags.BASE_STONE_MOON)
                .add(Blocks.SMOOTH_BASALT);

        tags.tag(NorthstarBlockTags.BASE_STONE_VENUS)
                .add(Blocks.TUFF);

        tags.tag(NorthstarBlockTags.BLOCKS_AIR)
                .add(Blocks.GLASS_PANE,
                        Blocks.WHITE_STAINED_GLASS_PANE,
                        Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
                        Blocks.GRAY_STAINED_GLASS_PANE,
                        Blocks.BLACK_STAINED_GLASS_PANE,
                        Blocks.BROWN_STAINED_GLASS_PANE,
                        Blocks.RED_STAINED_GLASS_PANE,
                        Blocks.ORANGE_STAINED_GLASS_PANE,
                        Blocks.YELLOW_STAINED_GLASS_PANE,
                        Blocks.LIME_STAINED_GLASS_PANE,
                        Blocks.GREEN_STAINED_GLASS_PANE,
                        Blocks.CYAN_STAINED_GLASS_PANE,
                        Blocks.LIGHT_BLUE_STAINED_GLASS_PANE,
                        Blocks.BLUE_STAINED_GLASS_PANE,
                        Blocks.PURPLE_STAINED_GLASS_PANE,
                        Blocks.MAGENTA_STAINED_GLASS_PANE,
                        Blocks.PINK_STAINED_GLASS_PANE)
                .add(BlockTags.WALLS)
                .add(Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER)
                .opt(BlockRegistry.HUGE_DIESEL_ENGINE.get());

        tags.tag(NorthstarBlockTags.MARS_DEEP_STONE_REPLACEABLE)
                .add(Blocks.TUFF)
                .add(AllPaletteStoneTypes.CRIMSITE.baseBlock.get());

        tags.tag(NorthstarBlockTags.MARS_STONE_REPLACEABLE)
                .add(Blocks.ANDESITE)
                .add(AllPaletteStoneTypes.CRIMSITE.baseBlock.get());

        tags.tag(NorthstarBlockTags.MERCURY_DEEP_STONE_REPLACEABLE)
                .add(Blocks.BLACKSTONE)
                .add(AllPaletteStoneTypes.CRIMSITE.baseBlock.get());

        tags.tag(NorthstarBlockTags.MERCURY_STONE_REPLACEABLE)
                .add(Blocks.ANDESITE);

        tags.tag(NorthstarBlockTags.MOON_DEEP_STONE_REPLACEABLE)
                .add(Blocks.ANDESITE, Blocks.SMOOTH_BASALT, Blocks.PACKED_ICE)
                .add(AllPaletteStoneTypes.ASURINE.baseBlock.get());

        tags.tag(NorthstarBlockTags.MOON_STONE_REPLACEABLE)
                .add(Blocks.ANDESITE);

        tags.tag(NorthstarBlockTags.NATURAL_MARS_BLOCKS)
                .add(Blocks.ANDESITE, Blocks.BASALT)
                .add(AllPaletteStoneTypes.CRIMSITE.baseBlock.get());

        tags.tag(NorthstarBlockTags.NATURAL_MERCURY_BLOCKS)
                .add(Blocks.ANDESITE, Blocks.BLACKSTONE)
                .add(AllPaletteStoneTypes.SCORCHIA.baseBlock.get());

        tags.tag(NorthstarBlockTags.NATURAL_MOON_BLOCKS)
                .add(Blocks.BLACKSTONE, Blocks.PACKED_ICE)
                .add(AllPaletteStoneTypes.ASURINE.baseBlock.get());

        tags.tag(NorthstarBlockTags.NATURAL_VENUS_BLOCKS)
                .add(Blocks.BASALT, Blocks.TUFF)
                .add(AllPaletteStoneTypes.SCORCHIA.baseBlock.get());

        tags.tag(NorthstarBlockTags.TIER_1_HEAT_RESISTANCE)
                .add(Blocks.IRON_BLOCK)
                .add(AllBlocks.INDUSTRIAL_IRON_BLOCK.get());

        tags.tag(NorthstarBlockTags.HEAVY_BLOCKS)
                .add(Blocks.IRON_BLOCK);

        tags.tag(NorthstarBlockTags.SUPER_HEAVY_BLOCKS)
                .add(Blocks.GOLD_BLOCK);

        tags.tag(NorthstarBlockTags.VENUS_DEEP_STONE_REPLACEABLE)
                .add(Blocks.TUFF)
                .add(AllPaletteStoneTypes.SCORCHIA.baseBlock.get());

        tags.tag(NorthstarBlockTags.VENUS_STONE_REPLACEABLE)
                .add(AllPaletteStoneTypes.SCORCHIA.baseBlock.get())
                .add(AllPaletteStoneTypes.OCHRUM.baseBlock.get());
    }

    private static void items(RegistrateTagsProvider<Item> provider) {
        Tags<Item, ItemLike> tags = new Tags<>(provider, Item::builtInRegistryHolder, ItemLike::asItem);

        for (NorthstarItemTags tag : NorthstarItemTags.values()) {
            if (tag.alwaysDataGen)
                tags.tag(tag);
        }

        tags.tag(NorthstarItemTags.IGNITION_SOURCE)
                .add(Items.FLINT_AND_STEEL)
                .add(Items.FIRE_CHARGE)

                .opt(ModCompat.MEK, "flamethrower");

        tags.tag(NorthstarItemTags.HEAT_RESISTANT)
                .add(AllItems.NETHERITE_DIVING_HELMET)
                .add(AllItems.NETHERITE_BACKTANK)
                .add(Items.NETHERITE_LEGGINGS)
                .add(AllItems.NETHERITE_DIVING_BOOTS);

        tags.tag(NorthstarItemTags.OXYGEN_SEALING)
                .add(AllItems.NETHERITE_DIVING_HELMET)
                .add(AllItems.NETHERITE_BACKTANK)
                .add(Items.NETHERITE_LEGGINGS)
                .add(AllItems.NETHERITE_DIVING_BOOTS);

        tags.tag(NorthstarItemTags.INSULATING)
                .add(Items.LEATHER_HELMET)
                .add(Items.LEATHER_CHESTPLATE)
                .add(Items.LEATHER_LEGGINGS)
                .add(Items.LEATHER_BOOTS);
    }

    private static void entities(RegistrateTagsProvider<EntityType<?>> provider) {
        Tags<EntityType<?>, EntityType<?>> tags = new Tags<>(provider, EntityType::builtInRegistryHolder, Function.identity());

        for (NorthstarEntityTags tag : NorthstarEntityTags.values()) {
            if (tag.alwaysDataGen)
                tags.tag(tag);
        }

        tags.tag(NorthstarEntityTags.CAN_SURVIVE_COLD)
                .add(EntityType.SKELETON)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.STRAY)
                .add(EntityType.WITHER)
                .add(EntityType.WITHER_SKELETON);

        tags.tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN)
                .add(EntityType.COD)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.SALMON)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.STRAY)
                .add(EntityType.TADPOLE)
                .add(EntityType.TROPICAL_FISH)
                .add(EntityType.WITHER)
                .add(EntityType.WITHER_SKELETON);
    }

    private static void fluids(RegistrateTagsProvider.IntrinsicImpl<Fluid> provider) {
        Tags<Fluid, Fluid> tags = new Tags<>(provider, Fluid::builtInRegistryHolder, Function.identity());

        for (NorthstarFluidTags tag : NorthstarFluidTags.values()) {
            if (tag.alwaysDataGen)
                tags.tag(tag);
        }

        tags.tag(NorthstarFluidTags.BREATHABLE)
                .add(NorthstarFluidTags.IS_OXY)
                .opt(TFMGFluids.AIR.get());

        tags.tag(NorthstarFluidTags.COMPAT_CBC_MOLTEN_CAST_IRON)
                .opt(ModCompat.CBC, "molten_cast_iron");

        tags.tag(NorthstarFluidTags.COMPAT_CDG_BIODIESEL)
                .opt(ModCompat.CDG, "biodiesel");
    }

    public static class Damage extends TagsProvider<DamageType> {
        public Damage(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, Registries.DAMAGE_TYPE, lookupProvider, Northstar.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(DamageTypeTags.BYPASSES_ARMOR)
                    .add(NorthstarDamageTypes.SUFFOCATION);
            tag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                    .add(NorthstarDamageTypes.SUFFOCATION);
        }
    }

}

package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.data.Tags;
import com.lightning.northstar.data.util.NorthstarDataGenTags;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.lightning.northstar.content.NorthstarTags.Namespace.COMMON;
import static com.lightning.northstar.content.NorthstarTags.Namespace.MOD;

public class NorthstarTags {

    public static void register() {
        NorthstarFluidTags.init();
        NorthstarBlockTags.init();
        NorthstarItemTags.init();
        NorthstarEntityTags.init();
        NorthstarBiomeTags.init();
    }

    enum Namespace {

        MOD(Northstar.MOD_ID),
        COMMON("forge"), // "forge" on Forge, "c" on NeoForge/Fabric
        ;

        public final String id;

        Namespace(String id) {
            this.id = id;
        }

        private <T> TagKey<T> tag(ResourceKey<Registry<T>> registry, Enum<?> entry, @Nullable String path) {
            return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath(id, path == null ? Lang.asId(entry.name()) : path));
        }
    }

    public enum NorthstarFluidTags implements Tags.Tag<Fluid> {

        BREATHABLE,
        COMPAT_CBC_MOLTEN_CAST_IRON,
        COMPAT_CDG_BIODIESEL,
        C_BIOFUEL(COMMON, "biofuel"),
        C_BRINE(COMMON, "brine"),
        C_CARBON(COMMON, "carbon"),
        C_CHLORINE(COMMON, "chlorine"),
        C_CHOCOLATE_ICE_CREAM(COMMON, "chocolate_ice_cream"),
        C_GASEOUS(COMMON, "gaseous"),
        C_HYDROCARBON(COMMON, "hydrocarbon"),
        C_HYDROGEN(COMMON, "hydrogen"),
        C_LIQUID_HYDROGEN(COMMON, "liquid_hydrogen"),
        C_LIQUID_OXYGEN(COMMON, "liquid_oxygen"),
        C_METHANE(COMMON, "methane"),
        C_OXYGEN(COMMON, "oxygen"),
        C_SODIUM(COMMON, "sodium"),
        C_STRAWBERRY_ICE_CREAM(COMMON, "strawberry_ice_cream"),
        C_SULFURIC_ACID(COMMON, "sulfuric_acid"),
        C_TITANIUM_TETRACHLORIDE(COMMON, "titanium_tetrachloride"),
        C_VANILLA_ICE_CREAM(COMMON, "vanilla_ice_cream"),
        /** @deprecated poor naming, use {@link #BREATHABLE} instead. */
        @Deprecated(since = "0.5.0", forRemoval = true)
        IS_OXY;

        public final TagKey<Fluid> tag;

        NorthstarFluidTags() {
            this(MOD);
        }

        NorthstarFluidTags(Namespace namespace) {
            this(namespace, null);
        }

        NorthstarFluidTags(Namespace namespace, @Nullable String path) {
            this.tag = namespace.tag(Registries.FLUID, this, path);
        }

        @Override
        public TagKey<Fluid> tag() {
            return tag;
        }

        public boolean matches(FluidStack fluid) {
            return fluid.getFluid().defaultFluidState().is(tag);
        }

        public boolean matches(Fluid fluid) {
            return fluid.defaultFluidState().is(tag);
        }

        public boolean matches(FluidState state) {
            return state.is(tag);
        }

        private static void init() {
        }

    }

    public enum NorthstarBlockTags implements Tags.Tag<Block> {

        AIR_PASSES_THROUGH,
        ARGYRE_REPLACEABLE,
        BASE_STONE_MARS,
        BASE_STONE_MERCURY,
        BASE_STONE_MOON,
        BASE_STONE_VENUS,
        BASE_SURFACE_BLOCKS_MARS,
        /** Temporary workaround for certain blocks that cannot be sealed with the current system even when they should be, eg: glass panes */
        @Deprecated
        BLOCKS_AIR,
        C_STORAGE_BLOCKS_MARTIAN_STEEL(COMMON, "storage_blocks/martian_steel"),
        C_STORAGE_BLOCKS_TITANIUM(COMMON, "storage_blocks/titanium"),
        C_STORAGE_BLOCKS_TUNGSTEN(COMMON, "storage_blocks/tungsten"),
        C_ORES_GLOWSTONE(COMMON, "ores/glowstone"), // TODO: should it just be "glowstone" or something else? because you would expect it to drop glowstone but it's glowstone ore
        C_ORES_TITANIUM(COMMON, "ores/titanium"),
        C_ORES_TUNGSTEN(COMMON, "ores/tungsten"),
        C_ORES_ZINC(COMMON, "ores/zinc"),
        HEAVY_BLOCKS,
        MARS_BLOCKS,
        MARS_DEEP_STONE_REPLACEABLE,
        MARS_STONE_REPLACEABLE,
        MERCURY_DEEP_STONE_REPLACEABLE,
        MERCURY_STONE_REPLACEABLE,
        MOON_BLOCKS,
        MOON_DEEP_STONE_REPLACEABLE,
        MOON_STONE_REPLACEABLE,
        MOON_SURFACE_REPLACEABLE,
        NATURAL_MARS_BLOCKS,
        NATURAL_MERCURY_BLOCKS,
        NATURAL_MOON_BLOCKS,
        NATURAL_VENUS_BLOCKS,
        SUPER_HEAVY_BLOCKS,
        TIER_1_HEAT_RESISTANCE,
        TIER_2_HEAT_RESISTANCE,
        TIER_3_HEAT_RESISTANCE,
        VENUS_DEEP_STONE_REPLACEABLE,
        VENUS_STONE,
        VENUS_STONE_REPLACEABLE;

        public final TagKey<Block> tag;

        NorthstarBlockTags() {
            this(MOD);
        }

        NorthstarBlockTags(Namespace namespace) {
            this(namespace, null);
        }

        NorthstarBlockTags(Namespace namespace, @Nullable String path) {
            this.tag = namespace.tag(Registries.BLOCK, this, path);
        }

        @Override
        public TagKey<Block> tag() {
            return tag;
        }

        public TagKey<Item> item() {
            return ItemTags.create(tag.location());
        }

        public <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> tagBlockAndItem() {
            return NorthstarDataGenTags.apply(tag, item());
        }

        @SuppressWarnings("deprecation")
        public boolean matches(Block block) {
            return block.builtInRegistryHolder().is(tag);
        }

        public boolean matches(ItemStack stack) {
            return stack != null && stack.getItem() instanceof BlockItem blockItem && matches(blockItem.getBlock());
        }

        public boolean matches(BlockState state) {
            return state.is(tag);
        }

        private static void init() {
        }

    }

    public enum NorthstarItemTags implements Tags.Tag<Item> {

        COILER_LOGS,
        CALORIAN_LOGS,
        ARGYRE_LOGS,
        C_DUSTS_SALT(COMMON, "dusts/salt"),
        C_DUSTS_VOLCANIC_ASH(COMMON, "dusts/volcanic_ash"),
        C_GEMS_SAPPHIRE(COMMON, "gems/sapphire"),
        C_FABRICS(COMMON, "fabrics"),
        C_FIBERS(COMMON, "fibers"),
        C_INGOTS_BRASS(COMMON, "ingots/brass"),
        C_INGOTS_MARTIAN_STEEL(COMMON, "ingots/martian_steel"),
        C_INGOTS_TITANIUM(COMMON, "ingots/titanium"),
        C_INGOTS_TUNGSTEN(COMMON, "ingots/tungsten"),
        C_NUGGETS_TITANIUM(COMMON, "nuggets/titanium"),
        C_NUGGETS_TUNGSTEN(COMMON, "nuggets/tungsten"),
        C_RAW_MATERIALS_MARTIAN_IRON_ORE(COMMON, "raw_materials/martian_iron_ore"),
        C_RAW_MATERIALS_TITANIUM(COMMON, "raw_materials/titanium"),
        C_RAW_MATERIALS_TUNGSTEN(COMMON, "raw_materials/tungsten"),
        C_SHEETS(COMMON, "plates"),
        C_SHEETS_BRASS(COMMON, "plates/brass"),
        C_SHEETS_COPPER(COMMON, "plates/copper"),
        C_SHEETS_GOLD(COMMON, "plates/gold"),
        C_SHEETS_IRON(COMMON, "plates/iron"),
        C_SHEETS_MARTIAN_STEEL(COMMON, "plates/martian_steel"),
        C_SHEETS_TITANIUM(COMMON, "plates/titanium"),
        C_SHEETS_TUNGSTEN(COMMON, "plates/tungsten"),
        C_STRIPPED_LOGS(COMMON, "stripped_logs"),
        HEAT_RESISTANT,
        /** Items that can be used to light up extinguished torches/lanterns */
        IGNITION_SOURCE,
        INSULATING,
        OXYGEN_SEALING,
        OXYGEN_SOURCES,
        WILTER_LOGS;

        public final TagKey<Item> tag;

        NorthstarItemTags() {
            this(MOD);
        }

        NorthstarItemTags(String path) {
            this(MOD, path);
        }

        NorthstarItemTags(Namespace namespace) {
            this(namespace, null);
        }

        NorthstarItemTags(Namespace namespace, @Nullable String path) {
            this.tag = namespace.tag(Registries.ITEM, this, path);
        }

        @Override
        public TagKey<Item> tag() {
            return tag;
        }

        @SuppressWarnings("deprecation")
        public boolean matches(Item item) {
            return item.builtInRegistryHolder()
                    .is(tag);
        }

        public boolean matches(ItemStack stack) {
            return stack.is(tag);
        }

        private static void init() {
        }

    }

    public enum NorthstarEntityTags implements Tags.Tag<EntityType<?>> {

        CAN_SURVIVE_COLD,
        CAN_SURVIVE_HEAT,
        DOESNT_REQUIRE_OXYGEN;

        public final TagKey<EntityType<?>> tag;

        NorthstarEntityTags() {
            this(MOD);
        }

        NorthstarEntityTags(Namespace namespace) {
            this(namespace, null);
        }

        NorthstarEntityTags(Namespace namespace, @Nullable String path) {
            this.tag = namespace.tag(Registries.ENTITY_TYPE, this, path);
        }

        @Override
        public TagKey<EntityType<?>> tag() {
            return tag;
        }

        public boolean matches(Entity entity) {
            return entity.getType().is(tag);
        }

        private static void init() {
        }

    }

    public enum NorthstarBiomeTags {

        @Deprecated(since = "0.5.4", forRemoval = true)
        IS_DUSTY,
        @Deprecated(since = "0.5.4", forRemoval = true)
        HAS_AMBIENT_GLOWSTONE_PARTICLE,
        ;

        public final TagKey<Biome> tag;

        NorthstarBiomeTags() {
            this(MOD);
        }

        NorthstarBiomeTags(Namespace namespace) {
            this(namespace, null);
        }

        NorthstarBiomeTags(Namespace namespace, @Nullable String path) {
            this.tag = namespace.tag(Registries.BIOME, this, path);
        }

        public boolean matches(Biome biome) {
            Optional<Holder<Biome>> holder = ForgeRegistries.BIOMES.getHolder(biome);
            return holder.isPresent() && holder.get().is(tag);
        }

        private static void init() {
        }

    }

}

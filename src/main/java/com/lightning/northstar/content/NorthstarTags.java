package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.simibubi.create.AllTags;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
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

public class NorthstarTags {

    public static void register() {
        NorthstarFluidTags.init();
        NorthstarBlockTags.init();
        NorthstarItemTags.init();
        NorthstarEntityTags.init();
        NorthstarBiomeTags.init();
    }

    public enum NameSpace {

        MOD(Northstar.MOD_ID, false, true),
        FORGE("forge"),
        TIC("tconstruct"),
        QUARK("quark");

        public final String id;
        public final boolean optionalDefault;
        public final boolean alwaysDatagenDefault;

        NameSpace(String id) {
            this(id, true, false);
        }

        NameSpace(String id, boolean optionalDefault, boolean alwaysDatagenDefault) {
            this.id = id;
            this.optionalDefault = optionalDefault;
            this.alwaysDatagenDefault = alwaysDatagenDefault;
        }
    }

    public enum NorthstarFluidTags {

        TIER_1_ROCKET_FUEL(NameSpace.MOD),
        TIER_2_ROCKET_FUEL(NameSpace.MOD),
        TIER_3_ROCKET_FUEL(NameSpace.MOD),
        OXYGEN(NameSpace.FORGE),
        IS_OXY(NameSpace.MOD);

        public final TagKey<Fluid> tag;
        public final boolean alwaysDatagen;

        NorthstarFluidTags() {
            this(NameSpace.MOD);
        }

        NorthstarFluidTags(NameSpace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        NorthstarFluidTags(NameSpace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        NorthstarFluidTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
            this(namespace, null, optional, alwaysDatagen);
        }

        NorthstarFluidTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = AllTags.optionalTag(BuiltInRegistries.FLUID, id);
            } else {
                tag = FluidTags.create(id);
            }
            this.alwaysDatagen = alwaysDatagen;
        }

        @SuppressWarnings("deprecation")
        public boolean matches(Fluid fluid) {
            return fluid.is(tag);
        }

        public boolean matches(FluidState state) {
            return state.is(tag);
        }

        private static void init() {
        }

    }

    public enum NorthstarBlockTags {

        AIR_PASSES_THROUGH(NameSpace.MOD),
        ARGYRE_REPLACES(NameSpace.MOD),
        NATURAL_MARS_BLOCKS(NameSpace.MOD),
        NATURAL_VENUS_BLOCKS(NameSpace.MOD),
        NATURAL_MOON_BLOCKS(NameSpace.MOD),
        NATURAL_MERCURY_BLOCKS(NameSpace.MOD),
        HEAVY_BLOCKS(NameSpace.MOD),
        SUPER_HEAVY_BLOCKS(NameSpace.MOD),
        TIER_1_HEAT_RESISTANCE(NameSpace.MOD),
        TIER_2_HEAT_RESISTANCE(NameSpace.MOD),
        TIER_3_HEAT_RESISTANCE(NameSpace.MOD),
        MOON_BLOCKS(NameSpace.MOD),
        MARS_BLOCKS(NameSpace.MOD);

        public final TagKey<Block> tag;
        public final boolean alwaysDatagen;

        NorthstarBlockTags() {
            this(NameSpace.MOD);
        }

        NorthstarBlockTags(NameSpace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        NorthstarBlockTags(NameSpace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        NorthstarBlockTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
            this(namespace, null, optional, alwaysDatagen);
        }

        NorthstarBlockTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = AllTags.optionalTag(BuiltInRegistries.BLOCK, id);
            } else {
                tag = BlockTags.create(id);
            }
            this.alwaysDatagen = alwaysDatagen;
        }

        @SuppressWarnings("deprecation")
        public boolean matches(Block block) {
            return block.builtInRegistryHolder()
                    .is(tag);
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

    public enum NorthstarItemTags {

        INSULATING,
        HEAT_RESISTANT,
        OXYGEN_SEALING,
        OXYGEN_SOURCES;

        public final TagKey<Item> tag;
        public final boolean alwaysDatagen;

        NorthstarItemTags() {
            this(NameSpace.MOD);
        }

        NorthstarItemTags(NameSpace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        NorthstarItemTags(NameSpace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        NorthstarItemTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
            this(namespace, null, optional, alwaysDatagen);
        }

        NorthstarItemTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = AllTags.optionalTag(BuiltInRegistries.ITEM, id);
            } else {
                tag = ItemTags.create(id);
            }
            this.alwaysDatagen = alwaysDatagen;
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

    public enum NorthstarEntityTags {

        DOESNT_REQUIRE_OXYGEN,
        CAN_SURVIVE_COLD;

        public final TagKey<EntityType<?>> tag;
        public final boolean alwaysDatagen;

        NorthstarEntityTags() {
            this(NameSpace.MOD);
        }

        NorthstarEntityTags(NameSpace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        NorthstarEntityTags(NameSpace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDatagenDefault);
        }

        NorthstarEntityTags(NameSpace namespace, boolean optional, boolean alwaysDatagen) {
            this(namespace, null, optional, alwaysDatagen);
        }

        NorthstarEntityTags(NameSpace namespace, String path, boolean optional, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = AllTags.optionalTag(BuiltInRegistries.ENTITY_TYPE, id);
            } else {
                tag = TagKey.create(Registries.ENTITY_TYPE, id);
            }
            this.alwaysDatagen = alwaysDatagen;
        }

        public boolean matches(Entity entity) {
            return entity.getType().is(tag);
        }

        private static void init() {
        }

    }

    public enum NorthstarBiomeTags {

        IS_DUSTY,
        HAS_AMBIENT_GLOWSTONE_PARTICLE;

        public final TagKey<Biome> tag;
        public final boolean alwaysDatagen;

        NorthstarBiomeTags() {
            this(NameSpace.MOD);
        }

        NorthstarBiomeTags(NameSpace namespace) {
            this(namespace, namespace.alwaysDatagenDefault);
        }

        NorthstarBiomeTags(NameSpace namespace, String path) {
            this(namespace, path, namespace.alwaysDatagenDefault);
        }

        NorthstarBiomeTags(NameSpace namespace, boolean alwaysDatagen) {
            this(namespace, null, alwaysDatagen);
        }

        NorthstarBiomeTags(NameSpace namespace, String path, boolean alwaysDatagen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
            tag = TagKey.create(Registries.BIOME, id);
            this.alwaysDatagen = alwaysDatagen;
        }

        private static void init() {
        }

    }

    public static TagKey<Fluid> forgeFluidTag(String path) {
        return forgeTag(BuiltInRegistries.FLUID, path);
    }

    public static <T> TagKey<T> forgeTag(Registry<T> registry, String path) {
        // TODO: what is it on neoforge
        return AllTags.optionalTag(registry, ResourceLocation.fromNamespaceAndPath("forge", path));
    }

}

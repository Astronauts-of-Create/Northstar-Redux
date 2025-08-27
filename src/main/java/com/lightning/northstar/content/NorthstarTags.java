package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Holder;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collections;

import static com.lightning.northstar.content.NorthstarTags.Namespace.*;

public class NorthstarTags {

    public static void register() {
        NorthstarFluidTags.init();
        NorthstarBlockTags.init();
        NorthstarItemTags.init();
        NorthstarEntityTags.init();
        NorthstarBiomeTags.init();
    }

    public static <T> TagKey<T> optionalTag(IForgeRegistry<T> registry, ResourceLocation id) {
        return registry.tags().createOptionalTagKey(id, Collections.emptySet());
    }

    public static <T> TagKey<T> forgeTag(IForgeRegistry<T> registry, String path) {
        return optionalTag(registry, ResourceLocation.fromNamespaceAndPath("forge", path));
    }

    public static TagKey<Block> forgeBlockTag(String path) {
        return forgeTag(ForgeRegistries.BLOCKS, path);
    }

    public static TagKey<Item> forgeItemTag(String path) {
        return forgeTag(ForgeRegistries.ITEMS, path);
    }

    public static TagKey<Fluid> forgeFluidTag(String path) {
        return forgeTag(ForgeRegistries.FLUIDS, path);
    }

    enum Namespace {

        MOD(Northstar.MOD_ID, false, true),
        FORGE("forge"),
        ;

        public final String id;
        public final boolean optionalDefault;
        public final boolean alwaysDataGenDefault;

        Namespace(String id) {
            this(id, true, false);
        }

        Namespace(String id, boolean optionalDefault, boolean alwaysDataGenDefault) {
            this.id = id;
            this.optionalDefault = optionalDefault;
            this.alwaysDataGenDefault = alwaysDataGenDefault;
        }
    }

    public enum NorthstarFluidTags {

        TIER_1_ROCKET_FUEL,
        TIER_2_ROCKET_FUEL,
        TIER_3_ROCKET_FUEL,
        IS_OXY,
        FORGE_OXYGEN(FORGE, "oxygen"),
        ;

        public final TagKey<Fluid> tag;
        public final boolean alwaysDataGen;

        NorthstarFluidTags() {
            this(MOD);
        }

        NorthstarFluidTags(Namespace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDataGenDefault);
        }

        NorthstarFluidTags(Namespace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDataGenDefault);
        }

        NorthstarFluidTags(Namespace namespace, boolean optional, boolean alwaysDataGen) {
            this(namespace, null, optional, alwaysDataGen);
        }

        NorthstarFluidTags(Namespace namespace, String path, boolean optional, boolean alwaysDataGen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = optionalTag(ForgeRegistries.FLUIDS, id);
            } else {
                tag = FluidTags.create(id);
            }
            this.alwaysDataGen = alwaysDataGen;
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

        AIR_PASSES_THROUGH,
        ARGYRE_REPLACES,
        NATURAL_MARS_BLOCKS,
        NATURAL_VENUS_BLOCKS,
        NATURAL_MOON_BLOCKS,
        NATURAL_MERCURY_BLOCKS,
        HEAVY_BLOCKS,
        SUPER_HEAVY_BLOCKS,
        TIER_1_HEAT_RESISTANCE,
        TIER_2_HEAT_RESISTANCE,
        TIER_3_HEAT_RESISTANCE,
        MOON_BLOCKS,
        MARS_BLOCKS,
        ;

        public final TagKey<Block> tag;
        public final boolean alwaysDataGen;

        NorthstarBlockTags() {
            this(MOD);
        }

        NorthstarBlockTags(Namespace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDataGenDefault);
        }

        NorthstarBlockTags(Namespace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDataGenDefault);
        }

        NorthstarBlockTags(Namespace namespace, boolean optional, boolean alwaysDataGen) {
            this(namespace, null, optional, alwaysDataGen);
        }

        NorthstarBlockTags(Namespace namespace, String path, boolean optional, boolean alwaysDataGen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = optionalTag(ForgeRegistries.BLOCKS, id);
            } else {
                tag = BlockTags.create(id);
            }
            this.alwaysDataGen = alwaysDataGen;
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

    public enum NorthstarItemTags {

        INSULATING,
        HEAT_RESISTANT,
        OXYGEN_SEALING,
        OXYGEN_SOURCES,
        ;

        public final TagKey<Item> tag;
        public final boolean alwaysDataGen;

        NorthstarItemTags() {
            this(MOD);
        }

        NorthstarItemTags(Namespace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDataGenDefault);
        }

        NorthstarItemTags(Namespace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDataGenDefault);
        }

        NorthstarItemTags(Namespace namespace, boolean optional, boolean alwaysDataGen) {
            this(namespace, null, optional, alwaysDataGen);
        }

        NorthstarItemTags(Namespace namespace, String path, boolean optional, boolean alwaysDataGen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = optionalTag(ForgeRegistries.ITEMS, id);
            } else {
                tag = ItemTags.create(id);
            }
            this.alwaysDataGen = alwaysDataGen;
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
        CAN_SURVIVE_COLD,
        ;

        public final TagKey<EntityType<?>> tag;
        public final boolean alwaysDataGen;

        NorthstarEntityTags() {
            this(MOD);
        }

        NorthstarEntityTags(Namespace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDataGenDefault);
        }

        NorthstarEntityTags(Namespace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDataGenDefault);
        }

        NorthstarEntityTags(Namespace namespace, boolean optional, boolean alwaysDataGen) {
            this(namespace, null, optional, alwaysDataGen);
        }

        NorthstarEntityTags(Namespace namespace, String path, boolean optional, boolean alwaysDataGen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = optionalTag(ForgeRegistries.ENTITY_TYPES, id);
            } else {
                tag = TagKey.create(Registries.ENTITY_TYPE, id);
            }
            this.alwaysDataGen = alwaysDataGen;
        }

        public boolean matches(Entity entity) {
            return entity.getType().is(tag);
        }

        private static void init() {
        }

    }

    public enum NorthstarBiomeTags {

        IS_DUSTY,
        HAS_AMBIENT_GLOWSTONE_PARTICLE,
        ;

        public final TagKey<Biome> tag;
        public final boolean alwaysDataGen;

        NorthstarBiomeTags() {
            this(MOD);
        }

        NorthstarBiomeTags(Namespace namespace) {
            this(namespace, namespace.optionalDefault, namespace.alwaysDataGenDefault);
        }

        NorthstarBiomeTags(Namespace namespace, String path) {
            this(namespace, path, namespace.optionalDefault, namespace.alwaysDataGenDefault);
        }

        NorthstarBiomeTags(Namespace namespace, boolean optional, boolean alwaysDataGen) {
            this(namespace, null, optional, alwaysDataGen);
        }

        NorthstarBiomeTags(Namespace namespace, String path, boolean optional, boolean alwaysDataGen) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace.id, path == null ? Lang.asId(name()) : path);
            if (optional) {
                tag = optionalTag(ForgeRegistries.BIOMES, id);
            } else {
                tag = TagKey.create(Registries.BIOME, id);
            }
            this.alwaysDataGen = alwaysDataGen;
        }

        public boolean matches(Biome biome) {
            Holder<Biome> bio = Holder.direct(biome);
            return bio.is(tag);
        }

        private static void init() {
        }

    }

}

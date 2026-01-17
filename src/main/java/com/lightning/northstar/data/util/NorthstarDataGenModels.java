package com.lightning.northstar.data.util;

import com.lightning.northstar.block.simple.VerticalSlabBlock;
import com.lightning.northstar.block.simple.VerticalSlabType;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

public class NorthstarDataGenModels {

    public static <I extends Item> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> itemGeneratedItem() {
        return (c, p) -> p.generated(c::get, p.modLoc("item/" + c.getName()));
    }

    public static <I extends Item> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> itemGeneratedBlock() {
        return (c, p) -> p.generated(c::get, p.modLoc("block/" + c.getName()));
    }

    public static <I extends Item> NonNullBiConsumer<DataGenContext<Item, I>, RegistrateItemModelProvider> itemGeneratedBlock(String... suffix) {
        return (c, p) -> p.generated(c::get, p.modLoc("block/" + c.getName() + String.join("", suffix)));
    }

    // temporary placeholders nothingness to bypass data generation errors, those will need to be done one day
    public static <B extends Block> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> manualModel() {
        return (c, p) -> {
        };
    }

    public static <B extends Block> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> cubeAllTranslucent() {
        return cubeAll("translucent");
    }

    public static <B extends Block> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> cubeAllCutoutMipped() {
        return cubeAll("cutout_mipped");
    }

    public static <B extends Block> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> cubeAll(String renderType) {
        return (c, p) -> p.simpleBlock(c.get(), p.models().cubeAll(c.getName(), p.blockTexture(c.get())).renderType(renderType));
    }

    public static <B extends Block> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> cross() {
        return (c, p) -> p.models().cross(c.getName(), p.blockTexture(c.get()));
    }

    public static <B extends Block> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> crossCutoutMipped() {
        return cross("cutout_mipped");
    }

    public static <B extends Block> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> cross(String renderType) {
        return (c, p) -> p.simpleBlock(c.get(), p.models().cross(c.getName(), p.blockTexture(c.get())).renderType(renderType));
    }

    public static <B extends Block> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> directionalCrossCutoutMipped() {
        return directionalCrossCutoutMipped("cutout_mipped");
    }

    public static <B extends Block> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> directionalCrossCutoutMipped(String renderType) {
        return (c, p) -> p.directionalBlock(c.get(), p.models().cross(c.getName(), p.blockTexture(c.get())).renderType(renderType));
    }

    public static <B extends LanternBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> lantern() {
        return (c, p) -> p.getVariantBuilder(c.get())
                .partialState()
                .with(LanternBlock.HANGING, false)
                .addModels(ConfiguredModel.builder()
                        .modelFile(p.models().singleTexture(c.getName(), p.mcLoc("block/template_lantern"), "lantern", p.blockTexture(c.get()))
                                .renderType("cutout"))
                        .build())
                .partialState()
                .with(LanternBlock.HANGING, true)
                .addModels(ConfiguredModel.builder()
                        .modelFile(p.models().singleTexture(c.getName() + "_hanging", p.mcLoc("block/template_hanging_lantern"), "lantern", p.blockTexture(c.get()))
                                .renderType("cutout"))
                        .build());
    }

    public static <B extends LeavesBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> leaves() {
        return (c, p) -> p.simpleBlock(c.get(), p.models().leaves(c.getName(), p.blockTexture(c.get())));
    }

    public static <B extends RotatedPillarBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> pillar() {
        return (c, p) -> p.logBlock(c.get());
    }

    public static <B extends SlabBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> slab() {
        return slab("");
    }

    public static <B extends SlabBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> slab(String suffix) {
        return (c, p) -> {
            ResourceLocation name = p.modLoc("block/" + c.getName().replaceFirst("_slab$", suffix));
            p.slabBlock(c.get(), name, name);
        };
    }

    public static <B extends VerticalSlabBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> verticalSlab() {
        return verticalSlab("");
    }

    public static <B extends VerticalSlabBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> verticalSlab(String suffix) {
        return (c, p) -> {
            ResourceLocation name = p.modLoc("block/" + c.getName().replaceFirst("(_slab_vertical|_vertical_slab)$", suffix));
            BlockModelBuilder slab = p.models().withExistingParent(c.getName(), p.modLoc("block/vertical_slab"))
                    .texture("side", name)
                    .texture("bottom", name)
                    .texture("top", name);

            p.getVariantBuilder(c.get())
                    .partialState()
                    .with(VerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE)
                    .addModels(new ConfiguredModel(p.models().getExistingFile(name)))
                    .partialState()
                    .with(VerticalSlabBlock.TYPE, VerticalSlabType.WEST)
                    .addModels(new ConfiguredModel(slab, 0, 0, false))
                    .partialState()
                    .with(VerticalSlabBlock.TYPE, VerticalSlabType.NORTH)
                    .addModels(new ConfiguredModel(slab, 0, 90, false))
                    .partialState()
                    .with(VerticalSlabBlock.TYPE, VerticalSlabType.EAST)
                    .addModels(new ConfiguredModel(slab, 0, 180, false))
                    .partialState()
                    .with(VerticalSlabBlock.TYPE, VerticalSlabType.SOUTH)
                    .addModels(new ConfiguredModel(slab, 0, 270, false));
        };
    }

    public static <B extends StairBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> stairs() {
        return stairs("");
    }

    public static <B extends StairBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> stairs(String suffix) {
        return (c, p) -> {
            ResourceLocation name = p.modLoc("block/" + c.getName().replaceFirst("_stairs$", suffix));
            p.stairsBlock(c.get(), name);
        };
    }

    public static <B extends WallBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> wall() {
        return wall("");
    }

    public static <B extends WallBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> wall(String suffix) {
        return (c, p) -> {
            ResourceLocation name = p.modLoc("block/" + c.getName().replaceFirst("_wall$", suffix));
            p.wallBlock(c.get(), name);
            p.models().wallInventory(c.getName() + "_inventory", name);
        };
    }

    public static <B extends PointedDripstoneBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> dripstone() {
        return (c, p) -> p.getVariantBuilder(c.get()).forAllStates(state -> {
            DripstoneThickness thickness = state.getValue(PointedDripstoneBlock.THICKNESS);
            Direction direction = state.getValue(PointedDripstoneBlock.TIP_DIRECTION);

            String name = c.getName() + "_" + direction.getSerializedName() + "_" + thickness.getSerializedName();

            return ConfiguredModel.builder()
                    .modelFile(p.models().cross(name, p.modLoc("block/" + name))
                            .renderType("cutout"))
                    .build();
        });
    }

    public static <B extends MultifaceBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> multiface(String renderType) {
        return (c, p) -> {
            BlockModelBuilder model = p.models()
                    .singleTexture(c.getName(), p.modLoc("block/multiface_single"), "0", p.blockTexture(c.get()))
                    .renderType(renderType);
            MultiPartBlockStateBuilder builder = p.getMultipartBuilder(c.get());

            for (Direction dir : Iterate.directions) {
                builder.part()
                        .modelFile(model)
                        .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                        .rotationY(dir.getAxis().isVertical() ? 0 : ((int) dir.toYRot() + 180) % 360)
                        .addModel()
                        .condition(MultifaceBlock.getFaceProperty(dir), true)
                        .end();
            }
        };
    }

}

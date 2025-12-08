package com.lightning.northstar.content;

import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.fluid.TitaniumTetrachlorideBlock;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.tterrag.registrate.builders.FluidBuilder.FluidTypeFactory;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.lightning.northstar.Northstar.REGISTRATE;

public class NorthstarFluids {

    static {
        REGISTRATE.setCreativeTab(NorthstarCreativeModeTab.ITEMS);
    }

    // thanks, create, for making this simple :]

    public static final FluidEntry<VirtualFluid> OXYGEN = REGISTRATE
            .virtualFluid("oxygen")
            .properties(p -> p.density(0))
            .tag(NorthstarFluidTags.C_GASEOUS.tag)
            .tag(NorthstarFluidTags.C_OXYGEN.tag)
            .register();

    public static final FluidEntry<VirtualFluid> HYDROGEN = REGISTRATE
            .virtualFluid("hydrogen")
            .properties(p -> p.density(0))
            .tag(NorthstarFluidTags.C_GASEOUS.tag)
            .tag(NorthstarFluidTags.C_HYDROGEN.tag)
            .register();

    public static final FluidEntry<VirtualFluid> CHLORINE = REGISTRATE
            .virtualFluid("chlorine")
            .properties(p -> p.density(0))
            .tag(NorthstarFluidTags.C_GASEOUS.tag)
            .tag(NorthstarFluidTags.C_CHLORINE.tag)
            .register();

    public static final FluidEntry<VirtualFluid> CHOCOLATE_ICE_CREAM = REGISTRATE
            .virtualFluid("chocolate_ice_cream")
            .tag(NorthstarFluidTags.C_CHOCOLATE_ICE_CREAM.tag)
            .register();

    public static final FluidEntry<VirtualFluid> VANILLA_ICE_CREAM = REGISTRATE
            .virtualFluid("vanilla_ice_cream")
            .tag(NorthstarFluidTags.C_VANILLA_ICE_CREAM.tag)
            .register();

    public static final FluidEntry<VirtualFluid> STRAWBERRY_ICE_CREAM = REGISTRATE
            .virtualFluid("strawberry_ice_cream")
            .tag(NorthstarFluidTags.C_STRAWBERRY_ICE_CREAM.tag)
            .register();

    public static final FluidEntry<VirtualFluid> SODIUM = REGISTRATE
            .virtualFluid("sodium")
            .tag(NorthstarFluidTags.C_SODIUM.tag)
            .register();

    public static final FluidEntry<VirtualFluid> CARBON = REGISTRATE
            .virtualFluid("carbon")
            .tag(NorthstarFluidTags.C_CARBON.tag)
            .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> TITANIUM_TETRACHLORIDE = REGISTRATE
            .standardFluid("titanium_tetrachloride",
                    SolidRenderedPlaceableFluidType.create(0xa59999, 0xdeffffff,
                            () -> 1f / 8f * 0.8f))
            .properties(b -> b.viscosity(4000).density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(1)
                    .tickRate(8)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_TITANIUM_TETRACHLORIDE.tag)
            .source(ForgeFlowingFluid.Source::new)
            .block(TitaniumTetrachlorideBlock::new)
            .build()
            .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> BRINE = REGISTRATE
            .standardFluid("brine",
                    SolidRenderedPlaceableFluidType.create(0xa59999, 0xdeffffff,
                            () -> 1f / 8f * 0.8f))
            .properties(b -> b.viscosity(2000).density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(1)
                    .tickRate(5)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_BRINE.tag)
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();


    public static final FluidEntry<ForgeFlowingFluid.Flowing> LIQUID_HYDROGEN = REGISTRATE
            .standardFluid("liquid_hydrogen",
                    SolidRenderedPlaceableFluidType.create(0xa59999, 0xdeffffff,
                            () -> 1f / 8f * 0.8f))
            .properties(b -> b.viscosity(2000)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(1)
                    .tickRate(5)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_LIQUID_HYDROGEN.tag)
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag)
            .build()
            .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> LIQUID_OXYGEN = REGISTRATE
            .standardFluid("liquid_oxygen",
                    SolidRenderedPlaceableFluidType.create(0x96AFAF, 0xdeffffff,
                            () -> 1f / 8f * 0.8f))
            .properties(b -> b.viscosity(2000)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(1)
                    .tickRate(5)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_LIQUID_OXYGEN.tag)
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> METHANE = REGISTRATE
            .standardFluid("methane",
                    SolidRenderedPlaceableFluidType.create(0x41E08E, 0xf8ffffff,
                            () -> 1f / 8f * 0.8f))
            .properties(b -> b.viscosity(2000)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(1)
                    .tickRate(5)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_METHANE.tag)
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .tag(AllItemTags.BLAZE_BURNER_FUEL_REGULAR.tag)
            .build()
            .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> SULFURIC_ACID = REGISTRATE
            .standardFluid("sulfuric_acid",
                    SolidRenderedPlaceableFluidType.create(0xA5EC00, 0xffffffff,
                            () -> 1f / 8f * 0.8f))
            .properties(b -> b.viscosity(2000)
                    .density(700))
            .fluidProperties(p -> p.levelDecreasePerBlock(1)
                    .tickRate(5)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_SULFURIC_ACID.tag)
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .build()
            .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> HYDROCARBON = REGISTRATE
            .standardFluid("hydrocarbon",
                    SolidRenderedPlaceableFluidType.create(0x070505, 0xffffffff,
                            () -> 1f / 8f * 0.25f))
            .lang("Liquid Hydrocarbons")
            .properties(b -> b.viscosity(1000)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_HYDROCARBON.tag)
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .tag(AllItemTags.BLAZE_BURNER_FUEL_REGULAR.tag)
            .build()
            .register();

    public static final FluidEntry<ForgeFlowingFluid.Flowing> BIOFUEL = REGISTRATE
            .standardFluid("biofuel",
                    SolidRenderedPlaceableFluidType.create(0x9ac846, 0xffffffff,
                            () -> 1f / 8f * 0.25f))
            .properties(b -> b.viscosity(1000)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_BIOFUEL.tag)
            .source(ForgeFlowingFluid.Source::new)
            .bucket()
            .tag(AllItemTags.BLAZE_BURNER_FUEL_REGULAR.tag)
            .build()
            .register();


    public static void register() {
    }

    public static abstract class TintedFluidType extends FluidType {

        protected static final int NO_TINT = 0xffffffff;
        private ResourceLocation stillTexture;
        private ResourceLocation flowingTexture;

        public TintedFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties);
            this.stillTexture = stillTexture;
            this.flowingTexture = flowingTexture;
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {

                @Override
                public ResourceLocation getStillTexture() {
                    return stillTexture;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return flowingTexture;
                }

                @Override
                public int getTintColor(FluidStack stack) {
                    return TintedFluidType.this.getTintColor(stack);
                }

                @Override
                public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                    return TintedFluidType.this.getTintColor(state, getter, pos);
                }

                @Override
                public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level,
                                                        int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                    Vector3f customFogColor = TintedFluidType.this.getCustomFogColor();
                    return customFogColor == null ? fluidFogColor : customFogColor;
                }

                @Override
                public void modifyFogRender(Camera camera, FogMode mode, float renderDistance, float partialTick,
                                            float nearDistance, float farDistance, FogShape shape) {
                    float modifier = TintedFluidType.this.getFogDistanceModifier();
                    float baseWaterFog = 96.0f;
                    if (modifier != 1f) {
                        RenderSystem.setShaderFogShape(FogShape.CYLINDER);
                        RenderSystem.setShaderFogStart(-8);
                        RenderSystem.setShaderFogEnd(baseWaterFog * modifier);
                    }
                }

            });
        }

        protected abstract int getTintColor(FluidStack stack);

        protected abstract int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos);

        protected Vector3f getCustomFogColor() {
            return null;
        }

        protected float getFogDistanceModifier() {
            return 1f;
        }

    }

    private static class SolidRenderedPlaceableFluidType extends TintedFluidType {

        private Vector3f fogColor;
        private int tintColor;
        private Supplier<Float> fogDistance;

        public static FluidTypeFactory create(int fogColor, int tintColor, Supplier<Float> fogDistance) {
            return (p, s, f) -> {
                SolidRenderedPlaceableFluidType fluidType = new SolidRenderedPlaceableFluidType(p, s, f);
                fluidType.fogColor = new Color(fogColor, false).asVectorF();
                fluidType.tintColor = tintColor;
                fluidType.fogDistance = fogDistance;
                return fluidType;
            };
        }

        private SolidRenderedPlaceableFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        @Override
        protected int getTintColor(FluidStack stack) {
            return NO_TINT;
        }

        /*
         * Removing alpha from tint prevents optifine from forcibly applying biome
         * colors to modded fluids (this workaround only works for fluids in the solid
         * render layer)
         */
        @Override
        public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
            return tintColor;
        }

        @Override
        protected Vector3f getCustomFogColor() {
            return fogColor;
        }

        @Override
        protected float getFogDistanceModifier() {
            return fogDistance.get();
        }

    }

}

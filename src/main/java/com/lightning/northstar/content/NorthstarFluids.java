package com.lightning.northstar.content;

import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.fluid.GasFluid;
import com.lightning.northstar.fluid.SulfuricAcidFluidBlock;
import com.lightning.northstar.fluid.TitaniumTetrachlorideBlock;
import com.lightning.northstar.item.DrinkableBucket;
import com.simibubi.create.AllFluids;
import com.simibubi.create.api.data.datamaps.BlazeBurnerFuel;
import com.simibubi.create.api.registry.CreateDataMaps;
import com.tterrag.registrate.builders.FluidBuilder.FluidTypeFactory;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3f;

import java.util.function.Supplier;

import static com.lightning.northstar.Northstar.REGISTRATE;

public class NorthstarFluids {

    static {
        REGISTRATE.setCreativeTab(NorthstarCreativeModeTab.ITEMS);
    }

    // thanks, create, for making this simple :]

    public static final FluidEntry<GasFluid> OXYGEN = REGISTRATE
            .gasFluid("oxygen")
            .properties(p -> p.density(0))
            .tag(NorthstarFluidTags.C_GASEOUS.tag)
            .tag(NorthstarFluidTags.C_OXYGEN.tag)
            .bucket()
            .lang("Oxygen Tank")
            .build()
            .register();

    public static final FluidEntry<GasFluid> HYDROGEN = REGISTRATE
            .gasFluid("hydrogen")
            .properties(p -> p.density(0))
            .tag(NorthstarFluidTags.C_GASEOUS.tag)
            .tag(NorthstarFluidTags.C_HYDROGEN.tag)
            .bucket()
            .lang("Hydrogen Tank")
            .build()
            .register();

    public static final FluidEntry<GasFluid> CHLORINE = REGISTRATE
            .gasFluid("chlorine")
            .properties(p -> p.density(0))
            .tag(NorthstarFluidTags.C_GASEOUS.tag)
            .tag(NorthstarFluidTags.C_CHLORINE.tag)
            .bucket()
            .lang("Chlorine Tank")
            .build()
            .register();

    public static final FluidEntry<GasFluid> CHOCOLATE_ICE_CREAM = REGISTRATE
            .gasFluid("chocolate_ice_cream")
            .tag(NorthstarFluidTags.C_CHOCOLATE_ICE_CREAM.tag)
            .bucket(DrinkableBucket::new)
            .properties(p -> p.food(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationModifier(0.8F)
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 280, 0, false, false, true), 1.0F)
                    .build()))
            .build()
            .register();

    public static final FluidEntry<GasFluid> VANILLA_ICE_CREAM = REGISTRATE
            .gasFluid("vanilla_ice_cream")
            .tag(NorthstarFluidTags.C_VANILLA_ICE_CREAM.tag)
            .bucket(DrinkableBucket::new)
            .properties(p -> p.food(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationModifier(0.7F)
                    .build()))
            .build()
            .register();

    public static final FluidEntry<GasFluid> STRAWBERRY_ICE_CREAM = REGISTRATE
            .gasFluid("strawberry_ice_cream")
            .tag(NorthstarFluidTags.C_STRAWBERRY_ICE_CREAM.tag)
            .bucket(DrinkableBucket::new)
            .properties(p -> p.food(new FoodProperties.Builder()
                    .nutrition(5)
                    .saturationModifier(0.7F)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 280, 0, false, false, true), 1.0F)
                    .build()))
            .build()
            .register();

    public static final FluidEntry<GasFluid> SODIUM = REGISTRATE
            .gasFluid("sodium")
            .tag(NorthstarFluidTags.C_SODIUM.tag)
            .bucket()
            .build()
            .register();

    public static final FluidEntry<GasFluid> CARBON = REGISTRATE
            .gasFluid("carbon")
            .tag(NorthstarFluidTags.C_CARBON.tag)
            .bucket()
            .lang("Carbon Tank")
            .build()
            .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> TITANIUM_TETRACHLORIDE = REGISTRATE
            .standardFluid("titanium_tetrachloride",
                    SolidRenderedPlaceableFluidType.create(0xa59999, 0xdeffffff, () -> 1f / 8f * 0.8f))
            .properties(b -> b
                    .viscosity(4000)
                    .density(1400))
            .fluidProperties(p -> p.levelDecreasePerBlock(1)
                    .tickRate(8)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_TITANIUM_TETRACHLORIDE.tag)
            .source(BaseFlowingFluid.Source::new)
            .block(TitaniumTetrachlorideBlock::new)
            .build()
            .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> BRINE = REGISTRATE
            .standardFluid("brine",
                    SolidRenderedPlaceableFluidType.create(0xa59999, 0xdeffffff, () -> 1f / 8f * 0.8f))
            .properties(b -> b
                    .viscosity(2000)
                    .density(1400))
            .fluidProperties(p -> p
                    .levelDecreasePerBlock(1)
                    .tickRate(5)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_BRINE.tag)
            .source(BaseFlowingFluid.Source::new)
            .bucket()
            .tag(Tags.Items.BUCKETS)
            .tag(NorthstarItemTags.C_BUCKETS_BRINE.tag)
            .build()
            .register();


    public static final FluidEntry<BaseFlowingFluid.Flowing> LIQUID_HYDROGEN = REGISTRATE
            .standardFluid("liquid_hydrogen",
                    SolidRenderedPlaceableFluidType.create(0xa59999, 0xdeffffff, () -> 1f / 8f * 0.8f))
            .properties(b -> b
                    .viscosity(2000)
                    .density(1400))
            .fluidProperties(p -> p
                    .levelDecreasePerBlock(1)
                    .tickRate(5)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_LIQUID_HYDROGEN.tag)
            .source(BaseFlowingFluid.Source::new)
            .bucket()
            .tag(Tags.Items.BUCKETS)
            .tag(NorthstarItemTags.C_BUCKETS_LIQUID_HYDROGEN.tag)
            .dataMap(CreateDataMaps.SUPERHEATED_BLAZE_BURNER_FUELS, new BlazeBurnerFuel(3200))
            .build()
            .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> LIQUID_OXYGEN = REGISTRATE
            .standardFluid("liquid_oxygen",
                    SolidRenderedPlaceableFluidType.create(0x96AFAF, 0xdeffffff, () -> 1f / 8f * 0.8f))
            .properties(b -> b
                    .viscosity(2000)
                    .density(1400))
            .fluidProperties(p -> p
                    .levelDecreasePerBlock(1)
                    .tickRate(5)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_LIQUID_OXYGEN.tag)
            .source(BaseFlowingFluid.Source::new)
            .bucket()
            .tag(Tags.Items.BUCKETS)
            .tag(NorthstarItemTags.C_BUCKETS_LIQUID_OXYGEN.tag)
            .build()
            .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> METHANE = REGISTRATE
            .standardFluid("methane",
                    SolidRenderedPlaceableFluidType.create(0x41E08E, 0xf8ffffff, () -> 1f / 8f * 0.8f))
            .properties(b -> b
                    .viscosity(2000)
                    .density(1400))
            .fluidProperties(p -> p
                    .levelDecreasePerBlock(1)
                    .tickRate(5)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_METHANE.tag)
            .source(BaseFlowingFluid.Source::new)
            .bucket()
            .tag(Tags.Items.BUCKETS)
            .tag(NorthstarItemTags.C_BUCKETS_METHANE.tag)
            .dataMap(CreateDataMaps.REGULAR_BLAZE_BURNER_FUELS, new BlazeBurnerFuel(1600))
            .build()
            .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> SULFURIC_ACID = REGISTRATE
            .standardFluid("sulfuric_acid",
                    SolidRenderedPlaceableFluidType.create(0xA5EC00, 0xffffffff, () -> 1f / 8f * 0.8f))
            .properties(b -> b
                    .viscosity(2000)
                    .density(700))
            .fluidProperties(p -> p
                    .levelDecreasePerBlock(1)
                    .tickRate(5)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_SULFURIC_ACID.tag)
            .source(BaseFlowingFluid.Source::new)
            .block(SulfuricAcidFluidBlock::new)
            .build()
            .bucket()
            .tag(Tags.Items.BUCKETS)
            .tag(NorthstarItemTags.C_BUCKETS_SULFURIC_ACID.tag)
            .build()
            .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> HYDROCARBON = REGISTRATE
            .standardFluid("hydrocarbon",
                    SolidRenderedPlaceableFluidType.create(0x070505, 0xffffffff, () -> 1f / 8f * 0.25f))
            .lang("Liquid Hydrocarbons")
            .properties(b -> b
                    .viscosity(1000)
                    .density(1400))
            .fluidProperties(p -> p
                    .levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_HYDROCARBON.tag)
            .source(BaseFlowingFluid.Source::new)
            .bucket()
            .tag(Tags.Items.BUCKETS)
            .tag(NorthstarItemTags.C_BUCKETS_HYDROCARBON.tag)
            .dataMap(CreateDataMaps.REGULAR_BLAZE_BURNER_FUELS, new BlazeBurnerFuel(1600))
            .build()
            .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> BIOFUEL = REGISTRATE
            .standardFluid("biofuel",
                    SolidRenderedPlaceableFluidType.create(0x9ac846, 0xffffffff, () -> 1f / 8f * 0.25f))
            .properties(b -> b
                    .viscosity(1000)
                    .density(1400))
            .fluidProperties(p -> p
                    .levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f))
            .tag(NorthstarFluidTags.C_BIOFUEL.tag)
            .source(BaseFlowingFluid.Source::new)
            .bucket()
            .tag(Tags.Items.BUCKETS)
            .tag(NorthstarItemTags.C_BUCKETS_BIOFUEL.tag)
            .dataMap(CreateDataMaps.REGULAR_BLAZE_BURNER_FUELS, new BlazeBurnerFuel(1600))
            .build()
            .register();


    public static void register() {
    }

    private static class SolidRenderedPlaceableFluidType extends AllFluids.TintedFluidType {

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

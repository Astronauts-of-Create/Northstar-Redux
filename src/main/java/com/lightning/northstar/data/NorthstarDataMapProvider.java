package com.lightning.northstar.data;

import com.lightning.northstar.api.data.datamap.DimensionInfo;
import com.lightning.northstar.content.NorthstarDataMaps;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NorthstarDataMapProvider extends DataMapProvider {
    protected NorthstarDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    private static @NotNull Builder builder() {
        return new Builder();
    }

    @Override
    protected void gather(@NotNull HolderLookup.Provider provider) {
        builder(NorthstarDataMaps.LEVEL_INFO)
                .add(NorthstarDimensions.MERCURY_DIM_TYPE,
                        builder()
                                .gravity(0.38)
                                .hasSky(true)
                                .temperature(400)
                                .computingCost(800)
                                .noOxygen()
                                .canSeeSkyAtDay(true)
                                .noWeather()
                                .engineConstant(6)
                                .seedOffset(2)
                                .sunMultiplier(8)
                                .build(), false)

                .add(NorthstarDimensions.VENUS_DIM_TYPE, builder()
                        .gravity(0.89)
                        .hasSky(true)
                        .temperature(464)
                        .atmosphericCost(4000)
                        .noOxygen()
                        .engineConstant(9)
                        .seedOffset(4)
                        .sunMultiplier(0.6f)
                        .build(), false)

                .add(ResourceLocation.withDefaultNamespace("overworld"), builder()
                        .hasSky(true)
                        .atmosphericCost(1600)
                        .build(), false)

                .add(ResourceLocation.withDefaultNamespace("the_nether"), builder()
                        .temperature(230)
                        .build(), false)

                .add(ResourceLocation.withDefaultNamespace("the_end"), builder()
                        .temperature(4)
                        .build(), false)

                .add(NorthstarDimensions.EARTH_ORBIT_DIM_TYPE, builder()
                        .gravity(0.06)
                        .hasSky(true)
                        .canSeeSkyAtDay(true)
                        .noWeather()
                        .build(), false)

                .add(NorthstarDimensions.MOON_DIM_TYPE, builder()
                        .gravity(0.16)
                        .hasSky(true)
                        .temperature(-183)
                        .computingCost(50)
                        .noOxygen()
                        .canSeeSkyAtDay(true)
                        .noWeather()
                        .seedOffset(3)
                        .sunMultiplier(1.5f)
                        .build(), false)

                .add(NorthstarDimensions.MARS_DIM_TYPE, builder()
                        .gravity(0.37)
                        .hasSky(true)
                        .temperature(-100)
                        .atmosphericCost(200)
                        .computingCost(400)
                        .noOxygen()
                        .engineConstant(3)
                        .seedOffset(2)
                        .sunMultiplier(1.2f)
                        .build(), false);
    }

    public static class Builder {
        private double gravity = 1;
        private int temperature = 15;
        private int atmosphereCost = 0;
        private int computingCost = 0;
        private boolean hasOxygen = true;
        private boolean hasSky = false;
        private boolean canSeeSkyAtDay = false;
        private boolean hasWeather = true;
        private double engineConstant = 1;
        private long seedOffset = 0;
        private float sunMultiplier = 1;

        public Builder gravity(double gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder temperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder atmosphericCost(int cost) {
            this.atmosphereCost = cost;
            return this;
        }

        public Builder computingCost(int cost) {
            this.computingCost = cost;
            return this;
        }

        public Builder hasOxygen(boolean hasOxygen) {
            this.hasOxygen = hasOxygen;
            return this;
        }

        public Builder noOxygen() {
            return hasOxygen(false);
        }

        public Builder canSeeSkyAtDay(boolean canSee) {
            this.canSeeSkyAtDay = canSee;
            return this;
        }

        public Builder hasWeather(boolean hasWeather) {
            this.hasWeather = hasWeather;
            return this;
        }

        public Builder noWeather() {
            return hasWeather(false);
        }

        public Builder engineConstant(double constant) {
            this.engineConstant = constant;
            return this;
        }

        public Builder seedOffset(long offset) {
            this.seedOffset = offset;
            return this;
        }

        public Builder sunMultiplier(float multiplier) {
            this.sunMultiplier = multiplier;
            return this;
        }

        public Builder hasSky(boolean hasSky) {
            this.hasSky = hasSky;
            return this;
        }

        @Contract(" -> new")
        public @NotNull DimensionInfo build() {
            return new DimensionInfo(
                    gravity,
                    temperature,
                    atmosphereCost,
                    computingCost,
                    hasOxygen,
                    hasSky,
                    canSeeSkyAtDay,
                    hasWeather,
                    engineConstant,
                    seedOffset,
                    sunMultiplier
            );
        }
    }
}

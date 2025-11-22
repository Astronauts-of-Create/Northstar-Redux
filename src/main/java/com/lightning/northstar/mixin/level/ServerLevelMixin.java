package com.lightning.northstar.mixin.level;

import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.lightning.northstar.world.sealer.ProgressiveBlockUpdater;
import com.lightning.northstar.world.sealer.SealingMode;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements NorthstarLevel {

    @Unique
    private final ProgressiveBlockUpdater northstar$updater = new ProgressiveBlockUpdater(SealingMode.OTHER);

    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void northstar$tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        ServerLevel self = (ServerLevel) (Object) this;

        ProfilerFiller profiler = getProfiler();
        profiler.push("northstar:process_seal_updates");

        northstar$temperature().processUpdates(self);
        northstar$oxygen().processUpdates(self);
        northstar$updater.processUpdates(self);

        profiler.pop();
    }

    @Override
    public void northstar$queueBlockUpdates(LongCollection positions) {
        northstar$updater.queueUpdates(positions);
    }

    //yay :]
    @ModifyReturnValue(method = "getSeed", at = @At("RETURN"))
    private long northstar$modifySeed(long seed) {
        return seed + NorthstarPlanets.getSeedOffset(dimension());
    }

}

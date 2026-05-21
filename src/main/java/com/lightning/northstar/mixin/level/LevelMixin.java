package com.lightning.northstar.mixin.level;

import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(Level.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class LevelMixin implements NorthstarLevel {

    @Shadow
    @Final
    private ResourceKey<Level> dimension;

    @Shadow
    protected abstract LevelEntityGetter<Entity> getEntities();

    @Unique
    private NorthstarTemperature northstar$temperature;
    @Unique
    private NorthstarOxygen northstar$oxygen;
    @Nullable
    @Unique
    private Planet northstar$planet;
    @Unique
    private PlanetDimension northstar$dimension;
    @Unique
    private float northstar$gravityScale;
    @Unique
    private boolean northstar$noGravity;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void northstar$init(CallbackInfo ci) {
        Level self = (Level) (Object) this;

        northstar$temperature = new NorthstarTemperature(self);
        northstar$oxygen = new NorthstarOxygen(self);

        northstar$onResourceReload(true);
    }

    @Override
    public NorthstarTemperature northstar$temperature() {
        return northstar$temperature;
    }

    @Override
    public NorthstarOxygen northstar$oxygen() {
        return northstar$oxygen;
    }

    @Override
    public void northstar$queueBlockUpdates(LongCollection positions) {
        // do nothing here, only happens on server levels
    }

    @Override
    public void northstar$onResourceReload() {
        northstar$onResourceReload(false);
    }

    @Unique
    private void northstar$onResourceReload(boolean init) {
        PlanetDimension dim = northstar$getPlanetTracker().getDimensionByLevel(dimension);
        if (dim == null) {
            dim = PlanetDimension.builder()
                    .name("default-" + (dimension == null ? "unknown" : dimension.location()))
                    .dimension(dimension == null ? Level.OVERWORLD : dimension)
                    .build();
        }

        northstar$planet = northstar$getPlanetTracker().getPlanetByLevel((Level) (Object) this);
        northstar$dimension = dim;
        northstar$gravityScale = dim.gravityScale();
        northstar$noGravity = dim.gravity() == 0;

        if (!init) {
            for (Entity entity : getEntities().getAll()) {
                entity.northstar$onResourceReload();
            }
        }
    }

    @Override
    public @Nullable Planet northstar$planet() {
        return northstar$planet;
    }

    @Override
    public PlanetDimension northstar$dimension() {
        return northstar$dimension;
    }

    @Override
    public float northstar$gravityScale() {
        return northstar$gravityScale;
    }

    @Override
    public boolean northstar$isZeroGravity() {
        return northstar$noGravity;
    }

}

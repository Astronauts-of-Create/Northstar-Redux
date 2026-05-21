package com.lightning.northstar.mixin.compat.create;

import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AirCurrent.class)
public class AirCurrentMixin {

    @Shadow(remap = false)
    @Final
    public IAirCurrentSource source;
    @Shadow(remap = false)
    public Direction direction;
    @Shadow(remap = false)
    public float maxDistance;
    @Shadow(remap = false)
    public List<?> segments;
    @Shadow(remap = false)
    public AABB bounds;

    @Inject(
            method = "rebuild",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void northstar$rebuild(CallbackInfo ci) {
        if (source.getAirCurrentWorld() == null || NorthstarOxygen.hasOxygen(source.getAirCurrentWorld(), source.getAirCurrentPos()))
            return;
        ci.cancel();

        direction = Direction.NORTH;
        maxDistance = 0;
        segments.clear();
        bounds = new AABB(0, 0, 0, 0, 0, 0);
    }

}

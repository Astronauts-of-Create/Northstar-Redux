package com.lightning.northstar.mixin.level;

import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public class LevelMixin implements NorthstarLevel {

    @Unique
    private NorthstarTemperature northstar$temperature;
    @Unique
    private NorthstarOxygen northstar$oxygen;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void northstar$init(CallbackInfo ci) {
        Level self = (Level) (Object) this;

        northstar$temperature = new NorthstarTemperature(self);
        northstar$oxygen = new NorthstarOxygen(self);
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

}

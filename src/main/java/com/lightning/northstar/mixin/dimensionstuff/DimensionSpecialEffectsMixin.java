package com.lightning.northstar.mixin.dimensionstuff;

import com.lightning.northstar.world.dimension.NorthstarDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(DimensionSpecialEffects.class)
public class DimensionSpecialEffectsMixin {

    @Shadow
    @Final
    private float[] sunriseCol;

    @Inject(method = "getSunriseColor", at = @At("RETURN"), cancellable = true)
    private void getSunriseColor(float Time, float tickDelta, CallbackInfoReturnable<float[]> info) {
        ResourceKey<Level> player_dim = Minecraft.getInstance().level.dimension();

        if (player_dim == NorthstarDimensions.MARS_DIM_KEY) {
            float f1 = Mth.cos(Time * ((float) Math.PI * 2F)) - 0.0F;
            if (f1 >= -0.4F && f1 <= 0.4F) {
                float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
                float f4 = 1.0F - (1.0F - Mth.sin(f3 * (float) Math.PI)) * 0.99F;
                f4 *= f4;
                sunriseCol[0] = f3 * 0.2F + 0.5F;
                sunriseCol[1] = f3 * f3 * 0.2F + 0.5F;
                sunriseCol[2] = f3 * f3 * 0.8F + 0.5F;
                sunriseCol[3] = f4;
                info.setReturnValue(sunriseCol);
                return;
            }
        }

        if (player_dim == Level.OVERWORLD) {
            float f1 = Mth.cos(Time * ((float) Math.PI * 2F)) - 0.0F;
            if (f1 >= -0.4F && f1 <= 0.4F) {
                float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
                float f4 = 1.0F - (1.0F - Mth.sin(f3 * (float) Math.PI)) * 0.99F;
                f4 *= f4;
                sunriseCol[0] = f3 * 0.3F + 0.7F;
                sunriseCol[1] = f3 * f3 * 0.7F + 0.4F;
                sunriseCol[2] = f3 * f3 * 0.0F + 0.2F;
                sunriseCol[3] = f4;
                info.setReturnValue(sunriseCol);
                return;
            }
        }

        if (player_dim == NorthstarDimensions.VENUS_DIM_KEY) {
            float f1 = Mth.cos(Time * ((float) Math.PI * 2F)) - 0.0F;
            if (f1 >= -0.4F && f1 <= 0.4F) {
                float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
                float f4 = 1.0F - (1.0F - Mth.sin(f3 * (float) Math.PI)) * 0.99F;
                f4 *= f4;
                sunriseCol[0] = f3 * 0.3F + 0.6F;
                sunriseCol[1] = f3 * f3 * 0.1F + 0.4F;
                sunriseCol[2] = f3 * f3 * 0.0F + 1.0F;
                sunriseCol[3] = f4;
                info.setReturnValue(sunriseCol);
                return;
            }
        }
    }
}
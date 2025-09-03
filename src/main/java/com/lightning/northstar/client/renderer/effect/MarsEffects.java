package com.lightning.northstar.client.renderer.effect;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.util.Mth;

public class MarsEffects extends DimensionSpecialEffects.OverworldEffects {

    private final float[] sunriseCol = new float[4];

    @Override
    public float[] getSunriseColor(float time, float tickDelta) {
        float f1 = Mth.cos(time * Mth.TWO_PI);
        if (f1 >= -0.4F && f1 <= 0.4F) {
            float f3 = f1 / 0.4F * 0.5F + 0.5F;
            float a = 1.0F - (1.0F - Mth.sin(f3 * Mth.PI)) * 0.99F;
            sunriseCol[0] = f3 * 0.2F + 0.5F;
            sunriseCol[1] = f3 * f3 * 0.2F + 0.5F;
            sunriseCol[2] = f3 * f3 * 0.8F + 0.5F;
            sunriseCol[3] = a * a;
            return sunriseCol;
        }
        return null;
    }

}

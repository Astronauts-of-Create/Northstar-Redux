package com.lightning.northstar.util;

import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record LinearCurve(float minX, float maxX, float y0, float y1, float[] xs, float[] ys) {

    public LinearCurve(float[] xs, float[] ys) {
        this(xs[0], xs[xs.length - 1], ys[0], ys[ys.length - 1], xs, ys);
        ensureSorted(xs);
    }

    static void ensureSorted(float[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] >= array[i])
                throw new IllegalArgumentException("Curve keys must be in order");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Float> x = new ArrayList<>();
        private List<Float> y = new ArrayList<>();

        public Builder add(float x, float y) {
            this.x.add(x);
            this.y.add(y);
            return this;
        }

        public LinearCurve build() {
            return new LinearCurve(NorthstarCodecs.floatListToArray(x), NorthstarCodecs.floatListToArray(y));
        }
    }

    public float get(float x) {
        if (x <= minX)
            return y0;
        if (x >= maxX)
            return y1;

        int index = Arrays.binarySearch(xs, x);
        if (index >= 0)
            return ys[index];
        index = -index - 2;
        return Mth.map(x, xs[index], xs[index + 1], ys[index], ys[index + 1]);
    }

}

package com.lightning.northstar.world.sealer.transform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransformProviders {

    private static final List<TransformProviderFactory> FROM_WORLD = new ArrayList<>();
    private static final List<TransformProvider> TO_WORLD = new ArrayList<>();
    private static TransformProvider TO_WORLD_MERGED = TransformProvider.IDENTITY;

    public static synchronized void registerToWorld(TransformProvider provider) {
        TO_WORLD.add(provider);
        TO_WORLD_MERGED = TransformProvider.combine(TO_WORLD);
    }

    public static synchronized void registerFromWorld(TransformProviderFactory factory) {
        FROM_WORLD.add(factory);
    }

    public static TransformProvider getToWorld() {
        return TO_WORLD_MERGED;
    }

    public static TransformProvider createFromWorld(Level level, BlockPos pos) {
        return TransformProvider.combine(FROM_WORLD.stream()
                .map(provider -> provider.createTransformProvider(level, pos))
                .filter(Objects::nonNull)
                .toList());
    }

}

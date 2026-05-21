package com.lightning.northstar.world.sealer.transform;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface TransformProvider {

    TransformProvider IDENTITY = (level, pos) -> null;

    @Nullable
    Vec3 applyTransform(Level level, Vec3 pos);

    default Vec3 applyTransformOrIdentity(Level level, Vec3 pos) {
        Vec3 transformed = applyTransform(level, pos);
        return transformed == null ? pos : transformed;
    }

    static TransformProvider combine(List<TransformProvider> providers) {
        return switch (providers.size()) {
            case 0 -> IDENTITY;
            case 1 -> providers.get(0);
            case 2 -> {
                TransformProvider first = providers.get(0);
                TransformProvider second = providers.get(1);
                yield (level, pos) -> {
                    Vec3 transformed = first.applyTransform(level, pos);
                    if (transformed != null) {
                        return transformed;
                    }
                    return second.applyTransform(level, pos);
                };
            }
            default -> {
                TransformProvider[] array = providers.toArray(TransformProvider[]::new);
                yield (level, pos) -> {
                    for (TransformProvider provider : array) {
                        Vec3 transformed = provider.applyTransform(level, pos);
                        if (transformed != null) {
                            return transformed;
                        }
                    }
                    return null;
                };
            }
        };
    }

}

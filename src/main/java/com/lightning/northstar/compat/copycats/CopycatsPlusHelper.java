package com.lightning.northstar.compat.copycats;

import com.copycatsplus.copycats.foundation.copycat.CCCopycatBlockEntity;
import com.lightning.northstar.data.ModCompat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface CopycatsPlusHelper {

    CopycatsPlusHelper $ = ModCompat.COPYCATS.<CopycatsPlusHelper>runIfLoaded(() -> Instance::new).orElseGet(Stub::new);

    @Nullable
    BlockState getCopycatMaterial(@Nullable BlockEntity be);

    class Stub implements CopycatsPlusHelper {
        @Override
        @Nullable
        public BlockState getCopycatMaterial(@Nullable BlockEntity be) {
            return null;
        }
    }

    class Instance implements CopycatsPlusHelper {
        @Override
        @Nullable
        public BlockState getCopycatMaterial(@Nullable BlockEntity be) {
            return be instanceof CCCopycatBlockEntity copycat ? copycat.getMaterial() : null;
        }
    }

}

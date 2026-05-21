package com.lightning.northstar.compat.copycats;

import com.copycatsplus.copycats.foundation.copycat.ICopycatBlockEntity;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlockEntity;
import com.lightning.northstar.data.ModCompat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface CopycatsPlusHelper {

    CopycatsPlusHelper $ = ModCompat.COPYCATS.<CopycatsPlusHelper>runIfLoaded(() -> Instance::new).orElseGet(Stub::new);

    List<BlockState> getCopycatMaterials(@Nullable BlockEntity be);

    class Stub implements CopycatsPlusHelper {
        private Stub() {
        }

        @Override
        public List<BlockState> getCopycatMaterials(@Nullable BlockEntity be) {
            return List.of();
        }
    }

    class Instance implements CopycatsPlusHelper {
        private Instance() {
        }

        @Override
        public List<BlockState> getCopycatMaterials(@Nullable BlockEntity be) {
            if (be instanceof IMultiStateCopycatBlockEntity copycat) {
                List<BlockState> materials = new ArrayList<>();
                for (String property : copycat.getMaterialItemStorage().getAllProperties()) {
                    if (copycat.getBlock().partExists(be.getBlockState(), property)) {
                        materials.add(copycat.getMaterialItemStorage().getMaterialItem(property).material());
                    }
                }
                return materials;
            }
            if (be instanceof ICopycatBlockEntity copycat) {
                return List.of(copycat.getMaterial());
            }
            return List.of();
        }
    }

}

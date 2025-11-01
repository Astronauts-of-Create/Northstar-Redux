package com.lightning.northstar.api.create;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface ReceivingKineticBlockEntity {

    /**
     * @see KineticBlockEntity#propagateRotationTo(KineticBlockEntity, BlockState, BlockState, BlockPos, boolean, boolean)
     */
    float propagateRotationFrom(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs);

}

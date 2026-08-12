package com.lightning.northstar.mixin.compat.create;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChuteBlockEntity.class)
public abstract class ChuteBlockEntityMixin extends SmartBlockEntity {

    @Shadow(remap = false)
    public abstract float getItemMotion();

    public ChuteBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @ModifyExpressionValue(
            method = "getItemMotion",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=-4"
            ),
            remap = false
    )
    private float northstar$modifyItemMotionGravity(float constant) {
        return constant * level.northstar$gravityScale();
    }

    @ModifyExpressionValue(
            method = "addToGoggleTooltip",
            at = @At(
                    value = "CONSTANT",
                    args = "stringValue=up"
            ),
            remap = false
    )
    private String northstar$modifyChuteDirectionTooltip(String constant) {
        return Mth.equal(getItemMotion(), 0) ? "none" : constant;
    }

}

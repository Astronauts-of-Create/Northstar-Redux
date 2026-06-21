package com.lightning.northstar.mixin.compat.create;

import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChuteBlockEntity.class)
public abstract class ChuteBlockEntityMixin extends SmartBlockEntity {

    @Shadow(remap = false)
    public abstract float getItemMotion();

    public ChuteBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @ModifyConstant(
            method = "getItemMotion",
            constant = @Constant(floatValue = -4f),
            remap = false
    )
    private float northstar$modifyItemMotionGravity(float constant) {
        return constant * level.northstar$gravityScale();
    }

    @ModifyConstant(
            method = "addToGoggleTooltip",
            constant = @Constant(stringValue = "up"),
            remap = false
    )
    private String northstar$modifyChuteDirectionTooltip(String constant) {
        return Mth.equal(getItemMotion(), 0) ? "none" : constant;
    }

}

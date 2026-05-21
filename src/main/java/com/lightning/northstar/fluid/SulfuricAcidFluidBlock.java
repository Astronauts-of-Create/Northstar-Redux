package com.lightning.northstar.fluid;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SulfuricAcidFluidBlock extends LiquidBlock {

    public SulfuricAcidFluidBlock(Supplier<? extends FlowingFluid> fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        super.entityInside(state, world, pos, entity);

        if (entity instanceof LivingEntity && !world.isClientSide) {
            if (entity.hurt(entity.level().damageSources().northstar$acid(), 6.0F)) {
                entity.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + world.random.nextFloat() * 0.4F);
            }
        }
    }

}

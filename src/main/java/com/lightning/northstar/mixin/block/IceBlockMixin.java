package com.lightning.northstar.mixin.block;

import com.lightning.northstar.particle.ColdAirParticleData;
import com.lightning.northstar.world.NorthstarTemperature;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public class IceBlockMixin {

    @Inject(method = "randomTick", at = @At("TAIL"))
    public void northstar$randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo info) {
        //just in case other mods use the iceblock class for some reason
        if (!state.is(Blocks.ICE)) {
            return;
        }

        float temperature = northstar$getHighestTemperatureAround(level, pos);
        if (random.nextFloat() > 0.4) {
            if (100 < temperature) {
                this.northstar$evaporate(level, pos);
            } else if (32 < temperature) {
                this.northstar$melt(level, pos);
            }
        } else if (32 < temperature) {
            northstar$coldAirParticles(level, pos, random);
        }
    }

    @Unique
    public float northstar$getHighestTemperatureAround(ServerLevel level, BlockPos pos) {
        float temperature = Float.NEGATIVE_INFINITY;

        for (Direction direction : Iterate.directions) {
            float temp = NorthstarTemperature.getTemperatureAt(level, pos.relative(direction));
            if (temp > temperature) {
                temperature = temp;
            }
        }

        return temperature;
    }

    @Unique
    public void northstar$coldAirParticles(Level level, BlockPos pos, RandomSource random) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).isAir() && random.nextInt(20) == 0) {
                double d0 = (double) x + (dir.getStepX() / 2) + random.nextDouble();
                double d1 = (double) y + 0.7D;
                double d2 = (double) z + (dir.getStepZ() / 2) + random.nextDouble();
                level.addParticle(new ColdAirParticleData(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Unique
    protected void northstar$melt(Level pLevel, BlockPos pPos) {
        pLevel.setBlockAndUpdate(pPos, Fluids.WATER.getFluidType().getBlockForFluidState(pLevel, pPos, Fluids.WATER.defaultFluidState()));
        pLevel.neighborChanged(pPos, Fluids.WATER.getFluidType().getBlockForFluidState(pLevel, pPos, Fluids.WATER.defaultFluidState()).getBlock(), pPos);
    }

    @Unique
    protected void northstar$evaporate(Level level, BlockPos pos) {
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        level.neighborChanged(pos, Blocks.AIR, pos);
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
        for (int l = 0; l < 8; ++l) {
            level.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }

}

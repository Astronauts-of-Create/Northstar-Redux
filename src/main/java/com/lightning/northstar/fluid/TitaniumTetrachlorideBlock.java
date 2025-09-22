package com.lightning.northstar.fluid;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class TitaniumTetrachlorideBlock extends LiquidBlock {

    public TitaniumTetrachlorideBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        // Titanium tetrachloride is extremely volatile when exposed to open air.
        world.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3.0f, Level.ExplosionInteraction.NONE);

        MutableBlockPos neighborPos = new MutableBlockPos();

        for (Direction direction : Iterate.directions) {
            neighborPos.setWithOffset(pos, direction);
            BlockState neighbor = world.getBlockState(neighborPos);

            if (neighbor.getBlock().isFlammable(neighbor, world, neighborPos, Direction.UP)) {
                BlockPos firePos = neighborPos.above(); // fire sits above the flammable block

                if (world.isEmptyBlock(firePos)) {
                    world.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 3);
                }
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        super.entityInside(state, world, pos, entity);

        if (entity instanceof ItemEntity item) {
            item.remove(Entity.RemovalReason.DISCARDED);
            world.playSound(null, item.blockPosition(), SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.5F, 1.0F);
            world.addParticle(ParticleTypes.SMOKE, item.getX(), item.getY(), item.getZ(),
                    0.1 * (world.random.nextDouble() - 0.5),
                    0.1 * (world.random.nextDouble() - 0.5),
                    0.1 * (world.random.nextDouble() - 0.5));
            return;
        }

        if (entity instanceof LivingEntity living && !world.isClientSide) {
            world.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3.0f, Level.ExplosionInteraction.NONE);
            living.hurt(world.damageSources().hotFloor(), 2.0F);
        }
    }

}

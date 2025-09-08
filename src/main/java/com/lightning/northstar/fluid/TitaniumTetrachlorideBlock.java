package com.lightning.northstar.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;

import java.util.function.Supplier;

public class TitaniumTetrachlorideBlock extends LiquidBlock {
    public TitaniumTetrachlorideBlock(Supplier<? extends FlowingFluid> fluid, Properties properties) {
        super(fluid, properties);
    }

//    @Override
//    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
//        return true;
//    }


    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    //    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        //Titanium tetrachloride is extremely volatile when exposed to open air.
        world.explode(null,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                3.0f, // explosion power
                Level.ExplosionInteraction.BLOCK); // destroys blocks


        BlockPos[] neighbors = {
                pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west()
        };

        for (BlockPos checkPos : neighbors) {
            BlockState neighbor = world.getBlockState(checkPos);

            if (neighbor.getBlock().isFlammable(neighbor, world, checkPos, Direction.UP)) {
                BlockPos firePos = checkPos.above(); // fire sits above the flammable block

                if (world.isEmptyBlock(firePos)) {
                    world.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 3);
                }
            }
        }

    }


    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        super.entityInside(state, world, pos, entity);

        if (entity instanceof Player living) {
            if (!world.isClientSide) {
                world.explode(null,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        3.0f, // explosion power
                        Level.ExplosionInteraction.NONE); // destroys blocks

//                world.playSound(null, entity.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
                DamageSource source = world.damageSources().hotFloor(); // or lava(), magic(), etc.
                living.hurt(source, 20.0F); // 2 damage (1 heart)
            }
        }

        // Absorb items
        if (entity instanceof ItemEntity item) {
            item.remove(Entity.RemovalReason.DISCARDED); // removes the item from the world
            // Optional: play a sound or particle effect
            world.playSound(null, item.blockPosition(), SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.5F, 1.0F);
            world.addParticle(
                    ParticleTypes.SMOKE, // particle type
                    item.getX(),         // x
                    item.getY(),         // y
                    item.getZ(),         // z
                    0.1 * (world.random.nextDouble() - 0.5), // dx
                    0.1 * (world.random.nextDouble() - 0.5), // dy
                    0.1 * (world.random.nextDouble() - 0.5)  // dz
            );
        }
    }

}

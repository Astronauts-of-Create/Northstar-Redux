package com.lightning.northstar.block.simple;

import com.lightning.northstar.block.entity.VenusExhaustBlockEntity;
import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.particle.NorthstarParticles;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class VenusSporeSpreaderBlock extends BaseEntityBlock implements IBE<VenusExhaustBlockEntity> {

    private static final MapCodec<VenusSporeSpreaderBlock> CODEC = simpleCodec(VenusSporeSpreaderBlock::new);

    public VenusSporeSpreaderBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public static void makeParticles(Level level, BlockPos pos) {
        if (!level.random.nextBoolean())
            return;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        level.addAlwaysVisibleParticle(NorthstarParticles.SULFUR_POOF.get(), true, x + 0.5, y + 2, z + 0.5, 0.0D, 0.0D, 0.0D);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int l = 0; l < 14 + level.random.nextInt(); ++l) {
            blockpos$mutableblockpos.set(x, y, z);
            BlockState blockstate = level.getBlockState(blockpos$mutableblockpos);
            if (!blockstate.isCollisionShapeFullBlock(level, blockpos$mutableblockpos)) {
                level.addAlwaysVisibleParticle(NorthstarParticles.SULFUR_POOF.get(), true, (double) blockpos$mutableblockpos.getX() + level.random.nextDouble(), (double) blockpos$mutableblockpos.getY() + level.random.nextDouble(), (double) blockpos$mutableblockpos.getZ() + level.random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, NorthstarBlockEntityTypes.VENUS_EXHAUST.get(), VenusExhaustBlockEntity::particleTick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public Class<VenusExhaustBlockEntity> getBlockEntityClass() {
        return VenusExhaustBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends VenusExhaustBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.VENUS_EXHAUST.get();
    }

}

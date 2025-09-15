package com.lightning.northstar.block.simple;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VenusMushroomBlock extends BushBlock implements BonemealableBlock {

    private static final MapCodec<VenusMushroomBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Properties.CODEC.fieldOf("properties").forGetter(VenusMushroomBlock::properties),
            ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("upright_feature").forGetter(b -> b.featureSupplier),
            ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("upside_down_feature").forGetter(b -> b.upsideDownSupplier)
    ).apply(i, VenusMushroomBlock::new));

    protected static final BooleanProperty IS_ON_CEILING = BooleanProperty.create("is_on_ceiling");
    protected static final VoxelShape CEILING_SHAPE = Block.box(5.0D, 10.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

    private final ResourceKey<ConfiguredFeature<?, ?>> upsideDownSupplier;
    private final ResourceKey<ConfiguredFeature<?, ?>> featureSupplier;

    public VenusMushroomBlock(Properties properties,
                              ResourceKey<ConfiguredFeature<?, ?>> upRightFeature,
                              @Nullable ResourceKey<ConfiguredFeature<?, ?>> upsideDownFeature) {
        super(properties);
        this.featureSupplier = upRightFeature;
        this.upsideDownSupplier = upsideDownFeature;
        this.registerDefaultState(this.defaultBlockState().setValue(IS_ON_CEILING, false));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(IS_ON_CEILING);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pState.getValue(IS_ON_CEILING)) {
            return CEILING_SHAPE;
        } else return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        if (pState.getValue(IS_ON_CEILING)) {
            BlockPos blockpos = pPos.above();
            BlockState blockstate = pLevel.getBlockState(blockpos);
            if (blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
                return true;
            } else {
                return blockstate.canSustainPlant(pLevel, blockpos, net.minecraft.core.Direction.DOWN, pState).isTrue();
            }
        } else {
            BlockPos blockpos = pPos.below();
            BlockState blockstate = pLevel.getBlockState(blockpos);
            if (blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
                return true;
            } else {
                return blockstate.canSustainPlant(pLevel, blockpos, net.minecraft.core.Direction.UP, pState).isTrue();
            }
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.isSolidRender(pLevel, pPos);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(IS_ON_CEILING, pContext.getClickedFace() == Direction.DOWN);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        growMushroom(level, pos, state, random, state.getValue(IS_ON_CEILING) ? upsideDownSupplier : featureSupplier);
    }

    public static boolean growMushroom(ServerLevel level, BlockPos pos, BlockState state, RandomSource random, ResourceKey<ConfiguredFeature<?, ?>> feature) {
        Optional<? extends Holder<ConfiguredFeature<?, ?>>> optional = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(feature);

        BlockGrowFeatureEvent event = EventHooks.fireBlockGrowFeature(level, random, pos, optional.orElse(null));
        if (event.isCanceled() || event.getFeature() == null) {
            return false;
        }

        level.removeBlock(pos, false);
        if (event.getFeature().value().place(level, level.getChunkSource().getGenerator(), random, pos)) {
            return true;
        }

        level.setBlock(pos, state, 3);
        return false;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(IS_ON_CEILING) ? ((VenusMushroomBlock) state.getBlock()).upsideDownSupplier != null : ((VenusMushroomBlock) (state.getBlock())).featureSupplier != null;
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return pState.getValue(IS_ON_CEILING) ? ((VenusMushroomBlock) (pState.getBlock())).upsideDownSupplier != null : ((VenusMushroomBlock) (pState.getBlock())).featureSupplier != null;
    // return (double)pRandom.nextFloat() < 0.4D;
    }

}

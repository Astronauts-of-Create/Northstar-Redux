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
            ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("ground").forGetter(b -> b.ground),
            ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("ceiling").forGetter(b -> b.ceiling)
    ).apply(i, VenusMushroomBlock::new));

    protected static final BooleanProperty IS_ON_CEILING = BooleanProperty.create("is_on_ceiling");

    protected static final VoxelShape GROUND_SHAPE = box(5, 0, 5, 11, 6, 11);
    protected static final VoxelShape CEILING_SHAPE = box(5, 10, 5, 11, 16, 11);

    private final ResourceKey<ConfiguredFeature<?, ?>> ground;
    private final ResourceKey<ConfiguredFeature<?, ?>> ceiling;

    public VenusMushroomBlock(Properties properties, ResourceKey<ConfiguredFeature<?, ?>> ground, ResourceKey<ConfiguredFeature<?, ?>> ceiling) {
        super(properties);
        this.ground = ground;
        this.ceiling = ceiling;

        registerDefaultState(defaultBlockState()
                .setValue(IS_ON_CEILING, false));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(IS_ON_CEILING));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(IS_ON_CEILING) ? CEILING_SHAPE : GROUND_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos otherPos = state.getValue(IS_ON_CEILING) ? pos.above() : pos.below();
        Direction direction = state.getValue(IS_ON_CEILING) ? Direction.DOWN : Direction.UP;

        BlockState blockstate = level.getBlockState(otherPos);
        return blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK) || blockstate.canSustainPlant(level, otherPos, direction, state).isTrue();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.isSolidRender(level, pos);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(IS_ON_CEILING, context.getClickedFace() == Direction.DOWN);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(IS_ON_CEILING) ? ((VenusMushroomBlock) (state.getBlock())).ceiling != null : ((VenusMushroomBlock) (state.getBlock())).ground != null;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return state.getValue(IS_ON_CEILING) ? ((VenusMushroomBlock) (state.getBlock())).ceiling != null : ((VenusMushroomBlock) (state.getBlock())).ground != null;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        Optional<Holder.Reference<ConfiguredFeature<?, ?>>> feature = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(state.getValue(IS_ON_CEILING) ? ceiling : ground);
        if (feature.isEmpty())
            return;

        BlockGrowFeatureEvent event = EventHooks.fireBlockGrowFeature(level, random, pos, feature.get());
        if (event.isCanceled())
            return;

        level.removeBlock(pos, false);
        if (event.getFeature().value().place(level, level.getChunkSource().getGenerator(), random, pos))
            return;

        level.setBlock(pos, state, 3);
    }

}

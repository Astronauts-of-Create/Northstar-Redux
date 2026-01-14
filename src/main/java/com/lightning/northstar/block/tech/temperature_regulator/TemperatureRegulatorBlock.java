package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarStats;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TemperatureRegulatorBlock extends HorizontalKineticBlock implements IBE<TemperatureRegulatorBlockEntity> {

    protected static final VoxelShape SHAPE = box(0, 0, 0, 16, 13, 16);

    public TemperatureRegulatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            player.awardStat(NorthstarStats.INTERACT_WITH_TEMPERATURE_REGULATOR);
        } else {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> withBlockEntityDo(level, pos, this::openScreen));
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreen(TemperatureRegulatorBlockEntity be) {
        ScreenOpener.open(new TemperatureRegulatorScreen(be.regulator, -1, be.getBlockPos()));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof TemperatureRegulatorBlockEntity regulator && regulator.getLevel() != null)
            return regulator.isCurrentlyWarm() ? 9 : 0;
        return 0;
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Class<TemperatureRegulatorBlockEntity> getBlockEntityClass() {
        return TemperatureRegulatorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TemperatureRegulatorBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.TEMPERATURE_REGULATOR.get();
    }

}

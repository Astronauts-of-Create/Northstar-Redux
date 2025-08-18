package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TemperatureRegulatorBlock extends HorizontalKineticBlock implements IBE<TemperatureRegulatorBlockEntity> {

    protected static final VoxelShape AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);

    public TemperatureRegulatorBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> withBlockEntityDo(level, pos, be -> this.displayScreen(be, player)));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> withBlockEntityDo(level, pos, be -> this.displayScreen(be, player)));
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof TemperatureRegulatorBlockEntity) {
                ((TemperatureRegulatorBlockEntity) blockentity).removeTemp((TemperatureRegulatorBlockEntity) blockentity);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AABB;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        TemperatureRegulatorBlockEntity reg = null;
        if (be instanceof TemperatureRegulatorBlockEntity) {
            reg = (TemperatureRegulatorBlockEntity) be;
        }
        if (reg == null || reg.getLevel() == null)
            return 0;
        return reg.temp > NorthstarPlanets.getPlanetTemp(reg.getLevel().dimension()) ? 9 : 0;
    }

    @OnlyIn(Dist.CLIENT)
    protected void displayScreen(TemperatureRegulatorBlockEntity be, Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new TemperatureRegulatorScreen(be));
    }


    @Override
    public Class<TemperatureRegulatorBlockEntity> getBlockEntityClass() {
        return TemperatureRegulatorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TemperatureRegulatorBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.TEMPERATURE_REGULATOR_BLOCK_ENTITY.get();
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

}

package com.lightning.northstar.block.tech.atmospheric_concentrator;

import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarStats;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AtmosphericConcentratorBlock extends HorizontalKineticBlock implements IBE<AtmosphericConcentratorBlockEntity> {

    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 13, 16);

    public AtmosphericConcentratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getPlayer() != null && context.getPlayer().isCrouching())
            return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
        return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        player.awardStat(NorthstarStats.INTERACT_WITH_ATMOSPHERIC_CONCENTRATOR);
        if (level.isClientSide()) {
            RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> withBlockEntityDo(level, pos, this::openScreen));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreen(AtmosphericConcentratorBlockEntity be) {
        if (be.getLevel().northstar$dimension().atmosphere().isVacuum()) {
            Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("northstar.gui.atmospheric_concentrator.no_atmosphere").withStyle(ChatFormatting.RED), false);
        } else {
            ScreenOpener.open(new AtmosphericConcentratorScreen(be));
        }
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
    public Class<AtmosphericConcentratorBlockEntity> getBlockEntityClass() {
        return AtmosphericConcentratorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AtmosphericConcentratorBlockEntity> getBlockEntityType() {
        return NorthstarBlockEntityTypes.ATMOSPHERIC_CONCENTRATOR.get();
    }

}

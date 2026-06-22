package com.lightning.northstar.block.tech.telescope;

import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.content.NorthstarStats;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.util.NorthstarLang;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TelescopeBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 24, 12);

    public TelescopeBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return use(level, pos, player).result();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return use(level, pos, player);
    }

    public ItemInteractionResult use(Level level, BlockPos pos, Player player) {
        player.awardStat(NorthstarStats.INTERACT_WITH_TELESCOPE);

        if (level.northstar$planet() == null) {
            player.displayClientMessage(Component.translatable("northstar.block.telescope.invalid_dimension").withStyle(ChatFormatting.RED), true);
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        // this isn't called on ClientLevel making isNight() always return false
        level.updateSkyBrightness();

        if (!level.isNight() && level.northstar$dimension().hasAtmosphere()) {
            player.displayClientMessage(Component.translatable("northstar.block.telescope.requires_night").withStyle(ChatFormatting.RED), true);
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        if (level.isClientSide()) {
            RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openScreen(level, pos));
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    private static void openScreen(Level level, BlockPos pos) {
        ScreenOpener.open(new TelescopeScreen(level, pos));
    }

    public static void handlePrintRequest(ServerPlayer player, BlockPos pos, ResourceLocation planetId) {
        Level level = player.level();
        Planet currentPlanet = level.northstar$planet();
        Planet targetPlanet = level.northstar$getPlanetTracker().getPlanetById(planetId);
        if (currentPlanet == null || targetPlanet == null) {
            return;
        }

        boolean foundPaper = false;
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack item = inventory.items.get(i);
            if (item.is(Items.PAPER)) {
                item.setCount(item.getCount() - 1);
                foundPaper = true;
                break;
            }
        }
        if (!foundPaper && !player.isCreative()) {
            return;
        }

        // TODO: The science value should be dynamic based on the origin and target planets as well as the telescope size
        float value = 1;
        int day = (int) (level.getDayTime() / 24000L);

        MutableComponent name = Component.translatable("item.northstar.astronomical_reading.planet", targetPlanet.getName());
        MutableComponent line1 = Component.translatable("item.northstar.astronomical_reading.value", NorthstarLang.numberDirect(value)).withStyle(ChatFormatting.WHITE);
        MutableComponent line0 = Component.translatable("item.northstar.astronomical_reading.day", NorthstarLang.numberDirect(day)).withStyle(ChatFormatting.WHITE);

        ItemStack reading = new ItemStack(NorthstarItems.ASTRONOMICAL_READING.get(), 1);
        reading.set(DataComponents.ITEM_NAME, name);
        reading.set(DataComponents.LORE, new ItemLore(List.of(line0, line1)));
        reading.set(NorthstarDataComponents.ASTRONOMICAL_READING_DATA, new AstronomicalReadingData(currentPlanet.key.location(), targetPlanet.key.location(), value, day));

        level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), reading));
        level.playSound(player, pos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1, 1);
    }

}

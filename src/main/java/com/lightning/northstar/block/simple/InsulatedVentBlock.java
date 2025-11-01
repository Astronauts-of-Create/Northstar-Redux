package com.lightning.northstar.block.simple;

import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.world.sealer.SealableBlock;
import com.lightning.northstar.world.sealer.SealingMode;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.utility.DyeHelper;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class InsulatedVentBlock extends VentBlock implements SealableBlock, IWrenchable {

    public static final Property<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    public InsulatedVentBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(COLOR, DyeColor.WHITE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(COLOR));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(NorthstarBlocks.VENT);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);

        if (newState.getBlock() instanceof VentBlock || movedByPiston)
            return;
        popResource(level, pos, new ItemStack(DyeHelper.getWoolOfDye(state.getValue(COLOR))));
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (state.hasProperty(COLOR)) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();

            IWrenchable.super.playRemoveSound(level, pos);
            popResource(level, pos, new ItemStack(DyeHelper.getWoolOfDye(state.getValue(COLOR))));
            level.setBlock(pos, NorthstarBlocks.VENT.get().withPropertiesOf(state), Block.UPDATE_ALL);
            return InteractionResult.SUCCESS;
        }

        return IWrenchable.super.onSneakWrenched(state, context);
    }

    @Override
    public boolean northstar$isFaceSealed(BlockGetter level, BlockPos pos, BlockState state, Direction direction, boolean source, SealingMode mode) {
        return mode == SealingMode.TEMPERATURE;
    }

    public static void generateBlockStateModel(DataGenContext<Block, InsulatedVentBlock> context, RegistrateBlockstateProvider provider) {
        provider.getVariantBuilder(context.get()).forAllStatesExcept(state -> {
            DyeColor color = state.getValue(COLOR);

            return ConfiguredModel.builder()
                    .modelFile(provider.models()
                            .withExistingParent("block/vent_insulated_" + color.getName(), provider.modLoc("block/vent_insulated"))
                            .texture("wool", ResourceLocation.withDefaultNamespace("block/" + color.getName() + "_wool")))
                    .build();
        }, WATERLOGGED);
    }

}

package com.lightning.northstar.block.tech.rocket_waypoint;

import com.lightning.northstar.accessor.NorthstarOutliner;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.lightning.northstar.item.atlas.SpaceAtlasContent;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RocketWaypointBlock extends Block implements IWrenchable {

    public static final VoxelShape SHAPE = Shapes.or(
            box(0, 0, 0, 16, 4, 16),
            box(3, 4, 3, 13, 14, 13),
            box(6, 14, 6, 10, 16, 10)
    );

    public RocketWaypointBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        Direction direction = hit.getDirection();
        if (!NorthstarItems.SPACE_ATLAS.isIn(stack) || direction.getAxis() == Direction.Axis.Y) {
            return super.use(state, level, pos, player, hand, hit);
        }

        if (!level.isClientSide()) {
            SpaceAtlasContent content = SpaceAtlasContent.fromTag(stack.getOrCreateTag());
            RocketDestination destination = new RocketDestination(level.dimension().location(), pos, direction);

            content.destinations.keySet().removeIf(dest -> destination.dim().equals(dest.dim()) && destination.pos().equals(dest.pos()));

            if (content.destinations.size() >= NorthstarConfigs.server().spaceAtlasMaxWaypoints.get()) {
                player.sendSystemMessage(Component.translatable("northstar.gui.rocket_waypoint.max_waypoints"));
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            playEffect(pos, direction);

            content.destinations.put(destination, SpaceAtlasContent.getDefaultLabel(pos, direction));
            player.sendSystemMessage(Component.translatable("northstar.gui.rocket_waypoint.added_waypoint"));

            content.toTag(stack.getOrCreateTag());
            player.setItemInHand(hand, stack);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    public static void playEffect(BlockPos pos, Direction dir) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Outliner.OutlineEntry outline = NorthstarOutliner.getInstance().northstar$add(pos, new RocketWaypointOutline(pos, dir));
            ((NorthstarOutliner.OutlineEntry) outline).northstar$setTimeToLive(RocketWaypointOutline.DURATION - Outliner.OutlineEntry.FADE_TICKS);
            outline.getOutline()
                    .getParams()
                    .colored(0x2c9edb)
                    .withFaceTexture(AllSpecialTextures.SELECTION)
                    .lineWidth(1 / 16f);
        });
    }

}

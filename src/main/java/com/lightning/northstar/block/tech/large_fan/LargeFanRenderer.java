package com.lightning.northstar.block.tech.large_fan;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.RenderTypes;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Arrays;

public class LargeFanRenderer extends SafeBlockEntityRenderer<LargeFanBlockEntity> {

    private static final ResourceLocation CHAIN_LOCATION = Northstar.asResource("textures/block/chain.png");
    private static final Vector3f[] CHAIN_STEP = Arrays.stream(Direction.values())
            .map(Direction::step)
            .map(v -> v.mul(6f / 16f))
            .toArray(Vector3f[]::new);

    private final Vector3f offset = new Vector3f();
    private final Vector3f scale = new Vector3f();
    private final Vector3f chainA = new Vector3f();
    private final Vector3f chainB = new Vector3f();
    private final Vector3f side = new Vector3f();

    public LargeFanRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(LargeFanBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = be.getBlockState();
        Direction.Axis axis = state.getValue(LargeFanBlock.AXIS);
        Direction dir = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        TenPatch patch = state.getValue(LargeFanBlock.PATCH);

        PartialModel model = switch (patch.type) {
            case SINGLE -> NorthstarPartialModels.LARGE_FAN_SINGLE;
            case CENTER -> NorthstarPartialModels.LARGE_FAN_CENTER;
            case CORNER -> NorthstarPartialModels.LARGE_FAN_CORNER;
            case SIDE -> NorthstarPartialModels.LARGE_FAN_SIDE;
        };
        CachedBuffers.partialFacing(model, state, dir)
                .rotateCenteredDegrees(patch.rotation, axis)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.cutout()));

        if (!be.isController())
            return;

        Vector3f offset = this.offset.set(be.getVolumeX(), be.getVolumeY(), be.getVolumeZ()).mul(0.5f);

        float rot = be.angle + be.effectiveSpeed.getValue() / (20 * 60) * partialTicks;
        float angle = rot * Mth.TWO_PI;
        int blades = be.blades;

        float size = be.width == 1 ? 0.5f - 2f / 16f : (be.width - 8f / 16f) * 0.5f;
        Vector3f scale = switch (axis) {
            case X -> this.scale.set(0.999f, size, size);
            case Y -> this.scale.set(size, 0.999f, size);
            case Z -> this.scale.set(size, size, 0.999f);
        };
        CachedBuffers.block(AllBlocks.SHAFT.getDefaultState().setValue(BlockStateProperties.AXIS, axis))
                .translate(offset)
                .scale(scale)
                .rotate(axis, angle)
                .uncenter()
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        CachedBuffers.partialFacing(NorthstarPartialModels.LARGE_FAN_ROTOR, state, dir)
                .translate(offset)
                .scale(size)
                .rotate(axis, angle)
                .uncenter()
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        if (be.chain != null)
            renderChain(be, be.getBlockPos(), be.chain, offset, ms, buffer, light, dir, rot);

        for (int i = 0; i < blades; i++) {
            float a = angle + Mth.TWO_PI * i / blades;

            CachedBuffers.partial(NorthstarPartialModels.LARGE_FAN_BLADE, state)
                    .translate(offset)
                    .scale(size)
                    .rotate(axis, a)
                    .rotateYDegrees(AngleHelper.horizontalAngle(dir))
                    .rotateXDegrees(AngleHelper.verticalAngle(dir))
                    .light(light)
                    .renderInto(ms, buffer.getBuffer(RenderType.cutout()));
        }
    }

    // not the prettiest code but it works. and yes the chain is very slightly skewed to avoid computing the side vector 3 times.
    // but it shouldn't be a problem unless the fan is thousands of blocks wide (and even then it might not be noticeable)
    protected void renderChain(LargeFanBlockEntity be, BlockPos pos1, BlockPos pos2, Vector3f center, PoseStack ms, MultiBufferSource buffer, int light, Direction dir, float rot) {
        Vector3f step = CHAIN_STEP[(be.flipChain ? dir.getOpposite() : dir).ordinal()];

        Vector3f chainA = this.chainA.set(center).add(step);
        Vector3f chainB = this.chainB.set(pos2.getX() - pos1.getX() + 0.5f, pos2.getY() - pos1.getY() + 0.5f, pos2.getZ() - pos1.getZ() + 0.5f).add(step);
        float length = chainA.distance(chainB);

        Vector3f side = chainB.sub(chainA, this.side).mul(1f / length);
        switch (dir.getAxis()) {
            case X -> side.rotateX(Mth.HALF_PI);
            case Y -> side.rotateY(Mth.HALF_PI);
            case Z -> side.rotateZ(Mth.HALF_PI);
        }

        Matrix4f mm = ms.last().pose();
        PoseStack.Pose mn = ms.last();
        VertexConsumer vc = buffer.getBuffer(RenderTypes.chain(CHAIN_LOCATION));

        float spacing = be.width * 0.5f * 2f / 16f;

        renderChain(mm, mn, vc, chainA, chainB, side, spacing - 1 / 16f, spacing, 2f / 16f, 1f / 16f, rot * 3, length * 3, dir, light);
        renderChain(mm, mn, vc, chainA, chainB, side, -spacing + 1 / 16f, -spacing, -2f / 16f, -1f / 16f, 1 - rot * 3, length * 3, dir.getOpposite(), light);
    }

    private static void renderChain(Matrix4f mm, PoseStack.Pose mn, VertexConsumer vc, Vector3f pos1, Vector3f pos2, Vector3f side, float d1, float d2, float d3, float d4, float offset, float length, Direction direction, int light) {
        length += offset;

        // front
        addVertex(mm, mn, vc, pos1.x + side.x * d1, pos1.y + side.y * d1, pos1.z + side.z * d1, 0f / 16f, offset, direction, light);
        addVertex(mm, mn, vc, pos1.x + side.x * d2, pos1.y + side.y * d2, pos1.z + side.z * d2, 3f / 16f, offset, direction, light);
        addVertex(mm, mn, vc, pos2.x + side.x * d3, pos2.y + side.y * d3, pos2.z + side.z * d3, 3f / 16f, length, direction, light);
        addVertex(mm, mn, vc, pos2.x + side.x * d4, pos2.y + side.y * d4, pos2.z + side.z * d4, 0f / 16f, length, direction, light);

        // back
        addVertex(mm, mn, vc, pos1.x + side.x * d1, pos1.y + side.y * d1, pos1.z + side.z * d1, 0f / 16f, offset, direction, light);
        addVertex(mm, mn, vc, pos2.x + side.x * d4, pos2.y + side.y * d4, pos2.z + side.z * d4, 0f / 16f, length, direction, light);
        addVertex(mm, mn, vc, pos2.x + side.x * d3, pos2.y + side.y * d3, pos2.z + side.z * d3, 3f / 16f, length, direction, light);
        addVertex(mm, mn, vc, pos1.x + side.x * d2, pos1.y + side.y * d2, pos1.z + side.z * d2, 3f / 16f, offset, direction, light);
    }

    private static void addVertex(Matrix4f pose, PoseStack.Pose normal, VertexConsumer vc, float x, float y, float z, float u, float v, Direction direction, int light) {
        vc.addVertex(pose, x, y, z)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(normal, direction.getStepX(), direction.getStepY(), direction.getStepZ());
    }

}

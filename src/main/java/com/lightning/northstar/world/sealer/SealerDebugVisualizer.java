package com.lightning.northstar.world.sealer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public interface SealerDebugVisualizer {

    void addConnection(long pos1, long pos2, int color);

    void complete();

    @OnlyIn(Dist.CLIENT)
    void render(PoseStack pose, MultiBufferSource buffer);

    Noop NOOP = new Noop();

    class Noop implements SealerDebugVisualizer {
        private Noop() {
        }

        @Override
        public void addConnection(long pos1, long pos2, int color) {
        }

        @Override
        public void complete() {
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void render(PoseStack pose, MultiBufferSource buffer) {
        }
    }

    class Client implements SealerDebugVisualizer {
        private final MutableBlockPos tempPos1 = new MutableBlockPos();
        private final MutableBlockPos tempPos2 = new MutableBlockPos();
        private final List<DebugConnection> debugConnections = new ArrayList<>();
        private final List<DebugConnection> finalDebugConnections = new ArrayList<>();

        @Override
        public void addConnection(long pos1, long pos2, int color) {
            debugConnections.add(new DebugConnection(pos1, pos2, color));
        }

        @Override
        public void complete() {
            finalDebugConnections.clear();
            finalDebugConnections.addAll(debugConnections);
            debugConnections.clear();
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void render(PoseStack pose, MultiBufferSource buffer) {
            VertexConsumer vc = buffer.getBuffer(RenderType.lines());

            pose.pushPose();
            pose.translate(0.5, 0.5, 0.5);
            Matrix4f mp = pose.last().pose();
            Matrix3f mn = pose.last().normal();

            for (DebugConnection connection : finalDebugConnections) {
                tempPos1.set(connection.pos1());
                tempPos2.set(connection.pos2());

                float nx = tempPos2.getX() - tempPos1.getX();
                float ny = tempPos2.getY() - tempPos1.getY();
                float nz = tempPos2.getZ() - tempPos1.getZ();

                vc.vertex(mp, tempPos1.getX(), tempPos1.getY(), tempPos1.getZ()).color(connection.color).normal(mn, nx, ny, nz).endVertex();
                vc.vertex(mp, tempPos2.getX(), tempPos2.getY(), tempPos2.getZ()).color(connection.color).normal(mn, nx, ny, nz).endVertex();
            }

            pose.popPose();
        }

        private record DebugConnection(long pos1, long pos2, int color) {
        }
    }

}

package com.lightning.northstar.world.sealer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.LongLongPair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.util.Color;

import java.util.ArrayList;
import java.util.List;

public interface SealerDebugVisualizer {

    void addConnection(long pos1, long pos2);

    void complete();

    @OnlyIn(Dist.CLIENT)
    void render(PoseStack pose, MultiBufferSource buffer);

    Noop NOOP = new Noop();

    class Noop implements SealerDebugVisualizer {
        private Noop() {
        }

        @Override
        public void addConnection(long pos1, long pos2) {
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

        private final int color = Color.HSBtoARGB((float) Math.random(), 0.8f, 0.8f);
        private final MutableBlockPos tempPos1 = new MutableBlockPos();
        private final MutableBlockPos tempPos2 = new MutableBlockPos();
        private final List<LongLongPair> debugConnections = new ArrayList<>();
        private final List<LongLongPair> finalDebugConnections = new ArrayList<>();

        @Override
        public void addConnection(long pos1, long pos2) {
            debugConnections.add(LongLongPair.of(pos1, pos2));
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
            PoseStack.Pose last = pose.last();

            for (LongLongPair connection : finalDebugConnections) {
                tempPos1.set(connection.firstLong());
                tempPos2.set(connection.secondLong());

                float nx = tempPos2.getX() - tempPos1.getX();
                float ny = tempPos2.getY() - tempPos1.getY();
                float nz = tempPos2.getZ() - tempPos1.getZ();

                vc.addVertex(last, tempPos1.getX() + 0.5f, tempPos1.getY() + 0.5f, tempPos1.getZ() + 0.5f).setColor(color).setNormal(last, nx, ny, nz);
                vc.addVertex(last, tempPos2.getX() + 0.5f, tempPos2.getY() + 0.5f, tempPos2.getZ() + 0.5f).setColor(color).setNormal(last, nx, ny, nz);
            }
        }
    }

}

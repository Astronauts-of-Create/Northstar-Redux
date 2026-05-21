package com.lightning.northstar.compat.oculus;

import com.lightning.northstar.data.ModCompat;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface OculusCompat {

    OculusCompat $ = ModCompat.OCULUS.<OculusCompat>runIfLoaded(() -> Instance::new).orElseGet(Stub::new);

    void pushRenderPhase(OculusPhase phase);

    void popRenderPhase();

    class Stub implements OculusCompat {
        private Stub() {
        }

        @Override
        public void pushRenderPhase(OculusPhase phase) {
        }

        @Override
        public void popRenderPhase() {
        }
    }

    class Instance implements OculusCompat {
        private static final WorldRenderingPhase[] PHASES = Arrays.stream(OculusPhase.values())
                .map(phase -> {
                    try {
                        return WorldRenderingPhase.valueOf(phase.name());
                    } catch (IllegalArgumentException ignored) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(WorldRenderingPhase[]::new);

        private Deque<WorldRenderingPhase> phases = new ArrayDeque<>();

        private Instance() {
        }

        @Override
        public void pushRenderPhase(OculusPhase phase) {
            WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
            if (pipeline != null) {
                phases.push(pipeline.getPhase());
                pipeline.setPhase(PHASES[phase.ordinal()]);
            }
        }

        @Override
        public void popRenderPhase() {
            WorldRenderingPipeline pipeline = Iris.getPipelineManager().getPipelineNullable();
            if (pipeline != null && !phases.isEmpty()) {
                pipeline.setPhase(phases.pop());
            }
        }

    }

}

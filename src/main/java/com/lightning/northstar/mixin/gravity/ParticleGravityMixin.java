package com.lightning.northstar.mixin.gravity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Particle.class)
public class ParticleGravityMixin {

    @Shadow
    @Final
    protected ClientLevel level;

    @ModifyConstant(
            method = {
                    "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)V",
                    "setPower"
            },
            constant = @Constant(doubleValue = (double) 0.1f)
    )
    private double northstar$modifyInitialVelocity(double original) {
        // particles have a small vertical velocity bias which makes them go up slightly before falling due to gravity.
        // however in zero gravity environments this causes all particles to float upwards looking weird, this fixes it
        return level.northstar$isZeroGravity() ? 0 : original;
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/particle/Particle;gravity:F",
                    opcode = Opcodes.GETFIELD
            )
    )
    private float northstar$modifyGravity(float original) {
        return original * level.northstar$gravityScale();
    }

    @Mixin(FallingDustParticle.class)
    public static abstract class FallingDustParticleMixin extends Particle {
        protected FallingDustParticleMixin(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
        }

        @ModifyConstant(method = "tick", constant = @Constant(doubleValue = (double) 0.003F))
        private double northstar$modifyGravity(double constant) {
            return constant * level.northstar$gravityScale();
        }
    }

    @Mixin(SquidInkParticle.class)
    public static abstract class SquidInkParticleMixin extends Particle {
        protected SquidInkParticleMixin(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
        }

        @ModifyConstant(method = "tick", constant = @Constant(doubleValue = (double) 0.0074F))
        private double northstar$modifyGravity(double constant) {
            return constant * level.northstar$gravityScale();
        }
    }

    // this is so dumb
    // all of that just because "Particle#gravity" and "SomeOtherParticle#gravity" aren't the same

    @Mixin(BubblePopParticle.class)
    public static abstract class BubblePopParticleMixin extends Particle {
        protected BubblePopParticleMixin(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
        }

        @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/BubblePopParticle;gravity:F", opcode = Opcodes.GETFIELD))
        private float northstar$modifyGravity(float original) {
            return original * level.northstar$gravityScale();
        }
    }

    @Mixin(CampfireSmokeParticle.class)
    public static abstract class CampfireSmokeParticleMixin extends Particle {
        protected CampfireSmokeParticleMixin(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
        }

        @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/CampfireSmokeParticle;gravity:F", opcode = Opcodes.GETFIELD))
        private float northstar$modifyGravity(float original) {
            return original * level.northstar$gravityScale();
        }
    }

    @Mixin(CherryParticle.class)
    public static abstract class CherryParticleMixin extends Particle {
        protected CherryParticleMixin(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
        }

        @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/CherryParticle;gravity:F", opcode = Opcodes.GETFIELD))
        private float northstar$modifyGravity(float original) {
            return original * level.northstar$gravityScale();
        }
    }

    @Mixin(DripParticle.class)
    public static abstract class DripParticleMixin extends Particle {
        protected DripParticleMixin(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
        }

        @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/DripParticle;gravity:F", opcode = Opcodes.GETFIELD))
        private float northstar$modifyGravity(float original) {
            return original * level.northstar$gravityScale();
        }
    }

    @Mixin(WakeParticle.class)
    public static abstract class WakeParticleMixin extends Particle {
        protected WakeParticleMixin(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
        }

        @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/WakeParticle;gravity:F", opcode = Opcodes.GETFIELD))
        private float northstar$modifyGravity(float original) {
            return original * level.northstar$gravityScale();
        }
    }

    @Mixin(WaterDropParticle.class)
    public static abstract class WaterDropParticleMixin extends Particle {
        protected WaterDropParticleMixin(ClientLevel level, double x, double y, double z) {
            super(level, x, y, z);
        }

        @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/WaterDropParticle;gravity:F", opcode = Opcodes.GETFIELD))
        private float northstar$modifyGravity(float original) {
            return original * level.northstar$gravityScale();
        }
    }

}

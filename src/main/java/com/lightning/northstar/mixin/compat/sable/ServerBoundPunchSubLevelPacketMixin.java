package com.lightning.northstar.mixin.compat.sable;

import com.lightning.northstar.accessor.MissingMixinException;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.SableConfig;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.index.SableAttributes;
import dev.ryanhcode.sable.network.packets.tcp.ServerboundPunchSubLevelPacket;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@Mixin(ServerboundPunchSubLevelPacket.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ServerBoundPunchSubLevelPacketMixin {

    @Shadow
    public static double punchCurve(double x) {
        throw new MissingMixinException();
    }

    @Shadow
    @Final
    private Vector3dc direction;

    @ModifyExpressionValue(
            method = "handle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"
            )
    )
    private boolean northstar$handleOnGround(
            boolean original,
            @Local(name = "level") ServerLevel level
    ) {
        return original || level.northstar$isZeroGravity();
    }

    @ModifyExpressionValue(
            method = "handle",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ryanhcode/sable/network/packets/tcp/ServerboundPunchSubLevelPacket;computeStrengthScalar(Ldev/ryanhcode/sable/sublevel/ServerSubLevel;Lorg/joml/Vector3dc;Lorg/joml/Vector3dc;)D",
                    ordinal = 1
            )
    )
    private double northstar$handle(
            double original,
            @Local(name = "player") Player player
    ) {
        if (player.onGround() || !player.level().northstar$isZeroGravity()) {
            return original;
        }

        double playerMass = 2f;

        double generalizedInverseMass = 1f / playerMass;
        double mass = 1.0 / generalizedInverseMass;
        double strengthMultiplier = SableConfig.SUB_LEVEL_PUNCH_STRENGTH_MULTIPLIER.getAsDouble();

        double playerStrength = punchCurve(mass) * strengthMultiplier;
        original = Math.min(playerStrength, original);

        Vector3d dir = new Vector3d(direction)
                .normalize()
                .mul(-Objects.requireNonNull(player.getAttribute(SableAttributes.PUNCH_STRENGTH)).getValue() * original * 0.01);

        player.setDeltaMovement(player.getDeltaMovement().add(JOMLConversion.toMojang(dir)));
        ((ServerPlayer) player).connection.send(new ClientboundSetEntityMotionPacket(player));

        return original;
    }

}

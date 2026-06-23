package com.lightning.northstar.mixin.client;

import com.lightning.northstar.NorthstarClient;
import com.lightning.northstar.api.client.NorthstarDimensionEffectsExtension;
import com.lightning.northstar.client.renderer.effect.SpaceEffects;
import com.lightning.northstar.content.NorthstarTextures;
import com.lightning.northstar.planet.PlanetRenderer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    private ClientLevel level;

    @Unique
    private final Matrix4f northstar$transform = new Matrix4f();
    @Unique
    private final Matrix3f northstar$normal = new Matrix3f();

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Lighting;setupLevel(Lorg/joml/Matrix4f;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void northstar$onSetupLight(PoseStack pose, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (level.effects() instanceof NorthstarDimensionEffectsExtension effects) {
            effects.northstar$setupLight(pose);
        }
    }

    @ModifyExpressionValue(
            method = "renderSky",
            at = {
                    @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/renderer/LevelRenderer;SUN_LOCATION:Lnet/minecraft/resources/ResourceLocation;",
                            opcode = Opcodes.GETSTATIC
                    ),
                    @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/renderer/LevelRenderer;MOON_LOCATION:Lnet/minecraft/resources/ResourceLocation;",
                            opcode = Opcodes.GETSTATIC
                    )
            }
    )
    private ResourceLocation northstar$disableVanillaSunAndMoon(ResourceLocation original) {
        return level.northstar$planet() == null ? original : NorthstarTextures.EMPTY;
    }

    // This mixin only applies to the overworld, the nether and the end are unaffected.
    // Other dimensions are handled from their own dimension special effects;
    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",
                    ordinal = 4,
                    remap = false
            )
    )
    private void northstar$onRenderSky(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci) {
        float starOpacity = Math.max(level.getStarBrightness(partialTick) * 2, NorthstarClient.getAtmosphereBlend());

        // We need to inject somewhere during the sun/moon/stars rendering so it renders during the proper phase for
        //  optimization/shader mods, we need to pop the pose that includes the rotation for our own rendering, the
        //  pose shouldn't be used anymore afterward but in case another mods injects between this and the popPose().
        PoseStack.Pose last = poseStack.last();
        northstar$transform.set(last.pose());
        northstar$normal.set(last.normal());
        poseStack.popPose();

        SpaceEffects.renderStars(poseStack, projectionMatrix, skyFogSetup, starOpacity);
        PlanetRenderer.render(level, poseStack, camera, starOpacity, NorthstarClient.getAtmosphereBlend());

        RenderSystem.depthMask(false);

        poseStack.pushPose();
        last = poseStack.last();
        last.pose().set(northstar$transform);
        last.normal().set(northstar$normal);
    }

    @ModifyExpressionValue(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"
            )
    )
    private float[] northstar$modifySunriseColor(float[] original) {
        if (original != null) {
            original[3] *= (1 - NorthstarClient.getAtmosphereBlend());
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 northstar$modifySkyColor(Vec3 original) {
        return original.lerp(Vec3.ZERO, NorthstarClient.getAtmosphereBlend());
    }

    @ModifyExpressionValue(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"
            )
    )
    private float northstar$modifyStarBrightness(float original) {
        return Math.max(original, NorthstarClient.getAtmosphereBlend());
    }

    @ModifyExpressionValue(
            method = "renderSnowAndRain",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
            )
    )
    private float northstar$modifyRainLevel(float original) {
        return original * (1f - NorthstarClient.getAtmosphereBlend());
    }

}

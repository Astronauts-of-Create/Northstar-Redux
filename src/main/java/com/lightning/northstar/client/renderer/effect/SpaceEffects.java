package com.lightning.northstar.client.renderer.effect;

import com.lightning.northstar.NorthstarClient;
import com.lightning.northstar.api.client.NorthstarDimensionEffectsExtension;
import com.lightning.northstar.compat.oculus.OculusCompat;
import com.lightning.northstar.compat.oculus.OculusPhase;
import com.lightning.northstar.planet.PlanetRenderer;
import com.lightning.northstar.planet.data.Atmosphere;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpaceEffects extends DimensionSpecialEffects implements NorthstarDimensionEffectsExtension {

    private static final VertexBuffer starBuffer1;
    private static final VertexBuffer starBuffer2;
    private static final VertexBuffer starBuffer3;

    static {
        starBuffer1 = createStars(1500, 10842L);
        starBuffer2 = createStars(1800, 64094L);
        starBuffer3 = createStars(2500, 92410L);
    }

    public SpaceEffects(float cloudLevel, boolean hasGround, SkyType skyType, boolean forceBrightLightmap, boolean constantAmbientLight) {
        super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
    }

    public SpaceEffects(boolean hasGround) {
        this(Float.NaN, hasGround, SkyType.NORMAL, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return fogColor;
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return false;
    }

    @Override
    public void northstar$setupFogRender(ViewportEvent.RenderFog fog) {
        if (!Minecraft.getInstance().level.northstar$dimension().hasAtmosphere()) {
            fog.setCanceled(true);
            fog.setNearPlaneDistance(Float.POSITIVE_INFINITY);
            fog.setFarPlaneDistance(Float.POSITIVE_INFINITY);
        }
    }

    @Override
    public float @Nullable [] getSunriseColor(float time, float tickDelta) {
        return null;
    }

    public float getStarBrightness(ClientLevel level, float partialTick) {
        Atmosphere atmosphere = level.northstar$dimension().atmosphere();
        float brightness = Math.max(level.getStarBrightness(partialTick) * (1 - level.getRainLevel(partialTick)) * 2, NorthstarClient.getAtmosphereBlend());
        return atmosphere.daytimeStarBrightness() + brightness * (1 - atmosphere.daytimeStarBrightness());
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        float starBrightness = getStarBrightness(level, partialTick);
        PoseStack pose = new PoseStack();
        pose.mulPose(modelViewMatrix);
        renderStars(pose, projectionMatrix, setupFog, starBrightness);
        PlanetRenderer.render(level, pose, camera, starBrightness, NorthstarClient.getAtmosphereBlend());
        return true;
    }

    public static void renderStars(PoseStack pose, Matrix4f projectionMatrix, Runnable setupFog, float starBrightness) {
        if (starBrightness <= 0) {
            return;
        }

        OculusCompat.$.pushRenderPhase(OculusPhase.STARS);

        pose.pushPose();

        Minecraft mc = Minecraft.getInstance();
        pose.mulPose(Axis.YP.rotationDegrees(-90));
        pose.mulPose(Axis.XP.rotationDegrees(mc.level.getTimeOfDay(mc.getTimer().getGameTimeDeltaTicks()) * 360));

        FogRenderer.setupNoFog();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.depthMask(false);

        RenderSystem.setShaderColor(1, 1, 1, starBrightness);
        starBuffer1.bind();
        starBuffer1.drawWithShader(pose.last().pose(), projectionMatrix, GameRenderer.getPositionShader());

        RenderSystem.setShaderColor(1, 1, 1, starBrightness * 2f / 3f);
        starBuffer2.bind();
        starBuffer2.drawWithShader(pose.last().pose(), projectionMatrix, GameRenderer.getPositionShader());

        RenderSystem.setShaderColor(1, 1, 1, starBrightness / 3f);
        starBuffer3.bind();
        starBuffer3.drawWithShader(pose.last().pose(), projectionMatrix, GameRenderer.getPositionShader());

        pose.popPose();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        setupFog.run();

        OculusCompat.$.popRenderPhase();
    }

    private static VertexBuffer createStars(int count, long seed) {
        RandomSource random = RandomSource.create(seed);
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        for (int i = 0; i < count; i++) {
            double dx = random.nextFloat() * 2 - 1;
            double dy = random.nextFloat() * 2 - 1;
            double dz = random.nextFloat() * 2 - 1;
            double size = 0.15 + random.nextFloat() * 0.1;
            double distance = dx * dx + dy * dy + dz * dz;
            if (distance < 1.0 && distance > 0.01) {
                distance = 1.0 / Math.sqrt(distance);
                dx *= distance;
                dy *= distance;
                dz *= distance;
                double x = dx * 100;
                double y = dy * 100;
                double z = dz * 100;
                double yaw = Math.atan2(dx, dz);
                double sinYaw = Math.sin(yaw);
                double cosYaw = Math.cos(yaw);
                double pitch = Math.atan2(Math.sqrt(dx * dx + dz * dz), dy);
                double sinPitch = Math.sin(pitch);
                double cosPitch = Math.cos(pitch);
                double rotation = random.nextDouble() * Math.PI * 2.0D;
                double sinRot = Math.sin(rotation);
                double cosRot = Math.cos(rotation);
                for (int j = 0; j < 4; j++) {
                    double rawVertexX = ((j & 2) - 1) * size;
                    double rawVertexY = ((j + 1 & 2) - 1) * size;
                    double rotatedU = rawVertexX * cosRot - rawVertexY * sinRot;
                    double rotatedV = rawVertexY * cosRot + rawVertexX * sinRot;
                    double vy = rotatedU * sinPitch;
                    double d24 = -rotatedU * cosPitch;
                    double vx = d24 * sinYaw - rotatedV * cosYaw;
                    double vz = rotatedV * sinYaw + d24 * cosYaw;
                    builder.addVertex((float) (x + vx), (float) (y + vy), (float) (z + vz));
                }
            }
        }

        VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buffer.bind();
        buffer.upload(builder.buildOrThrow());
        return buffer;
    }

}

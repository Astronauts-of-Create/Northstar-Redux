package com.lightning.northstar.planet;

import com.lightning.northstar.NorthstarClient;
import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.compat.oculus.OculusCompat;
import com.lightning.northstar.compat.oculus.OculusPhase;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarPlanets;
import com.lightning.northstar.content.NorthstarTextures;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.planet.data.PlanetProperties;
import com.lightning.northstar.planet.data.render.SimplePlanetRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlanetRenderer {

    /**
     * Calculate the view orientation for a specific dimension
     *
     * @param cameraX   the camera's x position in the dimension
     * @param cameraZ   the camera's z position in the dimension
     * @param planet    the planet the viewer is on
     * @param dimension the dimension the viewer is in
     */
    public static Quaterniond getViewRotation(double cameraX, double cameraZ, PlanetProperties planet, PlanetDimension dimension) {
        double days = NorthstarLevel.CLIENT_TRACKER.getDeltaDays();
        double rotationProgress = planet.rotationPeriodDays() == 0 ? 0 : (days % planet.rotationPeriodDays()) / planet.rotationPeriodDays();
        double axialPrecession = days / 365 * planet.axialPrecession() + planet.initialAxialPrecession();

        Vector3d orbitalPlane = planet.orbit().getRotationAxis(new Vector3d());
        Vector3d obliquityAxis = new Vector3d(0, 0, -1).cross(orbitalPlane);

        return new Quaterniond()
                // rotate from Y+ up to Z- up (looking up from the equator)
                .rotateX(Mth.HALF_PI + dimension.latitudeOffset())
                // rotate the planet around itself
                .rotateY(rotationProgress * Mth.TWO_PI - Mth.HALF_PI + dimension.longitudeOffset())
                // apply obliquity
                .rotateAxis(planet.obliquity(), obliquityAxis)
                // apply axial precession
                .rotateAxis(axialPrecession, orbitalPlane);
    }

    // FIXME: Since planets are scaled up to be nicely visible, their moons orbits also need to be scaled up to not clip through the planets themselves
    // FIXME: planets have to be rendered far to minimize view bobbing but doing so causes clipping on lower view distances

    public static void render(ClientLevel level, PoseStack pose, Camera camera, float starBrightness, float atmosphereBlend) {
        PlanetDimension dimension = level.northstar$dimension();
        Planet currentPlanet = level.northstar$planet();
        if (currentPlanet == null) {
            return;
        }
        pose.pushPose();

        Vec3 cameraPosition = camera.getPosition();
        Quaterniond viewRotation = getViewRotation(cameraPosition.x, cameraPosition.z, currentPlanet.properties, dimension);

        Vector3d universePosition = currentPlanet.position;
        Vector4f planetColor = new Vector4f(1, 1, 1, Math.max(starBrightness, atmosphereBlend));
        Vector3d travelDir = new Vector3d();
        Vector3d direction = new Vector3d();
        Matrix4f transform = new Matrix4f();

        BufferBuilder vc = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        final float planetScale = 16f;

        List<Planet> forRendering = new ArrayList<>(NorthstarLevel.CLIENT_TRACKER.getPlanets().values());
        forRendering.sort(Comparator.comparingDouble(planet -> -planet.position.distance(currentPlanet.position)));
        for (Planet planet : forRendering) {
            if (planet.key.equals(currentPlanet.key)) {
                continue;
            }
            PlanetProperties properties = planet.properties;

            // render the vanilla sun to ensure it matches the day/night cycle.
            if (planet.key.equals(NorthstarPlanets.SOL)) {
                pose.pushPose();
                pose.mulPose(Axis.YP.rotationDegrees(-90));
                pose.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true)) * 360 + 90));
                pose.translate(0, 0, -100);
                properties.renderer().render(level, pose, vc, 15, new Vector4f(1), planet);
                pose.popPose();
                continue;
            }

            direction.set(planet.position)
                    .sub(universePosition)
                    .rotate(viewRotation);

            double planetDistance = direction.length();
            direction.mul(1.0 / planetDistance);

            final double KM_PER_AU = 149597870.7;
            double size = properties.diameter() / KM_PER_AU / planetDistance * planetScale;
            size = Math.log1p(size) / (1 + Math.log1p(size));
            if (size <= Mth.EPSILON) {
                continue;
            }

            planet.localPosition
                    .cross(properties.orbit().getRotationAxis(new Vector3d()), travelDir)
                    .rotate(viewRotation)
                    .normalize();
            if (!travelDir.isFinite()) {
                travelDir.set(0, 1, 0);
            }

            pose.pushPose();
            pose.mulPose(transform.rotationTowards(direction.get(new Vector3f()), travelDir.get(new Vector3f())));
            pose.translate(0, 0, -100);
            properties.renderer().render(level, pose, vc, (float) size * 100, planetColor, planet);
            pose.popPose();
        }

        // It's Northstar time! (and then we northstared all over the place)
        pose.pushPose();
        pose.mulPose(Axis.XP.rotationDegrees(20));
        pose.mulPose(Axis.ZP.rotationDegrees(25));
        pose.translate(0, 0, -100);
        new SimplePlanetRenderer(NorthstarTextures.POLARIS).render(level, pose, vc, 10, planetColor, null);
        pose.popPose();

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        MeshData buffer = vc.build();
        if (buffer != null) {
            OculusCompat.$.pushRenderPhase(OculusPhase.STARS);

            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, NorthstarTextures.PLANET_ATLAS);
            BufferUploader.drawWithShader(buffer);
            RenderSystem.enableCull();

            OculusCompat.$.popRenderPhase();
        }

        List<PlanetProperties.TextureLayer> texture = currentPlanet.properties.texture();
        if (!texture.isEmpty()) {
            OculusCompat.$.pushRenderPhase(OculusPhase.SKY);

            int brightness = 255;
            int alpha = dimension.isOrbit() ? 255 : (int) (255 * atmosphereBlend);

            float atmosphereStart = level.getMaxBuildHeight() + NorthstarConfigs.server().atmosphereBaseHeight.get();
            float teleportHeight = atmosphereStart + NorthstarConfigs.server().atmosphereThickness.get() + NorthstarConfigs.server().atmosphereTeleportHeight.get();
            float size = Mth.clampedMap((float) camera.getPosition().y, atmosphereStart, teleportHeight, 100, 50);

            vc = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            Matrix4f matrix = pose.last().pose();

            for (PlanetProperties.TextureLayer layer : texture) {
                TextureAtlasSprite sprite = NorthstarClient.PLANET_ATLAS.getSprite(layer.texture());

                float w = sprite.contents().width();
                float uw = sprite.contents().height() / w;
                float u0 = (float) (NorthstarLevel.CLIENT_TRACKER.getDeltaDays() * layer.speed() % 1);
                if (layer.snap()) {
                    u0 = Mth.floor(u0 * w) / w;
                }
                float u1 = u0 + uw;
                if (u1 <= 1) {
                    vc.addVertex(matrix, -size, -100, +size).setUv(sprite.getU(u1), sprite.getV(0)).setColor(brightness, brightness, brightness, alpha);
                    vc.addVertex(matrix, +size, -100, +size).setUv(sprite.getU(u0), sprite.getV(0)).setColor(brightness, brightness, brightness, alpha);
                    vc.addVertex(matrix, +size, -100, -size).setUv(sprite.getU(u0), sprite.getV(1)).setColor(brightness, brightness, brightness, alpha);
                    vc.addVertex(matrix, -size, -100, -size).setUv(sprite.getU(u1), sprite.getV(1)).setColor(brightness, brightness, brightness, alpha);
                } else {
                    float first = 1 - u0;
                    float second = uw - first;
                    float middle = Mth.lerp(first / uw, +size, -size);

                    vc.addVertex(matrix, +size, -100, -size).setUv(sprite.getU(u0), sprite.getV(1)).setColor(brightness, brightness, brightness, alpha);
                    vc.addVertex(matrix, middle, -100, -size).setUv(sprite.getU(1), sprite.getV(1)).setColor(brightness, brightness, brightness, alpha);
                    vc.addVertex(matrix, middle, -100, +size).setUv(sprite.getU(1), sprite.getV(0)).setColor(brightness, brightness, brightness, alpha);
                    vc.addVertex(matrix, +size, -100, +size).setUv(sprite.getU(u0), sprite.getV(0)).setColor(brightness, brightness, brightness, alpha);

                    vc.addVertex(matrix, middle, -100, -size).setUv(sprite.getU(0), sprite.getV(1)).setColor(brightness, brightness, brightness, alpha);
                    vc.addVertex(matrix, -size, -100, -size).setUv(sprite.getU(second * 1), sprite.getV(1)).setColor(brightness, brightness, brightness, alpha);
                    vc.addVertex(matrix, -size, -100, +size).setUv(sprite.getU(second * 1), sprite.getV(0)).setColor(brightness, brightness, brightness, alpha);
                    vc.addVertex(matrix, middle, -100, +size).setUv(sprite.getU(0), sprite.getV(0)).setColor(brightness, brightness, brightness, alpha);
                }
            }

            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, NorthstarTextures.PLANET_ATLAS);
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            BufferUploader.drawWithShader(vc.buildOrThrow());

            OculusCompat.$.popRenderPhase();
        }

        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);

        pose.popPose();
    }

}

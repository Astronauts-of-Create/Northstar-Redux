package com.lightning.northstar.planet.data.render;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.NorthstarClient;
import com.lightning.northstar.planet.Planet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector4fc;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record PhasedPlanetRenderer(
        ResourceLocation texture,
        int phaseDuration,
        int phaseCount,
        int timeOffset,
        int columns,
        int rows
) implements PlanetSpriteRenderer {

    public PhasedPlanetRenderer {
        if (phaseCount == -1)
            phaseCount = columns * rows;
    }

    public static final ResourceLocation TYPE = Northstar.asResource("phase");
    public static final Codec<PhasedPlanetRenderer> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(PhasedPlanetRenderer::texture),
            Codec.INT.optionalFieldOf("phase_duration", 24000).forGetter(PhasedPlanetRenderer::phaseDuration),
            Codec.INT.optionalFieldOf("phase_count", -1).forGetter(r -> r.phaseCount == r.columns * r.rows ? -1 : r.phaseCount),
            Codec.INT.optionalFieldOf("time_offset", -6000).forGetter(PhasedPlanetRenderer::timeOffset),
            Codec.INT.optionalFieldOf("columns", 4).forGetter(PhasedPlanetRenderer::columns),
            Codec.INT.optionalFieldOf("rows", 2).forGetter(PhasedPlanetRenderer::rows)
    ).apply(i, PhasedPlanetRenderer::new));

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public void render(Level level, PoseStack pose, VertexConsumer vc, float size, Vector4fc color, Planet planet) {
        TextureAtlasSprite sprite = NorthstarClient.PLANET_ATLAS.getSprite(texture);

        int phase = (int) (((level.getDayTime() + timeOffset) / phaseDuration) % phaseCount);
        float stepU = 16f / columns;
        float stepV = 16f / rows;
        float u0 = stepU * (float) (phase % columns);
        float v0 = stepV * (float) (phase / columns);
        float u1 = u0 + stepU;
        float v1 = v0 + stepV;

        Matrix4f matrix = pose.last().pose();
        float s = size / 2f;
        vc.vertex(matrix, -s, +s, 0).uv(sprite.getU(u1), sprite.getV(v0)).color(color.x(), color.y(), color.z(), color.w()).endVertex();
        vc.vertex(matrix, +s, +s, 0).uv(sprite.getU(u0), sprite.getV(v0)).color(color.x(), color.y(), color.z(), color.w()).endVertex();
        vc.vertex(matrix, +s, -s, 0).uv(sprite.getU(u0), sprite.getV(v1)).color(color.x(), color.y(), color.z(), color.w()).endVertex();
        vc.vertex(matrix, -s, -s, 0).uv(sprite.getU(u1), sprite.getV(v1)).color(color.x(), color.y(), color.z(), color.w()).endVertex();
    }

}

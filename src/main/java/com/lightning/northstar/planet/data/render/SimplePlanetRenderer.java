package com.lightning.northstar.planet.data.render;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.NorthstarClient;
import com.lightning.northstar.planet.Planet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4fc;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record SimplePlanetRenderer(ResourceLocation texture) implements PlanetSpriteRenderer {

    public static final ResourceLocation TYPE = Northstar.asResource("simple");
    public static final MapCodec<SimplePlanetRenderer> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.fieldOf("value").forGetter(SimplePlanetRenderer::texture)
    ).apply(i,  SimplePlanetRenderer::new));

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public void render(Level level, PoseStack pose, VertexConsumer vc, float size, Vector4fc color, @Nullable Planet planet) {
        TextureAtlasSprite sprite = NorthstarClient.PLANET_ATLAS.getSprite(texture);

        Matrix4f matrix = pose.last().pose();
        float s = size / 2f;
        vc.addVertex(matrix, -s, +s, 0).setUv(sprite.getU0(), sprite.getV1()).setColor(color.x(), color.y(), color.z(), color.w());
        vc.addVertex(matrix, +s, +s, 0).setUv(sprite.getU1(), sprite.getV1()).setColor(color.x(), color.y(), color.z(), color.w());
        vc.addVertex(matrix, +s, -s, 0).setUv(sprite.getU1(), sprite.getV0()).setColor(color.x(), color.y(), color.z(), color.w());
        vc.addVertex(matrix, -s, -s, 0).setUv(sprite.getU0(), sprite.getV0()).setColor(color.x(), color.y(), color.z(), color.w());
    }

}

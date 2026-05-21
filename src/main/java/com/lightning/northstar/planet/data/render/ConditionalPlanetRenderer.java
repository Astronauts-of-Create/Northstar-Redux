package com.lightning.northstar.planet.data.render;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.planet.Planet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Vector4fc;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record ConditionalPlanetRenderer(
        ResourceKey<Level> level,
        PlanetSpriteRenderer ifTrue,
        PlanetSpriteRenderer orElse
) implements PlanetSpriteRenderer {

    public static final ResourceLocation TYPE = Northstar.asResource("conditional_dimension");
    public static final Codec<ConditionalPlanetRenderer> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(ConditionalPlanetRenderer::level),
            PlanetSpriteRenderer.CODEC_OR_INLINE.fieldOf("then").forGetter(ConditionalPlanetRenderer::ifTrue),
            PlanetSpriteRenderer.CODEC_OR_INLINE.fieldOf("else").forGetter(ConditionalPlanetRenderer::orElse)
    ).apply(i, ConditionalPlanetRenderer::new));


    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public void render(Level level, PoseStack pose, VertexConsumer vc, float size, Vector4fc color, Planet planet) {
        if (this.level.equals(level.dimension())) {
            ifTrue.render(level, pose, vc, size, color, planet);
        } else {
            orElse.render(level, pose, vc, size, color, planet);
        }
    }

}

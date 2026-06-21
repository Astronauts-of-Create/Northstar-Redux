package com.lightning.northstar.planet.data.render;

import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.util.SimpleRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector4fc;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface PlanetSpriteRenderer {

    SimpleRegistry<ResourceLocation, MapCodec<? extends PlanetSpriteRenderer>> REGISTRY = new SimpleRegistry<>();

    Codec<PlanetSpriteRenderer> CODEC = ResourceLocation.CODEC.dispatch(PlanetSpriteRenderer::type, REGISTRY.lookup("planet renderer"));
    Codec<PlanetSpriteRenderer> CODEC_OR_INLINE = Codec.either(ResourceLocation.CODEC, CODEC)
            .xmap(either -> either.map(SimplePlanetRenderer::new, Function.identity()),
                    renderer -> renderer instanceof SimplePlanetRenderer simple ? Either.left(simple.texture()) : Either.right(renderer));

    static PlanetSpriteRenderer simple(ResourceLocation texture) {
        return new SimplePlanetRenderer(texture);
    }

    @ApiStatus.Internal
    static void register() {
        PlanetSpriteRenderer.REGISTRY.register(ConditionalPlanetRenderer.TYPE, ConditionalPlanetRenderer.CODEC);
        PlanetSpriteRenderer.REGISTRY.register(NoopPlanetRenderer.TYPE, NoopPlanetRenderer.CODEC);
        PlanetSpriteRenderer.REGISTRY.register(PhasedPlanetRenderer.TYPE, PhasedPlanetRenderer.CODEC);
        PlanetSpriteRenderer.REGISTRY.register(SimplePlanetRenderer.TYPE, SimplePlanetRenderer.CODEC);
    }

    ResourceLocation type();

    void render(Level level, PoseStack pose, VertexConsumer vc, float size, Vector4fc color, Planet planet);

}

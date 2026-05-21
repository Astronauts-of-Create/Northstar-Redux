package com.lightning.northstar.planet.data.render;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.planet.Planet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Vector4fc;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NoopPlanetRenderer implements PlanetSpriteRenderer {

    public static final NoopPlanetRenderer INSTANCE = new NoopPlanetRenderer();
    public static final ResourceLocation TYPE = Northstar.asResource("no_op");
    public static final Codec<NoopPlanetRenderer> CODEC = Codec.unit(INSTANCE);

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public void render(Level level, PoseStack pose, VertexConsumer vc, float size, Vector4fc color, Planet planet) {
    }

}

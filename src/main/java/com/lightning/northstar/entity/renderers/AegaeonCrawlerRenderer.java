package com.lightning.northstar.entity.renderers;

import com.lightning.northstar.content.NorthstarEntityResources;
import com.lightning.northstar.entity.AegaeonCrawler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AegaeonCrawlerRenderer extends GeoEntityRenderer<AegaeonCrawler> {

    public AegaeonCrawlerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(NorthstarEntityResources.AEGAEON_CRAWLER_MODEL));
        this.shadowRadius = 0.5f; // optional, for shadow size
    }

    @Override
    public RenderType getRenderType(AegaeonCrawler animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        // Use translucent render type to allow alpha
        return RenderType.entityTranslucent(texture);
    }
}

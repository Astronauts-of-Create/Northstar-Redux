package com.lightning.northstar.client;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NorthstarAtlas extends TextureAtlasHolder {

    public NorthstarAtlas(TextureManager textureManager, ResourceLocation location, ResourceLocation atlasInfoLocation) {
        super(textureManager, location, atlasInfoLocation);
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return super.getSprite(location);
    }

}
package com.lightning.northstar.accessor;

import com.lightning.northstar.client.TilingAnchor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public interface NorthstarGuiGraphics {

    private GuiGraphics self() {
        return (GuiGraphics) this;
    }

    default void northstar$blitRepeating(TextureAtlasSprite sprite, int x, int y, int w, int h, TilingAnchor anchor) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.enableBlend();

        BufferBuilder vertexBuffer = Tesselator.getInstance().getBuilder();
        vertexBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f m = self().pose().last().pose();

        SpriteContents contents = sprite.contents();
        int spriteW = contents.width();
        int spriteH = contents.height();

        int tileCountX = w / spriteW;
        int remainderX = w - tileCountX * spriteW;
        int tileCountY = h / spriteH;
        int remainderY = h - tileCountY * spriteH;

        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();
        float diffU = uMax - uMin;
        float diffV = vMax - vMin;

        float localU0, localV0, localU1, localV1;


        for (int tileX = 0; tileX <= tileCountX; tileX++) {
            int tileW = tileX == tileCountX ? remainderX : spriteW;
            if (tileW == 0)
                break;

            int clippedX = spriteW - tileW;
            int drawX = x + tileX * spriteW;
            int nextX = drawX + spriteW - clippedX;

            float clippedU = diffU * clippedX / spriteW;
            if (anchor.left) {
                localU0 = uMin;
                localU1 = uMax - clippedU;
            } else {
                localU0 = uMin + clippedU;
                localU1 = uMax;
            }

            for (int tileY = 0; tileY <= tileCountY; tileY++) {
                int tileH = tileY == tileCountY ? remainderY : spriteH;
                if (tileH == 0)
                    break;

                int clippedY = spriteH - tileH;
                int drawY = y + tileY * spriteH;
                int nextY = drawY + spriteH - clippedY;

                float clippedV = diffV * clippedY / spriteH;
                if (anchor.bottom) {
                    localV0 = vMin + clippedV;
                    localV1 = vMax;
                } else {
                    localV0 = vMin;
                    localV1 = vMax - clippedV;
                }

                vertexBuffer.vertex(m, drawX, nextY, 0).uv(localU0, localV0).endVertex();
                vertexBuffer.vertex(m, nextX, nextY, 0).uv(localU1, localV0).endVertex();
                vertexBuffer.vertex(m, nextX, drawY, 0).uv(localU1, localV1).endVertex();
                vertexBuffer.vertex(m, drawX, drawY, 0).uv(localU0, localV1).endVertex();
            }
        }

        BufferUploader.drawWithShader(vertexBuffer.end());
        RenderSystem.disableBlend();
    }

    default void northstar$blitFloat(ResourceLocation texture, float x, float y, float w, float h, float u, float v, float uw, float vh) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        Matrix4f transform = self().pose().last().pose();
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(transform, x, y, 0).uv(u, v).endVertex();
        builder.vertex(transform, x, y + h, 0).uv(u, v + vh).endVertex();
        builder.vertex(transform, x + w, y + h, 0).uv(u + uw, v + vh).endVertex();
        builder.vertex(transform, x + w, y, 0).uv(u + uw, v).endVertex();
        BufferUploader.drawWithShader(builder.end());
        RenderSystem.disableBlend();
    }

}

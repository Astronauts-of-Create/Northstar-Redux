package com.lightning.northstar.client.renderer.effect;

import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.content.NorthstarTextures;
import com.lightning.northstar.particle.NorthstarParticles;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MarsEffects extends SpaceEffects {

    private int rainSoundTime;

    private final float[] rainSizeX = new float[1024];
    private final float[] rainSizeZ = new float[1024];

    public MarsEffects() {
        super(true);

        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                float f = (float) (j - 16);
                float f1 = (float) (i - 16);
                float f2 = Mth.sqrt(f * f + f1 * f1);
                rainSizeX[i << 5 | j] = -f1 / f2;
                rainSizeZ[i << 5 | j] = f / f2;
            }
        }
    }

    @Override
    public float @Nullable [] getSunriseColor(float time, float tickDelta) {
        // FIXME: Weird color (is it right?)
        float f1 = Mth.cos(time * Mth.TWO_PI);
        if (f1 >= -0.4F && f1 <= 0.4F) {
            float f3 = f1 / 0.4F * 0.5F + 0.5F;
            float a = 1 - (1 - Mth.sin(f3 * Mth.PI)) * 0.99F;
            sunriseCol[0] = f3 * 0.2F + 0.5F;
            sunriseCol[1] = f3 * f3 * 0.2F + 0.5F;
            sunriseCol[2] = f3 * f3 * 0.8F + 0.5F;
            sunriseCol[3] = a * a;
            return sunriseCol;
        }
        return null;
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return Vec3.ZERO.lerp(fogColor, brightness);
    }

    @Override
    public void northstar$setupFogRender(ViewportEvent.RenderFog fog) {
        if (fog.getType() != FogType.NONE || fog.getMode() != FogRenderer.FogMode.FOG_TERRAIN) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        float fogThickness = minecraft.level.getRainLevel(minecraft.getTimer().getGameTimeDeltaPartialTick(true));
        if (fogThickness != 0) {
            fog.setCanceled(true);
            fog.setNearPlaneDistance(Mth.lerp(fogThickness, fog.getNearPlaneDistance(), -8));
            fog.setFarPlaneDistance(Mth.lerp(fogThickness, fog.getFarPlaneDistance(), 48));
        }
    }

    // TODO: Cleanup
    @Override
    public boolean tickRain(ClientLevel level, int ticks, Camera camera) {
        Minecraft minecraft = Minecraft.getInstance();

        if (level.random.nextInt(2) == 0 && level.isDay()) {
            //Northstar.LOGGER.debug("tubble weed :)");
            int x = camera.getBlockPosition().getX() + level.random.nextIntBetweenInclusive(-64, 64);
            int z = camera.getBlockPosition().getZ() + level.random.nextIntBetweenInclusive(-64, 64);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            level.addAlwaysVisibleParticle(NorthstarParticles.DUST_CLOUD.get(), x, y + level.random.nextInt(3), z, 0, 0, 0);
        }

        float rain_det = minecraft.level.getRainLevel(minecraft.getTimer().getGameTimeDeltaPartialTick(true));
        if (rain_det > 0) {
            float f = minecraft.level.getRainLevel(minecraft.getTimer().getGameTimeDeltaPartialTick(true)) / (Minecraft.useFancyGraphics() ? 1 : 2);
            if (!(f <= 0)) {
                RandomSource randomsource = RandomSource.create((long) ticks * 312987231L);
                LevelReader levelreader = minecraft.level;
                BlockPos blockpos = new BlockPos(camera.getBlockPosition());
                BlockPos blockpos1 = null;
                int i = (int) (100 * f * f) / (minecraft.options.particles().get() == ParticleStatus.DECREASED ? 2 : 1);

                for (int j = 0; j < i; ++j) {
                    int k = randomsource.nextInt(21) - 10;
                    int l = randomsource.nextInt(21) - 10;
                    BlockPos blockpos2 = levelreader.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockpos.offset(k, 0, l));
                    Biome biome = levelreader.getBiome(blockpos2).value();
                    if (blockpos2.getY() > levelreader.getMinBuildHeight() && blockpos2.getY() <= blockpos.getY() + 10 && blockpos2.getY() >= blockpos.getY() - 10 && biome.getPrecipitationAt(blockpos2) == Biome.Precipitation.NONE && biome.warmEnoughToRain(blockpos2)) {
                        blockpos1 = blockpos2.below();
                        if (minecraft.options.particles().get() == ParticleStatus.MINIMAL) {
                            break;
                        }
                        double d0 = randomsource.nextDouble();
                        double d1 = randomsource.nextDouble();
                        BlockState blockstate = levelreader.getBlockState(blockpos1);
                        FluidState fluidstate = levelreader.getFluidState(blockpos1);
                        VoxelShape voxelshape = blockstate.getCollisionShape(levelreader, blockpos1);
                        double d2 = voxelshape.max(Direction.Axis.Y, d0, d1);
                        double d3 = fluidstate.getHeight(levelreader, blockpos1);
                        double d4 = Math.max(d2, d3);
                        if (level.random.nextInt(10) == 0) {
                            minecraft.level.addParticle(NorthstarParticles.DUST_CLOUD.get(), blockpos1.getX() + d0, blockpos1.getY() + d4 + level.random.nextInt(4), blockpos1.getZ() + d1, 0.0D, 0.0D, 0.0D);
                        }
                    }
                }
                if (blockpos1 != null && randomsource.nextInt(80) < this.rainSoundTime++) {
                    this.rainSoundTime = 0;
                    if (blockpos1.getY() > blockpos.getY() + 1 && levelreader.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockpos).getY() > Mth.floor((float) blockpos.getY())) {
                        minecraft.level.playLocalSound(blockpos1, NorthstarSounds.MARTIAN_DUST_STORM_ABOVE.get(), SoundSource.WEATHER, 0.5F, 0.5F, false);
                    } else {
                        minecraft.level.playLocalSound(blockpos1, NorthstarSounds.MARTIAN_DUST_STORM.get(), SoundSource.WEATHER, 1F, 1, false);
                    }
                }

            }
        }

        return true;
    }

    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        Minecraft minecraft = Minecraft.getInstance();

        float rain_det = level.getRainLevel(partialTick);
        if (rain_det > 0) {
            lightTexture.turnOnLightLayer();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Camera pCamera = Minecraft.getInstance().gameRenderer.getMainCamera();
            GameRenderer gRenderer = Minecraft.getInstance().gameRenderer;
            Vec3 vec3 = pCamera.getPosition();
            double fog_x = vec3.x();
            double fog_y = vec3.y();
            FogRenderer.setupColor(pCamera, partialTick, level, minecraft.options.getEffectiveRenderDistance(), gRenderer.getDarkenWorldAmount(partialTick));
            FogRenderer.levelFogColor();
            boolean flag2 = minecraft.level.effects().isFoggyAt(Mth.floor(fog_x), Mth.floor(fog_y)) || minecraft.gui.getBossOverlay().shouldCreateWorldFog();
            FogRenderer.setupFog(pCamera, FogRenderer.FogMode.FOG_TERRAIN, minecraft.options.getEffectiveRenderDistance() / 5f, flag2, partialTick);


            FogRenderer.setupNoFog();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            lightTexture.turnOnLightLayer();
            int i = Mth.floor(camX);
            int j = Mth.floor(camY);
            int k = Mth.floor(camZ);
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            int l = 2;
            if (Minecraft.useFancyGraphics()) {
                l = 3;
            }


            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int j1 = k - l; j1 <= k + l; ++j1) {
                for (int k1 = i - l; k1 <= i + l; ++k1) {
                    int l1 = (j1 - k + 8) * 32 + k1 - i + 8;
                    float d0 = rainSizeX[l1] * 1.25f;
                    float d1 = rainSizeZ[l1] * 1.25f;
                    blockpos$mutableblockpos.set(k1, camY, j1);
                    Biome biome = level.getBiome(blockpos$mutableblockpos).value();
                    if (biome.getPrecipitationAt(blockpos$mutableblockpos) == Biome.Precipitation.NONE) {
                        int i2 = level.getHeight(Heightmap.Types.MOTION_BLOCKING, k1, j1);
                        int j2 = j - l;
                        int k2 = j + l;
                        if (j2 < i2) {
                            j2 = i2;
                        }

                        if (k2 < i2) {
                            k2 = i2;
                        }

                        int l2 = Math.max(i2, j);
                        if (j2 != k2) {
                            blockpos$mutableblockpos.set(k1, j2, j1);
                            if ((biome.warmEnoughToRain(blockpos$mutableblockpos))) {
                                int i3 = ticks + k1 * k1 * 3121 + k1 * 45238971 + j1 * j1 * 418711 + j1 * 13761 & 31;
                                float f2 = -((float) i3 + partialTick) / 32 * (0.75F);
                                float f4 = 0.2f * rain_det;
                                blockpos$mutableblockpos.set(k1, l2, j1);
                                bufferbuilder.addVertex(k1 - (float) camX - d0 - 10.5f, k2 - (float) camY, j1 - (float) camZ - d1 + 10.5f).setUv(0 + f2, 0).setColor(1, 1, 1, f4).setUv2(0, 0);
                                bufferbuilder.addVertex(k1 - (float) camX + d0 + 10.5f, k2 - (float) camY, j1 - (float) camZ + d1 - 10.5f).setUv(1 + f2, 0).setColor(1, 1, 1, f4).setUv2(0, 0);
                                bufferbuilder.addVertex(k1 - (float) camX + d0 + 10.5f, j2 - (float) camY, j1 - (float) camZ + d1 - 10.5f).setUv(1 + f2, 1).setColor(1, 1, 1, f4).setUv2(0, 0);
                                bufferbuilder.addVertex(k1 - (float) camX - d0 - 10.5f, j2 - (float) camY, j1 - (float) camZ - d1 + 10.5f).setUv(0 + f2, 1).setColor(1, 1, 1, f4).setUv2(0, 0);
                            }
                        }
                    }

                }
            }

            MeshData mesh = bufferbuilder.build();
            if (mesh != null) {
                RenderSystem.setShaderColor(2, 1.178F, 0.698f, 0.5f); // 2????
                RenderSystem.setShaderTexture(0, NorthstarTextures.MARS_DUST);
                BufferUploader.drawWithShader(mesh);
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            lightTexture.turnOffLightLayer();
            RenderSystem.setShaderColor(1, 1, 1, 1);

        }

        return true;
    }
}

package com.lightning.northstar.client.renderer.effect;

import com.lightning.northstar.api.client.NorthstarDimensionEffectsExtension;
import com.lightning.northstar.content.NorthstarTextures;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
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
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class VenusEffects extends SpaceEffects implements NorthstarDimensionEffectsExtension {

    private static final Vector3f DIFFUSE_1 = Util.make(new Vector3f(0.2F, -1, -0.7F), Vector3f::normalize);
    private static final Vector3f DIFFUSE_2 = Util.make(new Vector3f(-0.2F, -1, 0.7F), Vector3f::normalize);

    private final float[] rainSizeX = new float[1024];
    private final float[] rainSizeZ = new float[1024];

    private int rainSoundTime;

    public VenusEffects() {
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
    public void northstar$setupLight() {
        RenderSystem.setupLevelDiffuseLighting(DIFFUSE_1, DIFFUSE_2);
    }

    @Override
    public void northstar$setupFogColor(ViewportEvent.ComputeFogColor fog) {
        float time = Minecraft.getInstance().level.getTimeOfDay((float) fog.getPartialTick());
        float darken = Mth.clamp(Mth.cos(time * Mth.TWO_PI) * 2 + 0.5f, 0.125f, 1);
        fog.setRed(0.975f * darken);
        fog.setGreen(0.914f * darken);
        fog.setBlue(0.471f * darken);
    }

    @Override
    public void northstar$setupFogRender(ViewportEvent.RenderFog fog) {
        if (fog.getType() != FogType.NONE || fog.getMode() != FogRenderer.FogMode.FOG_TERRAIN) {
            return;
        }

        fog.setCanceled(true);
        fog.setNearPlaneDistance(-8);
        fog.setFogShape(FogShape.SPHERE);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return Vec3.ZERO.lerp(fogColor, brightness);
    }

    @Override
    public float @Nullable [] getSunriseColor(float time, float tickDelta) {
        float f1 = Mth.cos(time * Mth.TWO_PI);
        if (f1 >= -0.4F && f1 <= 0.4F) {
            float f3 = f1 / 0.4F * 0.5F + 0.5F;
            float a = 1 - (1 - Mth.sin(f3 * Mth.PI)) * 0.99F;
            sunriseCol[0] = f3 * 0.3F + 0.6F;
            sunriseCol[1] = f3 * f3 * 0.1F + 0.4F;
            sunriseCol[2] = f3 * f3 * 0 + 1;
            sunriseCol[3] = a * a;
            return sunriseCol;
        }
        return null;
    }

    // TODO: Cleanup
    @Override
    public boolean tickRain(ClientLevel level, int ticks, Camera camera) {
        Minecraft minecraft = Minecraft.getInstance();

        float rain_det = minecraft.level.getRainLevel(minecraft.getTimer().getGameTimeDeltaPartialTick(true));
        if (!(rain_det <= 0)) {
            float f = rain_det / (Minecraft.useFancyGraphics() ? 1 : 2);
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
                        ParticleOptions particleoptions = ParticleTypes.SMOKE;
                        minecraft.level.addParticle(particleoptions, blockpos1.getX() + d0, blockpos1.getY() + d4, blockpos1.getZ() + d1, 0.0D, 0.0D, 0.0D);
                    }
                }
                if (blockpos1 != null && randomsource.nextInt(3) < this.rainSoundTime++) {
                    this.rainSoundTime = 0;
                    if (blockpos1.getY() > blockpos.getY() + 1 && levelreader.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockpos).getY() > Mth.floor((float) blockpos.getY())) {
                        minecraft.level.playLocalSound(blockpos1, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1F, 0.5F, false);
                    } else {
                        minecraft.level.playLocalSound(blockpos1, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1, false);
                    }
                }

            }
        }

        return true;
    }

    // TODO: Cleanup
    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        float rain_det = level.getRainLevel(partialTick);
        if (!(rain_det <= 0)) {
            lightTexture.turnOnLightLayer();
            int i = Mth.floor(camX);
            int j = Mth.floor(camY);
            int k = Mth.floor(camZ);
            BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            int l = 5;
            if (Minecraft.useFancyGraphics()) {
                l = 10;
            }

            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int j1 = k - l; j1 <= k + l; ++j1) {
                for (int k1 = i - l; k1 <= i + l; ++k1) {
                    int l1 = (j1 - k + 16) * 32 + k1 - i + 16;
                    float d0 = rainSizeX[l1] * 0.5f;
                    float d1 = rainSizeZ[l1] * 0.5f;
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
                            RandomSource randomsource = RandomSource.create(k1 * k1 * 3121L + k1 * 45238971L ^ j1 * j1 * 418711L + j1 * 13761L);
                            blockpos$mutableblockpos.set(k1, j2, j1);
                            if (biome.warmEnoughToRain(blockpos$mutableblockpos)) {

                                int i3 = ticks + k1 * k1 * 3121 + k1 * 45238971 + j1 * j1 * 418711 + j1 * 13761 & 31;
                                float f2 = -((float) i3 + partialTick) / 32 * (3 + randomsource.nextFloat());
                                float d2 = k1 + 0.5f - (float) camX;
                                float d4 = j1 + 0.5f - (float) camZ;
                                float f3 = (float) Math.sqrt(d2 * d2 + d4 * d4) / (float) l;
                                float f4 = ((1 - f3 * f3) * 0.5F + 0.5F) * rain_det;
                                blockpos$mutableblockpos.set(k1, l2, j1);
                                int j3 = LevelRenderer.getLightColor(level, blockpos$mutableblockpos);
                                bufferbuilder.addVertex(k1 - (float) camX - d0 + 0.5f, k2 - (float) camY, j1 - (float) camZ - d1 + 0.5f).setUv(0, (float) j2 * 0.25F + f2).setColor(1, 1, 1, f4).setLight(j3);
                                bufferbuilder.addVertex(k1 - (float) camX + d0 + 0.5f, k2 - (float) camY, j1 - (float) camZ + d1 + 0.5f).setUv(1, (float) j2 * 0.25F + f2).setColor(1, 1, 1, f4).setLight(j3);
                                bufferbuilder.addVertex(k1 - (float) camX + d0 + 0.5f, j2 - (float) camY, j1 - (float) camZ + d1 + 0.5f).setUv(1, (float) k2 * 0.25F + f2).setColor(1, 1, 1, f4).setLight(j3);
                                bufferbuilder.addVertex(k1 - (float) camX - d0 + 0.5f, j2 - (float) camY, j1 - (float) camZ - d1 + 0.5f).setUv(0, (float) k2 * 0.25F + f2).setColor(1, 1, 1, f4).setLight(j3);
                            }
                        }
                    }
                }
            }

            MeshData mesh = bufferbuilder.build();
            if (mesh != null) {
                RenderSystem.setShaderTexture(0, NorthstarTextures.ACID_RAIN);
                BufferUploader.drawWithShader(mesh);
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            lightTexture.turnOffLightLayer();
        }

        return true;
    }
}

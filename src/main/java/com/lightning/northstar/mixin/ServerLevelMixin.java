package com.lightning.northstar.mixin;

import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Shadow
    protected abstract BlockPos findLightningTargetAround(BlockPos arg);

    @Inject(method = "tickChunk", at = @At("HEAD"))
    public void tickChunk(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo info) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (level != null) {
            /*if (level.dimension() == NorthstarDimensions.MARS_DIM_KEY) {
                level.setRainLevel(15);
            }*/
            if (level.dimension() == NorthstarDimensions.VENUS_DIM_KEY) {
                //level.setRainLevel(15);
                ChunkPos chunkpos = pChunk.getPos();
                boolean flag = level.isRaining();
                int i = chunkpos.getMinBlockX();
                int j = chunkpos.getMinBlockZ();
                ProfilerFiller profilerfiller = level.getProfiler();
                profilerfiller.push("thunder");
                if (flag && level.random.nextInt(15000) == 0) {
                    // THUNDER TIME YEEHAW
                    BlockPos blockpos = findLightningTargetAround(level.getBlockRandomPos(i, 0, j, 15));
                    LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(level);
                    lightningbolt.moveTo(Vec3.atBottomCenterOf(blockpos));
                    level.addFreshEntity(lightningbolt);
                }
                profilerfiller.pop();
            }
        }
    }

    //yay :]
    @Inject(method = "getSeed", at = @At("HEAD"), cancellable = true)
    public void getSeed(CallbackInfoReturnable<Long> info) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (level != null) {
            long seed = level.getServer().getWorldData().worldGenOptions().seed();
            info.setReturnValue(seed + NorthstarPlanets.getSeedOffset(level.dimension()));
        }
    }

}

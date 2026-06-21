package com.lightning.northstar.block.tech.rocket_waypoint;

import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.outliner.AABBOutline;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RocketWaypointOutline extends AABBOutline {

    public static final int DURATION = 12;

    private final AABB target;
    private int ticks;

    public RocketWaypointOutline(BlockPos pos, Direction dir) {
        super(new AABB(BlockPos.ZERO));

        if (dir.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            pos = pos.relative(dir);
        }
        setBounds(new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ()));

        Direction side = dir.getClockWise();
        target = getBounds()
                .expandTowards(dir.getStepX() * 40, 0, dir.getStepZ() * 40)
                .expandTowards(side.getStepX() * 20, 0, side.getStepZ() * 20)
                .expandTowards(side.getStepX() * -20, 0, side.getStepZ() * -20);
    }

    @Override
    public void tick() {
        super.tick();
        ticks++;
    }

    @Override
    public void render(PoseStack pose, SuperRenderTypeBuffer buffer, Vec3 camera, float partialTick) {
        params.loadColor(colorTemp);
        renderBox(pose, buffer, camera, interpolateAABB(getBounds(), target, Math.min((ticks + partialTick) / DURATION, 1)), colorTemp, LightTexture.FULL_BRIGHT, false);
    }

    private static AABB interpolateAABB(AABB start, AABB end, float delta) {
        return new AABB(
                Mth.lerp(delta, start.minX, end.minX),
                Mth.lerp(delta, start.minY, end.minY),
                Mth.lerp(delta, start.minZ, end.minZ),
                Mth.lerp(delta, start.maxX, end.maxX),
                Mth.lerp(delta, start.maxY, end.maxY),
                Mth.lerp(delta, start.maxZ, end.maxZ)
        );
    }

}

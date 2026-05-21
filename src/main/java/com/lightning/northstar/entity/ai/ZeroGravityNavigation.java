package com.lightning.northstar.entity.ai;

import com.google.common.collect.ImmutableSet;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ZeroGravityNavigation extends GroundPathNavigation {

    public ZeroGravityNavigation(Mob mob, Level level, GroundPathNavigation nav) {
        super(mob, level);
        copy(nav, this);
    }

    @Override
    protected boolean canMoveDirectly(Vec3 pos1, Vec3 pos2) {
        return isClearForMovementBetween(mob, pos1, pos2, mob.canBreatheUnderwater());
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        nodeEvaluator = new FlyNodeEvaluator();
        return new PathFinder(nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected Vec3 getTempMobPos() {
        return new Vec3(mob.getX(), mob.getY(), mob.getZ());
    }

    @Override
    protected boolean canUpdatePath() {
        return !mob.isPassenger();
    }

    @Override
    @Nullable
    public Path createPath(BlockPos pos, int accuracy) {
        // PathNavigation.super.createPath
        return this.createPath(ImmutableSet.of(pos), 8, false, accuracy);
    }

    // Needed to allow random movement goals to pick a point anywhere instead of on the ground
    @Override
    public boolean isStableDestination(BlockPos pos) {
        return true;
    }

    public static void copy(GroundPathNavigation src, GroundPathNavigation dst) {
        dst.setCanOpenDoors(src.canOpenDoors());
        dst.setCanPassDoors(src.canPassDoors());
        dst.setAvoidSun(src.northstar$isAvoidSun());
        dst.setCanWalkOverFences(src.getNodeEvaluator().canWalkOverFences());
    }

}

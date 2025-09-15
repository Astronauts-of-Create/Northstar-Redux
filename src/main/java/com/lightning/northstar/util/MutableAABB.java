package com.lightning.northstar.util;

import net.minecraft.core.Vec3i;

// minimalistic mutable AABB
public class MutableAABB {

    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;

    public MutableAABB() {
    }

    public MutableAABB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public void zero() {
        minX = 0;
        minY = 0;
        minZ = 0;
        maxX = 0;
        maxY = 0;
        maxZ = 0;
    }

    public void neg() {
        minX = minY = minZ = Integer.MAX_VALUE;
        maxX = maxY = maxZ = Integer.MIN_VALUE;
    }

    public void inf() {
        minX = minY = minZ = Integer.MIN_VALUE;
        maxX = maxY = maxZ = Integer.MAX_VALUE;
    }

    public void union(Vec3i vec) {
        union(vec.getX(), vec.getY(), vec.getZ());
    }

    public void union(int x, int y, int z) {
        if (x < minX) minX = x;
        if (y < minY) minY = y;
        if (z < minZ) minZ = z;
        if (x > maxX) maxX = x;
        if (y > maxY) maxY = y;
        if (z > maxZ) maxZ = z;
    }

    public void set(MutableAABB other) {
        this.minX = other.minX;
        this.minY = other.minY;
        this.minZ = other.minZ;
        this.maxX = other.maxX;
        this.maxY = other.maxY;
        this.maxZ = other.maxZ;
    }

    public boolean contains(Vec3i pos) {
        return contains(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean contains(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public void setCentered(int cx, int cy, int cz, int sx, int sy, int sz) {
        minX = cx - sx;
        minY = cy - sy;
        minZ = cz - sz;
        maxX = cx + sx;
        maxY = cy + sy;
        maxZ = cz + sz;
    }

    public int width() {
        return maxX - minX;
    }

    public int height() {
        return maxY - minY;
    }

    public int depth() {
        return maxZ - minZ;
    }

}

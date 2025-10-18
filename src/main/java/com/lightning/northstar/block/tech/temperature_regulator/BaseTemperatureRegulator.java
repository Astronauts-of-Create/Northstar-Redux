package com.lightning.northstar.block.tech.temperature_regulator;

import com.lightning.northstar.util.MutableAABB;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.lightning.northstar.world.sealer.SealingMode;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BaseTemperatureRegulator {

    protected final MutableAABB bounds = new MutableAABB();
    protected final ProgressiveBlockSealer sealer = new ProgressiveBlockSealer(SealingMode.TEMPERATURE) {
        @Override
        protected boolean isAirOccluded(BlockGetter level, BlockPos from, BlockPos to, Direction direction) {
            if (!bounds.contains(to))
                return true;
            return super.isAirOccluded(level, from, to, direction);
        }
    };

    protected boolean limit;
    protected int sizeX, sizeY, sizeZ;
    protected float temperature;

    public BaseTemperatureRegulator() {
        limit = false;
        sizeX = sizeY = sizeZ = 2;

        bounds.inf();
    }

    public void setBounds(Vec3i pos, boolean limit, int sizeX, int sizeY, int sizeZ) {
        this.limit = limit;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        if (limit) {
            bounds.setCentered(pos.getX(), pos.getY(), pos.getZ(), sizeX, sizeY, sizeZ);
        } else {
            bounds.inf();
        }
    }

    public void write(CompoundTag compound) {
        compound.putFloat("temperature", temperature);
        compound.putBoolean("limit", limit);
        compound.putInt("sizeX", sizeX);
        compound.putInt("sizeY", sizeY);
        compound.putInt("sizeZ", sizeZ);
    }

    public void read(CompoundTag compound, BlockPos pos) {
        temperature = compound.getFloat("temperature");

        setBounds(pos, compound.getBoolean("limit"), compound.getInt("sizeX"), compound.getInt("sizeY"), compound.getInt("sizeZ"));

        // pre 0.3.0
        if (compound.contains("temp", Tag.TAG_INT)) {
            temperature = compound.getInt("temp");
        }
    }

}

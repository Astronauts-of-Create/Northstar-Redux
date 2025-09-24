package com.lightning.northstar.block.tech.combustion_engine;

import com.lightning.northstar.content.NorthstarSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EngineHumSound extends AbstractTickableSoundInstance {

    protected final CombustionEngineBlockEntity parent;

    public EngineHumSound(CombustionEngineBlockEntity parent) {
        super(NorthstarSounds.COMBUSTION_ENGINE.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.parent = parent;

        looping = true;
        x = parent.getBlockPos().getX();
        y = parent.getBlockPos().getY();
        z = parent.getBlockPos().getZ();
    }

    @Override
    public void tick() {
        if (Mth.equal(parent.generatorSpeed, 0) || parent.isRemoved()) {
            stop();
        }
    }

}

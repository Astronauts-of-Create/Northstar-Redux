//package com.lightning.northstar.block.tech.combustion_engine;
//
//import com.lightning.northstar.content.NorthstarSounds;
//import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
//import net.minecraft.client.resources.sounds.SoundInstance;
//import net.minecraft.sounds.SoundSource;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//@OnlyIn(Dist.CLIENT)
//public class EngineHumSound extends AbstractTickableSoundInstance {
//
//    private float pitch;
//    private boolean stopped=false;
//    private CombustionEngineBlockEntity parent;
//
//    public EngineHumSound(CombustionEngineBlockEntity pParent) {
//        super(NorthstarSounds.COMBUSTION_ENGINE.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
//        this.pitch = 1;
//        this.parent = pParent;
//
//        volume = 1f;
//        looping = true;
//        delay = 0;
//        relative = true;
//
//        this.x = parent.getBlockPos().getX();
//        this.y = parent.getBlockPos().getY();
//        this.z = parent.getBlockPos().getZ();
//    }
//
//    @Override
//    public void tick() {
//        System.out.println("Tick");
//        //TODO: I'm Not sure what the MOST efficient way to play sound... I don't mind doing it this way, but we should try to avoid tick logic AS MUCH as possible
////        if (parent.isRemoved() || parent.getGeneratedSpeed() == 0) stop();
////        else {
////            setPitch(parent.getGeneratedSpeed());
////        }
//    }
//
////    public boolean isStopped() {
////        return this.stopped;
////    }
//
//    @Override
//    public float getPitch() {
//        return pitch;
//    }
//
//}
//

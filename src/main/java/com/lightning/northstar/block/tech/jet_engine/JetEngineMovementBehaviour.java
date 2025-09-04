package com.lightning.northstar.block.tech.jet_engine;

import com.lightning.northstar.contraptions.RocketContraption;
import com.lightning.northstar.contraptions.RocketContraptionEntity;
import com.lightning.northstar.particle.*;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class JetEngineMovementBehaviour implements MovementBehaviour {

    /*@Override
    public boolean renderAsNormalBlockEntity() {
        return false;
    }*/

    @Override
    public ItemStack canBeDisabledVia(MovementContext context) {
        return null;
    }

    @Override
    public void tick(MovementContext context) {
        if (!context.world.isClientSide() || !context.state.getValue(JetEngineBlock.BOTTOM)) return;
        Minecraft mc = Minecraft.getInstance();
        ParticleStatus status = mc.options.particles().get();
        //ALL > DECREASED > MINIMAL
        if (status == ParticleStatus.MINIMAL) return;

        RandomSource r = context.world.getRandom();
        if (context.contraption instanceof RocketContraption rc) {
            RocketContraptionEntity rce = ((RocketContraptionEntity) context.contraption.entity);


            if (rce.lift_vel > 0) {
                Vec3 v = context.position;
//                Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f).multiply(1, 0, 1));
                if (rce.blasting) {
                    if (status == ParticleStatus.ALL && r.nextInt(8) == 0)
                        context.world.addAlwaysVisibleParticle(new RocketSmokeParticleData(), v.x, v.y, v.z, 0, 0, 0);
                    else if (r.nextInt(5) == 0)
                        context.world.addAlwaysVisibleParticle(new RocketFlameParticleData(), v.x, v.y, v.z, 0, 0, 0);
                } else if (status == ParticleStatus.ALL) {//Stalling
                    if (r.nextInt(8) == 0) context.world.addParticle(new ColdAirParticleData(), v.x, v.y, v.z, 0, 0, 0);
                }

            } else if (rce.landingMode && rce.lift_vel < 0 && context.contraption.entity.getY() < rce.getSlowdownHeightThreshold()) {
                Vec3 v = context.position;
//                Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f).multiply(1, 0, 1));
                if (rce.slowing) {
                    if (status == ParticleStatus.ALL && r.nextInt(3) == 0)
                        context.world.addAlwaysVisibleParticle(new RocketSmokeLandingParticleData(), v.x, v.y - 2, v.z, 0, 0, 0);
                    else if (r.nextFloat() < 0.6)
                        context.world.addAlwaysVisibleParticle(new RocketFlameLandingParticleData(), v.x, v.y - 2, v.z, 0, 0, 0);
                }
            }
        } else if (status == ParticleStatus.ALL) {//Stalling
            Vec3 v = context.position;
//            Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f).multiply(1, 0, 1));
            if (r.nextInt(8) == 0) context.world.addParticle(new ColdAirParticleData(), v.x, v.y, v.z, 0, 0, 0);
        }
    }

}

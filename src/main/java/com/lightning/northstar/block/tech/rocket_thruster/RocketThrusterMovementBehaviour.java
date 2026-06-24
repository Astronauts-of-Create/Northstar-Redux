package com.lightning.northstar.block.tech.rocket_thruster;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.contraption.rocket.LaunchStatus;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.particle.NorthstarParticles;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class RocketThrusterMovementBehaviour implements MovementBehaviour {

    @Override
    public ItemStack canBeDisabledVia(MovementContext context) {
        return null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tick(MovementContext context) {
        if (!context.world.isClientSide() ||
            context.state.getValue(RocketThrusterBlock.BOTTOM) ||
            !(context.contraption.entity instanceof RocketContraptionEntity rocket)) {
            return;
        }
        ParticleStatus particleStatus = Minecraft.getInstance().options.particles().get();
        if (particleStatus == ParticleStatus.MINIMAL) {
            return;
        }
        Vec3 pos = context.position;
        if (rocket.areThrustersEnabled()) {
            float velocity = rocket.getVelocity() - 2;
            context.world.addAlwaysVisibleParticle(NorthstarParticles.ROCKET_PLUME.get(), true, pos.x, pos.y - 0.5, pos.z, 0, velocity, 0);
        } else if (rocket.getStatus() == LaunchStatus.COUNTDOWN || NorthstarConfigs.client().alwaysEnableThrusterParticles.get()) {
            if (particleStatus == ParticleStatus.ALL || context.world.getGameTime() % 4 == 0) {
                context.world.addAlwaysVisibleParticle(NorthstarParticles.COLD_AIR.get(), true, pos.x, pos.y - 0.3, pos.z, 0, 0, 0);
            }
        }
    }

}

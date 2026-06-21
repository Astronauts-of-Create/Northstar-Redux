package com.lightning.northstar.block.tech.oxygen_filler;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class OxygenFillerMovementBehaviour implements MovementBehaviour {

    @Override
    public void tick(MovementContext context) {
        OxygenFillerActor actor = OxygenFillerActor.get(context);
        ItemStack item = actor.container.getItem(0);
        if (!NorthstarItemTags.OXYGEN_SOURCES.matches(item)) {
            return;
        }

        MountedFluidStorageWrapper fluids = context.contraption.getStorage().getFluids();
        Fluid oxygen = NorthstarOxygen.findOxygenInStorage(fluids);
        if (oxygen == null) {
            return;
        }

        int current = item.getOrDefault(NorthstarDataComponents.OXYGEN, 0);
        int capacity = NorthstarOxygen.getTankCapacity(context.world, item);
        int fillable = Math.min(capacity - current, NorthstarConfigs.server().mountedOxygenFillerSpeed.get());
        if (fillable <= 0) {
            return;
        }

        int consumed = fluids.drain(new FluidStack(oxygen, fillable), context.world.isClientSide ? FluidAction.SIMULATE : FluidAction.EXECUTE).getAmount();
        if (consumed <= 0) {
            return;
        }

        if (current + consumed >= capacity) {
            BlockPos pos = BlockPos.containing(context.contraption.entity.toGlobalVector(Vec3.atCenterOf(context.localPos), 0));
            AllSoundEvents.CONFIRM.playOnServer(context.world, pos, 0.4f, 0);
        }

        item.set(NorthstarDataComponents.OXYGEN, current + consumed);
        actor.container.setItem(0, item);
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        OxygenFillerRenderer.renderInContraption(context, matrices, buffer, OxygenFillerActor.get(context).container.getItem(0));
    }

}

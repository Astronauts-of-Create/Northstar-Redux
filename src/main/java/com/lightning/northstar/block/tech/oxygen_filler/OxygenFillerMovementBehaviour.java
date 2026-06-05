package com.lightning.northstar.block.tech.oxygen_filler;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.lightning.northstar.accessor.NorthstarContraption;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class OxygenFillerMovementBehaviour implements MovementBehaviour {

    @Override
    public void tick(MovementContext context) {
        OxygenFillerActor actor = OxygenFillerActor.get(context);
        ItemStack item = actor.container.getItem(0);
        if (!NorthstarItemTags.OXYGEN_SOURCES.matches(item)) {
            return;
        }

        IFluidHandler fluids = ((NorthstarContraption) context.contraption).northstar$getStorage().getFluids();
        Fluid oxygen = NorthstarOxygen.findOxygenInStorage(fluids);
        if (oxygen == null) {
            return;
        }

        CompoundTag compound = item.getOrCreateTag();
        int fillable = Math.min(NorthstarOxygen.MAXIMUM_OXYGEN - compound.getInt("Oxygen"), NorthstarConfigs.server().mountedOxygenFillerSpeed.get());
        if (fillable <= 0) {
            return;
        }

        int consumed = fluids.drain(new FluidStack(oxygen, fillable), context.world.isClientSide ? FluidAction.SIMULATE : FluidAction.EXECUTE).getAmount();
        if (consumed <= 0) {
            return;
        }

        if (compound.getInt("Oxygen") + consumed >= NorthstarOxygen.MAXIMUM_OXYGEN) {
            BlockPos pos = BlockPos.containing(context.contraption.entity.toGlobalVector(Vec3.atCenterOf(context.localPos), 0));
            AllSoundEvents.CONFIRM.playOnServer(context.world, pos, 0.4f, 0);
        }

        compound.putInt("Oxygen", compound.getInt("Oxygen") + consumed);
        actor.container.setItem(0, item);
    }

    @Override
    public boolean renderAsNormalBlockEntity() {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        OxygenFillerRenderer.renderInContraption(context, matrices, buffer, OxygenFillerActor.get(context).container.getItem(0));
    }

}

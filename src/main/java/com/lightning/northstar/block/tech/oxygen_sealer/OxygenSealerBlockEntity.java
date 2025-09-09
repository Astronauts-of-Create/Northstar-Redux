package com.lightning.northstar.block.tech.oxygen_sealer;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.particle.OxyFlowParticleData;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.lightning.northstar.world.NorthstarOxygen;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class OxygenSealerBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation {

    private final ProgressiveBlockSealer sealer = new ProgressiveBlockSealer();
    private SmartFluidTankBehaviour oxygenTank;
    private int sealCooldown;
    private float drain;
    private boolean active;

    private int audioTick;

    public OxygenSealerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(oxygenTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, true));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (isFluidHandlerCap(cap) && (side == null || side == getBlockState().getValue(OxygenSealerBlock.HORIZONTAL_FACING).getOpposite()))
            return oxygenTank.getCapability().cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void initialize() {
        super.initialize();

        NorthstarOxygen.getDimension(level).getSealers().add(this);
    }

    @Override
    public void destroy() {
        super.destroy();

        NorthstarOxygen.getDimension(level).getSealers().remove(this);
    }

    @Override
    public void tick() {
        super.tick();

        if (sealer.isSealInProgress()) {
            if (sealer.updateSeal(level, getMaximumSealedBlocks())) {
                sealCooldown = NorthstarConfigs.server().sealerCheckDelay.get();
            }
        } else if (sealCooldown-- <= 0) {
            sealer.beginSeal(level, worldPosition, Direction.UP);
        }

        if (sealer.hasLeak()) {
            active = false;
            return;
        }

        FluidStack fluid = oxygenTank.getPrimaryHandler().getFluid();
        boolean isOxygen = NorthstarFluidTags.IS_OXY.matches(fluid) || NorthstarFluidTags.FORGE_OXYGEN.matches(fluid);
        if (!isOxygen || fluid.isEmpty() || isOverStressed() || speed == 0f) {
            active = false;
            return;
        }

        int drainable = Math.min((int) drain, fluid.getAmount());
        oxygenTank.getPrimaryHandler().drain(drainable, IFluidHandler.FluidAction.EXECUTE);
        drain -= drainable;
        active = drain < 1;

        if (active) {
            drain += sealer.getSealedBlocks().size() * NorthstarConfigs.server().oxygenSealerOxygenPerBlockPerTick.getF();

            if (level.isClientSide) {
                if (audioTick++ % 13 == 0) {
                    level.playLocalSound(worldPosition, NorthstarSounds.AIRFLOW.get(), SoundSource.BLOCKS, 0.1f, 0, false);
                }

                if (level.random.nextFloat() < AllConfigs.client().fanParticleDensity.get())
                    level.addParticle(new OxyFlowParticleData(getBlockPos().offset(0, 1, 0)), worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, 0, 0, 0);

            }
        }
    }

    public int getMaximumSealedBlocks() {
        return (int) (NorthstarConfigs.server().oxygenSealerBlocksPerRpm.get() * Math.abs(speed));
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (super.addToGoggleTooltip(tooltip, isPlayerSneaking)) {
            tooltip.add(Component.empty());
        }

        NorthstarLang.translate("gui.goggles.oxygen_sealer")
                .forGoggles(tooltip);

        sealer.addToGoggleTooltip(tooltip, getMaximumSealedBlocks());
        if (!sealer.hasLeak()) {
            NorthstarLang.translate("gui.goggles.oxygen_sealer.oxygen_usage")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            CreateLang.number(sealer.getSealedBlocks().size() * NorthstarConfigs.server().oxygenSealerOxygenPerBlockPerTick.getF())
                    .style(ChatFormatting.AQUA)
                    .add(NorthstarLang.MB_PER_TICK)
                    .forGoggles(tooltip, 1);
        }

        if (isPlayerSneaking)
            sealer.addCooldownTooltip(tooltip, sealCooldown, getMaximumSealedBlocks());

        tooltip.add(Component.empty());

        containedFluidTooltip(tooltip, isPlayerSneaking, getCapability(ForgeCapabilities.FLUID_HANDLER));

        return true;
    }

    public boolean isActive() {
        return active;
    }

    public ProgressiveBlockSealer getSealer() {
        return sealer;
    }

}

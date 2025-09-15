package com.lightning.northstar.block.tech.oxygen_sealer;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarBlockEntityTypes;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.particle.OxyFlowParticleData;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.SealingProvider;
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
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class OxygenSealerBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation, SealingProvider {

    protected final ProgressiveBlockSealer sealer = new ProgressiveBlockSealer();
    protected SmartFluidTankBehaviour oxygenTank;
    protected int sealCooldown;
    protected float drain;
    protected boolean active;

    protected int audioTick;

    public OxygenSealerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(oxygenTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, true));
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, NorthstarBlockEntityTypes.OXYGEN_SEALER.get(), (be, face) -> {
            if (face == be.getBlockState().getValue(OxygenSealerBlock.HORIZONTAL_FACING).getOpposite())
                return be.oxygenTank.getCapability();
            return null;
        });
    }

    @Override
    public void initialize() {
        super.initialize();

        level.northstar$oxygen().registerSealer(this);
    }

    @Override
    public void invalidate() {
        super.invalidate();

        level.northstar$oxygen().unregisterSealer(this);
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
        if (!NorthstarOxygen.isOxygen(fluid.getFluid()) || fluid.isEmpty() || isOverStressed() || speed == 0f) {
            active = false;
            return;
        }

        int drainable = Math.min((int) drain, fluid.getAmount());
        oxygenTank.getPrimaryHandler().drain(drainable, IFluidHandler.FluidAction.EXECUTE);
        drain -= drainable;
        active = drain < 1;

        if (active) {
            drain += sealer.getSealedBlockCount() * NorthstarConfigs.server().oxygenSealerOxygenPerBlockPerTick.getF();

            if (level.isClientSide) {
                if (audioTick++ % 13 == 0) {
                    level.playLocalSound(worldPosition, NorthstarSounds.AIRFLOW.get(), SoundSource.BLOCKS, 0.1f, 0, false);
                }

                if (level.random.nextFloat() < AllConfigs.client().fanParticleDensity.get())
                    level.addParticle(new OxyFlowParticleData(getBlockPos().offset(0, 1, 0)), worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, 0, 0, 0);

            }
        }
    }

    @Override
    public boolean isSealed(Vec3 pos) {
        return isSealed(Mth.floor(pos.x), Mth.floor(pos.y), Mth.floor(pos.z));
    }

    @Override
    public boolean isSealed(Vec3i pos) {
        return isSealed(pos.getX(), pos.getY(), pos.getZ());
    }

    private boolean isSealed(int x, int y, int z) {
        return active && sealer.getSealedBlocks().contains(BlockPos.asLong(x, y, z));
    }

    public int getMaximumSealedBlocks() {
        return (int) (NorthstarConfigs.server().oxygenSealerBlocksPerRpm.get() * Math.abs(speed));
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (super.addToGoggleTooltip(tooltip, isPlayerSneaking)) {
            tooltip.add(Component.empty());
        }

        NorthstarLang.translate("gui.oxygen_sealer")
                .forGoggles(tooltip);

        sealer.addToGoggleTooltip(tooltip, getMaximumSealedBlocks());
        if (!sealer.hasLeak()) {
            if (active) {
                NorthstarLang.translate("gui.oxygen_sealer.oxygen_usage")
                        .style(ChatFormatting.GRAY)
                        .forGoggles(tooltip);
                CreateLang.number(sealer.getSealedBlockCount() * NorthstarConfigs.server().oxygenSealerOxygenPerBlockPerTick.getF())
                        .style(ChatFormatting.AQUA)
                        .add(NorthstarLang.MB_PER_TICK)
                        .forGoggles(tooltip, 1);
            } else {
                NorthstarLang.translate("gui.oxygen_sealer.no_oxygen")
                        .style(ChatFormatting.RED)
                        .forGoggles(tooltip);
            }
        }

        if (isPlayerSneaking)
            sealer.addCooldownTooltip(tooltip, sealCooldown, getMaximumSealedBlocks());

        tooltip.add(Component.empty());

        containedFluidTooltip(tooltip, isPlayerSneaking, oxygenTank.getCapability());

        return true;
    }

    public boolean isActive() {
        return active;
    }

    public ProgressiveBlockSealer getSealer() {
        return sealer;
    }

}

package com.lightning.northstar.block.tech.oxygen_sealer;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.particle.NorthstarParticles;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.oxygen.OxygenTrackingSealer;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.sealer.SealingMode;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.kinetics.base.IRotate.StressImpact;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class OxygenSealerBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation, NorthstarOxygen.Provider {

    protected final OxygenTrackingSealer sealer = new OxygenTrackingSealer(SealingMode.OXYGEN);
    protected SmartFluidTankBehaviour tank;
    protected int sealCooldown;
    protected float drain;
    protected float pendingDrain;
    protected float activeDrain;
    protected boolean active;

    protected int audioTick;

    public OxygenSealerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, true));
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
            sealer.renderLeakPath(level);
            active = false;
            return;
        }

        FluidStack fluid = tank.getPrimaryHandler().getFluid();
        if (!NorthstarOxygen.isOxygen(fluid.getFluid()) || fluid.isEmpty() || isOverStressed() || speed == 0f) {
            active = false;
            return;
        }

        int drainable = Math.min((int) pendingDrain, fluid.getAmount());
        tank.getPrimaryHandler().drain(drainable, IFluidHandler.FluidAction.EXECUTE);
        pendingDrain -= drainable;
        active = pendingDrain < 1;

        if (!active)
            return;

        drain = sealer.getPassiveDrain() + sealer.getActiveDrain() + sealer.calculateDynamicConsumption(level) + activeDrain;
        pendingDrain += drain;
        activeDrain = 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        if (!active)
            return;

        if (audioTick++ % 13 == 0) {
            level.playLocalSound(worldPosition, NorthstarSounds.AIRFLOW.get(), SoundSource.BLOCKS, 0.1f, 0, false);
        }

        if (level.random.nextFloat() < AllConfigs.client().fanParticleDensity.get())
            level.addParticle(NorthstarParticles.OXY_FLOW.get(), worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5, 0, 0, 0);
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

    @Override
    public void drainOxygen(float oxygen) {
        activeDrain += oxygen;
    }

    public int getMaximumSealedBlocks() {
        return (int) (NorthstarConfigs.server().oxygenSealerBlocksPerRpm.get() * Math.abs(speed));
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        NorthstarLang.translate("gui.oxygen_sealer")
                .forGoggles(tooltip);

        if (StressImpact.isEnabled())
            addStressImpactStats(tooltip, calculateStressApplied());

        sealer.addToGoggleTooltip(tooltip, getMaximumSealedBlocks(), isPlayerSneaking);
        if (!sealer.hasLeak()) {
            NorthstarLang.translate("gui.oxygen_sealer.oxygen_usage")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            CreateLang.number(drain)
                    .style(ChatFormatting.GOLD)
                    .add(NorthstarLang.MB_PER_TICK)
                    .forGoggles(tooltip, 1);
        }
        NorthstarLang.addTankTooltip(tooltip, tank.getPrimaryHandler());

        if (!active && !sealer.hasLeak()) {
            NorthstarLang.translate("gui.oxygen_sealer.no_oxygen")
                    .style(ChatFormatting.RED)
                    .forGoggles(tooltip);
        }

        if (isPlayerSneaking)
            sealer.addCooldownTooltip(tooltip, sealCooldown, getMaximumSealedBlocks());

        return true;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (isFluidHandlerCap(cap) && (side == null || side == getBlockState().getValue(OxygenSealerBlock.HORIZONTAL_FACING).getOpposite()))
            return tank.getCapability().cast();
        return super.getCapability(cap, side);
    }

    public boolean isActive() {
        return active;
    }

    public ProgressiveBlockSealer getSealer() {
        return sealer;
    }

}

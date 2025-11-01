package com.lightning.northstar.block.tech.large_fan;

import com.lightning.northstar.api.create.ReceivingKineticBlockEntity;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.world.oxygen.NorthstarOxygen;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.lightning.northstar.world.sealer.SealingMode;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class LargeFanBlockEntity extends KineticBlockEntity implements IMultiBlockEntityContainer, ReceivingKineticBlockEntity {

    /** Minimum amount of blades that must be installed for the fan to spin */
    public static final int MINIMUM_BLADES = 2;
    /** Maximum amount of blades that can be installed */
    public static final int MAXIMUM_BLADES = 8;

    protected BlockPos controllerPos;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected int width;
    protected int height;

    protected boolean updateNeighbors;
    protected BlockPos chain;
    protected boolean flipChain;

    /** Installed blade count */
    protected int blades;
    /** Amount of opened blocks on each side, used to calculate efficiency */
    protected int openBlocks;
    /** Effective speed in RPM */
    protected LerpedFloat effectiveSpeed = LerpedFloat.linear();

    /** Visual rotation progress, used by the renderer */
    protected float angle;

    public LargeFanBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        width = 1;
        height = 1;

        setLazyTickRate(20);
    }

    @Override
    public void destroy() {
        super.destroy();
        dropBlades();
    }

    @Override
    public void tick() {
        super.tick();

        if (lastKnownPos == null) {
            lastKnownPos = worldPosition;
        } else if (!lastKnownPos.equals(worldPosition)) {
            removeController(true);
            lastKnownPos = worldPosition;
        }

        if (updateConnectivity)
            updateConnectivity();

        if (updateNeighbors) {
            updateNeighbors = false;
            getEdges(getAxis(), getWidth(), getHeight()).forEach(pos -> onNeighborChange(worldPosition.offset(pos), true));
        }

        if (blades >= MINIMUM_BLADES) {
            float speed = getSpeed();
            if (speed == 0) {
                NorthstarOxygen oxygenLevel = level.northstar$oxygen();
                if (!oxygenLevel.hasOxygen() && oxygenLevel.hasOxygen(worldPosition))
                    speed = 32f;
            }

            float targetSpeed = speed * (64f / 256f);
            if (Math.abs(targetSpeed) >= Math.abs(effectiveSpeed.getValue()))
                effectiveSpeed.chase(targetSpeed, 0.05f, LerpedFloat.Chaser.EXP);
            else
                effectiveSpeed.chase(targetSpeed, 0.2f, LerpedFloat.Chaser.LINEAR);

            effectiveSpeed.tickChaser();

            angle = (angle + effectiveSpeed.getValue() / (20 * 60)) % 1;
        } else {
            effectiveSpeed.setValue(0);
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();

        MutableBlockPos pos1 = new MutableBlockPos();
        MutableBlockPos pos2 = new MutableBlockPos();
        Axis axis = getAxis();
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        int accessible = 0;

        for (int dir : Iterate.positiveAndNegative) {
            pos1.set(worldPosition);

            int maximum = 0;
            for (int i = 1; i <= width; i++) {
                pos2.set(pos1);
                switch (axis) {
                    case X -> pos1.setWithOffset(worldPosition, i * dir, 0, 0);
                    case Y -> pos1.setWithOffset(worldPosition, 0, i * dir, 0);
                    case Z -> pos1.setWithOffset(worldPosition, 0, 0, i * dir);
                }
                if (ProgressiveBlockSealer.isAirOccluded(level, pos2, pos1, direction, SealingMode.OXYGEN))
                    break;
                maximum = i;
            }

            accessible += maximum;
            direction = direction.getOpposite();
        }

        if (accessible != openBlocks) {
            openBlocks = accessible;
            sendData();
        }
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (isController())
            return super.createRenderBoundingBox().expandTowards(getVolumeX() - 1, getVolumeY() - 1, getVolumeZ() - 1);
        return super.createRenderBoundingBox();
    }

    public void onNeighborChange(BlockPos neighbor, boolean manual) {
        BlockState neighborState = level.getBlockState(neighbor);

        if (neighbor.equals(chain) && !(neighborState.getBlock() instanceof ChainDriveBlock)) {
            BlockPos block = getBlockAttachedTo(chain);
            chain = null;
            if (block != null && level.getBlockEntity(block) instanceof KineticBlockEntity be) {
                be.detachKinetics();
                be.attachKinetics();
            }
            updateNeighbors = true;
            sendData();
        } else if (manual && chain == null && neighborState.getBlock() instanceof ChainDriveBlock && level.getBlockEntity(neighbor) instanceof KineticBlockEntity be) {
            be.detachKinetics();
            be.attachKinetics();
        }
    }

    /** Gets the block of this structure that is attached to the specific block */
    private BlockPos getBlockAttachedTo(BlockPos pos) {
        MutableBlockPos testPos = new MutableBlockPos();
        for (Direction direction : Iterate.directions) {
            testPos.setWithOffset(pos, direction);
            if (isMultiBlockPart(testPos))
                return testPos;
        }
        return null;
    }

    @Override
    public float propagateRotationFrom(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        return propagateRotationOf(target, stateFrom, stateTo);
    }

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff,
                                     boolean connectedViaAxes, boolean connectedViaCogs) {
        float ratio = propagateRotationOf(target, stateFrom, stateTo);
        return ratio != 0 ? ratio : super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
    }

    private float propagateRotationOf(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo) {
        BlockPos pos = target.getBlockPos();
        if (isMultiBlockPart(target.getBlockPos()))
            return 1;

        LargeFanBlockEntity controller = getControllerBE();
        if (controller == null)
            return 0;

        BlockPos chain = controller.chain;

        if (chain != null && !(level.getBlockState(controller.chain).getBlock() instanceof ChainDriveBlock))
            chain = null;
        if (chain == null &&
                stateTo.getBlock() instanceof ChainDriveBlock &&
                stateTo.getValue(ChainDriveBlock.AXIS) == stateFrom.getValue(LargeFanBlock.AXIS))
            chain = pos;

        if (!Objects.equals(chain, controller.chain)) {
            controller.chain = chain;
            controller.sendData();
        }
        if (pos.equals(chain))
            return 1;

        return 0;
    }

    public int getExtraSealedVolume() {
        if (getSpeed() == 0 || !isController())
            return 0; // passive mode, no volume added

        return (int) Math.round((Math.log(blades) / Math.log(2) + 1) * Math.pow(width, NorthstarConfigs.server().largeFanSizeExponent.get()) * Math.abs(effectiveSpeed.getValue()) * NorthstarConfigs.server().largeFanMultiplier.get());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!isController()) {
            LargeFanBlockEntity controller = getControllerBE();
            if (controller != null)
                return controller.addToGoggleTooltip(tooltip, isPlayerSneaking);
            return false;
        }

        CreateLang.translate("gui.goggles.kinetic_stats")
                .forGoggles(tooltip);

        addStressImpactStats(tooltip, calculateStressApplied() * width * width);

        if (blades < MINIMUM_BLADES) {
            CreateLang.text("Not enough blades")
                    .style(ChatFormatting.RED)
                    .forGoggles(tooltip);
        } else {
            CreateLang.text("Added volume:")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);
            CreateLang.number(getExtraSealedVolume())
                    .style(ChatFormatting.BLUE)
                    .text(ChatFormatting.GRAY, " blocks")
                    .forGoggles(tooltip, 1);
        }

        return true;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        updateConnectivity = compound.contains("Uninitialized");

        controllerPos = null;
        if (compound.contains("Controller"))
            controllerPos = NBTHelper.readBlockPos(compound, "Controller");

        lastKnownPos = null;
        if (compound.contains("LastKnownPos"))
            lastKnownPos = NBTHelper.readBlockPos(compound, "LastKnownPos");

        width = compound.getInt("Width");
        height = compound.getInt("Height");

        if (width > getMaxWidth() || (compound.contains("MaxWidth") && compound.getInt("MaxWidth") != getMaxWidth()))
            updateConnectivity = true;

        chain = compound.contains("Chain") ? NBTHelper.readBlockPos(compound, "Chain") : null;
        flipChain = compound.getBoolean("FlipChain");

        blades = compound.getInt("Blades");

        effectiveSpeed.readNBT(compound.getCompound("EffectiveSpeed"), clientPacket);

        updateNeighbors = true;
        invalidateRenderBoundingBox();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        if (updateConnectivity)
            compound.putBoolean("Uninitialized", true);

        if (controllerPos != null)
            compound.put("Controller", NbtUtils.writeBlockPos(controllerPos));
        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));

        if (!clientPacket)
            compound.putInt("MaxWidth", getMaxWidth());
        compound.putInt("Width", width);
        compound.putInt("Height", height);

        if (chain != null)
            compound.put("Chain", NbtUtils.writeBlockPos(chain));
        compound.putBoolean("FlipChain", flipChain);

        compound.putInt("Blades", blades);
        compound.put("EffectiveSpeed", effectiveSpeed.writeNBT());
    }

    // region IMultiBlockEntityContainer

    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide)
            return;
        if (!isController())
            return;
        if (width > getMaxWidth())
            ConnectivityHandler.splitMulti(this);
        ConnectivityHandler.formMulti(this);
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controllerPos;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LargeFanBlockEntity getControllerBE() {
        if (isController() || !hasLevel())
            return this;
        if (level.getBlockEntity(controllerPos) instanceof LargeFanBlockEntity fan)
            return fan;
        return null;
    }

    @Override
    public boolean isController() {
        return controllerPos == null || controllerPos.equals(worldPosition);
    }

    @Override
    public void setController(BlockPos pos) {
        this.controllerPos = pos;
    }

    @Override
    public void removeController(boolean keepContents) {
        if (level.isClientSide)
            return;
        controllerPos = null;
        updateConnectivity = true;
        width = 1;
        height = 1;
        invalidateRenderBoundingBox();

        updateBlockState(Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
        setChanged();
        sendData();
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        updateBlockState(Block.UPDATE_CLIENTS | Block.UPDATE_INVISIBLE);
        invalidateRenderBoundingBox();

        if (!isController()) {
            LargeFanBlockEntity controller = getControllerBE();
            if (controller != null) {
                int transferred = Math.min(MAXIMUM_BLADES - controller.blades, blades);
                controller.blades += transferred;
                blades -= transferred;
            }

            dropBlades();
        }

        chain = null;
        detachKinetics();
        attachKinetics();
        setChanged();
    }

    private void dropBlades() {
        if (blades != 0) {
            Block.popResource(level, worldPosition, NorthstarItems.FAN_BLADE.asStack(blades));
            blades = 0;
        }
    }

    @Override
    public Axis getMainConnectionAxis() {
        return getAxis();
    }

    @Override
    public int getMaxLength(Axis longAxis, int width) {
        if (longAxis == getAxis())
            return 1;
        return getMaxWidth();
    }

    @Override
    public int getMaxWidth() {
        return NorthstarConfigs.server().largeFanMaxSize.get();
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    public Axis getAxis() {
        return getBlockState().getValue(LargeFanBlock.AXIS);
    }

    public int getVolumeX() {
        return getAxis() == Axis.X ? height : width;
    }

    public int getVolumeY() {
        return getAxis() == Axis.Y ? height : width;
    }

    public int getVolumeZ() {
        return getAxis() == Axis.Z ? height : width;
    }

    private void updateBlockState(int update) {
        Axis axis = getAxis();

        Predicate<Direction> connected = dir -> isMultiBlockPart(worldPosition.relative(dir));
        TenPatch patch = TenPatch.pickPatch(axis, connected);

        level.setBlock(worldPosition, getBlockState().setValue(LargeFanBlock.PATCH, patch), update);
    }

    private boolean isMultiBlockPart(BlockPos pos) {
        BlockPos controller = getController();
        return pos.getX() >= controller.getX() && pos.getX() < controller.getX() + getVolumeX() &&
                pos.getY() >= controller.getY() && pos.getY() < controller.getY() + getVolumeY() &&
                pos.getZ() >= controller.getZ() && pos.getZ() < controller.getZ() + getVolumeZ();
    }

    // endregion

    private static List<Vec3i> getEdges(Axis axis, int width, int height) {
        Vec3i step1 = axis == Axis.X ? new Vec3i(0, 1, 0) : new Vec3i(1, 0, 0);
        Vec3i step2 = axis == Axis.Z ? new Vec3i(0, 1, 0) : new Vec3i(0, 0, 1);

        Vec3i axisDir = Direction.get(Direction.AxisDirection.POSITIVE, axis).getNormal();

        List<Vec3i> corners = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            Vec3i step = axisDir.multiply(i);
            Vec3i corner1 = step2.multiply(-1).offset(step);
            Vec3i corner2 = step1.multiply(-1).offset(step);
            Vec3i corner3 = step2.multiply(width).offset(step);
            Vec3i corner4 = step1.multiply(width).offset(step);

            for (int j = 0; j < width; j++) {
                corners.add(corner1);
                corners.add(corner2);
                corners.add(corner3);
                corners.add(corner4);

                corner1 = corner1.offset(step1);
                corner2 = corner2.offset(step2);
                corner3 = corner3.offset(step1);
                corner4 = corner4.offset(step2);
            }
        }

        return corners;
    }

}

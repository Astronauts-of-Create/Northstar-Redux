package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.contraption.rocket.RocketContraption;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.lightning.northstar.util.BetterSimpleContainer;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RocketStationBlockEntity extends SmartBlockEntity implements IDisplayAssemblyExceptions {

    public final SimpleContainer container = new BetterSimpleContainer(2);

    public AssemblyException lastException;
    public RocketDestination destination;

    public RocketStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        registerAwardables(behaviours, AllAdvancements.CONTRAPTION_ACTORS);
    }

    @Override
    public void destroy() {
        super.destroy();

        Containers.dropContents(level, worldPosition, container);
    }

    public RocketContraption assembleContraption() {
        lastException = null;
        RocketContraption rocket;
        try {
            if (level.northstar$planet() == null) {
                throw new AssemblyException(Component.translatable("northstar.contraption.rocket.assembly.unsupported_dimension"));
            }

            rocket = new RocketContraption();
            rocket.assemble(getLevel(), getBlockPos());

            if (!rocket.hasControls) {
                throw new AssemblyException(Component.translatable("northstar.contraption.rocket.assembly.missing_controls"));
            }

            rocket.destination = destination;
        } catch (AssemblyException exception) {
            lastException = exception;
            rocket = null;
        }
        sendData();
        return rocket;
    }

    public void assemble() {
        RocketContraption rocket = assembleContraption();
        if (rocket == null) {
            return;
        }

        RocketContraptionEntity entity = RocketContraptionEntity.create(level, rocket);
        entity.setPos(Vec3.atLowerCornerOf(worldPosition));

        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);

        rocket.removeBlocksFromWorld(level, BlockPos.ZERO);
        level.addFreshEntity(entity);
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        AssemblyException.write(compound, lastException);
        compound.put("Inventory", container.createTag());
        if (destination != null) compound.put("Destination", destination.toTag());
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        lastException = AssemblyException.read(compound);
        container.fromTag(compound.getList("Inventory", Tag.TAG_COMPOUND));
        destination = RocketDestination.fromTag(compound.getCompound("Destination"));

        if (compound.contains("item", Tag.TAG_COMPOUND)) {
            container.setItem(0, ItemStack.of(compound.getCompound("item")));
        }
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return lastException;
    }

}

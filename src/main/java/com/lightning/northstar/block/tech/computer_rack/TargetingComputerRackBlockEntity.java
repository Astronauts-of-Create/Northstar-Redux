package com.lightning.northstar.block.tech.computer_rack;

import com.lightning.northstar.content.NorthstarItems;
import com.lightning.northstar.util.BetterSimpleContainer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TargetingComputerRackBlockEntity extends SmartBlockEntity {

    public final SimpleContainer container = new BetterSimpleContainer(6);

    public TargetingComputerRackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void destroy() {
        super.destroy();

        ItemHelper.dropContents(level, worldPosition, new InvWrapper(container));
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        container.fromTag(compound.getList("Items", Tag.TAG_COMPOUND));

        for (int i = 0; i < 6; i++) {
            if (compound.contains("slot" + (i + 1), Tag.TAG_COMPOUND)) {
                container.setItem(i, ItemStack.of(compound.getCompound("slot" + (i + 1))));
            }
        }
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        compound.put("Items", container.createTag());
    }

    public int getComputerCount() {
        int count = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (container.getItem(i).is(NorthstarItems.TARGETING_COMPUTER.get())) {
                count++;
            }
        }
        return count;
    }

}

package com.lightning.northstar.contraption;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.MutablePair;

// Create devs, please make setBlock() sync the NBT :(
public class ActorConfigPacket extends SimplePacketBase {

    private final int contraptionId;
    private final BlockPos localPos;
    private final CompoundTag nbt;

    public ActorConfigPacket(FriendlyByteBuf buffer) {
        contraptionId = buffer.readVarInt();
        localPos = buffer.readBlockPos();
        nbt = buffer.readNbt();
    }

    public ActorConfigPacket(int contraptionId, BlockPos localPos, CompoundTag nbt) {
        this.contraptionId = contraptionId;
        this.localPos = localPos;
        this.nbt = nbt;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(contraptionId);
        buffer.writeBlockPos(localPos);
        buffer.writeNbt(nbt);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            if (!(Minecraft.getInstance().level.getEntity(contraptionId) instanceof AbstractContraptionEntity entity))
                return;
            Contraption contraption = entity.getContraption();

            StructureTemplate.StructureBlockInfo block = contraption.getBlocks().get(localPos);
            if (block != null) {
                contraption.getBlocks().put(localPos, new StructureTemplate.StructureBlockInfo(block.pos(), block.state(), nbt));
            }

            MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor = contraption.getActorAt(localPos);
            if (actor == null)
                return;
            if (actor.right.temporaryData instanceof ITakeConfig config) {
                config.handleServerConfig(nbt);
            }
        });
        return true;
    }

    public interface ITakeConfig {
        void handleServerConfig(CompoundTag nbt);
    }

}

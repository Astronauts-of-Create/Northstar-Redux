package com.lightning.northstar.contraptions;

import com.lightning.northstar.content.NorthstarPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;

// Create devs, please make setBlock() sync the NBT :(
public class ActorConfigPacket implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, ActorConfigPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, p -> p.contraptionId,
            BlockPos.STREAM_CODEC, p -> p.localPos,
            ByteBufCodecs.COMPOUND_TAG, p -> p.nbt,
            ActorConfigPacket::new
    );

    private final int contraptionId;
    private final BlockPos localPos;
    private final CompoundTag nbt;

    public ActorConfigPacket(int contraptionId, BlockPos localPos, CompoundTag nbt) {
        this.contraptionId = contraptionId;
        this.localPos = localPos;
        this.nbt = nbt;
    }

    @Override
    public void handle(LocalPlayer localPlayer) {
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
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return NorthstarPackets.ACTOR_CONFIG;
    }

    public interface ITakeConfig {
        void handleServerConfig(CompoundTag nbt);
    }

}

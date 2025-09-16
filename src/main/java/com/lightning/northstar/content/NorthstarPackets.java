package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.rocket_controls.RocketControlsInputPacket;
import com.lightning.northstar.block.tech.rocket_station.RocketStationEditPacket;
import com.lightning.northstar.block.tech.telescope.TelescopePrintPacket;
import com.lightning.northstar.block.tech.temperature_regulator.TemperatureRegulatorEditPacket;
import com.lightning.northstar.contraption.ActorConfigPacket;
import com.lightning.northstar.contraption.rocket.packet.RocketContraptionQuickSyncPacket;
import com.lightning.northstar.contraption.rocket.packet.RocketContraptionSyncPacket;
import com.lightning.northstar.contraption.rocket.packet.RocketControlPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum NorthstarPackets implements BasePacketPayload.PacketTypeProvider {

    // client to server
    UPDATE_ROCKET_STATION(RocketStationEditPacket.class, RocketStationEditPacket.STREAM_CODEC),
    UPDATE_TEMPERATURE_REGULATOR(TemperatureRegulatorEditPacket.class, TemperatureRegulatorEditPacket.STREAM_CODEC),
    TELESCOPE_PRINT(TelescopePrintPacket.class, TelescopePrintPacket.STREAM_CODEC),
    ROCKET_CONTROLS_INPUT(RocketControlsInputPacket.class, RocketControlsInputPacket.STREAM_CODEC),

    // server to client
    ROCKET_SYNC(RocketContraptionSyncPacket.class, RocketContraptionSyncPacket.STREAM_CODEC),
    ROCKET_QUICK_SYNC(RocketContraptionQuickSyncPacket.class, RocketContraptionQuickSyncPacket.STREAM_CODEC),
    ROCKET_CONTROL(RocketControlPacket.class, RocketControlPacket.STREAM_CODEC),
    ACTOR_CONFIG(ActorConfigPacket.class, ActorConfigPacket.STREAM_CODEC);

    public static final int NETWORK_VERSION = 4;

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> NorthstarPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(Northstar.asResource(name)),
                clazz, codec
        );
    }

    public static void registerPackets() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(Northstar.MOD_ID, NETWORK_VERSION);
        for (NorthstarPackets packet : NorthstarPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) type.type();
    }

}

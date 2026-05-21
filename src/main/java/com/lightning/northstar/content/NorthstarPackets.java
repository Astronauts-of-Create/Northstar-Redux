package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.rocket_station.RocketStationEditPacket;
import com.lightning.northstar.block.tech.telescope.TelescopePrintPacket;
import com.lightning.northstar.block.tech.temperature_regulator.TemperatureRegulatorEditPacket;
import com.lightning.northstar.contraption.ActorConfigPacket;
import com.lightning.northstar.contraption.rocket.packet.RocketDestinationPacket;
import com.lightning.northstar.contraption.rocket.packet.RocketSeatsPacket;
import com.lightning.northstar.contraption.rocket.packet.RocketSyncPacket;
import com.lightning.northstar.network.packet.ForceContraptionControlPacket;
import com.lightning.northstar.network.packet.RelativeTeleportPacket;
import com.lightning.northstar.network.packet.SpaceAtlasEditPacket;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

public enum NorthstarPackets {

    // client to server
    UPDATE_ROCKET_STATION(RocketStationEditPacket.class, RocketStationEditPacket::new, PLAY_TO_SERVER),
    UPDATE_SPACE_ATLAS(SpaceAtlasEditPacket.class, SpaceAtlasEditPacket::new, PLAY_TO_SERVER),
    UPDATE_TEMPERATURE_REGULATOR(TemperatureRegulatorEditPacket.class, TemperatureRegulatorEditPacket::new, PLAY_TO_SERVER),
    TELESCOPE_PRINT(TelescopePrintPacket.class, TelescopePrintPacket::new, PLAY_TO_SERVER),

    // server to client
    FORCE_CONTRAPTION_CONTROL(ForceContraptionControlPacket.class, ForceContraptionControlPacket::new, PLAY_TO_CLIENT),
    ROCKET_DESTINATION(RocketDestinationPacket.class, RocketDestinationPacket::new, PLAY_TO_CLIENT),
    ROCKET_SEATS(RocketSeatsPacket.class, RocketSeatsPacket::new, PLAY_TO_CLIENT),
    ROCKET_SYNC(RocketSyncPacket.class, RocketSyncPacket::new, PLAY_TO_CLIENT),
    ACTOR_CONFIG(ActorConfigPacket.class, ActorConfigPacket::new, PLAY_TO_CLIENT),
    RELATIVE_TELEPORT(RelativeTeleportPacket.class, RelativeTeleportPacket::new, PLAY_TO_CLIENT);

    public static final ResourceLocation CHANNEL_NAME = Northstar.asResource("main");
    public static final int NETWORK_VERSION = 7;
    public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
    private static SimpleChannel channel;

    private final PacketType<?> packetType;

    <T extends SimplePacketBase> NorthstarPackets(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
        packetType = new PacketType<>(type, factory, direction);
    }

    public static void registerPackets() {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .serverAcceptedVersions(NETWORK_VERSION_STR::equals)
                .clientAcceptedVersions(NETWORK_VERSION_STR::equals)
                .networkProtocolVersion(() -> NETWORK_VERSION_STR)
                .simpleChannel();

        for (NorthstarPackets packet : values()) {
            packet.packetType.register();
        }
    }

    public static SimpleChannel getChannel() {
        return channel;
    }

    public static void sendToNear(Level world, BlockPos pos, int range, Object message) {
        getChannel().send(PacketDistributor.NEAR.with(TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), range, world.dimension())), message);
    }

    private static class PacketType<T extends SimplePacketBase> {
        private static int index = 0;

        private BiConsumer<T, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, T> decoder;
        private BiConsumer<T, Supplier<Context>> handler;
        private Class<T> type;
        private NetworkDirection direction;

        private PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            encoder = T::write;
            decoder = factory;
            handler = (packet, contextSupplier) -> {
                Context context = contextSupplier.get();
                if (packet.handle(context)) {
                    context.setPacketHandled(true);
                }
            };
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            getChannel()
                    .messageBuilder(type, index++, direction)
                    .encoder(encoder)
                    .decoder(decoder)
                    .consumerNetworkThread(handler)
                    .add();
        }
    }

}
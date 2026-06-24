package com.lightning.northstar.block.tech.rocket_station;

import com.lightning.northstar.contraption.ActorConfigPacket;
import com.lightning.northstar.util.BetterSimpleContainer;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.SimpleContainer;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RocketStationActor implements ActorConfigPacket.ITakeConfig {

    public final SimpleContainer container;

    public RocketStationActor(MovementContext context) {
        container = new BetterSimpleContainer(2);
        container.fromTag(context.blockEntityData.getList("Inventory", Tag.TAG_COMPOUND), context.world.registryAccess());
        container.addListener($ -> {
            context.blockEntityData.put("Inventory", container.createTag(context.world.registryAccess()));
            if (!context.world.isClientSide()) {
                ActorConfigPacket.update(context.contraption.entity, context.localPos, context.blockEntityData);
            }
        });
    }

    @Override
    public void handleServerConfig(MovementContext context, CompoundTag nbt) {
        container.fromTag(nbt.getList("Inventory", Tag.TAG_COMPOUND), context.world.registryAccess());
    }

    public static RocketStationActor get(MovementContext context) {
        if (context.temporaryData instanceof RocketStationActor actor) {
            return actor;
        }

        RocketStationActor actor = new RocketStationActor(context);
        context.temporaryData = actor;
        return actor;
    }

}

package com.lightning.northstar.block.tech.oxygen_filler;

import com.lightning.northstar.contraption.ActorConfigPacket;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class OxygenFillerActor implements ActorConfigPacket.ITakeConfig {

    public final SimpleContainer container;

    private OxygenFillerActor(MovementContext context) {
        container = new SimpleContainer(1);
        container.setItem(0, ItemStack.parseOptional(context.world.registryAccess(), context.blockEntityData.getCompound("item")));
        container.addListener($ -> {
            context.blockEntityData.put("item", container.getItem(0).saveOptional(context.world.registryAccess()));
            if (!context.world.isClientSide()) {
                ActorConfigPacket.update(context.contraption.entity, context.localPos, context.blockEntityData);
            }
        });
    }

    public static OxygenFillerActor get(MovementContext context) {
        if (context.temporaryData instanceof OxygenFillerActor actor) {
            return actor;
        }
        OxygenFillerActor actor = new OxygenFillerActor(context);
        context.temporaryData = actor;
        return actor;
    }

    @Override
    public void handleServerConfig(MovementContext context, CompoundTag nbt) {
        container.setItem(0, ItemStack.parseOptional(context.world.registryAccess(), nbt.getCompound("item")));
    }

}

package com.lightning.northstar.world.oxygen;

import net.minecraft.world.level.block.Blocks;

public class DefaultOxygenConsumers {

    public static void register() {
        OxygenConsumer.REGISTRY.register(Blocks.TORCH, OxygenConsumer.scaleBy(2f));
        OxygenConsumer.REGISTRY.register(Blocks.WALL_TORCH, OxygenConsumer.scaleBy(2f));
        OxygenConsumer.REGISTRY.register(Blocks.LANTERN, OxygenConsumer.scaleBy(2f));
        OxygenConsumer.REGISTRY.register(Blocks.SOUL_TORCH, OxygenConsumer.scaleBy(2f));
        OxygenConsumer.REGISTRY.register(Blocks.SOUL_WALL_TORCH, OxygenConsumer.scaleBy(2f));
        OxygenConsumer.REGISTRY.register(Blocks.SOUL_LANTERN, OxygenConsumer.scaleBy(2f));
        OxygenConsumer.REGISTRY.register(Blocks.JACK_O_LANTERN, OxygenConsumer.scaleBy(2f));
    }

}

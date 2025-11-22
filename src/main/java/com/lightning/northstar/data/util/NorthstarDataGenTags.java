package com.lightning.northstar.data.util;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class NorthstarDataGenTags {

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> apply(TagKey<Block> block, TagKey<Item> item) {
        return builder -> builder.tag(block)
                .item()
                .tag(item)
                .build();
    }

}

package com.lightning.northstar.block.crops;

import com.lightning.northstar.content.NorthstarItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MarsTulipBlock extends MartianFlowerBlock {

    public MarsTulipBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Item getSeedItem() {
        return NorthstarItems.MARS_TULIP_SEEDS.get();
    }

}

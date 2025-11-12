package com.lightning.northstar.block.simple;

import com.lightning.northstar.util.NorthstarDataGenHelper;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

public class AureneGlassBlock {


    public static BlockEntry<GlassBlock> registerAureneGlassBlock(CreateRegistrate registrate, String name, MapColor mapColor) {
        return registrate
                .block(name, GlassBlock::new)
                .initialProperties(() -> Blocks.GLASS)
                .properties(p -> p
                        .mapColor(mapColor)
                        .strength(0.3F)
                        .sound(SoundType.GLASS)
                        .noOcclusion()
                        .isValidSpawn((s, g, p2, e) -> false)
                        .isRedstoneConductor((s, g, p2) -> false)
                        .isSuffocating((s, g, p2) -> false)
                        .isViewBlocking((s, g, p2) -> false)
                )
                .blockstate(NorthstarDataGenHelper.manualModel())
                .simpleItem()
                .register();
    }
}

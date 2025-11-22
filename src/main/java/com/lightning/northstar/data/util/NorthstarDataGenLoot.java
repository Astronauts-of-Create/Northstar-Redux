package com.lightning.northstar.data.util;

import com.lightning.northstar.block.simple.VerticalSlabBlock;
import com.lightning.northstar.block.simple.VerticalSlabType;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class NorthstarDataGenLoot {

    public static NonNullBiConsumer<RegistrateBlockLootTables, VerticalSlabBlock> verticalSlabLoot() {
        return (c, b) -> c.add(b, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(c.applyExplosionDecay(b, LootItem.lootTableItem(b)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE))))))));
    }

    public static <B extends Block> NonNullBiConsumer<RegistrateBlockLootTables, B> cropLoot(ItemLike result, ItemLike seeds, int maxAge) {
        return (c, b) -> {
            LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)
                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, maxAge));
            c.add(b, c.createCropDrops(b, result.asItem(), seeds.asItem(), condition));
        };
    }

}

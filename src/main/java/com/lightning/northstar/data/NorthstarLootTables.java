package com.lightning.northstar.data;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import static com.tterrag.registrate.providers.loot.RegistrateBlockLootTables.createSilkTouchDispatchTable;

public class NorthstarLootTables {
    public static LootTable.Builder customLoot(RegistrateBlockLootTables rblt, Block block,
                                               ItemEntry<Item> loot, int min, int max) {
        return createSilkTouchDispatchTable(block, rblt.applyExplosionDecay(block,
                LootItem.lootTableItem(loot)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
        )
        );
    }
}

package com.lightning.northstar.world.oxygen;

import com.lightning.northstar.world.sealer.SealableBlock;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Oxygen consumers are blocks that may consume oxygen at different rates. The interface can either be implemented onto
 * the block instance directly or implementations can be associated to existing blocks via {@link #REGISTRY}.
 * Note that the block must be counted as part of the sealed volume for it to interact, this can be done by
 * adding the block to the {@code northstar:blocks_air} tag or implementing {@link SealableBlock}.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface OxygenConsumer {

    SimpleRegistry<Block, OxygenConsumer> REGISTRY = SimpleRegistry.create();

    static <B extends Block> NonNullConsumer<? super B> oxygenConsumer(OxygenConsumer consumer) {
        return b -> REGISTRY.register(b, consumer);
    }

    static OxygenConsumer scaleBy(float usage) {
        return (level, pos, base) -> base * usage;
    }

    /**
     * Tests if this consumer should be displayed on goggles but not actually affect oxygen sealers.
     */
    default boolean northstar$isGogglesOnly(BlockGetter level, BlockPos pos) {
        return false;
    }

    /**
     * Tests if the oxygen consumption is dynamic and should be checked each tick, if false then the consumption is only
     * checked once on seal updates with {@link #northstar$getOxygenConsumption(BlockGetter, BlockPos, float)}
     *
     * @return true if the block might change its oxygen consumption and needs to be checked each tick
     */
    default boolean northstar$isOxygenConsumptionDynamic(BlockGetter level, BlockPos pos) {
        return false;
    }

    /**
     * Calculates the oxygen usage in mB/t
     * Note for implementers: if dynamic, make sure that the block is what you expect as it might change in the world and seals don't update instantly.
     *
     * @param level the level of the block
     * @param pos   the position of the block
     * @param base  the base oxygen consumption, in mB/t
     */
    float northstar$getOxygenConsumption(BlockGetter level, BlockPos pos, float base);

}

package com.lightning.northstar.world.oxygen;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.world.sealer.ProgressiveBlockSealer;
import com.lightning.northstar.world.sealer.SealingMode;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class OxygenTrackingSealer extends ProgressiveBlockSealer {

    protected final List<Pair<BlockPos, OxygenConsumer>> sealingConsumers = new ArrayList<>();
    protected final List<Pair<BlockPos, OxygenConsumer>> consumers = new ArrayList<>();
    protected float baseConsumption;
    protected float sealingDynamicConsumption;
    protected float dynamicConsumption;

    public OxygenTrackingSealer(SealingMode mode) {
        super(mode);
    }

    @Override
    public boolean beginSeal(Level level, BlockPos origin, @Nullable Direction originDirection) {
        baseConsumption = NorthstarConfigs.server().oxygenSealerOxygenPerBlockPerTick.getF();
        return super.beginSeal(level, origin, originDirection);
    }

    @Override
    protected void onSealComplete(int maximumSealed, long lastChecked) {
        super.onSealComplete(maximumSealed, lastChecked);

        consumers.clear();
        consumers.addAll(sealingConsumers);
        sealingConsumers.clear();

        dynamicConsumption = sealingDynamicConsumption;
        sealingDynamicConsumption = 0;
    }

    @Override
    protected void onBlockAdded(BlockGetter level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        OxygenConsumer consumer = block instanceof OxygenConsumer cons ? cons : OxygenConsumer.REGISTRY.get(block);

        if (consumer != null && !consumer.northstar$isGogglesOnly(level, pos)) {
            if (consumer.northstar$isOxygenConsumptionDynamic(level, pos)) {
                sealingConsumers.add(Pair.of(new BlockPos(pos), consumer));
            } else {
                sealingDynamicConsumption += consumer.northstar$getOxygenConsumption(level, pos, baseConsumption);
            }
        }
    }

    public float calculateDynamicConsumption(Level level) {
        float sum = 0;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, j = consumers.size(); i < j; i++) {
            Pair<BlockPos, OxygenConsumer> consumer = consumers.get(i);
            sum += consumer.right().northstar$getOxygenConsumption(level, consumer.left(), baseConsumption);
        }
        return sum;
    }

    public float getDynamicConsumption() {
        return dynamicConsumption;
    }

}

package com.lightning.northstar.mixin.accessor;

import com.lightning.northstar.accessor.NorthstarOutliner;
import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.outliner.Outliner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = Outliner.class, remap = false)
public class OutlinerMixin implements NorthstarOutliner {

    @Shadow
    @Final
    private Map<Object, Outliner.OutlineEntry> outlines;

    @Override
    public Outliner.OutlineEntry northstar$add(Object slot, Outline outline) {
        Outliner.OutlineEntry entry = new Outliner.OutlineEntry(outline);
        outlines.put(slot, entry);
        return entry;
    }

    @Mixin(value = Outliner.OutlineEntry.class, remap = false)
    public static class OutlineEntryMixin implements NorthstarOutliner.OutlineEntry {
        @Shadow
        private int ticksTillRemoval;

        @Override
        public void northstar$setTimeToLive(int ttl) {
            ticksTillRemoval = ttl;
        }
    }

}

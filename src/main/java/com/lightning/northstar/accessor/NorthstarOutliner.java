package com.lightning.northstar.accessor;

import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.outliner.Outliner;

// Such a good API but all kept private
public interface NorthstarOutliner {

    static NorthstarOutliner getInstance() {
        return (NorthstarOutliner) Outliner.getInstance();
    }

    default Outliner.OutlineEntry northstar$add(Object slot, Outline outline) {
        throw new MissingMixinException();
    }

    interface OutlineEntry {
        default void northstar$setTimeToLive(int ttl) {
            throw new MissingMixinException();
        }
    }

}

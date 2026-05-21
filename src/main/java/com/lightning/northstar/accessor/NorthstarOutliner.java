package com.lightning.northstar.accessor;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.outliner.Outliner;

// Such a good API but all kept private
public interface NorthstarOutliner {

    static NorthstarOutliner getInstance() {
        return (NorthstarOutliner) CreateClient.OUTLINER;
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

package com.lightning.northstar.accessor;

import com.simibubi.create.content.contraptions.MountedStorageManager;

public interface NorthstarContraption {

    default MountedStorageManager northstar$getStorage() {
        throw new MissingMixinException();
    }

}

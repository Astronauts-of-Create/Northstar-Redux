package com.lightning.northstar.compat.sable;

import com.lightning.northstar.world.sealer.transform.TransformProviders;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;

public class NorthstarSable {

    public static void init() {
        TransformProviders.registerToWorld((level, pos) -> {
            SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, pos);
            return subLevel == null ? null : subLevel.logicalPose().transformPosition(pos);
        });
        TransformProviders.registerFromWorld((level, pos) -> {
            SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, pos);
            return (l, p) -> subLevel == null ? null : subLevel.logicalPose().transformPositionInverse(p);
        });
    }

}

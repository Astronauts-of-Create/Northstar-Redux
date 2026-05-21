package com.lightning.northstar.compat.vs2;

import com.lightning.northstar.world.sealer.transform.TransformProviders;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4dc;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class NorthstarValkyrienSkies {

    public static void init() {
        TransformProviders.registerToWorld((level, pos) -> {
            Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
            return ship == null ? null : mulMatVec(ship.getShipToWorld(), pos);
        });
        TransformProviders.registerFromWorld((level, pos) -> {
            Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
            if (ship == null) {
                return null;
            }
            return (l, p) -> mulMatVec(ship.getTransform().getWorldToShip(), p);
        });
    }

    private static Vec3 mulMatVec(Matrix4dc m, Vec3 p) {
        double x = m.m00() * p.x + m.m10() * p.y + m.m20() * p.z + m.m30();
        double y = m.m01() * p.x + m.m11() * p.y + m.m21() * p.z + m.m31();
        double z = m.m02() * p.x + m.m12() * p.y + m.m22() * p.z + m.m32();
        return new Vec3(x, y, z);
    }

}

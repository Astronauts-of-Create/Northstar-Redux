package com.lightning.northstar.entity.ai;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ZeroGravityMoveControl extends MoveControl {

    private int maxTurn;

    public ZeroGravityMoveControl(Mob mob, int maxTurn) {
        super(mob);
        this.maxTurn = maxTurn;
    }

    @Override
    public void tick() {
        // Modified version of FlyingMoveControl

        if (operation == MoveControl.Operation.MOVE_TO) {
            //operation = MoveControl.Operation.WAIT;
            double dx = wantedX - mob.getX();
            double dy = wantedY - mob.getY();
            double dz = wantedZ - mob.getZ();
            double xz2 = dx * dx + dz * dz;
            double dist2 = dy * dy + xz2;
            if (dist2 < 2.5000003E-7F) {
                mob.setYya(0.0F);
                mob.setZza(0.0F);
                return;
            }

            float h = (float) (Mth.atan2(dz, dx) * 180.0F / (float) Math.PI) - 90.0F;
            mob.setYRot(rotlerp(mob.getYRot(), h, 90.0F));
            float i;
            if (mob.onGround() || !mob.getAttributes().hasAttribute(Attributes.FLYING_SPEED)) {
                i = (float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            } else {
                i = (float) (speedModifier * mob.getAttributeValue(Attributes.FLYING_SPEED));
            }

            mob.setSpeed(i);
            double j = Math.sqrt(dx * dx + dz * dz);
            if (Math.abs(dy) > 1.0E-5F || Math.abs(j) > 1.0E-5F) {
                float k = (float) (-(Mth.atan2(dy, j) * 180.0F / (float) Math.PI));
                mob.setXRot(rotlerp(mob.getXRot(), k, maxTurn));
                mob.setYya(Math.abs(dy) < 0.1 ? i * 0.01f : dy > 0.0 ? i : -i);

                if (dy > 0.2 && mob.onGround()) {
                    mob.getJumpControl().jump();
                }
            }
        } else {
            mob.setYya(0.0F);
            mob.setZza(0.0F);
        }
    }
}

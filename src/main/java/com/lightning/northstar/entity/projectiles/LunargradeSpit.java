package com.lightning.northstar.entity.projectiles;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarEntityTypes;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LunargradeSpit extends AbstractSpitProjectile {

    public static final ResourceLocation SPIT_TEXTURE = Northstar.asResource("textures/entity/llama/spit.png");

    public LunargradeSpit(EntityType<? extends LunargradeSpit> entityType, Level level) {
        super(entityType, level);
    }

    public LunargradeSpit(Level level, LivingEntity spitter) {
        super(NorthstarEntityTypes.LUNARGRADE_SPIT.get(), level, spitter);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (getOwner() instanceof LivingEntity owner) {
            result.getEntity().hurt(level().damageSources().mobProjectile(this, owner), 1);
        }
    }

}

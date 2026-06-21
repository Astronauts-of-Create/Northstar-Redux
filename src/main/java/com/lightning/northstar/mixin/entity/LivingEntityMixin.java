package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.world.NorthstarDimensions;
import com.lightning.northstar.world.temperature.NorthstarTemperature;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(LivingEntity.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void northstar$tick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        NorthstarTemperature.tickEntity(self);

        // TODO: Implement proper weather system instead of hardcoding Mars
        Level level = level();
        if (level.dimension() == NorthstarDimensions.MARS &&
            level.getRainLevel(0) > 0 &&
            level.getBrightness(LightLayer.SKY, blockPosition()) == 15 &&
            getY() < level.getMaxBuildHeight() + NorthstarConfigs.server().atmosphereBaseHeight.get() &&
            !(self instanceof Player player && (player.isSpectator() || player.getAbilities().flying))) {
            float force = 0.005f;
            // TODO: This should probably change direction instead of always pushing that axis
            setDeltaMovement(getDeltaMovement().add(force, 0, -force));
        }
    }

}

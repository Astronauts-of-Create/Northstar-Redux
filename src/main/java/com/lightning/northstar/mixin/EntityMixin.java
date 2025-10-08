package com.lightning.northstar.mixin;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.util.mixinInterfaces.EntityMixin_I;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityMixin_I {

    @Shadow
    public abstract Level level();

    @Unique
    RocketContraptionEntity ridingRocket = null;

    public RocketContraptionEntity getRidingRocket() {//TODO: theres gotta be a better way to do this without adding data to all entities (But I think a global hashmap would be worse)
        Entity self = (Entity) (Object) this;
        if (ridingRocket != null &&
                (!ridingRocket.isAlive() || !ridingRocket.entitiesWithinContraption.contains(self))
        ) ridingRocket = null;
        return ridingRocket;
    }

    public void setRidingRocket(RocketContraptionEntity rocket) {
        ridingRocket = rocket;
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
            at = @At("HEAD"), cancellable = true)
    private void northstar$StartRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        //We must use a mixin to be more proactive in stopping the player from riding when they should not
        if ((Object) this instanceof LivingEntity self) {
            EntityMixin_I mix = (EntityMixin_I) self;
            if (mix.getRidingRocket() != null && vehicle != mix.getRidingRocket()) {
                Northstar.LOGGER.warn("Passenger trying to mount non-rocket while already in a rocket.");
                //TODO: Send a client message telling the user they cannot mount another entity while in rocket
//                Minecraft.getInstance().player.displayClientMessage(
//                        Component.translatable("northstar.contraption.cannot_mount_another_entity_while_in_rocket"), true);
                cir.cancel();
            }
        }
    }
}
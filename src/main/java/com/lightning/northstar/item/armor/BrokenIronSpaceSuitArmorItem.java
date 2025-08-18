package com.lightning.northstar.item.armor;

import com.lightning.northstar.client.model.armor.BrokenIronSpaceSuitArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class BrokenIronSpaceSuitArmorItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    public BrokenIronSpaceSuitArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Properties properties) {
        super(material, type, properties);
    }

    // region GeoAnimatable

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);

        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (renderer == null)
                    renderer = new GeoArmorRenderer<>(new BrokenIronSpaceSuitArmorModel());
                renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableCache;
    }

    private PlayState predicate(AnimationState<BrokenIronSpaceSuitArmorItem> event) {
        event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
        return PlayState.CONTINUE;
    }

    // endregion

}

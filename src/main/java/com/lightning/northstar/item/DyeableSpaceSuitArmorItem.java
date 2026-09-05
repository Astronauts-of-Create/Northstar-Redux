package com.lightning.northstar.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DyeableSpaceSuitArmorItem extends DyeableArmorItem implements GeoItem {

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<GeoModel<DyeableSpaceSuitArmorItem>> model;

    public DyeableSpaceSuitArmorItem(ArmorMaterial material, Type type, Properties properties, Supplier<GeoModel<DyeableSpaceSuitArmorItem>> model) {
        super(material, type, properties);
        this.model = model;
    }

    @Override
    public int getColor(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(TAG_DISPLAY);
        return tag != null && tag.contains(TAG_COLOR, Tag.TAG_ANY_NUMERIC) ? tag.getInt(TAG_COLOR) : 0xFFFFFF;
    }

    // region GeckoLib

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);

        consumer.accept(new IClientItemExtensions() {
            private DyeableGeoArmorRenderer<?> renderer;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> baseModel) {
                DyeableGeoArmorRenderer<?> renderer = this.renderer;
                if (renderer == null) {
                    this.renderer = renderer = new DyeableGeoArmorRenderer<>(model.get()) {
                        private Color color = Color.WHITE;

                        @Override
                        public void prepForRender(@Nullable Entity entity, ItemStack stack, @Nullable EquipmentSlot slot, @Nullable HumanoidModel<?> baseModel) {
                            super.prepForRender(entity, stack, slot, baseModel);

                            int color = getColor(stack) | 0xFF000000;
                            if (this.color.argbInt() != color) {
                                this.color = new Color(color);
                            }
                        }

                        @Override
                        protected boolean isBoneDyeable(GeoBone bone) {
                            return bone.getName().contains("dyed");
                        }

                        @Override
                        protected Color getColorForBone(GeoBone bone) {
                            return color;
                        }
                    };
                }
                renderer.prepForRender(entity, stack, slot, baseModel);
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableCache;
    }

    // endregion

}

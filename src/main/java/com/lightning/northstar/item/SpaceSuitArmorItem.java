package com.lightning.northstar.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpaceSuitArmorItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<GeoModel<SpaceSuitArmorItem>> model;

    public SpaceSuitArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, Supplier<GeoModel<SpaceSuitArmorItem>> model) {
        super(material, type, properties);
        this.model = model;
    }

    // region GeckoLib

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> regularRenderer;
            private GeoArmorRenderer<?> dyeableRenderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T entity, ItemStack stack, @Nullable EquipmentSlot slot, @Nullable HumanoidModel<T> baseModel) {
                GeoArmorRenderer<?> renderer = stack.is(ItemTags.DYEABLE) ? dyeableRenderer : regularRenderer;
                if (renderer == null) {
                    regularRenderer = new GeoArmorRenderer<>(model.get());
                    dyeableRenderer = new DyeableGeoArmorRenderer<>(model.get()) {
                        private Color color = Color.WHITE;

                        @Override
                        public Color getRenderColor(SpaceSuitArmorItem animatable, float partialTick, int packedLight) {
                            return Color.WHITE;
                        }

                        @Override
                        public void prepForRender(Entity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> baseModel, MultiBufferSource bufferSource, float partialTick, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch) {
                            super.prepForRender(entity, stack, slot, baseModel, bufferSource, partialTick, limbSwing, limbSwingAmount, netHeadYaw, headPitch);

                            int color = DyedItemColor.getOrDefault(stack, 0xFFFFFF) | 0xFF000000;
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
                    renderer = stack.is(ItemTags.DYEABLE) ? this.dyeableRenderer : this.regularRenderer;
                }
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

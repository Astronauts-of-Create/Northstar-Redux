package com.lightning.northstar.client.renderer.armor;

import com.lightning.northstar.block.tech.NorthstarPartialModels;
import com.lightning.northstar.content.NorthstarItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.mixin.accessor.EntityRenderDispatcherAccessor;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SpaceSuitLayerRenderer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public SpaceSuitLayerRenderer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack ms, MultiBufferSource buffer, int light, T entity,
                       float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw,
                       float pHeadPitch) {
        if (entity.getPose() == Pose.SLEEPING)
            return;

        Item item = entity.getItemBySlot(EquipmentSlot.HEAD).getItem();
        PartialModel model;
        if (NorthstarItems.BROKEN_IRON_SPACE_SUIT_HELMET.is(item)) {
            model = NorthstarPartialModels.BROKEN_IRON_SPACE_SUIT_HELMET;
        } else if (NorthstarItems.IRON_SPACE_SUIT_HELMET.is(item)) {
            model = NorthstarPartialModels.IRON_SPACE_SUIT_HELMET;
        } else if (NorthstarItems.MARTIAN_STEEL_SPACE_SUIT_HELMET.is(item)) {
            model = NorthstarPartialModels.MARTIAN_STEEL_SPACE_SUIT_HELMET;
        } else {
            return;
        }

        M entityModel = getParentModel();
        if (!(entityModel instanceof HumanoidModel))
            return;

        BlockState air = Blocks.AIR.defaultBlockState();
        RenderType renderType = Sheets.translucentCullBlockSheet();
        SuperByteBuffer helmet = CachedBuffers.partial(model, air);

        ms.pushPose();

        ((HumanoidModel<?>) entityModel).head.translateAndRotate(ms);
        ms.translate(0.5, 1.45, -0.5);
        ms.scale(-1, -1, 1);

        helmet.light(light)
                .renderInto(ms, buffer.getBuffer(renderType));

        ms.popPose();
    }

    public static void registerOnAll(EntityRenderDispatcher renderManager) {
        for (EntityRenderer<? extends Player> renderer : renderManager.getSkinMap().values())
            registerOn(renderer);
        for (EntityRenderer<?> renderer : ((EntityRenderDispatcherAccessor) renderManager).create$getRenderers().values())
            registerOn(renderer);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void registerOn(EntityRenderer<?> entityRenderer) {
        if (!(entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer))
            return;
        if (!(livingRenderer.getModel() instanceof HumanoidModel))
            return;
        SpaceSuitLayerRenderer<?, ?> layer = new SpaceSuitLayerRenderer<>(livingRenderer);
        livingRenderer.addLayer((SpaceSuitLayerRenderer) layer);
    }

}

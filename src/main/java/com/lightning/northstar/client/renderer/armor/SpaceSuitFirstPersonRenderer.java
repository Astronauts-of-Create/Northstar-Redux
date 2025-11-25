package com.lightning.northstar.client.renderer.armor;

import com.lightning.northstar.content.NorthstarEntityResources;
import com.lightning.northstar.content.NorthstarItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderArmEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class SpaceSuitFirstPersonRenderer {

    private static ResourceLocation activeHand = NorthstarEntityResources.IRON_SPACESUIT_ARMOR_ARM;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        ItemStack chestplate = mc.player == null ? null : mc.player.getInventory().getArmor(2);

        if (chestplate == null) {
            activeHand = null;
        } else if (chestplate.is(NorthstarItems.BROKEN_IRON_SPACE_SUIT_CHESTPIECE.get())) {
            activeHand = NorthstarEntityResources.BROKEN_IRON_SPACESUIT_ARMOR_ARM;
        } else if (chestplate.is(NorthstarItems.IRON_SPACE_SUIT_CHESTPIECE.get())) {
            activeHand = NorthstarEntityResources.IRON_SPACESUIT_ARMOR_ARM;
        } else if (chestplate.is(NorthstarItems.MARTIAN_STEEL_SPACE_SUIT_CHESTPIECE.get())) {
            activeHand = NorthstarEntityResources.MARTIAN_STEEL_SPACESUIT_ARMOR_ARM;
        } else {
            activeHand = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderPlayerHand(RenderArmEvent event) {
        if (activeHand == null) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        MultiBufferSource buffer = event.getMultiBufferSource();
        if (!(mc.getEntityRenderDispatcher().getRenderer(player) instanceof PlayerRenderer pr))
            return;

        PlayerModel<AbstractClientPlayer> model = pr.getModel();
        model.attackTime = 0.0F;
        model.crouching = false;
        model.swimAmount = 0.0F;
        model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        ModelPart armPart = event.getArm() == HumanoidArm.LEFT ? model.leftSleeve : model.rightSleeve;
        armPart.xRot = 0.0F;
        armPart.render(event.getPoseStack(), buffer.getBuffer(RenderType.entitySolid(activeHand)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        event.setCanceled(true);
    }

}

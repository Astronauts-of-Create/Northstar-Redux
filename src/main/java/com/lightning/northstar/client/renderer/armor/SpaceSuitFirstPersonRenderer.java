package com.lightning.northstar.client.renderer.armor;

import com.lightning.northstar.content.NorthstarEntityResources;
import com.lightning.northstar.content.NorthstarItems;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpaceSuitFirstPersonRenderer {

    private static final Map<Item, Pair<ResourceLocation, ResourceLocation>> HAND_TEXTURES = new HashMap<>();

    public static synchronized void registerHandTexture(Item item, @Nullable ResourceLocation texture, @Nullable ResourceLocation overlay) {
        HAND_TEXTURES.put(item, Pair.of(texture, overlay));
    }

    public static void register() {
        registerHandTexture(
                NorthstarItems.BROKEN_IRON_SPACE_SUIT_CHESTPIECE.get(),
                NorthstarEntityResources.BROKEN_IRON_SPACESUIT_ARMOR_ARM,
                NorthstarEntityResources.BROKEN_IRON_SPACESUIT_ARMOR_ARM_OVERLAY
        );
        registerHandTexture(
                NorthstarItems.IRON_SPACE_SUIT_CHESTPIECE.get(),
                NorthstarEntityResources.IRON_SPACESUIT_ARMOR_ARM,
                NorthstarEntityResources.IRON_SPACESUIT_ARMOR_ARM_OVERLAY
        );
        registerHandTexture(
                NorthstarItems.MARTIAN_STEEL_SPACE_SUIT_CHESTPIECE.get(),
                null,
                NorthstarEntityResources.MARTIAN_STEEL_SPACESUIT_ARMOR_ARM
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderPlayerHand(RenderArmEvent event) {
        AbstractClientPlayer player = event.getPlayer();
        if (!(Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player) instanceof PlayerRenderer pr)) {
            return;
        }

        ItemStack chestplate = player.getInventory().getArmor(2);
        Pair<ResourceLocation, ResourceLocation> textures = HAND_TEXTURES.get(chestplate.getItem());
        if (textures == null) {
            return;
        }

        event.setCanceled(true);

        PlayerModel<AbstractClientPlayer> model = pr.getModel();
        pr.setModelProperties(player);
        model.attackTime = 0;
        model.crouching = false;
        model.swimAmount = 0;
        model.setupAnim(player, 0, 0, 0, 0, 0);
        ModelPart armPart = event.getArm() == HumanoidArm.LEFT ? model.leftSleeve : model.rightSleeve;
        armPart.xRot = 0;

        if (textures.first() != null) {
            int color = chestplate.getItem() instanceof DyeableLeatherItem dyeable ? dyeable.getColor(chestplate) : 0xFFFFFF;
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;

            armPart.render(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.entityCutout(textures.first())), event.getPackedLight(), OverlayTexture.NO_OVERLAY, r, g, b, 1);
        }

        if (textures.second() != null) {
            armPart.render(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.entityCutout(textures.second())), event.getPackedLight(), OverlayTexture.NO_OVERLAY);
        }
    }

}

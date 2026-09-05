package com.lightning.northstar.client.model.armor;

import com.lightning.northstar.content.NorthstarEntityResources;
import com.lightning.northstar.item.DyeableSpaceSuitArmorItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class IronSpaceSuitArmorModel extends GeoModel<DyeableSpaceSuitArmorItem> {

    @Override
    public ResourceLocation getAnimationResource(DyeableSpaceSuitArmorItem item) {
        return NorthstarEntityResources.IRON_SPACESUIT_ARMOR_ANIMATIONS;
    }

    @Override
    public ResourceLocation getModelResource(DyeableSpaceSuitArmorItem item) {
        return NorthstarEntityResources.IRON_SPACESUIT_ARMOR_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DyeableSpaceSuitArmorItem item) {
        return NorthstarEntityResources.IRON_SPACESUIT_ARMOR_TEXTURE;
    }

}

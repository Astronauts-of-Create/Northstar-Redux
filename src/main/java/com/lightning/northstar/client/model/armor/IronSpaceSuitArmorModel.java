package com.lightning.northstar.client.model.armor;

import com.lightning.northstar.content.NorthstarEntityResources;
import com.lightning.northstar.item.SpaceSuitArmorItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class IronSpaceSuitArmorModel extends GeoModel<SpaceSuitArmorItem> {

    @Override
    public ResourceLocation getAnimationResource(SpaceSuitArmorItem item) {
        return NorthstarEntityResources.IRON_SPACESUIT_ARMOR_ANIMATIONS;
    }

    @Override
    public ResourceLocation getModelResource(SpaceSuitArmorItem item) {
        return NorthstarEntityResources.IRON_SPACESUIT_ARMOR_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SpaceSuitArmorItem item) {
        return NorthstarEntityResources.IRON_SPACESUIT_ARMOR_TEXTURE;
    }

}

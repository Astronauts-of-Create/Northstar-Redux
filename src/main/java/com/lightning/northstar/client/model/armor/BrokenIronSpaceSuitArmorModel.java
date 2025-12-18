package com.lightning.northstar.client.model.armor;

import com.lightning.northstar.content.NorthstarEntityResources;
import com.lightning.northstar.item.SpaceSuitArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BrokenIronSpaceSuitArmorModel extends GeoModel<SpaceSuitArmorItem> {

    @Override
    public ResourceLocation getAnimationResource(SpaceSuitArmorItem animatable) {
        return NorthstarEntityResources.BROKEN_IRON_SPACESUIT_ARMOR_ANIMATIONS;
    }

    @Override
    public ResourceLocation getModelResource(SpaceSuitArmorItem object) {
        return NorthstarEntityResources.BROKEN_IRON_SPACESUIT_ARMOR_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SpaceSuitArmorItem object) {
        return NorthstarEntityResources.BROKEN_IRON_SPACESUIT_ARMOR_TEXTURE;
    }

}

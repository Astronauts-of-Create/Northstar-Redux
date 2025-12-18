package com.lightning.northstar.mixin;

import com.lightning.northstar.api.WhenModLoaded;
import com.lightning.northstar.data.ModCompat;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class NorthstarMixinPlugin implements IMixinConfigPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger(NorthstarMixinPlugin.class);

    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        try {
            List<AnnotationNode> annotationNodes = MixinService.getService()
                    .getBytecodeProvider()
                    .getClassNode(mixinClassName)
                    .visibleAnnotations;
            if (annotationNodes == null)
                return true;

            for (AnnotationNode node : annotationNodes) {
                if (node.desc.equals(Type.getDescriptor(WhenModLoaded.class))) {
                    List<ModCompat> mods = Annotations.getValue(node, "value", false, ModCompat.class);

                    for (ModCompat mod : mods) {
                        ModList modlist = ModList.get();
                        boolean loaded = modlist == null ?
                                FMLLoader.getLoadingModList().getMods().stream().anyMatch(m -> m.getModId().equals(mod.modId)) :
                                modlist.isLoaded(mod.modId);
                        if (loaded) {
                            return true;
                        }
                    }

                    return mods.isEmpty();
                }
            }
        } catch (ClassNotFoundException | IOException exception) {
            LOGGER.error("", exception);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

}

package com.lightning.northstar.planet;

import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.planet.data.PlanetProperties;
import com.lightning.northstar.util.NorthstarLang;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class Planet {

    public final ResourceKey<PlanetProperties> key;
    public final PlanetProperties properties;
    @Nullable
    public final Planet centralBody;
    public final List<PlanetDimension> dimensions;
    public final List<Planet> satellites;
    /** Local position around the parent planet */
    public final Vector3d localPosition;
    /** Absolute position in the solar system */
    public final Vector3d position;
    public GravitationalSystem system;

    Planet(ResourceKey<PlanetProperties> key, PlanetProperties properties, @Nullable Planet centralBody, List<PlanetDimension> dimensions) {
        this.key = key;
        this.properties = properties;
        this.centralBody = centralBody;
        this.dimensions = dimensions;
        this.satellites = new ArrayList<>();
        this.localPosition = new Vector3d();
        this.position = new Vector3d();
    }

    void postBuild() {
        satellites.sort(Comparator.comparingDouble(satellite -> satellite.properties.orbit().approximateRadius()));
    }

    public void walkPreOrder(Consumer<Planet> consumer) {
        consumer.accept(this);
        for (Planet satellite : satellites) {
            satellite.walkPreOrder(consumer);
        }
    }

    public void walkPostOrder(Consumer<Planet> consumer) {
        for (Planet moon : satellites) {
            moon.walkPostOrder(consumer);
        }
        consumer.accept(this);
    }

    @OnlyIn(Dist.CLIENT)
    public String getLiteralName() {
        String key = getTranslationKey();
        return I18n.exists(key) ? I18n.get(key) : NorthstarLang.getFallbackName(this.key);
    }

    @Contract("-> new")
    public MutableComponent getName() {
        return Component.translatableWithFallback(getTranslationKey(), NorthstarLang.getFallbackName(key));
    }

    private String getTranslationKey() {
        ResourceLocation loc = this.key.location();
        return loc.getNamespace() + ".planets." + loc.getPath().replace('/', '.') + ".name";
    }

    @Contract("_ -> new")
    public MutableComponent getDimensionName(PlanetDimension dimension) {
        return Component.translatable("northstar.gui.planet_and_dimension", getName(), dimension.formattedName());
    }

}

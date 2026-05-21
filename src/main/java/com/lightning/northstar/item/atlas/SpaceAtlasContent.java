package com.lightning.northstar.item.atlas;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.contraption.rocket.RocketDestination;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpaceAtlasContent {

    public final Map<ResourceLocation, AtlasPlanet> planets = new HashMap<>();
    public final Map<RocketDestination, Component> destinations = new LinkedHashMap<>();

    public static class AtlasPlanet {

        public final ResourceLocation id;
        public final List<AtlasReading> readings = new ArrayList<>();
        public float science;

        public AtlasPlanet(ResourceLocation id) {
            this.id = id;
        }

        public void recalculateScience(float weightExp) {
            readings.sort(Comparator.comparing(reading -> -reading.science));

            float science = 0;
            Object2IntMap<ResourceLocation> counts = new Object2IntOpenHashMap<>();
            for (AtlasReading reading : readings) {
                science += reading.science * (float) Math.pow(weightExp, counts.mergeInt(reading.origin, 0, (a, b) -> a + b + 1));
            }
            this.science = science;
        }

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("science", science);

            ListTag readings = new ListTag();
            for (AtlasReading reading : this.readings) {
                readings.add(reading.toTag());
            }
            tag.put("readings", readings);

            return tag;
        }

        public static AtlasPlanet fromTag(ResourceLocation id, CompoundTag tag) {
            AtlasPlanet planet = new AtlasPlanet(id);

            planet.science = tag.getFloat("science");
            for (Tag reading : tag.getList("readings", Tag.TAG_COMPOUND)) {
                planet.readings.add(AtlasReading.fromTag((CompoundTag) reading));
            }

            return planet;
        }
    }

    public static class AtlasReading {
        public ResourceLocation origin;
        public float science;
        public int day;

        public AtlasReading() {
        }

        public AtlasReading(ResourceLocation origin, float science, int day) {
            this.origin = origin;
            this.science = science;
            this.day = day;
        }

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("origin", origin.toString());
            tag.putFloat("science", science);
            tag.putInt("day", day);
            return tag;
        }

        public static AtlasReading fromTag(CompoundTag tag) {
            ResourceLocation origin = ResourceLocation.tryParse(tag.getString("origin"));
            if (origin == null) {
                origin = ResourceLocation.parse("invalid");
            }
            return new AtlasReading(origin, tag.getFloat("science"), tag.getInt("day"));
        }
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        toTag(tag);
        return tag;
    }

    public void toTag(CompoundTag tag) {
        CompoundTag atlas = new CompoundTag();

        CompoundTag planets = new CompoundTag();
        for (Map.Entry<ResourceLocation, AtlasPlanet> entry : this.planets.entrySet()) {
            planets.put(entry.getKey().toString(), entry.getValue().toTag());
        }
        atlas.put("planets", planets);

        ListTag destinations = new ListTag();
        for (Map.Entry<RocketDestination, Component> entry : this.destinations.entrySet()) {
            CompoundTag dest = entry.getKey().toTag();
            dest.put("label", ExtraCodecs.COMPONENT.encodeStart(NbtOps.INSTANCE, entry.getValue()).getOrThrow(false, Util.prefix("Failed to encode Component:", Northstar.LOGGER::error)));
            destinations.add(dest);
        }
        atlas.put("destinations", destinations);

        tag.put("atlas", atlas);
    }

    public static SpaceAtlasContent fromTag(CompoundTag tag) {
        SpaceAtlasContent content = new SpaceAtlasContent();
        CompoundTag atlas = tag.getCompound("atlas");

        CompoundTag planets = atlas.getCompound("planets");
        for (String key : planets.getAllKeys()) {
            ResourceLocation loc = ResourceLocation.tryParse(key);
            if (loc == null) {
                continue;
            }
            content.planets.put(loc, AtlasPlanet.fromTag(loc, planets.getCompound(key)));
        }

        ListTag destinations = atlas.getList("destinations", Tag.TAG_COMPOUND);
        for (Tag destinationTag : destinations) {
            RocketDestination destination = RocketDestination.fromTag((CompoundTag) destinationTag);
            if (destination != null && destination.pos() != null) {
                Component label = ExtraCodecs.COMPONENT.parse(NbtOps.INSTANCE, ((CompoundTag) destinationTag).get("label"))
                        .result()
                        .orElseGet(() -> getDefaultLabel(destination.pos(), destination.dir()));

                content.destinations.put(destination, label);
            }
        }

        return content;
    }

    @Contract("_, _ -> new")
    public static MutableComponent getDefaultLabel(BlockPos pos, Direction dir) {
        return Component.literal(
                RegistrateLangProvider.toEnglishName(dir.getSerializedName()) + " of " +
                LangNumberFormat.format(pos.getX()) + ", " +
                LangNumberFormat.format(pos.getY()) + ", " +
                LangNumberFormat.format(pos.getZ())
        );
    }

}

package com.lightning.northstar.data;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

import java.util.function.Function;
import java.util.stream.Stream;

public final class Tags<T, R> {

    private final RegistrateTagsProvider<T> provider;
    private final Function<T, ResourceLocation> locationExtractor;
    private final Function<R, T> mapping;

    public Tags(RegistrateTagsProvider<T> provider,
                Function<T, Holder.Reference<T>> referenceExtractor,
                Function<R, T> mapping) {
        this.provider = provider;
        this.locationExtractor = referenceExtractor
                .andThen(Holder.Reference::key)
                .andThen(ResourceKey::location);
        this.mapping = mapping;
    }

    public Appender<T, R> tag(TagKey<T> tag) {
        return new Appender<>(this, tag, provider.addTag(tag).getInternalBuilder());
    }

    public Appender<T, R> tag(Tag<T> tag) {
        return tag(tag.tag());
    }

    public record Appender<T, R>(Tags<T, R> tags, TagKey<T> key, TagBuilder builder) {

        public Appender<T, R> add(R value) {
            builder.addElement(tags.locationExtractor.apply(tags.mapping.apply(value)));
            return this;
        }

        public Appender<T, R> opt(R value) {
            builder.addOptionalElement(tags.locationExtractor.apply(tags.mapping.apply(value)));
            return this;
        }

        @SafeVarargs
        public final Appender<T, R> add(R... values) {
            Stream.of(values)
                    .map(tags.mapping)
                    .map(tags.locationExtractor)
                    .forEach(builder::addElement);
            return this;
        }

        @SafeVarargs
        public final Appender<T, R> opt(R... values) {
            Stream.of(values)
                    .map(tags.mapping)
                    .map(tags.locationExtractor)
                    .forEach(builder::addOptionalElement);
            return this;
        }

        public Appender<T, R> add(TagKey<T> value) {
            builder.addTag(value.location());
            return this;
        }

        public Appender<T, R> opt(TagKey<T> value) {
            builder.addOptionalTag(value.location());
            return this;
        }

        public Appender<T, R> add(Tag<T> value) {
            return add(value.tag());
        }

        public Appender<T, R> opt(Tag<T> value) {
            return opt(value.tag());
        }

        public Appender<T, R> add(Mod mod, String path) {
            builder.addElement(mod.loc(path));
            return this;
        }

        public Appender<T, R> opt(Mod mod, String path) {
            builder.addOptionalElement(mod.loc(path));
            return this;
        }

    }

    public interface Tag<T> {
        TagKey<T> tag();
    }

}

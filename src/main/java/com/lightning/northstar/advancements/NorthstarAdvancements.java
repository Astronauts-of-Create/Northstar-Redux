package com.lightning.northstar.advancements;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.lightning.northstar.advancements.predicate.OnGroundEntitySubPredicate;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.world.NorthstarDimensions;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static com.lightning.northstar.advancements.NorthstarAdvancement.TaskType.SILENT;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NorthstarAdvancements implements DataProvider {

    public static final List<NorthstarAdvancement> ENTRIES = new ArrayList<>();
    public static final NorthstarAdvancement
            $ = null,

    ROOT = create("root", b -> b
            .icon(NorthstarBlocks.TELESCOPE)
            .title("Welcome to Northstar!")
            .description("Shoot for the stars!")
            .awardedForFree()
            .special(SILENT)),

    ONE_SMALL_STEP = create("one_small_step", b -> b
            .icon(NorthstarBlocks.MOON_SAND)
            .title("One Small Step")
            .description("Set foot on the moon")
            .externalTrigger(PlayerTrigger.TriggerInstance.located(EntityPredicate.Builder.entity()
                    .located(LocationPredicate.inDimension(NorthstarDimensions.THE_MOON))
                    .subPredicate(OnGroundEntitySubPredicate.INSTANCE)
                    .build()))
            .after(ROOT)),

    ONE_GIANT_LEAP = create("one_giant_leap", b -> b
            .icon(NorthstarBlocks.MARS_SAND)
            .title("One Giant Leap")
            .description("Set foot on Mars")
            .externalTrigger(PlayerTrigger.TriggerInstance.located(EntityPredicate.Builder.entity()
                    .located(LocationPredicate.inDimension(NorthstarDimensions.MARS))
                    .subPredicate(OnGroundEntitySubPredicate.INSTANCE)
                    .build()))
            .after(ONE_SMALL_STEP));

    private static NorthstarAdvancement create(String id, UnaryOperator<NorthstarAdvancement.Builder> b) {
        return new NorthstarAdvancement(id, b);
    }

    // region DataGen

    private final PackOutput output;

    public NorthstarAdvancements(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        Path path = output.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = advancement -> {
            if (!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            }
            futures.add(DataProvider.saveStable(cache, advancement.deconstruct().serializeToJson(), getPath(path, advancement)));
        };

        for (NorthstarAdvancement advancement : ENTRIES) {
            advancement.save(consumer);
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static Path getPath(Path pathIn, Advancement advancementIn) {
        return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Northstar's Advancements";
    }

    public static void provideLangEntries(BiConsumer<String, String> consumer) {
        for (NorthstarAdvancement advancement : ENTRIES) {
            advancement.appendToLang(consumer);
        }
    }

    // endregion

    public static void register() {
    }

}

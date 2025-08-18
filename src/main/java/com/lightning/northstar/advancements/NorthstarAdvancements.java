package com.lightning.northstar.advancements;

import com.lightning.northstar.content.NorthstarBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static com.lightning.northstar.advancements.NorthstarAdvancement.TaskType.SILENT;

public class NorthstarAdvancements {

    public static final List<NorthstarAdvancement> ENTRIES = new ArrayList<>();
    public static final NorthstarAdvancement START = null,

    ROOT = create("root", b -> b.icon(NorthstarBlocks.TELESCOPE)
            .title("Welcome to Northstar!")
            .description("Shoot for the stars!")
            .awardedForFree()
            .special(SILENT)),

    ONE_SMALL_STEP = create("one_small_step", b -> b.icon(NorthstarBlocks.MOON_SAND)
            .title("One Small Step")
            .description("Set foot on the moon")
            .after(ROOT)),

    ONE_GIANT_LEAP = create("one_giant_leap", b -> b.icon(NorthstarBlocks.MARS_SAND)
            .title("One Giant Leap")
            .description("Set foot on Mars")
            .after(ONE_SMALL_STEP)),

    END = null;

    private static NorthstarAdvancement create(String id, UnaryOperator<NorthstarAdvancement.Builder> b) {
        return new NorthstarAdvancement(id, b);
    }

    public static void register() {
    }

}

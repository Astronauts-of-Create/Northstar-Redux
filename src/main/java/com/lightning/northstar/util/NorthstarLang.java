package com.lightning.northstar.util;

import com.lightning.northstar.Northstar;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;

public class NorthstarLang {

    public static final LangBuilder MB = CreateLang.translate("generic.unit.millibuckets");
    public static final LangBuilder MB_PER_TICK = translate("generic.unit.millibuckets_per_tick");

    public static LangBuilder builder() {
        return new LangBuilder(Northstar.MOD_ID);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

}

package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.api.data.datamap.DimensionInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.NotNull;


public class NorthstarDataMaps {
    public static final DataMapType<DimensionType, DimensionInfo> LEVEL_INFO = DataMapType.builder(
            Northstar.asResource("level_info"),
            Registries.DIMENSION_TYPE,
            DimensionInfo.CODEC
    ).build();

    public static void register(@NotNull RegisterDataMapTypesEvent event) {
        event.register(LEVEL_INFO);
    }
}

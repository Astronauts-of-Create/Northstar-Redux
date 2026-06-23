package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.world.planet.core.NorthstarVegetationConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class NorthstarTreeGrowers {

    public static final TreeGrower ARGYRE_SAPLING = new TreeGrower(
            Northstar.asResource("argyre_sapling").toString(),
            Optional.empty(),
            Optional.of(NorthstarVegetationConfiguredFeatures.ARGYRE),
            Optional.empty()
    );

    public static final TreeGrower COILER = new TreeGrower(
            Northstar.asResource("coiler").toString(),
            Optional.empty(),
            Optional.of(NorthstarVegetationConfiguredFeatures.COILER),
            Optional.empty()
    );

    public static final TreeGrower WILTER = new TreeGrower(
            Northstar.asResource("wilter").toString(),
            Optional.empty(),
            Optional.of(NorthstarVegetationConfiguredFeatures.WILTER),
            Optional.empty()
    );

}

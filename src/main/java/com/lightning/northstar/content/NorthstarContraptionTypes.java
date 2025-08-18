package com.lightning.northstar.content;

import com.lightning.northstar.contraptions.RocketContraption;
import com.simibubi.create.content.contraptions.ContraptionType;

public class NorthstarContraptionTypes {

    public static final ContraptionType ROCKET = ContraptionType.register("northstar:rocket", RocketContraption::new);

    public static void register() {
    }

}

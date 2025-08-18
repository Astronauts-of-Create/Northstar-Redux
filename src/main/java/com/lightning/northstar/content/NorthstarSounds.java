package com.lightning.northstar.content;

import com.lightning.northstar.Northstar;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NorthstarSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Northstar.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MARTIAN_DUST_STORM = registerSound("martian_dust_storm");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARTIAN_DUST_STORM_ABOVE = registerSound("martian_dust_storm_above");

    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_WORM_CLICK_NOTICE = registerSound("mars_worm_click_notice");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_WORM_CLICK = registerSound("mars_worm_click");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_WORM_HURT = registerSound("mars_worm_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_WORM_DEATH = registerSound("mars_worm_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_WORM_ATTACK = registerSound("mars_worm_attack");

    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_TOAD_HURT = registerSound("mars_toad_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_TOAD_DEATH = registerSound("mars_toad_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_TOAD_IDLE = registerSound("mars_toad_idle");

    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_COBRA_HURT = registerSound("mars_cobra_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_COBRA_DEATH = registerSound("mars_cobra_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_COBRA_IDLE = registerSound("mars_cobra_idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_COBRA_HISS = registerSound("mars_cobra_hiss");

    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_MOTH_HURT = registerSound("mars_moth_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_MOTH_DEATH = registerSound("mars_moth_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_MOTH_IDLE = registerSound("mars_moth_idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> MARS_MOTH_SNORE = registerSound("mars_moth_snore");

    public static final DeferredHolder<SoundEvent, SoundEvent> MOON_SNAIL_HURT = registerSound("moon_snail_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOON_SNAIL_DIE = registerSound("moon_snail_die");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOON_SNAIL_IDLE = registerSound("moon_snail_idle");

    public static final DeferredHolder<SoundEvent, SoundEvent> MOON_EEL_HURT = registerSound("moon_eel_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOON_EEL_DIE = registerSound("moon_eel_die");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOON_EEL_IDLE = registerSound("moon_eel_idle");

    public static final DeferredHolder<SoundEvent, SoundEvent> MOON_LUNARGRADE_HURT = registerSound("moon_lunargrade_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOON_LUNARGRADE_DIE = registerSound("moon_lunargrade_die");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOON_LUNARGRADE_IDLE = registerSound("moon_lunargrade_idle");


    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_STONE_BULL_HURT = registerSound("venus_stone_bull_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_STONE_BULL_DEATH = registerSound("venus_stone_bull_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_STONE_BULL_IDLE = registerSound("venus_stone_bull_idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_STONE_BULL_ATTACK = registerSound("venus_stone_bull_attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_STONE_BULL_CHARGE = registerSound("venus_stone_bull_charge");

    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_MIMIC_HURT = registerSound("venus_mimic_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_MIMIC_DEATH = registerSound("venus_mimic_die");
    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_MIMIC_IDLE = registerSound("venus_mimic_idle");

    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_SCORPION_HURT = registerSound("venus_scorpion_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_SCORPION_DEATH = registerSound("venus_scorpion_die");
    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_SCORPION_IDLE = registerSound("venus_scorpion_idle");

    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_VULTURE_HURT = registerSound("venus_vulture_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_VULTURE_IDLE = registerSound("venus_vulture_idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> VENUS_VULTURE_DIE = registerSound("venus_vulture_die");


    public static final DeferredHolder<SoundEvent, SoundEvent> MERCURY_RAPTOR_HURT = registerSound("mercury_raptor_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MERCURY_RAPTOR_DIE = registerSound("mercury_raptor_die");
    public static final DeferredHolder<SoundEvent, SoundEvent> MERCURY_RAPTOR_IDLE = registerSound("mercury_raptor_idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> MERCURY_RAPTOR_ATTACK = registerSound("mercury_raptor_attack");

    public static final DeferredHolder<SoundEvent, SoundEvent> MERCURY_ROACH_HURT = registerSound("mercury_roach_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MERCURY_ROACH_DIE = registerSound("mercury_roach_die");
    public static final DeferredHolder<SoundEvent, SoundEvent> MERCURY_ROACH_IDLE = registerSound("mercury_roach_idle");

    public static final DeferredHolder<SoundEvent, SoundEvent> MERCURY_TORTOISE_HURT = registerSound("mercury_tortoise_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MERCURY_TORTOISE_DIE = registerSound("mercury_tortoise_die");
    public static final DeferredHolder<SoundEvent, SoundEvent> MERCURY_TORTOISE_IDLE = registerSound("mercury_tortoise_idle");


    public static final DeferredHolder<SoundEvent, SoundEvent> LASER_AMBIENT = registerSound("laser_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> LASER_BURN = registerSound("laser_burn");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROCKET_TAKEOFF = registerSound("rocket_takeoff");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROCKET_BLAST = registerRangedSound("rocket_blast", 32f);
    public static final DeferredHolder<SoundEvent, SoundEvent> ROCKET_LANDING = registerRangedSound("rocket_landing", 32f);
    public static final DeferredHolder<SoundEvent, SoundEvent> AIRFLOW = registerSound("airflow");

    public static void register(IEventBus eventbus) {
        SOUNDS.register(eventbus);
    }

    public static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name) {
        ResourceLocation location = Northstar.asResource(name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(location));
    }

    public static DeferredHolder<SoundEvent, SoundEvent> registerRangedSound(String name, float range) {
        ResourceLocation location = Northstar.asResource(name);
        return SOUNDS.register(name, () -> SoundEvent.createFixedRangeEvent(location, range));
    }

}

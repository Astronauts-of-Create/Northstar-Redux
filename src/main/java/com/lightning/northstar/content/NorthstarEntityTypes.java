package com.lightning.northstar.content;

import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.contraption.rocket.RocketContraptionEntity;
import com.lightning.northstar.entity.*;
import com.lightning.northstar.entity.projectiles.LunargradeSpit;
import com.lightning.northstar.entity.projectiles.VenusScorpionSpit;
import com.lightning.northstar.entity.variants.FrozenZombieEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootTable;

import static com.lightning.northstar.Northstar.REGISTRATE;

// TODO: mobs don't have loot tables?
public class NorthstarEntityTypes {

    // region Venus

    public static final EntityEntry<VenusMimicEntity> VENUS_MIMIC = REGISTRATE
            .entity("venus_mimic", VenusMimicEntity::new, MobCategory.MONSTER)
            .properties(p -> p.sized(1.0f, 1.0f)
                    .fireImmune()
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    public static final EntityEntry<AegaeonCrawler> AEGAEON_CRAWLER = REGISTRATE
            .entity("aegaeon_crawler", AegaeonCrawler::new, MobCategory.MONSTER)
            .properties(p -> p.sized(0.8f, 1.5f)
                    .fireImmune()
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .register();



    public static final EntityEntry<VenusScorpionEntity> VENUS_SCORPION = REGISTRATE
            .entity("venus_scorpion", VenusScorpionEntity::new, MobCategory.MONSTER)
            .properties(p -> p.sized(0.8f, 1.5f)
                    .fireImmune()
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    public static final EntityEntry<VenusStoneBullEntity> VENUS_STONE_BULL = REGISTRATE
            .entity("venus_stone_bull", VenusStoneBullEntity::new, MobCategory.MONSTER)
            .properties(p -> p.sized(1.85f, 1.5f)
                    .fireImmune()
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    public static final EntityEntry<VenusVultureEntity> VENUS_VULTURE = REGISTRATE
            .entity("venus_vulture", VenusVultureEntity::new, MobCategory.CREATURE)
            .properties(p -> p.sized(0.8f, 0.9f)
                    .fireImmune()
                    .clientTrackingRange(16))
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    // endregion
    // region Mars

    public static final EntityEntry<MarsWormEntity> MARS_WORM = REGISTRATE
            .entity("mars_worm", MarsWormEntity::new, MobCategory.MONSTER)
            .lang("Mars Echo Worm")
            .properties(p -> p.sized(1.5f, 0.75f)
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.CAN_SURVIVE_COLD.tag)
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    public static final EntityEntry<MarsToadEntity> MARS_TOAD = REGISTRATE
            .entity("mars_toad", MarsToadEntity::new, MobCategory.MONSTER)
            .lang("Mars Root Toad")
            .properties(p -> p.sized(0.7f, 0.5f)
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.CAN_SURVIVE_COLD.tag)
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    public static final EntityEntry<MarsCobraEntity> MARS_COBRA = REGISTRATE
            .entity("mars_cobra", MarsCobraEntity::new, MobCategory.MONSTER)
            .properties(p -> p.sized(1f, 0.7f)
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.CAN_SURVIVE_COLD.tag)
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    public static final EntityEntry<MarsMothEntity> MARS_MOTH = REGISTRATE
            .entity("mars_moth", MarsMothEntity::new, MobCategory.MONSTER)
            .lang("Mars Devil Moth")
            .properties(p -> p.sized(0.8f, 0.9f)
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.CAN_SURVIVE_COLD.tag)
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    // endregion
    // region Moon

    public static final EntityEntry<MoonLunargradeEntity> MOON_LUNARGRADE = REGISTRATE
            .entity("moon_lunargrade", MoonLunargradeEntity::new, MobCategory.MONSTER)
            .properties(p -> p.sized(0.9f, 0.7f)
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.CAN_SURVIVE_COLD.tag)
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    public static final EntityEntry<MoonSnailEntity> MOON_SNAIL = REGISTRATE
            .entity("moon_snail", MoonSnailEntity::new, MobCategory.MONSTER)
            .properties(p -> p.sized(0.5f, 0.5f)
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.CAN_SURVIVE_COLD.tag)
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    public static final EntityEntry<MoonEelEntity> MOON_EEL = REGISTRATE
            .entity("moon_eel", MoonEelEntity::new, MobCategory.MONSTER)
            .properties(p -> p.sized(0.5f, 0.3f)
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.CAN_SURVIVE_COLD.tag)
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    public static final EntityEntry<FrozenZombieEntity> FROZEN_ZOMBIE = REGISTRATE
            .entity("frozen_zombie", FrozenZombieEntity::new, MobCategory.MONSTER)
            .properties(p -> p.sized(0.6f, 1.95f)
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.CAN_SURVIVE_COLD.tag)
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    // endregion
    // region Mercury

    public static final EntityEntry<MercuryRaptorEntity> MERCURY_RAPTOR = REGISTRATE
            .entity("mercury_raptor", MercuryRaptorEntity::new, MobCategory.MONSTER)
            .lang("Mercurian Raptor")
            .properties(p -> p.sized(0.7f, 1.4f)
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.CAN_SURVIVE_COLD.tag)
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    public static final EntityEntry<MercuryRoachEntity> MERCURY_ROACH = REGISTRATE
            .entity("mercury_roach", MercuryRoachEntity::new, MobCategory.MONSTER)
            .properties(p -> p.sized(0.6f, 0.5f)
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.CAN_SURVIVE_COLD.tag)
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    public static final EntityEntry<MercuryTortoiseEntity> MERCURY_TORTOISE = REGISTRATE
            .entity("mercury_tortoise", MercuryTortoiseEntity::new, MobCategory.MONSTER)
            .properties(p -> p.sized(1f, 0.9f)
                    .clientTrackingRange(8))
            .tag(NorthstarEntityTags.CAN_SURVIVE_COLD.tag)
            .tag(NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.tag)
            .loot((p, e) -> p.add(e, LootTable.lootTable()))
            .register();

    // endregion

    public static final EntityEntry<LunargradeSpit> LUNARGRADE_SPIT = REGISTRATE.
            <LunargradeSpit>entity("lunargrade_spit", LunargradeSpit::new, MobCategory.MISC)
            .properties(p -> p.sized(0.25f, 0.25f)
                    .clientTrackingRange(8))
            .register();

    public static final EntityEntry<VenusScorpionSpit> VENUS_SCORPION_SPIT = REGISTRATE
            .<VenusScorpionSpit>entity("venus_scorpion_spit", VenusScorpionSpit::new, MobCategory.MISC)
            .properties(p -> p.sized(0.25f, 0.25f)
                    .clientTrackingRange(8))
            .register();

    // contraptions

    public static final EntityEntry<RocketContraptionEntity> ROCKET_CONTRAPTION = REGISTRATE
            .entity("rocket_contraption", RocketContraptionEntity::new, MobCategory.MISC)
            .visual(() -> ContraptionVisual::new)
            .lang("Rocket")
            .properties(b -> b.setTrackingRange(200)
                    .setUpdateInterval(40)
                    .setShouldReceiveVelocityUpdates(false))
            .properties(AbstractContraptionEntity::build)
            .properties(EntityType.Builder::fireImmune)
            .renderer(() -> ContraptionEntityRenderer::new)
            .register();

    public static void register() {
    }

}

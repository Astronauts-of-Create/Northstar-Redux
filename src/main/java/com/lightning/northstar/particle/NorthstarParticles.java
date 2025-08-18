package com.lightning.northstar.particle;

import com.lightning.northstar.Northstar;
import com.simibubi.create.foundation.particle.ICustomParticleData;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;
import java.util.function.Supplier;

public enum NorthstarParticles {

    GLOWSTONE_PARTICLE(GlowstoneParticleData::new),
    ROCKET_FLAME(RocketFlameParticleData::new),
    ROCKET_FLAME_LANDING(RocketFlameLandingParticleData::new),
    ROCKET_SMOKE(RocketSmokeParticleData::new),
    ROCKET_SMOKE_LANDING(RocketSmokeLandingParticleData::new),
    COLD_AIR(ColdAirParticleData::new),
    OXY_FLOW(OxyFlowParticleData::new),
    SNOWFLAKE(SnowflakeParticleData::new),
    SNAIL_SLIME(SnailSlimeParticleData::new),
    SULFUR_POOF(SulfurPoofParticleData::new),
    DUST_CLOUD(DustCloudParticleData::new);

    private final ParticleEntry<?> entry;

    <D extends ParticleOptions> NorthstarParticles(Supplier<? extends ICustomParticleData<D>> typeFactory) {
        entry = new ParticleEntry<>(name().toLowerCase(Locale.ROOT), typeFactory);
    }

    public static void register(IEventBus modEventBus) {
        ParticleEntry.REGISTER.register(modEventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        for (NorthstarParticles particle : values())
            particle.entry.registerFactory(event);
    }

    public ParticleType<?> get() {
        return entry.object.get();
    }

    public String parameter() {
        return entry.name;
    }

    private static class ParticleEntry<D extends ParticleOptions> {
        private static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, Northstar.MOD_ID);

        private final String name;
        private final Supplier<? extends ICustomParticleData<D>> typeFactory;
        private final DeferredHolder<ParticleType<?>, ParticleType<D>> object;

        public ParticleEntry(String name, Supplier<? extends ICustomParticleData<D>> typeFactory) {
            this.name = name;
            this.typeFactory = typeFactory;
            object = REGISTER.register(name, () -> this.typeFactory.get().createType());
        }

        @OnlyIn(Dist.CLIENT)
        public void registerFactory(RegisterParticleProvidersEvent event) {
            typeFactory.get().register(object.get(), event);
        }

    }

}

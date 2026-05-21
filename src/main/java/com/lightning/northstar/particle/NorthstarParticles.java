package com.lightning.northstar.particle;

import com.lightning.northstar.Northstar;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NorthstarParticles {

    private static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Northstar.MOD_ID);
    private static final List<Consumer<RegisterParticleProvidersEvent>> FACTORIES = new ArrayList<>();

    public static final RegistryObject<SimpleParticleType>
            COLD_AIR = simpleSprite("cold_air", () -> ColdAirParticle::new),
            DUST_CLOUD = simpleSprite("dust_cloud", () -> DustCloudParticle::new),
            GLOWSTONE = simpleSprite("glowstone", () -> GlowstoneParticle::new),
            LEAK = simpleSprite("leak", () -> LeakParticle::new),
            OXY_FLOW = simpleSprite("oxy_flow", () -> OxyFlowParticle::new),
            ROCKET_PLUME = simpleSprite("rocket_plume", () -> RocketPlumeParticle::new),
            SNAIL_SLIME = simpleSprite("snail_slime", () -> SnailSlimeParticle::new),
            SNOWFLAKE = simpleSprite("snowflake", () -> SnowflakeParticle::new),
            SULFUR_POOF = simpleSprite("sulfur_poof", () -> SulfurPoofParticle::new);

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        for (Consumer<RegisterParticleProvidersEvent> factory : FACTORIES) {
            factory.accept(event);
        }
        FACTORIES.clear();
    }

    private static RegistryObject<SimpleParticleType> simpleSprite(String name, Supplier<SpriteParticleProvider<SimpleParticleType>> supplier) {
        RegistryObject<SimpleParticleType> entry = REGISTER.register(name, () -> new SimpleParticleType(false));
        FACTORIES.add(event -> {
            SpriteParticleProvider<SimpleParticleType> factory = supplier.get();
            event.registerSpriteSet(entry.get(), sprite -> (a, b, c, d, e, f, g, h) -> factory.createParticle(a, b, c, d, e, f, g, h, sprite));
        });
        return entry;
    }

    public static int getLight(int lightColor, float partialTick, int age, int lifetime) {
        float f = Mth.clamp((age + partialTick) / lifetime, 0.0F, 1.0F);
        int block = lightColor & 255;
        int sky = lightColor >> 16 & 255;
        block += (int) (f * 15 * 16);
        if (block > 240) {
            block = 240;
        }
        return block | sky << 16;
    }

    public interface SpriteParticleProvider<T extends ParticleOptions> {
        Particle createParticle(T type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet);
    }

}

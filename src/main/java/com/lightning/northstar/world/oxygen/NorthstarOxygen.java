package com.lightning.northstar.world.oxygen;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.oxygen_sealer.OxygenSealerBlockEntity;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarDataComponents;
import com.lightning.northstar.content.NorthstarTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.world.SealingProvider;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.lightning.northstar.world.sealer.ProgressiveBlockUpdater;
import com.lightning.northstar.world.sealer.SealingMode;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@EventBusSubscriber(modid = Northstar.MOD_ID)
public class NorthstarOxygen {

    /** Maximum oxygen for spacesuits, in mB; use is 1 mB/s, defaults to 30 minutes so 1.5 minecraft days */
    public static final int MAXIMUM_OXYGEN = 1800;

    private final Level level;
    private final Set<Provider> providers;
    private final ProgressiveBlockUpdater updater;

    public NorthstarOxygen(Level level) {
        this.level = level;
        this.providers = new HashSet<>();
        this.updater = new ProgressiveBlockUpdater(SealingMode.OXYGEN);
    }

    public boolean hasOxygen() {
        return NorthstarPlanets.getPlanetOxy(level.dimension());
    }

    public Provider getSealer(Vec3 pos) {
        for (Provider sealer : providers) {
            if (sealer.isSealed(pos)) {
                return sealer;
            }
        }
        return null;
    }

    public Provider getSealer(Vec3i pos) {
        for (Provider sealer : providers) {
            if (sealer.isSealed(pos)) {
                return sealer;
            }
        }
        return null;
    }

    public boolean hasOxygen(Vec3 pos) {
        return NorthstarPlanets.getPlanetOxy(level.dimension()) || getSealer(pos) != null;
    }

    public boolean hasOxygen(Vec3i pos) {
        return NorthstarPlanets.getPlanetOxy(level.dimension()) || getSealer(pos) != null;
    }

    public void registerSealer(Provider provider) {
        providers.add(provider);
    }

    public void unregisterSealer(Provider provider) {
        providers.remove(provider);
    }

    public void enqueueUpdates(LongCollection positions) {
        updater.queueUpdates(positions);
    }

    @ApiStatus.Internal
    public void processUpdates(ServerLevel level) {
        updater.processUpdates(level);
    }

    public interface Provider extends SealingProvider {
        void drainOxygen(float oxygen);
    }

    public static boolean isOxygen(Fluid fluid) {
        return NorthstarFluidTags.C_OXYGEN.matches(fluid) || NorthstarFluidTags.BREATHABLE.matches(fluid);
    }

    public static boolean hasOxygen(Level level, Vec3 pos) {
        return level.northstar$oxygen().hasOxygen(pos);
    }

    public static boolean hasOxygen(Level level, Vec3i pos) {
        return level.northstar$oxygen().hasOxygen(pos);
    }

    public static NorthstarOxygen getDimension(Level level) {
        return level.northstar$oxygen();
    }

    public static ItemStack getOxygenTank(LivingEntity entity) {
        for (ItemStack item : entity.getArmorSlots()) {
            if (item.is(NorthstarTags.NorthstarItemTags.OXYGEN_SOURCES.tag)) {
                return item;
            }
        }
        return ItemStack.EMPTY;
    }

    @SubscribeEvent
    public static void onBreathe(LivingBreatheEvent event) {
        LivingEntity entity = event.getEntity();
        Level world = entity.level();

        if (entity instanceof Player player && (player.isCreative() || player.isSpectator()))
            return;

        if (NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.matches(entity)) {
            event.setCanBreathe(true);
            event.setCanRefillAir(true);
            return;
        }

        NorthstarOxygen oxygen = world.northstar$oxygen();
        boolean atmosphereBreathable = oxygen.hasOxygen();
        if (atmosphereBreathable && event.canBreathe())
            return;

        Provider sealer = oxygen.getSealer(entity.getEyePosition());
        if (sealer != null) {
            event.setCanBreathe(true);
            event.setCanRefillAir(true);
            sealer.drainOxygen(NorthstarConfigs.server().oxygenSealerEntityActiveDrain.getF());
            return;
        }

        boolean isFullyCovered = true;
        ItemStack oxygenSource = ItemStack.EMPTY;
        for (ItemStack armor : entity.getArmorSlots()) {
            if (armor.isEmpty()) {
                isFullyCovered = false;
                break;
            }
            if (NorthstarItemTags.OXYGEN_SOURCES.matches(armor)) {
                oxygenSource = armor;
            }
        }

        if (isFullyCovered && !oxygenSource.isEmpty() && depleteOxygen(oxygenSource, world.getGameTime() % 20 == 0)) {
            event.setCanBreathe(true);
            event.setCanRefillAir(true);
        } else if (!atmosphereBreathable && world.getGameTime() % 10 != entity.getId() % 10) {
            entity.hurt(world.damageSources().northstar$suffocation(), 1);
        }
    }

    public static boolean depleteOxygen(ItemStack stack, boolean deplete) {
        if (!stack.has(NorthstarDataComponents.OXYGEN))
            return false;

        int oxygen = stack.get(NorthstarDataComponents.OXYGEN);
        if (oxygen <= 0)
            return false;

        if (deplete)
            stack.set(NorthstarDataComponents.OXYGEN, Math.min(oxygen - 1, MAXIMUM_OXYGEN));
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onPostRender(RenderLevelStageEvent event) {
        if (!NorthstarConfigs.client().debugSealerBounds.get())
            return;

        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS)) {
            PoseStack pose = event.getPoseStack();
            Vec3 pos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

            pose.pushPose();
            pose.translate(-pos.x, -pos.y, -pos.z);
            for (SealingProvider provider : getDimension(Minecraft.getInstance().level).providers) {
                if (provider instanceof OxygenSealerBlockEntity sealer)
                    sealer.getSealer().getVisualizer().render(pose, Minecraft.getInstance().renderBuffers().bufferSource());
            }
            pose.popPose();
        }
    }

}

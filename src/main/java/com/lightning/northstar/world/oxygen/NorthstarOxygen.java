package com.lightning.northstar.world.oxygen;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.block.tech.oxygen_sealer.OxygenSealerBlockEntity;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarFluidTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.world.SealingProvider;
import com.lightning.northstar.world.sealer.ProgressiveBlockUpdater;
import com.lightning.northstar.world.sealer.SealingMode;
import com.lightning.northstar.world.sealer.transform.TransformProviders;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllEnchantments;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = Northstar.MOD_ID, bus = Bus.FORGE)
public class NorthstarOxygen {

    private final Level level;
    private final Set<Provider> providers;
    private final ProgressiveBlockUpdater updater;

    public NorthstarOxygen(Level level) {
        this.level = level;
        this.providers = new HashSet<>();
        this.updater = new ProgressiveBlockUpdater(SealingMode.OXYGEN);
    }

    public boolean hasOxygen() {
        return isBreathable(level.northstar$dimension().atmosphere().fluid());
    }

    public Provider getSealer(Vec3 pos) {
        return getSealerDirect(TransformProviders.getToWorld().applyTransformOrIdentity(level, pos));
    }

    public Provider getSealer(Vec3i pos) {
        Vec3 transformed = TransformProviders.getToWorld().applyTransform(level, Vec3.atCenterOf(pos));
        if (transformed != null) {
            return getSealerDirect(transformed);
        }
        for (Provider sealer : providers) {
            if (sealer.isSealed(pos)) {
                return sealer;
            }
        }
        return null;
    }

    private Provider getSealerDirect(Vec3 pos) {
        for (Provider sealer : providers) {
            if (sealer.isSealed(pos)) {
                return sealer;
            }
        }
        return null;
    }

    public boolean hasOxygen(Vec3 pos) {
        return hasOxygen() || getSealer(pos) != null;
    }

    public boolean hasOxygen(Vec3i pos) {
        return hasOxygen() || getSealer(pos) != null;
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

    public static boolean isBreathable(Fluid fluid) {
        return NorthstarFluidTags.BREATHABLE.matches(fluid);
    }

    @Nullable
    public static Fluid findOxygenInStorage(IFluidHandler handler) {
        for (int i = 0; i < handler.getTanks(); i++) {
            Fluid fluid = handler.getFluidInTank(i).getFluid();
            if (isBreathable(fluid)) {
                return fluid;
            }
        }
        return null;
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
            if (item.is(NorthstarItemTags.OXYGEN_SOURCES.tag)) {
                return item;
            }
        }
        return ItemStack.EMPTY;
    }

    public static int getTankCapacity(ItemStack item) {
        return NorthstarConfigs.server().spacesuitBaseOxygen.get() +
               NorthstarConfigs.server().spacesuitAdditionalOxygen.get() * item.getEnchantmentLevel(AllEnchantments.CAPACITY.get());
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
            if (armor.isEmpty() || !NorthstarItemTags.OXYGEN_SEALING.matches(armor)) {
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
            DamageSource source = isFullyCovered ?
                    world.damageSources().northstar$suffocationNoOxygen() :
                    world.damageSources().northstar$suffocationNoSuit();
            entity.hurt(source, 1);
        }
    }

    public static boolean depleteOxygen(ItemStack stack, boolean deplete) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("Oxygen", CompoundTag.TAG_INT))
            return false;

        int oxygen = tag.getInt("Oxygen");
        if (oxygen <= 0)
            return false;

        if (deplete)
            tag.putInt("Oxygen", Math.min(oxygen - 1, getTankCapacity(stack)));
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

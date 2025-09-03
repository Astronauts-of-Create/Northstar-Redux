package com.lightning.northstar.world;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.accessor.NorthstarDamageSources;
import com.lightning.northstar.block.tech.oxygen_sealer.OxygenSealerBlockEntity;
import com.lightning.northstar.content.NorthstarTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarEntityTags;
import com.lightning.northstar.content.NorthstarTags.NorthstarItemTags;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.*;

@EventBusSubscriber(modid = Northstar.MOD_ID, bus = Bus.FORGE)
public class NorthstarOxygen {

    /** Maximum oxygen for spacesuits, in mB; use is 1 mB/s, defaults to 30 minutes so 1.5 minecraft days */
    public static final int MAXIMUM_OXYGEN = 1800;

    private final Level level;
    private final Set<OxygenSealerBlockEntity> sealers;

    public NorthstarOxygen(Level level) {
        this.level = level;
        this.sealers = new HashSet<>();
    }

    public boolean hasOxygen(Vec3 pos) {
        return hasOxygen(BlockPos.asLong(Mth.floor(pos.x), Mth.floor(pos.y), Mth.floor(pos.z)));
    }

    public boolean hasOxygen(Vec3i pos) {
        return hasOxygen(BlockPos.asLong(pos.getX(), pos.getY(), pos.getZ()));
    }

    private boolean hasOxygen(long pos) {
        if (NorthstarPlanets.getPlanetOxy(level.dimension()))
            return true;

        for (OxygenSealerBlockEntity sealer : sealers) {
            if (sealer.isActive() && sealer.getSealer().getSealedBlocks().contains(pos)) {
                return true;
            }
        }
        return false;
    }

    public Set<OxygenSealerBlockEntity> getSealers() {
        return sealers;
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

    public static ItemStack getOxy(LivingEntity entity) {
        for (ItemStack items : entity.getArmorSlots()) {
            if (items.is(NorthstarTags.NorthstarItemTags.OXYGEN_SOURCES.tag)) {
                return items;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void tickEntity(LivingEntity entity) {
        Level world = entity.level();

        // use the entity id to avoid ticking it all once to make different entities take damage at different times and minimize lag spikes
        if (world.getGameTime() % 20 != entity.getId() % 20)
            return;

        if (entity instanceof Player player && (player.isCreative() || player.isSpectator()))
            return;
        if (NorthstarEntityTags.DOESNT_REQUIRE_OXYGEN.matches(entity))
            return;
        if (hasOxygen(entity.level(), entity.getEyePosition()))
            return;

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

        if (!isFullyCovered || oxygenSource.isEmpty() || !depleteOxygen(oxygenSource)) {
            // TODO: figure out why loom is having a stroke on that particular class
            entity.hurt(((NorthstarDamageSources) world.damageSources()).northstar$suffocation(), 1);
        }
    }

    public static boolean depleteOxygen(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("Oxygen", CompoundTag.TAG_INT))
            return false;

        int oxygen = tag.getInt("Oxygen");
        if (oxygen <= 0)
            return false;

        tag.putInt("Oxygen", Math.min(oxygen - 1, MAXIMUM_OXYGEN));
        return true;
    }

    @SubscribeEvent
    public static void onPostRender(RenderLevelStageEvent event) {
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS)) {
            PoseStack pose = event.getPoseStack();

            Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 pos = camera.getPosition();

            pose.pushPose();
            pose.translate(-pos.x, -pos.y, -pos.z);
            for (OxygenSealerBlockEntity sealer : getDimension(Minecraft.getInstance().level).sealers) {
                sealer.getSealer().getVisualizer().render(pose, Minecraft.getInstance().renderBuffers().bufferSource());
            }
            pose.popPose();
        }
    }

}

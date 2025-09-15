package com.lightning.northstar.block.tech.rocket_controls;

import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.contraptions.RocketContraptionEntity;
import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.foundation.utility.ControlsUtil;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

public class RocketControlsClientHandler {

    public static Collection<Integer> currentlyPressed = new HashSet<>();

    public static int PACKET_RATE = 5;
    private static int packetCooldown;
    private static int displaytime = 0;
    private static int launchTime = 0;

    private static WeakReference<RocketContraptionEntity> entityRef = new WeakReference<>(null);
    private static BlockPos controlsPos;

    public static void levelUnloaded(LevelAccessor level) {
        packetCooldown = 0;
        entityRef = new WeakReference<>(null);
        controlsPos = null;
        currentlyPressed.clear();
    }

    public static void startControlling(RocketContraptionEntity rce, BlockPos controllerLocalPos) {
        entityRef = new WeakReference<>(rce);
        controlsPos = controllerLocalPos;
        displaytime = 0;

        if (rce != null && rce.isLaunchingOrLanding()) {
            Minecraft.getInstance().player.displayClientMessage(
                    CreateLang.translateDirect("contraption.controls.start_controlling", rce.getContraptionName()), true);
        } else {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable("northstar.contraption.controls.rocket_tut").withStyle(ChatFormatting.AQUA), true);
        }
    }

    public static void stopControlling() {
        ControlsUtil.getControls()
                .forEach(kb -> kb.setDown(ControlsUtil.isActuallyPressed(kb)));
        RocketContraptionEntity contrapEntity = entityRef.get();

        if (!currentlyPressed.isEmpty() && contrapEntity != null)
            NorthstarPackets.getChannel().sendToServer(new RocketControlsInputPacket(currentlyPressed, false,
                    contrapEntity.getId(), controlsPos, true));

        packetCooldown = 0;
        entityRef = new WeakReference<>(null);
        controlsPos = null;
        currentlyPressed.clear();

        Minecraft.getInstance().player.displayClientMessage(CreateLang.translateDirect("contraption.controls.stop_controlling"),
                true);
    }

    public static void tick() {
        //rce is the rocket that is being controlled, if its null, do nothing
        RocketContraptionEntity rce = entityRef.get();
        LocalPlayer player = Minecraft.getInstance().player;
        if (rce == null || player == null) return;
        if (displaytime < 61) displaytime++;


        //Server side communicates with client side,
        //The server handles when the launch happens, the client rocket is only the display puppet of the server side rocket
        if (rce.isActiveLaunch()) {
            //Sync our visual launch time with the clients side (We dont want the number to fluctuate too much)
            if (Math.abs(rce.getLaunchTime() - launchTime) > 10) launchTime = rce.getLaunchTime();
            if (launchTime % 20 == 0) {
                player.displayClientMessage(Component.literal("T-" + (launchTime / 20)).withStyle(ChatFormatting.AQUA), true);
                player.level().playSound(player, player.blockPosition(), SoundEvents.NOTE_BLOCK_PLING.get(), SoundSource.BLOCKS, 10, launchTime / 20 == 0 ? 10 : 1);
            }
            launchTime--;
        }

        if (rce.landingMode && rce.getY() < rce.getSlowdownHeightThreshold()) {
            if (rce.getControllingPlayer().isPresent()) {
                if (rce.getControllingPlayer().get() == player.getUUID()) {
                    if (rce.auto_land_mode) {
                        player.displayClientMessage(Component.translatable("northstar.contraption.controls.landing_notification").withStyle(ChatFormatting.RED), true);
                    } else {
                        player.displayClientMessage(Component.translatable("northstar.contraption.controls.landing_warning").withStyle(ChatFormatting.RED), true);
                    }
                }
            }
        }

        if (packetCooldown > 0)
            packetCooldown--;

        if (rce.isRemoved() || InputConstants.isKeyDown(Minecraft.getInstance()
                .getWindow()
                .getWindow(), GLFW.GLFW_KEY_ESCAPE)) {
            BlockPos pos = controlsPos;
            stopControlling();
            NorthstarPackets.getChannel()
                    .sendToServer(new RocketControlsInputPacket(currentlyPressed, false, rce.getId(), pos, true));
            return;
        }

        Vector<KeyMapping> controls = ControlsUtil.getControls();
        Collection<Integer> pressedKeys = new HashSet<>();
        for (int i = 0; i < controls.size(); i++) {
            if (ControlsUtil.isActuallyPressed(controls.get(i)))
                pressedKeys.add(i);
        }
        rce.clientControl(controlsPos, pressedKeys, player);


        Collection<Integer> newKeys = new HashSet<>(pressedKeys);
        Collection<Integer> releasedKeys = currentlyPressed;
        newKeys.removeAll(releasedKeys);
        releasedKeys.removeAll(pressedKeys);


        // Released Keys
        if (!releasedKeys.isEmpty()) {
            NorthstarPackets.getChannel()
                    .sendToServer(new RocketControlsInputPacket(releasedKeys, false, rce.getId(), controlsPos, false));
    // AllSoundEvents.CONTROLLER_CLICK.playAt(player.level, player.blockPosition(), 1f, .5f, true);
        }

        // Newly Pressed Keys
        if (!newKeys.isEmpty()) {
            NorthstarPackets.getChannel().sendToServer(new RocketControlsInputPacket(newKeys, true, rce.getId(), controlsPos, false));
            packetCooldown = PACKET_RATE;
    // AllSoundEvents.CONTROLLER_CLICK.playAt(player.level, player.blockPosition(), 1f, .75f, true);
        }

        // Keepalive Pressed Keys
        if (packetCooldown == 0) {
            if (!pressedKeys.isEmpty()) {
                NorthstarPackets.getChannel()
                        .sendToServer(new RocketControlsInputPacket(pressedKeys, true, rce.getId(), controlsPos, false));
                packetCooldown = PACKET_RATE;
            }
        }
        if (!currentlyPressed.isEmpty()) {
            if (currentlyPressed.contains(4)) {
                //Start Controlling
            }
            if (currentlyPressed.contains(5)) {
                //Stop Controlling
                launchTime = -20;//So we dont get a double message
                stopControlling();
            }
        }

        currentlyPressed = pressedKeys;
        controls.forEach(kb -> kb.setDown(false));
    }

    @Nullable
    public static RocketContraptionEntity getContraption() {
        return entityRef.get();
    }

    @Nullable
    public static BlockPos getControlsPos() {
        return controlsPos;
    }
}

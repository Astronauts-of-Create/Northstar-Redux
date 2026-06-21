package com.lightning.northstar.mixin.entity;

import com.lightning.northstar.accessor.NorthstarPlayer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

@Mixin(LocalPlayer.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LocalPlayerMixin implements NorthstarPlayer {

    @Override
    public void northstar$showTitle(Component title, Component subtitle, int fadeInTime, int displayTime, int fadeOutTime) {
        Gui gui = Minecraft.getInstance().gui;
        gui.setTimes(fadeInTime, displayTime, fadeOutTime);
        gui.setSubtitle(subtitle);
        gui.setTitle(title);
    }

}

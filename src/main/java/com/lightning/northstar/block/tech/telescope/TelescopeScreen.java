package com.lightning.northstar.block.tech.telescope;

import com.google.common.collect.Lists;
import com.lightning.northstar.Northstar;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.lightning.northstar.world.dimension.NorthstarPlanets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;

public class TelescopeScreen extends AbstractSimiContainerScreen<TelescopeMenu> {

    private static final ResourceLocation TELESCOPE_TEXTURE = Northstar.asResource("textures/gui/telescope_gui.png");
    private static final ResourceLocation TELESCOPE_TEXTURE_SIDE = Northstar.asResource("textures/gui/telescope_gui_side.png");
    private static final ResourceLocation MERCURY = Northstar.asResource("textures/environment/mercury_far.png");
    private static final ResourceLocation VENUS = Northstar.asResource("textures/environment/venus_far.png");
    private static final ResourceLocation EARTH = Northstar.asResource("textures/environment/earth_far.png");
    private static final ResourceLocation MOON = Northstar.asResource("textures/environment/moon_far.png");
    private static final ResourceLocation MARS = Northstar.asResource("textures/environment/mars_far.png");
    private static final ResourceLocation PHOBOS_DEIMOS = Northstar.asResource("textures/environment/phobos_and_deimos_far.png");
    private static final ResourceLocation CERES = Northstar.asResource("textures/environment/ceres_far.png");
    private static final ResourceLocation JUPITER = Northstar.asResource("textures/environment/jupiter_far.png");
    private static final ResourceLocation SATURN = Northstar.asResource("textures/environment/saturn_far.png");
    private static final ResourceLocation URANUS = Northstar.asResource("textures/environment/uranus_far.png");
    private static final ResourceLocation NEPTUNE = Northstar.asResource("textures/environment/neptune_far.png");
    private static final ResourceLocation PLUTO = Northstar.asResource("textures/environment/pluto_far.png");
    private static final ResourceLocation ERIS = Northstar.asResource("textures/environment/eris_far.png");
    private static final ResourceLocation BACKGROUND = Northstar.asResource("textures/environment/space_background.png");
    private static final ResourceLocation MOON_GLOW = ResourceLocation.parse("textures/environment/moon_phases.png");
    private static final ResourceLocation MOON_FLAT = Northstar.asResource("textures/environment/moon_flat.png");

    private boolean isScrolling;
    private double scrollX = 450;
    private double scrollY = 450;

    private Inventory inv;
    public String selectedPlanet = null;

    public TelescopeScreen(TelescopeMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.inv = inv;

        imageWidth = 300;
        imageHeight = 300;
        titleLabelX = 150;
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        IconButton printButton = new IconButton(x - 33, y + 200, AllIcons.I_ADD);
        printButton.withCallback(() -> {
            if (selectedPlanet != null) {
                NorthstarPackets.getChannel().sendToServer(TelescopePrintPacket.print(menu.contentHolder.getBlockPos(), selectedPlanet));
                //Northstar.LOGGER.debug("WE'VE BEEN CLICKED, SCATTER!!!!!");
            }
        });

        printButton.active = paperCheck();
        printButton.setToolTip(Component.translatable("northstar.gui.telescope.button_tooltip"));
        addRenderableWidget(printButton);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int pMouseX, int pMouseY) {
        graphics.drawString(font, title, titleLabelX - font.width(title) / 2, titleLabelY, 0x404040, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.enableScissor(x + 3, y + 3, x + imageWidth - 3, y + imageHeight - 3);
        graphics.blit(BACKGROUND, x, y, (int) -scrollX, (int) -scrollY, imageWidth, imageHeight, 900, 900);
        renderPlanets(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();

        graphics.blit(TELESCOPE_TEXTURE_SIDE, x - 174, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        graphics.blit(TELESCOPE_TEXTURE, x, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        renderTooltip(graphics, mouseX, mouseY);
        renderPlanetTooltips(graphics, mouseX, mouseY);
        renderSelectedPlanet(graphics);
    }

    public void renderPlanets(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        PoseStack pose = graphics.pose();

        ResourceKey<Level> player_dim = Minecraft.getInstance().player.level().dimension();
        if (player_dim != NorthstarDimensions.MARS_DIM_KEY) {
            int mars_x = (int) NorthstarPlanets.mars_x;
            int mars_y = (int) NorthstarPlanets.mars_y;
            pose.pushPose();
            pose.scale(0.05F, 0.05F, 0.05F);
            graphics.blit(MARS, (mars_x * 20) + (int) scrollX * 20, (mars_y * 20) + (int) scrollY * 20, 0, 0, 255, 255);
            pose.popPose();

            int pd_x = (int) NorthstarPlanets.pd_x;
            int pd_y = (int) NorthstarPlanets.pd_y;
            pose.pushPose();
            pose.scale(0.05F, 0.05F, 0.05F);
            graphics.blit(PHOBOS_DEIMOS, (pd_x * 20) + (int) scrollX * 20, (pd_y * 20) + (int) scrollY * 20, 0, 0, 255, 255);
            pose.popPose();
        }

        if (player_dim != NorthstarDimensions.VENUS_DIM_KEY) {
            int venus_x = (int) NorthstarPlanets.venus_x;
            int venus_y = (int) NorthstarPlanets.venus_y;
            pose.pushPose();
            pose.scale(0.05F, 0.05F, 0.05F);
            graphics.blit(VENUS, ((venus_x * 20) + (int) scrollX * 20), ((venus_y * 20) + (int) scrollY * 20), 0, 0, 255, 255);
            pose.popPose();
        }

        if (player_dim != NorthstarDimensions.MERCURY_DIM_KEY) {
            int mercury_x = (int) NorthstarPlanets.mercury_x;
            int mercury_y = (int) NorthstarPlanets.mercury_y;
            pose.pushPose();
            pose.scale(0.05F, 0.05F, 0.05F);
            graphics.blit(MERCURY, (mercury_x * 20) + (int) scrollX * 20, (mercury_y * 20) + (int) scrollY * 20, 0, 0, 255, 255);
            pose.popPose();
        }

        int jupiter_x = (int) NorthstarPlanets.jupiter_x;
        int jupiter_y = (int) NorthstarPlanets.jupiter_y;
        pose.pushPose();
        pose.scale(0.05F, 0.05F, 0.05F);
        graphics.blit(JUPITER, (jupiter_x * 20) + (int) scrollX * 20, (jupiter_y * 20) + (int) scrollY * 20, 0, 0, 255, 255);
        pose.popPose();

        int saturn_x = (int) NorthstarPlanets.saturn_x;
        int saturn_y = (int) NorthstarPlanets.saturn_y;
        pose.pushPose();
        pose.scale(0.05F, 0.05F, 0.05F);
        graphics.blit(SATURN, (saturn_x * 20) + (int) scrollX * 20, (saturn_y * 20) + (int) scrollY * 20, 0, 0, 255, 255);
        pose.popPose();

        int uranus_x = (int) NorthstarPlanets.uranus_x;
        int uranus_y = (int) NorthstarPlanets.uranus_y;
        pose.pushPose();
        pose.scale(0.05F, 0.05F, 0.05F);
        graphics.blit(URANUS, (uranus_x * 20) + (int) scrollX * 20, (uranus_y * 20) + (int) scrollY * 20, 0, 0, 255, 255);
        pose.popPose();

        int neptune_x = (int) NorthstarPlanets.neptune_x;
        int neptune_y = (int) NorthstarPlanets.neptune_y;
        pose.pushPose();
        pose.scale(0.05F, 0.05F, 0.05F);
        graphics.blit(NEPTUNE, (neptune_x * 20) + (int) scrollX * 20, (neptune_y * 20) + (int) scrollY * 20, 0, 0, 255, 255);
        pose.popPose();

        int pluto_x = (int) NorthstarPlanets.pluto_x;
        int pluto_y = (int) NorthstarPlanets.pluto_x;
        pose.pushPose();
        pose.scale(0.05F, 0.05F, 0.05F);
        graphics.blit(PLUTO, (pluto_x * 20) + (int) scrollX * 20, (pluto_y * 20) + (int) scrollY * 20, 0, 0, 255, 255);
        pose.popPose();

        int eris_x = (int) NorthstarPlanets.eris_x;
        int eris_y = (int) NorthstarPlanets.eris_y;
        pose.pushPose();
        pose.scale(0.05F, 0.05F, 0.05F);
        graphics.blit(ERIS, (eris_x * 20) + (int) scrollX * 20, (eris_y * 20) + (int) scrollY * 20, 0, 0, 255, 255);
        pose.popPose();

        if (player_dim != ClientLevel.OVERWORLD && player_dim != NorthstarDimensions.MOON_DIM_KEY) {
            int earth_x = (int) NorthstarPlanets.earth_x;
            int earth_y = (int) NorthstarPlanets.earth_y;
            pose.pushPose();
            pose.scale(0.05F, 0.05F, 0.05F);
            graphics.blit(EARTH, (earth_x * 20) + (int) scrollX * 20, (earth_y * 20) + (int) scrollY * 20, 0, 0, 255, 255);
            pose.popPose();
            int moon_x = (int) NorthstarPlanets.moon_x;
            int moon_y = (int) NorthstarPlanets.moon_y;
            pose.pushPose();
            pose.scale(0.05F, 0.05F, 0.05F);
            graphics.blit(MOON, (moon_x * 20) + (int) scrollX * 20, (moon_y * 20) + (int) scrollY * 20, 0, 0, 255, 255);
            pose.popPose();
        }


        if (player_dim == ClientLevel.OVERWORLD) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            pose.pushPose();
            pose.scale(2F, 1F, 1F);
            int moon_phase = Minecraft.getInstance().level.getMoonPhase();
            int moon_uv_x = (moon_phase % 4) * 64;
            int moon_uv_y = (moon_phase / 4) * 128;
            graphics.blit(MOON_GLOW, ((int) NorthstarPlanets.earth_moon_x + (int) scrollX / 2) - 27, ((int) NorthstarPlanets.earth_moon_y + (int) scrollY) - 57, 0 + moon_uv_x, 0 + moon_uv_y, 64, 128);
            pose.popPose();

            pose.pushPose();
            RenderSystem.disableBlend();
            pose.scale(2F, 1F, 1F);
            graphics.blit(MOON_FLAT, ((int) NorthstarPlanets.earth_moon_x + (int) scrollX / 2) - 27, ((int) NorthstarPlanets.earth_moon_y + (int) scrollY) - 57, 0 + moon_uv_x, 0 + moon_uv_y, 64, 128);
            pose.popPose();
        }
    }

    public boolean paperCheck() {
        if (inv.player.isCreative()) {
            return true;
        }

        boolean flag = false;

        if (inv != null) {
            for (int p = 0; p < 36; p++) {
                ItemStack items = inv.getItem(p);
                Item item = items.getItem();
                if (item == Items.PAPER) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public void renderSelectedPlanet(GuiGraphics graphics) {
        if (selectedPlanet != null) {
            int x = (width - imageWidth) / 2;
            int y = (height - imageHeight) / 2;

            graphics.blit(getPlanetSprite(selectedPlanet), x - 40, y + 93, 0, 0, 35, 35, 35, 35);
            graphics.drawString(font, getPlanetName(selectedPlanet), x - 45, y + 130, 6944, false);
            graphics.drawString(font, "X: " + (int) NorthstarPlanets.getPlanetX(selectedPlanet), x - 45, y + 140, 6944, false);
            graphics.drawString(font, "Y: " + (int) NorthstarPlanets.getPlanetY(selectedPlanet), x - 45, y + 150, 6944, false);
        }
    }

    public void renderPlanetTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        int x = ((width - (imageWidth + (imageWidth / 2))) / 2);
        int y = (height - (imageHeight + (imageHeight / 2))) / 2;

        ResourceKey<Level> player_dim = Minecraft.getInstance().player.level().dimension();
        if ((Math.abs(NorthstarPlanets.mars_x + scrollX + 8 - mouseX) < 8 && Math.abs(NorthstarPlanets.mars_y + scrollY + 8 - mouseY) < 8) && player_dim != NorthstarDimensions.MARS_DIM_KEY) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.mars.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.mars.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.mars.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.mars.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.mars.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.mars_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.mars_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if ((Math.abs(NorthstarPlanets.earth_x + scrollX + 8 - mouseX) < 8 && Math.abs(NorthstarPlanets.earth_y + scrollY + 8 - mouseY) < 8) && player_dim != ClientLevel.OVERWORLD) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.earth.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.earth.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.earth.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.earth.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.earth.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.earth_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.earth_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if ((Math.abs(NorthstarPlanets.moon_x + scrollX + 8 - mouseX) < 8 && Math.abs(NorthstarPlanets.moon_y + scrollY + 8 - mouseY) < 8) && player_dim != ClientLevel.OVERWORLD && player_dim != NorthstarDimensions.MOON_DIM_KEY) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.moon.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.moon.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.moon.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.moon.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.moon.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.moon_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.moon_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if ((Math.abs((NorthstarPlanets.pd_x) + scrollX + 5 - mouseX) < 5 && Math.abs((NorthstarPlanets.pd_y) + scrollY + 5 - mouseY) < 5) && player_dim != NorthstarDimensions.MARS_DIM_KEY) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.phobos_deimos.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.phobos_deimos.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.phobos_deimos.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.phobos_deimos.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.phobos_deimos.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.pd_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.pd_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if ((Math.abs((NorthstarPlanets.venus_x) + scrollX + 8 - mouseX) < 8 && Math.abs((NorthstarPlanets.venus_y) + scrollY + 8 - mouseY) < 8) && player_dim != NorthstarDimensions.VENUS_DIM_KEY) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.venus.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.venus.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.venus.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.venus.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.venus.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.venus_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.venus_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if ((Math.abs(NorthstarPlanets.mercury_x + scrollX + 7 - mouseX) < 8 && Math.abs(NorthstarPlanets.mercury_y + scrollY + 7 - mouseY) < 8) && player_dim != NorthstarDimensions.MERCURY_DIM_KEY) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.mercury.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.mercury.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.mercury.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.mercury.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.mercury.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.mercury_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.mercury_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if ((Math.abs(NorthstarPlanets.earth_moon_x + scrollX - mouseX) < 24 && Math.abs(NorthstarPlanets.earth_moon_y + scrollY - mouseY) < 24) && player_dim == ClientLevel.OVERWORLD && player_dim != NorthstarDimensions.MOON_DIM_KEY) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.moon.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.moon.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.moon.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.moon.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.moon.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.earth_moon_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.earth_moon_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if ((Math.abs(NorthstarPlanets.ceres_x + scrollX + 6 - mouseX) < 6 && Math.abs(NorthstarPlanets.ceres_y + scrollY + 6 - mouseY) < 6)) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.ceres.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.ceres.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.ceres.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.ceres.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.ceres.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.ceres_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.ceres_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if (Math.abs((NorthstarPlanets.jupiter_x) + scrollX + 12 - mouseX) < 12 && Math.abs((NorthstarPlanets.jupiter_y) + scrollY + 12 - mouseY) < 12) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.jupiter.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.jupiter.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.jupiter.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.jupiter.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.jupiter.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.jupiter_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.jupiter_x)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if (Math.abs((NorthstarPlanets.saturn_x) + scrollX + 8 - mouseX) < 8 && Math.abs((NorthstarPlanets.saturn_y) + scrollY + 8 - mouseY) < 8) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.saturn.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.saturn.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.saturn.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.saturn.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.saturn.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.saturn_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.saturn_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if (Math.abs((NorthstarPlanets.uranus_x) + scrollX + 8 - mouseX) < 8 && Math.abs((NorthstarPlanets.uranus_y) + scrollY + 8 - mouseY) < 8) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.uranus.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.uranus.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.uranus.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.uranus.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.uranus.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.uranus_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.uranus_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if (Math.abs((NorthstarPlanets.neptune_x) + scrollX + 8 - mouseX) < 8 && Math.abs((NorthstarPlanets.neptune_y) + scrollY + 8 - mouseY) < 8) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.neptune.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.neptune.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.neptune.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.neptune.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.neptune.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.neptune_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.neptune_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if (Math.abs((NorthstarPlanets.pluto_x) + scrollX + 6 - mouseX) < 6 && Math.abs((NorthstarPlanets.pluto_y) + scrollY + 6 - mouseY) < 6) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.pluto.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.pluto.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.pluto.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.pluto.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.pluto.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + String.valueOf((int) NorthstarPlanets.pluto_x)).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + String.valueOf((int) NorthstarPlanets.pluto_y)).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        } else if (Math.abs((NorthstarPlanets.eris_x) + scrollX + 6 - mouseX) < 6 && Math.abs((NorthstarPlanets.eris_y) + scrollY + 6 - mouseY) < 6) {
            List<Component> list = Lists.newArrayList();
            RenderSystem.colorMask(true, true, true, true);
            list.add((Component.translatable("planets.eris.name").withStyle(ChatFormatting.AQUA)));
            list.add((Component.translatable("planets.eris.type").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.eris.grav").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.eris.temp").withStyle(ChatFormatting.GRAY)));
            list.add((Component.translatable("planets.eris.atmosphere").withStyle(ChatFormatting.GRAY)));
            list.add((Component.literal("X:  " + (int) NorthstarPlanets.eris_x).withStyle(ChatFormatting.WHITE)));
            list.add((Component.literal("Y:  " + (int) NorthstarPlanets.eris_y).withStyle(ChatFormatting.WHITE)));

            graphics.renderComponentTooltip(font, list, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            }
            scroll(pDragX, pDragY);
            return true;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
        if (pButton == 0 || pButton == 1) {
            if (Math.abs(NorthstarPlanets.mercury_x + scrollX + 7 - mouseX) < 8 && Math.abs(NorthstarPlanets.mercury_y + scrollY + 7 - mouseY) < 8 && Minecraft.getInstance().level.dimension() != NorthstarDimensions.MERCURY_DIM_KEY) {
                selectedPlanet = "mercury";
            }
            if (Math.abs((NorthstarPlanets.venus_x) + scrollX + 8 - mouseX) < 8 && Math.abs((NorthstarPlanets.venus_y) + scrollY + 8 - mouseY) < 8 && Minecraft.getInstance().level.dimension() != NorthstarDimensions.VENUS_DIM_KEY) {
                selectedPlanet = "venus";
            }
            if (Math.abs(NorthstarPlanets.earth_x + scrollX + 8 - mouseX) < 8 && Math.abs(NorthstarPlanets.earth_y + scrollY + 8 - mouseY) < 8 && Minecraft.getInstance().level.dimension() != Level.OVERWORLD) {
                selectedPlanet = "earth";
            }
            if ((Math.abs(NorthstarPlanets.earth_moon_x + scrollX - mouseX) < 18 && Math.abs(NorthstarPlanets.earth_moon_y + scrollY - mouseY) < 18) && Minecraft.getInstance().level.dimension() == Level.OVERWORLD && Minecraft.getInstance().level.dimension() != NorthstarDimensions.MOON_DIM_KEY) {
                selectedPlanet = "earth_moon";
            }
            if (Math.abs(NorthstarPlanets.moon_x + scrollX + 8 - mouseX) < 8 && Math.abs(NorthstarPlanets.moon_y + scrollY + 8 - mouseY) < 8 && Minecraft.getInstance().level.dimension() != Level.OVERWORLD && Minecraft.getInstance().level.dimension() != NorthstarDimensions.MOON_DIM_KEY) {
                selectedPlanet = "moon";
            }
            if (Math.abs(NorthstarPlanets.mars_x + scrollX + 8 - mouseX) < 8 && Math.abs(NorthstarPlanets.mars_y + scrollY + 8 - mouseY) < 8 && Minecraft.getInstance().level.dimension() != NorthstarDimensions.MARS_DIM_KEY) {
                selectedPlanet = "mars";
            }
            if (Math.abs(NorthstarPlanets.ceres_x + scrollX + 8 - mouseX) < 8 && Math.abs(NorthstarPlanets.ceres_y + scrollY + 8 - mouseY) < 8) {
                selectedPlanet = "ceres";
            }
            if (Math.abs(NorthstarPlanets.jupiter_x + scrollX + 8 - mouseX) < 8 && Math.abs(NorthstarPlanets.jupiter_y + scrollY + 8 - mouseY) < 8) {
                selectedPlanet = "jupiter";
            }
            if (Math.abs((NorthstarPlanets.saturn_x) + scrollX + 8 - mouseX) < 8 && Math.abs((NorthstarPlanets.saturn_y) + scrollY + 8 - mouseY) < 8) {
                selectedPlanet = "saturn";
            }
            if (Math.abs(NorthstarPlanets.uranus_x + scrollX + 7 - mouseX) < 8 && Math.abs(NorthstarPlanets.uranus_y + scrollY + 7 - mouseY) < 8) {
                selectedPlanet = "uranus";
            }
            if (Math.abs(NorthstarPlanets.neptune_x + scrollX + 7 - mouseX) < 8 && Math.abs(NorthstarPlanets.neptune_y + scrollY + 7 - mouseY) < 8) {
                selectedPlanet = "neptune";
            }
            if (Math.abs(NorthstarPlanets.pluto_x + scrollX + 7 - mouseX) < 8 && Math.abs(NorthstarPlanets.pluto_y + scrollY + 7 - mouseY) < 8) {
                selectedPlanet = "pluto";
            }
            if (Math.abs(NorthstarPlanets.eris_x + scrollX + 7 - mouseX) < 8 && Math.abs(NorthstarPlanets.eris_y + scrollY + 7 - mouseY) < 8) {
                selectedPlanet = "eris";
            }
        }

        return super.mouseClicked(mouseX, mouseY, pButton);
    }


    public void scroll(double pDragX, double pDragY) {
        this.scrollX = Mth.clamp(this.scrollX + pDragX, 0, 900);
        this.scrollY = Mth.clamp(this.scrollY + pDragY, 0, 900);
    }


    public ResourceLocation getPlanetSprite(String planet) {
        return switch (planet) {
            case "mercury" -> MERCURY;
            case "venus" -> VENUS;
            case "earth" -> EARTH;
            case "earth_moon", "moon" -> MOON;
            case "mars" -> MARS;
            case "ceres" -> CERES;
            case "jupiter" -> JUPITER;
            case "saturn" -> SATURN;
            case "uranus" -> URANUS;
            case "neptune" -> NEPTUNE;
            case "pluto" -> PLUTO;
            case "eris" -> ERIS;
            default -> null;
        };

    }

    public Component getPlanetName(String planet) {
        return Component.translatable("planets." + planet + ".name");
    }

}

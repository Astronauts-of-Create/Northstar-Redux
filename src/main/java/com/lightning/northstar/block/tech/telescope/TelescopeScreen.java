package com.lightning.northstar.block.tech.telescope;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.accessor.NorthstarLevel;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarTextures;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.util.PressureUnit;
import com.lightning.northstar.util.TemperatureUnit;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TelescopeScreen extends AbstractSimiScreen {

    private static final ResourceLocation TEXTURE = Northstar.asResource("textures/gui/telescope_gui.png");
    private static final ResourceLocation SPACE_BACKGROUND = Northstar.asResource("textures/gui/space_background.png");

    public static final int VIEW_SIZE = 300;

    private final Level level;
    private final BlockPos pos;

    private List<TelescopeNode> nodes;
    private float minX;
    private float minY;
    private float maxX;
    private float maxY;

    private float scrollX = 0.5f;
    private float scrollY = 0.5f;
    private float zoom = 1f;

    private LerpedFloat smoothZoom = LerpedFloat.linear().startWithValue(zoom);

    private Planet hoveredPlanet;
    private Planet selectedPlanet;
    private LerpedFloat progress = LerpedFloat.linear().startWithValue(0);

    public TelescopeScreen(Level level, BlockPos pos) {
        super(NorthstarBlocks.TELESCOPE.get().getName());
        this.level = level;
        this.pos = pos;

        windowWidth = VIEW_SIZE;
        windowHeight = VIEW_SIZE;
    }

    @Override
    protected void init() {
        super.init();

        Planet currentPlanet = level.northstar$planet();
        if (currentPlanet == null) {
            onClose();
            return;
        }

        nodes = new ArrayList<>();
        for (Planet root : NorthstarLevel.CLIENT_TRACKER.getRoots()) {
            nodes.add(createPlanetNode(root, null));
        }

        minX = Float.POSITIVE_INFINITY;
        minY = Float.POSITIVE_INFINITY;
        maxX = Float.NEGATIVE_INFINITY;
        maxY = Float.NEGATIVE_INFINITY;

        for (TelescopeNode node : nodes) {
            node.calculateSpacing(0);
            minX = Math.min(minX, node.position.x - node.size / 2f - 1);
            minY = Math.min(minY, node.position.y - node.size / 2f - 1);
            maxX = Math.max(maxX, node.position.x + node.size / 2f + 1);
            maxY = Math.max(maxY, node.position.y + node.size / 2f + 1);
        }

        for (int i = 0; i < nodes.size(); i++) {
            nodes.addAll(nodes.get(i).children);
        }
    }

    private TelescopeNode createPlanetNode(Planet planet, TelescopeNode parent) {
        TelescopeNode node = new TelescopeNode(planet, parent);
        for (Planet satellite : planet.satellites) {
            node.children.add(createPlanetNode(satellite, node));
        }
        return node;
    }

    @Override
    public void tick() {
        super.tick();

        Component message = TelescopeBlock.canPlayerUse(level, pos, minecraft.player);
        if (message != null) {
            minecraft.player.displayClientMessage(message, true);
            onClose();
            return;
        }

        smoothZoom.chase(zoom, 0.1f, LerpedFloat.Chaser.EXP);
        smoothZoom.tickChaser();

        if (selectedPlanet != null) {
            float value = progress.getValue();
            if (value >= 1) {
                CatnipServices.NETWORK.sendToServer(new TelescopePrintPacket(pos, selectedPlanet.key.location()));
                selectedPlanet = null;
                progress.setValue(0);
            } else {
                progress.setValue(Math.min(1, value + Math.max(0.25f, value) * 0.25f));
            }
        }
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.enableScissor(guiLeft + 8, guiTop + 8, guiLeft + windowWidth - 8, guiTop + windowHeight - 8);
        hoveredPlanet = renderPlanets(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();

        graphics.blit(TEXTURE, guiLeft, guiTop, 0, 0, windowWidth, windowHeight, windowWidth, windowHeight);

        if (hoveredPlanet != null &&
            mouseX < guiLeft + 8 &&
            mouseY < guiTop + 8 &&
            mouseX > guiLeft + windowWidth - 8 &&
            mouseY > guiTop + windowHeight - 8) {
            hoveredPlanet = null; // mouse is out of the window, hide it
        }

        if (hoveredPlanet != null) {
            TemperatureUnit temperature = NorthstarConfigs.client().temperatureUnit.get();
            PressureUnit pressure = NorthstarConfigs.client().pressureUnit.get();
            List<Component> tooltip = new ArrayList<>();

            tooltip.add(hoveredPlanet.getName().withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));

            Component planetType = Component.translatable("northstar.planet.class." + hoveredPlanet.properties.type())
                    .withStyle(ChatFormatting.AQUA);
            tooltip.add(Component.translatable("northstar.gui.generic_planet.planet.type", planetType).withStyle(ChatFormatting.GRAY));

            Component diameter = NorthstarLang.numberDirect(hoveredPlanet.properties.diameter())
                    .append(" km")
                    .withStyle(ChatFormatting.AQUA);
            tooltip.add(Component.translatable("northstar.gui.generic_planet.planet.diameter", diameter).withStyle(ChatFormatting.GRAY));

            if (hoveredPlanet.dimensions.isEmpty()) {
                tooltip.add(Component.translatable("northstar.gui.telescope.cannot_be_landed").withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("northstar.gui.generic_planet.planet.dimensions").withStyle(ChatFormatting.GRAY));

                for (PlanetDimension dimension : hoveredPlanet.dimensions) {
                    tooltip.add(Component.literal(" ").append(dimension.formattedName()).append(":"));

                    Component indent = Component.literal("  ");

                    MutableComponent atmosphere = dimension.hasAtmosphere() ?
                            dimension.atmosphere().asFluidStack(1).getHoverName().copy() :
                            Component.translatable("northstar.gui.generic_planet.dimension.atmosphere.none");

                    tooltip.add(indent.copy().append(Component.translatable(
                            "northstar.gui.generic_planet.dimension.atmosphere",
                            atmosphere.withStyle(ChatFormatting.AQUA)
                    ).withStyle(ChatFormatting.GRAY)));
                    if (dimension.hasAtmosphere()) {
                        tooltip.add(indent.copy().append(Component.translatable(
                                "northstar.gui.generic_planet.dimension.atmosphere_pressure",
                                Component.literal(pressure.format(dimension.atmosphere().pressure())).withStyle(ChatFormatting.AQUA)
                        ).withStyle(ChatFormatting.GRAY)));
                    }

                    Vector2f temp = dimension.averageTemperature();
                    MutableComponent displayedTemperature = Mth.equal(temp.x, temp.y) ?
                            NorthstarLang.numberDirect(temperature.fromCelsius(temp.x)).append(temperature.symbol) :
                            Component.translatable(
                                    "northstar.gui.generic_planet.dimension.temperature.range",
                                    NorthstarLang.numberDirect(temperature.fromCelsius(temp.x)).append(temperature.symbol),
                                    NorthstarLang.numberDirect(temperature.fromCelsius(temp.y)).append(temperature.symbol)
                            );

                    tooltip.add(indent.copy().append(Component.translatable(
                            "northstar.gui.generic_planet.dimension.temperature",
                            displayedTemperature.withStyle(ChatFormatting.AQUA)
                    ).withStyle(ChatFormatting.GRAY)));

                    MutableComponent gravity = dimension.gravity() == 0f ?
                            Component.translatable("northstar.gui.generic_planet.dimension.gravity.none") :
                            NorthstarLang.numberDirect(dimension.gravity()).append("m/s²");

                    tooltip.add(indent.copy().append(Component.translatable(
                            "northstar.gui.generic_planet.dimension.gravity",
                            gravity.withStyle(ChatFormatting.AQUA)
                    ).withStyle(ChatFormatting.GRAY)));
                }
            }

            tooltip.add(Component.empty());

            Component holdForReading = Component.translatable("northstar.gui.telescope.hold_for_reading").withStyle(ChatFormatting.DARK_GRAY);
            if (hoveredPlanet == selectedPlanet) {
                int length = font.width(holdForReading) / font.width("|");
                int filled = (int) (progress.getValue(partialTick) * length);
                Component bar = Component.empty()
                        .append(Component.literal("|".repeat(filled)).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal("|".repeat(length - filled)).withStyle(ChatFormatting.DARK_GRAY));
                tooltip.add(bar);
            } else {
                tooltip.add(holdForReading);
                tooltip.add(Component.translatable("northstar.gui.telescope.hold_for_reading_paper").withStyle(ChatFormatting.DARK_GRAY));
            }

            graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }
    }

    private Planet renderPlanets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        PoseStack pose = graphics.pose();
        pose.pushPose();

        onDragged(0, 0);

        float sizeX = maxX - minX;
        float sizeY = maxY - minY;
        float scale = windowWidth / Math.max(sizeX, sizeY) * smoothZoom.getValue(partialTick);
        float cameraX = minX + scrollX * sizeX;
        float cameraY = minY + scrollY * sizeY;

        pose.translate(guiLeft + windowWidth * 0.5f, guiTop + windowHeight * 0.5f, 0);
        pose.scale(scale, scale, 1);
        pose.translate(-cameraX, -cameraY, 0);

        graphics.northstar$blitFloat(SPACE_BACKGROUND, -sizeX * 0.5f, -sizeY * 0.5f, sizeX, sizeY, 0, 0, 1, 1);

        Planet hovered = null;
        double days = NorthstarLevel.CLIENT_TRACKER.getDeltaDays() * NorthstarConfigs.server().telescopePlanetSpeed.getF();

        Vector3f projectedMouse = new Vector3f(mouseX, mouseY, 0);
        projectedMouse.mulPosition(pose.last().pose().invert(new Matrix4f()));

        BufferBuilder vc = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (TelescopeNode node : nodes) {
            if (node.parent != null) {
                float angle = (float) node.planet.properties.orbit().getVisualAngle(days);
                node.position.set(Mth.cos(angle) * node.radius, Mth.sin(angle) * node.radius)
                        .add(node.parent.position);
            }

            float x = node.position.x;
            float y = node.position.y;

            if (projectedMouse.x >= x - 0.25f &&
                projectedMouse.x <= x + 0.25f &&
                projectedMouse.y >= y - 0.25f &&
                projectedMouse.y <= y + 0.25f) {
                hovered = node.planet;
            }

            pose.pushPose();
            pose.translate(x, y, 0);
            node.planet.properties.renderer().render(level, pose, vc, 0.5f, new Vector4f(1), node.planet);
            pose.popPose();
        }

        pose.popPose();

        MeshData buffer = vc.build();
        if (buffer != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, NorthstarTextures.PLANET_ATLAS);
            RenderSystem.enableBlend();
            BufferUploader.drawWithShader(buffer);
            RenderSystem.disableBlend();
        }

        return hovered;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (selectedPlanet == null) {
            onDragged(dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void onDragged(double dragX, double dragY) {
        float zoom = smoothZoom.getValue(minecraft.getTimer().getGameTimeDeltaPartialTick(true));
        float size = 0.5f / zoom;
        float sensitivity = 1f / VIEW_SIZE / zoom;
        scrollX = Mth.clamp(scrollX - (float) dragX * sensitivity, size, 1 - size);
        scrollY = Mth.clamp(scrollY - (float) dragY * sensitivity, size, 1 - size);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        selectedPlanet = hoveredPlanet;
        progress.setValue(0);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        selectedPlanet = null;
        progress.setValue(0);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        zoom = Math.max(zoom + (float) scrollY, 1);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private static class TelescopeNode {
        private final Planet planet;
        private final TelescopeNode parent;
        private final Vector2f position;
        private final List<TelescopeNode> children;
        private int radius;
        private int size;

        public TelescopeNode(Planet planet, TelescopeNode parent) {
            this.planet = planet;
            this.parent = parent;
            this.position = new Vector2f((float) planet.position.x, (float) planet.position.z);
            this.children = new ArrayList<>();
        }

        private int calculateSpacing(int baseRadius) {
            int spacing = 0;
            for (TelescopeNode child : children) {
                spacing += child.calculateSpacing(spacing);
            }
            radius = baseRadius + spacing + 1;
            size = 1 + spacing * 2;
            return size;
        }
    }

}

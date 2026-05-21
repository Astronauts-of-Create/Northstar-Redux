package com.lightning.northstar.block.tech.telescope;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarBlocks;
import com.lightning.northstar.content.NorthstarPackets;
import com.lightning.northstar.content.NorthstarTextures;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.PlanetRenderer;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.planet.data.render.NoopPlanetRenderer;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.util.PressureUnit;
import com.lightning.northstar.util.TemperatureUnit;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaterniond;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TelescopeScreen extends AbstractSimiScreen {

    private static final ResourceLocation TEXTURE = Northstar.asResource("textures/gui/telescope_gui.png");
    private static final ResourceLocation SPACE_BACKGROUND = Northstar.asResource("textures/gui/space_background.png");

    public static final int FULL_SIZE = 900;
    public static final int VIEW_SIZE = 300;

    private final Level level;
    private final BlockPos pos;

    private float scrollX = FULL_SIZE / 2f - VIEW_SIZE / 2f;
    private float scrollY = FULL_SIZE / 2f - VIEW_SIZE / 2f;

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
    public void tick() {
        super.tick();

        if (selectedPlanet != null) {
            float value = progress.getValue();
            if (value >= 1) {
                NorthstarPackets.getChannel().sendToServer(new TelescopePrintPacket(pos, selectedPlanet.key.location()));
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
        graphics.blit(SPACE_BACKGROUND, guiLeft, guiTop, scrollX, scrollY, windowWidth, windowHeight, FULL_SIZE, FULL_SIZE);
        hoveredPlanet = renderPlanets(graphics, mouseX, mouseY);
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
                            dimension.atmosphere().asFluidStack(1).getDisplayName().copy() :
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

    private Planet renderPlanets(GuiGraphics graphics, int mouseX, int mouseY) {
        Planet currentPlanet = level.northstar$planet();
        if (currentPlanet == null) {
            onClose();
            return null;
        }

        Vector3d universePosition = currentPlanet.position;
        Vector3d direction = new Vector3d();
        Quaterniond viewRotation = PlanetRenderer.getViewRotation(pos.getX(), pos.getZ(), currentPlanet.properties, level.northstar$dimension());

        Planet hovered = null;
        double hoveredDistance = Double.POSITIVE_INFINITY;

        BufferBuilder vc = Tesselator.getInstance().getBuilder();
        vc.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (Planet planet : currentPlanet.system.planets()) {
            if (planet.key.equals(currentPlanet.key) || planet.properties.renderer() instanceof NoopPlanetRenderer) {
                continue;
            }

            direction.set(planet.position)
                    .sub(universePosition)
                    .rotate(viewRotation);
            if (direction.y > 0) {
                continue;
            }

            double distance = direction.length();
            direction.mul(1.0 / distance);

            final double KM_PER_AU = 149597870.7;
            int size = (int) Mth.clamp(planet.properties.diameter() / KM_PER_AU / distance * 20000, 0, 8); // magic value?
            if (size < 1) {
                continue;
            }

            float posX = Mth.map((float) direction.x, -1, +1, 0, FULL_SIZE) - scrollX + guiLeft;
            float posY = Mth.map((float) direction.z, -1, +1, 0, FULL_SIZE) - scrollY + guiTop;

            if (mouseX >= posX - size &&
                mouseX <= posX + size &&
                mouseY >= posY - size &&
                mouseY <= posY + size &&
                distance < hoveredDistance) {
                hovered = planet;
                hoveredDistance = distance;
            }

            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(posX, posY, 0);
            planet.properties.renderer().render(level, pose, vc, size * 2, new Vector4f(1), planet);
            pose.popPose();
        }

        BufferBuilder.RenderedBuffer buffer = vc.endOrDiscardIfEmpty();
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
            scrollX = Mth.clamp(scrollX - (float) dragX, 0, FULL_SIZE - VIEW_SIZE);
            scrollY = Mth.clamp(scrollY - (float) dragY, 0, FULL_SIZE - VIEW_SIZE);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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

}

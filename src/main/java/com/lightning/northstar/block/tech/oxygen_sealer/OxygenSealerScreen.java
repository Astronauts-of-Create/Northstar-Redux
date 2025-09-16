package com.lightning.northstar.block.tech.oxygen_sealer;

import com.lightning.northstar.Northstar;
import com.lightning.northstar.client.TilingAnchor;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.content.NorthstarFluids;
import com.lightning.northstar.content.NorthstarTechBlocks;
import com.lightning.northstar.util.NorthstarLang;
import com.lightning.northstar.world.NorthstarOxygen;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class OxygenSealerScreen extends AbstractSimiScreen {

    private static final ResourceLocation BACKGROUND = Northstar.asResource("textures/gui/oxygen_sealer.png");

    private final MovingOxygenSealer sealer;

    public OxygenSealerScreen(MovingOxygenSealer sealer) {
        super(NorthstarTechBlocks.OXYGEN_SEALER.get().getName());
        this.sealer = sealer;

        setWindowSize(204, 64);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(BACKGROUND, guiLeft, guiTop, 0, 0, windowWidth, windowHeight, 256, 64);

        int totalOxygen = 0;
        int totalCapacity = 0;

        IFluidHandler fluids = sealer.contraption.getSharedFluidTanks();
        for (int i = 0; i < fluids.getTanks(); i++) {
            FluidStack stack = fluids.getFluidInTank(i);
            if (NorthstarOxygen.isOxygen(stack.getFluid())) {
                totalOxygen += stack.getAmount();
                totalCapacity += fluids.getTankCapacity(i);
            } else if (stack.isEmpty()) {
                totalCapacity += fluids.getTankCapacity(i);
            }
        }

        float usagePerTick = sealer.sealer.getSealedBlockCount() * NorthstarConfigs.server().oxygenSealerOxygenPerBlockPerTick.getF();
        int remainingTicks = usagePerTick <= Mth.EPSILON ? Integer.MAX_VALUE : Mth.floor(totalOxygen / usagePerTick);
        int remainingSeconds = remainingTicks / 20;

        String remainingTime = remainingTicks == Integer.MAX_VALUE ? "Forever" :
                remainingSeconds >= 60 * 60 ?
                        "%d:%02d:%02d".formatted(remainingSeconds / 60 / 60, remainingSeconds / 60 % 60, remainingSeconds % 60) :
                        "%02d:%02d".formatted(remainingSeconds / 60, remainingSeconds % 60);

        Component status = sealer.sealer.hasLeak() ?
                Component.translatable("northstar.gui.goggles.sealer.area_too_big").withStyle(ChatFormatting.RED) :
                sealer.active ?
                        Component.translatable("northstar.gui.oxygen_sealer.sealed").withStyle(ChatFormatting.GREEN) :
                        Component.translatable("northstar.gui.oxygen_sealer.no_oxygen").withStyle(ChatFormatting.GOLD);
        MutableComponent line1 = Component.translatable("northstar.generic.status").append(status);
        MutableComponent line2 = NorthstarLang.translate("gui.goggles.sealer.blocks_filled")
                .add(Lang.number(sealer.sealer.getSealedBlockCount())
                        .style(ChatFormatting.AQUA))
                .component();
        MutableComponent line3 = NorthstarLang.translate("gui.oxygen_sealer.oxygen_usage")
                .add(Lang.number(usagePerTick)
                        .style(ChatFormatting.AQUA)
                        .add(NorthstarLang.MB_PER_TICK))
                .component();
        MutableComponent line4 = NorthstarLang.translate("gui.oxygen_sealer.available_oxygen")
                .add(Lang.number(totalOxygen)
                        .add(NorthstarLang.MB)
                        .style(ChatFormatting.AQUA))
                .component();
        MutableComponent line5 = NorthstarLang.translate("gui.oxygen_sealer.remaining_time")
                .add(Lang.text(remainingTime)
                        .style(ChatFormatting.AQUA))
                .component();

        TextureAtlasSprite texture = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(IClientFluidTypeExtensions.of(NorthstarFluids.OXYGEN.get()).getStillTexture());

        int barHeight = 48;
        float fraction = totalCapacity == 0 ? totalOxygen == 0 ? 0 : 1 : (float) totalOxygen / totalCapacity;
        int height = (int) (barHeight * fraction);
        graphics.northstar$blitRepeating(texture, guiLeft + 180, guiTop + 8 + barHeight - height, 16, height, TilingAnchor.BOTTOM_LEFT);

        graphics.drawString(font, line1, guiLeft + 5, guiTop + 7, 0xFFFFFFFF);
        graphics.drawString(font, line2, guiLeft + 5, guiTop + 17, 0xFFFFFFFF);
        graphics.drawString(font, line3, guiLeft + 5, guiTop + 27, 0xFFFFFFFF);
        graphics.drawString(font, line4, guiLeft + 5, guiTop + 37, 0xFFFFFFFF);
        graphics.drawString(font, line5, guiLeft + 5, guiTop + 47, 0xFFFFFFFF);
    }

}

package com.lightning.northstar.mixin.item;

import com.lightning.northstar.world.temperature.NorthstarTemperature;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item {

    @Shadow
    @Final
    public Fluid content;

    @Shadow
    protected abstract void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos);

    public BucketItemMixin(Properties properties) {
        super(properties);
    }

    // 3 checks are performed when emptying a bucket:
    // - Water is checked for ultrawarm dimensions in FluidType#isVaporizedOnPlacement (and other custom fluid that override this)
    // - Water is checked again via ultrawarm and minecraft:water fluid tag, removed by handler below
    // - All fluids are checked to be frozen/vaporized before placement via other handler below

    @ModifyExpressionValue(
            method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/material/Fluid;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean northstar$emptyContentIsUltraWarm(
            boolean original,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) BlockPos pos
    ) {
        // If the block's temperature is controlled by Northstar, always disable ultrawarm and let the handler below take care of it.
        return original && !NorthstarTemperature.isSealed(level, pos);
    }

    // ??? can't seem to inject on the variable for some reason so this will have to do
    @Definition(id = "LiquidBlockContainer", type = LiquidBlockContainer.class)
    @Expression("@(?) instanceof LiquidBlockContainer")
    @Inject(
            method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At(value = "MIXINEXTRAS:EXPRESSION"),
            cancellable = true
    )
    private void northstar$emptyContent(Player player, Level level, BlockPos pos, BlockHitResult result, ItemStack container, CallbackInfoReturnable<Boolean> cir) {
        float temperature = level.northstar$temperature().getTemperature(pos, false);
        if (Float.isNaN(temperature)) {
            return;
        }

        if (temperature >= NorthstarTemperature.getBoilingPoint(content.defaultFluidState())) {
            cir.setReturnValue(true);

            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.8f);
            for (int i = 0; i < 8; i++) {
                level.addParticle(ParticleTypes.LARGE_SMOKE, x + Math.random(), y + Math.random(), z + Math.random(), 0, 0, 0);
            }
            playEmptySound(player, level, pos);
        } else if (temperature <= NorthstarTemperature.getFreezingPoint(content.defaultFluidState())) {
            cir.setReturnValue(true);

            if (content.is(FluidTags.WATER) && level.getBlockState(pos).isAir()) {
                level.setBlock(pos, Blocks.ICE.defaultBlockState(), Block.UPDATE_ALL);
            }

            playEmptySound(player, level, pos);
        }
    }

}

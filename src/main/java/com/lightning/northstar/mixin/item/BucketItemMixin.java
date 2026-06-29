package com.lightning.northstar.mixin.item;

import com.lightning.northstar.world.temperature.NorthstarTemperature;
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
    private Fluid content;

    @Shadow
    protected abstract void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos);

    public BucketItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(
            method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/dimension/DimensionType;ultraWarm()Z"
            ),
            cancellable = true
    )
    private void northstar$emptyContent(Player player, Level level, BlockPos pos, BlockHitResult result, ItemStack container, CallbackInfoReturnable<Boolean> cir) {
        float temperature = NorthstarTemperature.getTemperatureAt(level, pos);

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

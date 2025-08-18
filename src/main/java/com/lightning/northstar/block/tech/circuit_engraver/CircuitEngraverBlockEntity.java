package com.lightning.northstar.block.tech.circuit_engraver;

import com.lightning.northstar.block.tech.circuit_engraver.EngravingBehaviour.EngravingBehaviourSpecifics;
import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Optional;

public class CircuitEngraverBlockEntity extends KineticBlockEntity implements EngravingBehaviourSpecifics {

    public EngravingBehaviour engravingBehaviour;
    private int sound;

    public CircuitEngraverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (engravingBehaviour.running && sound++ % 80 == 0 && level != null) {
            sound = 0;
            level.playSound(null, getBlockPos(), NorthstarSounds.LASER_BURN.get(), SoundSource.BLOCKS, 0.5f, 0.5f);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        engravingBehaviour = new EngravingBehaviour(this);
        behaviours.add(engravingBehaviour);

        registerAwardables(behaviours, AllAdvancements.PRESS, AllAdvancements.COMPACTING,
                AllAdvancements.TRACK_CRAFTING);
    }

    @Override
    public boolean tryProcessInBasin(boolean simulate) {
        return false;
    }

    @Override
    public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
        Optional<RecipeHolder<EngravingRecipe>> recipe = getRecipe(input.stack);
        if (!recipe.isPresent())
            return false;
        if (simulate)
            return true;
        EngravingBehaviour.particleItems.add(input.stack);
        List<ItemStack> outputs = RecipeApplier.applyRecipeOn(level, canProcessInBulk() ? input.stack : input.stack.copyWithCount(1), recipe.get());

        for (ItemStack created : outputs) {
            if (!created.isEmpty()) {
                break;
            }
        }

        outputList.addAll(outputs);
        return true;
    }

    public float getRenderedHeadOffset(float partialTicks) {
        int localTick;
        float offset = 0;
        if (engravingBehaviour.running) {
            if (engravingBehaviour.runningTicks < 20) {
                localTick = engravingBehaviour.runningTicks;
                float num = (localTick + partialTicks) / 20f;
                num = ((2 - Mth.cos((float) (num * Math.PI))) / 2);
                offset = num - .5f;
            } else if (engravingBehaviour.runningTicks <= 20) {
                offset = 1;
            } else {
                localTick = 40 - engravingBehaviour.runningTicks;
                float num = (localTick - partialTicks) / 20f;
                num = ((2 - Mth.cos((float) (num * Math.PI))) / 2);
                offset = num - .5f;
            }
        }
        return offset + 7 / 16f;
    }

    public float getRenderedHeadRotationSpeed(float partialTicks) {
        float speed = getSpeed();
        if (engravingBehaviour.running) {
            if (engravingBehaviour.runningTicks < 15) {
                return speed;
            }
            if (engravingBehaviour.runningTicks <= 20) {
                return speed * 2;
            }
            return speed;
        }
        return speed / 2;
    }

    @Override
    public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
        ItemStack item = itemEntity.getItem();
        Optional<RecipeHolder<EngravingRecipe>> recipe = getRecipe(item);
        if (!recipe.isPresent())
            return false;
        if (simulate)
            return true;

        ItemStack itemCreated = ItemStack.EMPTY;
        EngravingBehaviour.particleItems.add(item);
        if (canProcessInBulk() || item.getCount() == 1) {
            RecipeApplier.applyRecipeOn(itemEntity, recipe.get().value());
            itemCreated = itemEntity.getItem().copy();
        } else {
            for (ItemStack result : RecipeApplier.applyRecipeOn(level, item.copyWithCount(1),
                    recipe.get())) {
                if (itemCreated.isEmpty())
                    itemCreated = result.copy();
                ItemEntity created =
                        new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
                created.setDefaultPickUpDelay();
                created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, .05f));
                level.addFreshEntity(created);
            }
            item.shrink(1);
        }
        return true;
    }

    private static final RecipeWrapper engraverInv = new RecipeWrapper(new ItemStackHandler(1));

    public Optional<RecipeHolder<EngravingRecipe>> getRecipe(ItemStack item) {
        Optional<RecipeHolder<EngravingRecipe>> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, item, NorthstarRecipeTypes.ENGRAVING.getType(), EngravingRecipe.class);
        if (assemblyRecipe.isPresent())
            return assemblyRecipe;
        return NorthstarRecipeTypes.ENGRAVING.find(engraverInv, level);
    }


    @Override
    public boolean canProcessInBulk() {
        return false;
    }

    @Override
    public void onPressingCompleted() {
    }

    @Override
    public int getParticleAmount() {
        return 15;
    }

    @Override
    public float getKineticSpeed() {
        return getSpeed();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        compound.putBoolean("Running", engravingBehaviour.running);
        compound.putInt("Ticks", engravingBehaviour.runningTicks);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        engravingBehaviour.running = compound.getBoolean("Running");
        engravingBehaviour.runningTicks = compound.getInt("Ticks");
    }

}

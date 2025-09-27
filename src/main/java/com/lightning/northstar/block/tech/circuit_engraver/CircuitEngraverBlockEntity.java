package com.lightning.northstar.block.tech.circuit_engraver;

import com.lightning.northstar.content.NorthstarSounds;
import com.lightning.northstar.item.NorthstarRecipeTypes;
import com.lightning.northstar.client.BasicTickableSoundInstance;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Optional;

import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.PASS;

public class CircuitEngraverBlockEntity extends KineticBlockEntity {

    private static final RecipeWrapper recipeInventory = new RecipeWrapper(new ItemStackHandler(1));

    private boolean disabled;
    private boolean running;
    private EngravingRecipe currentRecipe;
    private int processingTicks;
    private int emptyTicks;

    @OnlyIn(Dist.CLIENT)
    private BasicTickableSoundInstance sound;

    public CircuitEngraverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        behaviours.add(new BeltProcessingBehaviour(this)
                .whenItemEnters(this::onItemReceived)
                .whileItemHeld(this::onItemHeld));
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).expandTowards(0, -1.5, 0);
    }

    public BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if (getSpeed() == 0 || disabled)
            return PASS;
        Optional<EngravingRecipe> recipe = getRecipe(transported.stack);
        if (recipe.isEmpty())
            return PASS;
        running = true;
        currentRecipe = recipe.get();
        processingTicks = 0;
        emptyTicks = 0;
        sendData();
        return HOLD;
    }

    public BeltProcessingBehaviour.ProcessingResult onItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if (currentRecipe == null || disabled || getSpeed() == 0)
            return PASS;
        emptyTicks = 0;
        if (currentRecipe != null && processingTicks < currentRecipe.getProcessingDuration())
            return HOLD;

        List<TransportedItemStack> outputs = RecipeApplier.applyRecipeOn(level, transported.stack.copyWithCount(1), currentRecipe)
                .stream()
                .map(stack -> {
                    TransportedItemStack copy = transported.copy();
                    boolean centered = BeltHelper.isItemUpright(stack);
                    copy.stack = stack;
                    copy.locked = true;
                    copy.angle = centered ? 180 : level.random.nextInt(360);
                    return copy;
                })
                .toList();

        TransportedItemStack held = transported.copy();
        held.stack.shrink(1);
        if (outputs.isEmpty())
            handler.handleProcessingOnItem(transported, TransportedResult.convertTo(held));
        else
            handler.handleProcessingOnItem(transported, TransportedResult.convertToAndLeaveHeld(outputs, held));

        processingTicks = 0;
        if (held.stack.isEmpty()) {
            currentRecipe = null;
            running = false;
            sendData();
        }

        return HOLD;
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide)
            return;

        // Not sure if there is a better way to do that, otherwise the animation plays forever
        if (running && emptyTicks++ >= 2) {
            running = false;
            sendData();
        }

        if (currentRecipe != null)
            processingTicks += Mth.clamp((int) Math.abs(getSpeed() / 64f), 1, 256);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();

        if (running) {
            if (sound == null || sound.isStopped()) {
                sound = new BasicTickableSoundInstance(NorthstarSounds.LASER_AMBIENT.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom(), this);
                sound.setLooping(true);
                Minecraft.getInstance().getSoundManager().play(sound);
            }
        } else if (sound != null) {
            sound.cancel();
            sound = null;
        }
    }

    public void updateRedstone() {
        if (level.isClientSide())
            return;
        boolean powered = level.hasNeighborSignal(worldPosition);
        if (powered == disabled)
            return;
        disabled = powered;
        if (powered) {
            running = false;
            currentRecipe = null;
            processingTicks = 0;
        }
        sendData();
    }

    public Optional<EngravingRecipe> getRecipe(ItemStack item) {
        Optional<EngravingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, item,
                NorthstarRecipeTypes.ENGRAVING.getType(), EngravingRecipe.class);
        if (assemblyRecipe.isPresent())
            return assemblyRecipe;

        recipeInventory.setItem(0, item);
        return NorthstarRecipeTypes.ENGRAVING.find(recipeInventory, level);
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        running = compound.getBoolean("Running");
        processingTicks = compound.getInt("ProcessingTicks");
        disabled = compound.getBoolean("Disabled");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putBoolean("Running", running);
        compound.putInt("ProcessingTicks", processingTicks);
        compound.putBoolean("Disabled", disabled);
    }

}

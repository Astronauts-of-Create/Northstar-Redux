package com.lightning.northstar.contraption.rocket;

import com.lightning.northstar.block.simple.InterplanetaryNavigatorBlock;
import com.lightning.northstar.block.tech.auto_lander.AutoLanderBlock;
import com.lightning.northstar.block.tech.computer_rack.TargetingComputerRackBlockEntity;
import com.lightning.northstar.block.tech.rocket_controls.RocketControlsBlock;
import com.lightning.northstar.block.tech.rocket_thruster.RocketThrusterBlock;
import com.lightning.northstar.compat.copycats.CopycatsPlusHelper;
import com.lightning.northstar.config.NorthstarConfigs;
import com.lightning.northstar.config.ServerConfig;
import com.lightning.northstar.content.NorthstarContraptionTypes;
import com.lightning.northstar.content.NorthstarTags.NorthstarBlockTags;
import com.lightning.northstar.contraption.FuelType;
import com.lightning.northstar.planet.Planet;
import com.lightning.northstar.planet.data.PlanetDimension;
import com.lightning.northstar.planet.data.PlanetProperties;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public class RocketContraption extends TranslatingContraption {

    // large enough to be considered infinite, not large enough to allow for numeric overflow when calculating thrust
    public static final int INFINITE_THRUSTERS = 1_000_000;

    // non-serialized, only used for checks during assembly
    public boolean hasControls;

    public boolean infiniteFuel;

    public boolean hasAutoLander;
    public boolean hasInterplanetaryNavigator;
    public int thrusterCount;
    public int computerCount;
    public float heatShielding;
    public float weight;

    /** Current rocket destination */
    public RocketDestination destination;
    /** Origin dimension, set during takeoff to create a return ticket upon landing */
    public RocketDestination origin;

    private Set<BlockPos> cachedUpColliders;
    private Set<BlockPos> cachedDownColliders;

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        if (!searchMovedStructure(world, pos, null))
            return false;
        startMoving(world);
        return true;
    }

    @Override
    protected Pair<StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (state.isAir()) {
            return super.capture(world, pos);
        }

        if (AllBlocks.CREATIVE_FLUID_TANK.has(state)) {
            infiniteFuel = true;
        }
        if (AllBlocks.CREATIVE_MOTOR.has(state)) {
            thrusterCount = INFINITE_THRUSTERS;
        }
        if (AllBlocks.CREATIVE_CRATE.has(state)) {
            heatShielding = Float.POSITIVE_INFINITY;
        }

        if (block instanceof RocketControlsBlock) {
            hasControls = true;
        }

        if (block instanceof AutoLanderBlock) {
            hasAutoLander = true;
        }

        if (block instanceof InterplanetaryNavigatorBlock) {
            hasInterplanetaryNavigator = true;
        }

        if (block instanceof RocketThrusterBlock && thrusterCount != INFINITE_THRUSTERS) {
            thrusterCount++;
        }

        if (blockEntity instanceof TargetingComputerRackBlockEntity rack) {
            computerCount += rack.getComputerCount();
        }

        float volume = calculateShapeVolume(state.getShape(world, pos));
        List<BlockState> copycatMaterials = CopycatsPlusHelper.$.getCopycatMaterials(blockEntity);
        if (copycatMaterials.isEmpty()) {
            captureBlock(state, volume);
        } else {
            volume /= copycatMaterials.size();
            for (BlockState material : copycatMaterials) {
                captureBlock(material, volume);
            }
        }

        return super.capture(world, pos);
    }

    private void captureBlock(BlockState state, float volume) {
        if (NorthstarBlockTags.HEAVY_BLOCKS.matches(state)) {
            weight += 5 * volume;
        } else if (NorthstarBlockTags.SUPER_HEAVY_BLOCKS.matches(state)) {
            weight += 10 * volume;
        } else {
            weight += 1 * volume;
        }

        if (!Float.isInfinite(heatShielding)) {
            if (NorthstarBlockTags.TIER_1_HEAT_RESISTANCE.matches(state)) {
                heatShielding += 3 * volume;
            } else if (NorthstarBlockTags.TIER_2_HEAT_RESISTANCE.matches(state)) {
                heatShielding += 8 * volume;
            } else if (NorthstarBlockTags.TIER_3_HEAT_RESISTANCE.matches(state)) {
                heatShielding += 20 * volume;
            }
        }
    }

    private float calculateShapeVolume(VoxelShape shape) {
        float volume = 0;
        for (AABB aabb : shape.toAabbs()) {
            volume += (float) (aabb.getXsize() * aabb.getYsize() * aabb.getZsize());
        }
        return volume;
    }

    @Override
    public Set<BlockPos> getOrCreateColliders(Level world, Direction movementDirection) {
        return switch (movementDirection) {
            case UP -> {
                Set<BlockPos> colliders = cachedUpColliders;
                if (colliders == null)
                    cachedUpColliders = colliders = super.getOrCreateColliders(world, movementDirection);
                yield colliders;
            }
            case DOWN -> {
                Set<BlockPos> colliders = cachedDownColliders;
                if (colliders == null)
                    cachedDownColliders = colliders = super.getOrCreateColliders(world, movementDirection);
                yield colliders;
            }
            default -> super.getOrCreateColliders(world, movementDirection);
        };
    }

    public float calculateAvailableFuel() {
        float total = 0;
        MountedFluidStorageWrapper fluids = getStorage().getFluids();
        for (int i = 0, j = fluids.getTanks(); i < j; i++) {
            FluidStack fluid = fluids.getFluidInTank(i);
            FuelType fuel = FuelType.getFuelType(fluid.getFluid());
            if (fuel != null) {
                total += fuel.gjPerMb() * fluid.getAmount();
            }
        }
        return total;
    }

    public float getTargetingComputerReduction() {
        return (float) (NorthstarConfigs.server().targetingComputerEfficiency.get() * Math.min((float) computerCount / NorthstarConfigs.server().targetingComputersNeeded.get(), 1f));
    }

    public FuelCost calculateRequiredFuel(
            Planet planetFrom, PlanetDimension dimensionFrom,
            Planet planetTo, PlanetDimension dimensionTo
    ) {
        float distance = (float) planetFrom.position.distance(planetTo.position);

        ServerConfig config = NorthstarConfigs.server();
        float takeoffFuel = weight * Math.max(dimensionFrom.gravityScale(), 0.05f) * config.takeoffFuelScale.getF();
        float landingFuel = weight * Math.max(dimensionTo.gravityScale(), 0.05f) * config.landingFuelScale.getF();
        float travelFuel = weight * distance * config.travelFuelScale.getF();

        return new FuelCost(
                takeoffFuel,
                travelFuel,
                landingFuel,
                1 - getTargetingComputerReduction()
        );
    }

    public float calculateRequiredHeatShielding(PlanetDimension dimensionFrom, PlanetDimension dimensionTo) {
        return Math.max(
                dimensionFrom.heatShieldingRequirement(getBlocks().size()),
                dimensionTo.heatShieldingRequirement(getBlocks().size())
        );
    }

    public int calculateRequiredThrusters(float gravity) {
        return Math.max(1, Mth.ceil(gravity * weight / NorthstarConfigs.server().thrusterPower.get()));
    }

    public boolean isClearForLaunch(Level origin, Level destination) {
        Planet originPlanet = origin.northstar$planet();
        Planet destinationPlanet = destination.northstar$planet();
        PlanetDimension originDimension = origin.northstar$dimension();
        PlanetDimension destinationDimension = destination.northstar$dimension();
        return originPlanet != null &&
               destinationPlanet != null &&
               (calculateAvailableFuel() >= calculateRequiredFuel(originPlanet, originDimension, destinationPlanet, destinationDimension).total() || infiniteFuel) &&
               (thrusterCount >= calculateRequiredThrusters(Math.max(origin.northstar$gravity(), destination.northstar$gravity()))) &&
               (heatShielding >= calculateRequiredHeatShielding(originDimension, destinationDimension)) &&
               (hasInterplanetaryNavigator || !PlanetProperties.isInterplanetary(originPlanet, destinationPlanet));
    }

    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        return false;
    }

    @Override
    public ContraptionType getType() {
        return NorthstarContraptionTypes.ROCKET.get();
    }

    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return super.isAnchoringBlockAt(pos.relative(Direction.DOWN));
    }

    @Override
    public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) {
        CompoundTag rocket = new CompoundTag();

        rocket.putBoolean("InfiniteFuel", infiniteFuel);

        rocket.putBoolean("HasAutoLander", hasAutoLander);
        rocket.putBoolean("HasInterplanetaryNavigator", hasInterplanetaryNavigator);
        rocket.putInt("ThrusterCount", thrusterCount);
        rocket.putInt("ComputerCount", computerCount);
        rocket.putFloat("HeatShielding", heatShielding);
        rocket.putFloat("Weight", weight);

        if (destination != null) rocket.put("Destination", destination.toTag());
        if (origin != null) rocket.put("Origin", origin.toTag());

        CompoundTag tag = super.writeNBT(registries, spawnPacket);
        tag.put("Rocket", rocket);
        return tag;
    }

    @Override
    public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
        CompoundTag rocket = nbt.getCompound("Rocket");

        infiniteFuel = rocket.getBoolean("InfiniteFuel");

        hasAutoLander = rocket.getBoolean("HasAutoLander");
        hasInterplanetaryNavigator = rocket.getBoolean("HasInterplanetaryNavigator");
        thrusterCount = rocket.getInt("ThrusterCount");
        computerCount = rocket.getInt("ComputerCount");
        heatShielding = rocket.getFloat("HeatShielding");
        weight = rocket.getFloat("Weight");

        destination = RocketDestination.fromTag(rocket.getCompound("Destination"));
        origin = RocketDestination.fromTag(rocket.getCompound("Origin"));

        super.readNBT(world, nbt, spawnData);
    }

    public static void consumeFuel(IFluidHandler fluids, float energy) {
        for (int slot = 0; slot < fluids.getTanks(); slot++) {
            FluidStack stack = fluids.getFluidInTank(slot);
            FuelType fuel = FuelType.getFuelType(stack.getFluid());
            if (fuel == null || Mth.equal(fuel.gjPerMb(), 0))
                continue;
            int burnable = Math.min(Mth.floor(energy / fuel.gjPerMb()), stack.getAmount());
            stack.shrink(burnable);
            energy -= burnable;
            if (energy < 1)
                return;
        }
    }

}

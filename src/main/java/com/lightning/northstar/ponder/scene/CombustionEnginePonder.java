package com.lightning.northstar.ponder.scene;

import com.lightning.northstar.content.NorthstarBlocks;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CombustionEnginePonder {

    public static void program(SceneBuilder scene, SceneBuildingUtil util) {
        var select = util.select;
        var world = scene.world;

        scene.title("combustion_engine", "Generating Rotational Force using Combustion Engines");
        scene.setSceneOffsetY(-2);
        scene.configureBasePlate(1, 1, 5);
        scene.removeShadow();

        scene.addKeyframe();

        world.showSection(select.layer(0), Direction.UP);
        world.showSection(select.fromTo(1, 1, 1, 5, 1, 5), Direction.UP);

        // scaffolding
        scene.idle(5);
        world.showSection(select.position(4, 2, 3), Direction.DOWN);
        world.showSection(select.position(2, 2, 3), Direction.DOWN);

        // engine, shaft and speedometer
        scene.idle(5);
        world.showSection(select.position(4, 3, 3), Direction.DOWN);
        scene.idle(5);
        world.showSection(select.position(3, 3, 3), Direction.DOWN);
        scene.idle(5);
        world.showSection(select.position(2, 3, 3), Direction.DOWN);

        scene.overlay
                .showSelectionWithText(select.position(4, 3, 3), 80)
                .text("Combustion Engines burn fuel to produce kinetic force");

        scene.idle(90);

        scene.addKeyframe();

        // pipe, pump and cogwheels
        world.showSection(select.fromTo(6, 1, 1, 6, 4, 6), Direction.WEST);
        world.showSection(select.position(5, 3, 3), Direction.WEST);
        world.setKineticSpeed(select.position(6, 1, 2), 32);
        world.setKineticSpeed(select.position(6, 2, 2), 32);
        world.setKineticSpeed(select.position(6, 2, 3), -32);
        world.propagatePipeChange(util.grid.at(6, 2, 3));

        scene.idle(10);

        scene.overlay
                .showSelectionWithText(select.position(5, 3, 3), 80)
                .text("Fuel must be inserted on the side opposite of the shaft");

        scene.idle(40);

        world.setKineticSpeed(select.fromTo(2, 3, 3, 4, 3, 3), 32);

        scene.idle(50);

        scene.overlay
                .showSelectionWithText(select.position(2, 3, 3), 80)
                .text("And the engine will start automatically");

        scene.idle(90);

        scene.overlay
                .showSelectionWithText(select.position(2, 3, 3), 80)
                .text("The rotation speed and stress capacity depend on the fuel being used");

        scene.idle(90);

        scene.addKeyframe();

        //world.setKineticSpeed(select.fromTo(2, 3, 3, 4, 3, 3), 0);

        scene.overlay
                .showText(80)
                .text("The engine also requires oxidizer which can be obtained from one of 3 sources");

        scene.idle(90);

        scene.addKeyframe();

        scene.overlay
                .showText(80)
                .text("The simplest is the atmosphere, this will work on Earth and other planets with a breathable atmosphere");

        scene.idle(90);

        scene.addKeyframe();

        world.hideSection(select.fromTo(2, 2, 3, 3, 3, 3), Direction.WEST);

        scene.idle(20);

        world.setBlock(util.grid.at(2, 2, 3), NorthstarBlocks.OXYGEN_SEALER.getDefaultState(), false);
        world.showSection(select.position(2, 2, 3), Direction.EAST);
        //world.setKineticSpeed(select.position(2, 2, 3), 64);

        scene.idle(10);

        scene.overlay
                .showSelectionWithText(select.position(2, 2, 3), 80)
                .text("Alternatively an artificial atmosphere can be created with an Oxygen Sealer");

        scene.idle(90);

        world.hideSection(select.position(2, 2, 3), Direction.WEST);

        scene.idle(10);

        scene.addKeyframe();

        world.hideSection(select.position(4, 2, 3), Direction.WEST);

        scene.idle(20);

        BlockState pipe = AllBlocks.FLUID_PIPE.getDefaultState()
                .setValue(PipeBlock.NORTH, false)
                .setValue(PipeBlock.EAST, false)
                .setValue(PipeBlock.SOUTH, true)
                .setValue(PipeBlock.WEST, false)
                .setValue(PipeBlock.UP, true)
                .setValue(PipeBlock.DOWN, false);
        world.setBlock(util.grid.at(4, 2, 3), pipe, false);

        for (int z = 3; z <= 6; z++) {
            scene.idle(5);
            scene.world.showSection(select.position(4, 2, z), Direction.NORTH);
        }
        scene.world.showSection(select.position(4, 1, 6), Direction.NORTH);
        scene.world.showSection(select.position(3, 1, 6), Direction.NORTH);

        world.setKineticSpeed(select.position(4, 1, 6), 32);
        world.setKineticSpeed(select.position(3, 1, 6), -32);
        world.propagatePipeChange(util.grid.at(4, 1, 6));

        scene.idle(5);

        scene.overlay
                .showText(80)
                .text("Or Oxidizer can be inserted from below the engine.");

        scene.idle(90);

        scene.markAsFinished();
    }

}

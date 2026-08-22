package com.lightning.northstar.ponder.scene;

import com.lightning.northstar.block.tech.oxygen_filler.OxygenFillerBlockEntity;
import com.lightning.northstar.content.NorthstarItems;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.Direction;

public class OxygenFillerPonder {

    public static void program(SceneBuilder scene, SceneBuildingUtil util) {
        var select = util.select;
        var world = scene.world;

        scene.title("oxygen_filler", "Using the Oxygen Filler");
        scene.configureBasePlate(1, 1, 5);

        scene.addKeyframe();

        world.showSection(select.layer(0).substract(select.position(4, 0, 6)), Direction.UP);

        scene.idle(5);

        world.showSection(select.position(2, 1, 3), Direction.DOWN);

        scene.idle(5);

        world.showSection(select.position(2, 2, 3), Direction.DOWN);

        scene.idle(5);

        scene.overlay
                .showText(80)
                .text("The Oxygen Filler can be used to fill or empty spacesuits");

        scene.idle(90);

        scene.addKeyframe();

        scene.overlay
                .showControls(
                        new InputWindowElement(util.vector.blockSurface(util.grid.at(2, 2, 3), Direction.NORTH), Pointing.RIGHT)
                                .rightClick()
                                .withItem(NorthstarItems.IRON_SPACE_SUIT_CHESTPIECE.asStack()),
                        40
                );

        scene.overlay
                .showSelectionWithText(select.position(2, 2, 3), 40)
                .text("Items can be inserted or removed by right-clicking");

        scene.idle(10);

        world.modifyBlockEntity(util.grid.at(2, 2, 3), OxygenFillerBlockEntity.class, be -> be.container.setItem(0, NorthstarItems.IRON_SPACE_SUIT_CHESTPIECE.asStack()));

        scene.idle(30);

        scene.addKeyframe();

        world.showSection(select.fromTo(4, 1, 2, 4, 2, 2), Direction.DOWN);

        scene.idle(10);

        scene.overlay
                .showSelectionWithText(select.fromTo(4, 1, 2, 4, 2, 2), 80)
                .text("Oxygen Gas is required to fill spacesuits");

        scene.idle(90);

        world.showSection(select.fromTo(2, 1, 4, 4, 2, 4)
                .add(select.position(4, 1, 3))
                .add(select.fromTo(5, 1, 3, 5, 1, 6))
                .add(select.position(4, 0, 6)), Direction.NORTH);

        scene.idle(20);

        scene.addKeyframe();

        scene.idle(10);

        world.setKineticSpeed(select.position(4, 0, 6), -16);
        world.setKineticSpeed(select.fromTo(5, 1, 3, 5, 1, 6), 32);
        world.setKineticSpeed(select.position(4, 1, 3), -32);
        world.propagatePipeChange(util.grid.at(4, 1, 3));

        scene.overlay
                .showSelectionWithText(select.position(4, 1, 3), 80)
                .text("It can then be pumped into the spacesuit");

        scene.idle(80);

        scene.addKeyframe();

        scene.idle(10);

        world.modifyBlock(util.grid.at(4, 1, 3), state -> state.setValue(PumpBlock.FACING, Direction.NORTH), true);
        world.propagatePipeChange(util.grid.at(4, 1, 3));

        scene.overlay
                .showSelectionWithText(select.position(4, 1, 3), 80)
                .text("Or extracted back");

        scene.idle(90);

        scene.markAsFinished();
    }

}

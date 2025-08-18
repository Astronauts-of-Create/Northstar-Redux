package com.lightning.northstar.ponder.scene;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RocketStationPonder {

    public static void program(SceneBuilder sceneBuilder, SceneBuildingUtil util) {
        var scene = new CreateSceneBuilder(sceneBuilder);
        scene.title("rocket", "Building a rocket");
        scene.configureBasePlate(0, 0, 15);
        scene.showBasePlate();
        scene.scaleSceneView(.7f);
        scene.setSceneOffsetY(-3);

        Selection rocket = util.select().fromTo(3, 1, 3, 11, 18, 11);
        Selection front = util.select().fromTo(2, 1, 2, 11, 15, 5);
        Selection right = util.select().fromTo(0, 1, 0, 5, 18, 14);

        Selection thruster = util.select().fromTo(6, 3, 6, 8, 4, 8);
        Selection fuelTank = util.select().fromTo(6, 6, 7, 7, 7, 8);
        Selection storage = util.select().fromTo(8, 6, 7, 8, 7, 8);
        Selection seat = util.select().position(7, 9, 7);
        Selection station = util.select().position(8, 9, 8);
        Selection controls = util.select().position(7, 9, 8);
        Selection oxygen = util.select().position(6, 9, 8);
        Selection navigator = util.select().fromTo(8, 9, 6, 8, 10, 6);

        AABB glue1 = new AABB(util.grid().at(3, 1, 3));
        AABB glue2 = new AABB(Vec3.atLowerCornerOf(util.grid().at(3, 1, 3)), Vec3.atLowerCornerWithOffset(util.grid().at(11, 18, 11), 1, 1, 1));

        scene.world().showSection(util.select().everywhere(), Direction.DOWN);
        scene.idle(20);

        scene.world().hideSection(util.select().layersFrom(5).add(front).add(right), Direction.UP);
        scene.idle(20);

        scene.addKeyframe();
        scene.overlay()
                .showOutlineWithText(thruster, 80)
                .text("The rocket needs thruster(s) to be able to fly, the amount required is dependent on the size and weight of the rocket.");
        scene.idle(80);

        scene.world().showSection(util.select().layers(5, 3).substract(front).substract(right), Direction.DOWN);
        scene.idle(20);

        scene.addKeyframe();
        scene.overlay()
                .showOutlineWithText(fuelTank, 80)
                .text("The thrusters require fuel, you need one or more tanks containing Methane or Hydrocarbon.");
        scene.idle(80);

        scene.addKeyframe();
        scene.overlay()
                .showOutlineWithText(storage, 80)
                .text("And optionally some additional storage for equipment and resources.");
        scene.idle(80);

        scene.world().showSection(util.select().layers(8, 4).substract(front).substract(right), Direction.DOWN);
        scene.addKeyframe();

        scene.addKeyframe();
        scene.overlay()
                .showOutlineWithText(seat, 80)
                .text("Rockets also need at least one seat to sit on during the flight.");
        scene.idle(80);

        scene.addKeyframe();
        scene.overlay()
                .showOutlineWithText(oxygen, 80)
                .text("The oxygen sealer allows to breath inside the rocket during the flight.");
        scene.idle(80);

        scene.addKeyframe();
        scene.overlay()
                .showOutlineWithText(station, 80)
                .text("The rocket station allows to choose the destination and assemble the rocket.");
        scene.idle(80);

        scene.addKeyframe();
        scene.overlay()
                .showOutlineWithText(controls, 80)
                .text("Rocket controls allow to launch the rocket once assembled.");
        scene.idle(80);

        scene.addKeyframe();
        scene.overlay()
                .showOutlineWithText(navigator, 80)
                .text("An interplanetary navigator is required to travel to other planets, this doesn't apply to moons.");
        scene.idle(80);

        scene.world().showSection(util.select().layers(12, 7).add(front).add(right), Direction.DOWN);
        scene.addKeyframe();
        scene.idle(20);

        scene.overlay()
                .showText(80)
                .text("Don't forget to glue it. You can use scaffolding or individual glue sections.");
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, glue1, glue1, 5);
        scene.idle(1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, glue1, glue2, 90);
        scene.idle(90);

        scene.markAsFinished();
    }

}

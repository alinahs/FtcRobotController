package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="AutoNada", group="Linear OpMode")
public class AutoNada extends LinearOpMode {

    @Override
    public void runOpMode() {

        // Tell the driver the robot is ready
        telemetry.addData("Status", "Initialized. Waiting for start...");
        telemetry.update();

        // Wait for the start button to be pressed
        waitForStart();

        // Do nothing, just idle until stop is pressed
        while (opModeIsActive()) {
            telemetry.addData("Status", "Doing nothing...");
            telemetry.update();
        }
    }
}
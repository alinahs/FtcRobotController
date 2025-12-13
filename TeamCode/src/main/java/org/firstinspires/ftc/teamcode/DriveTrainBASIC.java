package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="DriveTrainBASIC", group="Linear OpMode")
public class DriveTrainBASIC extends LinearOpMode {

    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;

    @Override
    public void runOpMode() {

        // Motor Mapping
        frontLeftMotor  = hardwareMap.get(DcMotor.class, "LFMotor");
        backLeftMotor   = hardwareMap.get(DcMotor.class,  "LBMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "RFMotor");
        backRightMotor  = hardwareMap.get(DcMotor.class,  "RBMotor");

        // Reverse right side motors for mecanum
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);  // Changed from FORWARD
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {

            // Joystick controls
            double y  = -gamepad1.left_stick_y;    // forward/back
            double x  =  gamepad1.left_stick_x;    // strafe
            double rx =  gamepad1.right_stick_x;   // rotate

            // Prevent overpowering motors
            double max = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);

            // Standard mecanum drive (robot-centric)
            double frontLeftPower  = (y + x + rx) / max;
            double backLeftPower   = (y - x + rx) / max;
            double frontRightPower = (y - x - rx) / max;
            double backRightPower  = (y + x - rx) / max;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            telemetry.addData("FL", frontLeftPower);
            telemetry.addData("FR", frontRightPower);
            telemetry.addData("BL", backLeftPower);
            telemetry.addData("BR", backRightPower);
            telemetry.update();
        }
    }
}
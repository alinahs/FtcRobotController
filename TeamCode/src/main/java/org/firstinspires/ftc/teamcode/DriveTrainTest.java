package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


@TeleOp(name="DriveTrainTest", group="Linear OpMode")
public class DecemberTest extends LinearOpMode {

    //declaring motor variables
    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
    private IMU imu;

    @Override
    public void runOpMode() {

        //Connecting wheel motors with control hub ports using hardware map
        frontLeftMotor = hardwareMap.get(DcMotor.class, "LFMotor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "LBMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "RFMotor");
        backRightMotor = hardwareMap.get(DcMotor.class, "RBMotor");

        // Reversing right motors (Because the left and right motors face opposite directions despite being the same product)
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);

        //  IMU setup!!
        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters params = new IMU.Parameters(new org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles());
        imu.initialize(params);


        waitForStart();
        while(opModeIsActive()){
            // Driving logic
            double lx = 0.6 * gamepad1.left_stick_x;
            double ly = -0.6 * gamepad1.left_stick_y;
            double rx = 0.6 * gamepad1.right_stick_x;

            double max = Math.max(Math.abs(lx) + Math.abs(ly) + Math.abs(rx), 1);

            double drivePower = 0.8 - (0.6 * gamepad1.right_trigger);


            double heading = -1 * imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            double adjustedLx = -ly * Math.sin(heading) + lx * Math.cos(heading);
            double adjustedLy = ly * Math.cos(heading) + lx * Math.sin(heading);

            frontLeftMotor.setPower(((adjustedLy + adjustedLx + rx) / max) * drivePower);
            backLeftMotor.setPower(((adjustedLy - adjustedLx + rx) / max) * drivePower);
            frontRightMotor.setPower(((-adjustedLy + adjustedLx + rx) / max) * drivePower);
            backRightMotor.setPower(((adjustedLy + adjustedLx - rx) / max) * drivePower);
        }
    }
}

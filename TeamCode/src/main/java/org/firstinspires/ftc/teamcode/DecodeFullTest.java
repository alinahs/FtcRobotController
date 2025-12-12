package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "DecodeFullTest", group = "Linear OpMode")
public class DecodeFullTest extends LinearOpMode {
    final double FEED_TIME_SECONDS = 0.80;
    final double STOP_SPEED = 0.0;
    final double FULL_SPEED = 1.0;

    final double LAUNCHER_CLOSE_TARGET_VELOCITY = 1200;
    final double LAUNCHER_CLOSE_MIN_VELOCITY = 1175;
    final double LAUNCHER_FAR_TARGET_VELOCITY = 1350;
    final double LAUNCHER_FAR_MIN_VELOCITY = 1325;

    double launcherTarget = LAUNCHER_CLOSE_TARGET_VELOCITY;
    double launcherMin = LAUNCHER_CLOSE_MIN_VELOCITY;

    final double LEFT_POSITION = 0.2962;
    final double RIGHT_POSITION = 0;

    // Drive motors using your naming convention
    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;

    private DcMotorEx leftLauncher = null;
    private DcMotorEx rightLauncher = null;
    private DcMotor intake = null;
    private CRServo leftFeeder = null;
    private CRServo rightFeeder = null;
    private Servo diverter = null;

    ElapsedTime leftFeederTimer = new ElapsedTime();
    ElapsedTime rightFeederTimer = new ElapsedTime();

    private enum LaunchState {IDLE, SPIN_UP, LAUNCH, LAUNCHING}
    private LaunchState leftLaunchState;
    private LaunchState rightLaunchState;

    private enum DiverterDirection {LEFT, RIGHT}
    private DiverterDirection diverterDirection = DiverterDirection.LEFT;

    private enum IntakeState {ON, OFF}
    private IntakeState intakeState = IntakeState.OFF;

    private enum LauncherDistance {CLOSE, FAR}
    private LauncherDistance launcherDistance = LauncherDistance.CLOSE;

    @Override
    public void runOpMode() {
        // Initialize states
        leftLaunchState = LaunchState.IDLE;
        rightLaunchState = LaunchState.IDLE;

        // Motor mapping
        frontLeftMotor  = hardwareMap.get(DcMotor.class, "LFMotor");
        backLeftMotor   = hardwareMap.get(DcMotor.class, "LBMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "RFMotor");
        backRightMotor  = hardwareMap.get(DcMotor.class, "RBMotor");

        leftLauncher = hardwareMap.get(DcMotorEx.class, "left_launcher");
        rightLauncher = hardwareMap.get(DcMotorEx.class, "right_launcher");
        intake = hardwareMap.get(DcMotor.class, "intake");
        leftFeeder = hardwareMap.get(CRServo.class, "left_feeder");
        rightFeeder = hardwareMap.get(CRServo.class, "right_feeder");
        diverter = hardwareMap.get(Servo.class, "diverter");

        // Drive motor directions (kept from your logic)
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);

        leftLauncher.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFeeder.setDirection(DcMotorSimple.Direction.REVERSE);

        // Set zero power behavior
        frontLeftMotor.setZeroPowerBehavior(BRAKE);
        backLeftMotor.setZeroPowerBehavior(BRAKE);
        frontRightMotor.setZeroPowerBehavior(BRAKE);
        backRightMotor.setZeroPowerBehavior(BRAKE);
        leftLauncher.setZeroPowerBehavior(BRAKE);
        rightLauncher.setZeroPowerBehavior(BRAKE);

        // Launcher PID
        leftLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftLauncher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));
        rightLauncher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));

        // Initialize servos
        leftFeeder.setPower(STOP_SPEED);
        rightFeeder.setPower(STOP_SPEED);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // === Driving Logic (your code) ===
            double y  = -gamepad1.left_stick_y;    // forward/back
            double x  =  gamepad1.left_stick_x;    // strafe
            double rx =  gamepad1.right_stick_x;   // rotate

            double max = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);

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

            // === Launcher controls ===
            if (gamepad1.y) {
                leftLauncher.setVelocity(launcherTarget);
                rightLauncher.setVelocity(launcherTarget);
            } else if (gamepad1.b) {
                leftLauncher.setVelocity(STOP_SPEED);
                rightLauncher.setVelocity(STOP_SPEED);
            }

            if (gamepad1.dpad_down) {
                diverterDirection = (diverterDirection == DiverterDirection.LEFT) ? DiverterDirection.RIGHT : DiverterDirection.LEFT;
                diverter.setPosition(diverterDirection == DiverterDirection.LEFT ? LEFT_POSITION : RIGHT_POSITION);
            }

            if (gamepad1.a) {
                if (intakeState == IntakeState.ON) {
                    intakeState = IntakeState.OFF;
                    intake.setPower(0);
                } else {
                    intakeState = IntakeState.ON;
                    intake.setPower(1);
                }
            }

            if (gamepad1.dpad_up) {
                if (launcherDistance == LauncherDistance.CLOSE) {
                    launcherDistance = LauncherDistance.FAR;
                    launcherTarget = LAUNCHER_FAR_TARGET_VELOCITY;
                    launcherMin = LAUNCHER_FAR_MIN_VELOCITY;
                } else {
                    launcherDistance = LauncherDistance.CLOSE;
                    launcherTarget = LAUNCHER_CLOSE_TARGET_VELOCITY;
                    launcherMin = LAUNCHER_CLOSE_MIN_VELOCITY;
                }
            }

            launchLeft(gamepad1.left_bumper);
            launchRight(gamepad1.right_bumper);

            telemetry.addData("State", leftLaunchState);
            telemetry.addData("launch distance", launcherDistance);
            telemetry.addData("Left Launcher Velocity", leftLauncher.getVelocity());
            telemetry.addData("Right Launcher Velocity", rightLauncher.getVelocity());
            telemetry.update();
        }
    }

    void launchLeft(boolean shotRequested) {
        switch (leftLaunchState) {
            case IDLE:
                if (shotRequested) leftLaunchState = LaunchState.SPIN_UP;
                break;
            case SPIN_UP:
                leftLauncher.setVelocity(launcherTarget);
                rightLauncher.setVelocity(launcherTarget);
                if (leftLauncher.getVelocity() > launcherMin) leftLaunchState = LaunchState.LAUNCH;
                break;
            case LAUNCH:
                leftFeeder.setPower(FULL_SPEED);
                leftFeederTimer.reset();
                leftLaunchState = LaunchState.LAUNCHING;
                break;
            case LAUNCHING:
                if (leftFeederTimer.seconds() > FEED_TIME_SECONDS) {
                    leftLaunchState = LaunchState.IDLE;
                    leftFeeder.setPower(STOP_SPEED);
                }
                break;
        }
    }

    void launchRight(boolean shotRequested) {
        switch (rightLaunchState) {
            case IDLE:
                if (shotRequested) rightLaunchState = LaunchState.SPIN_UP;
                break;
            case SPIN_UP:
                leftLauncher.setVelocity(launcherTarget);
                rightLauncher.setVelocity(launcherTarget);
                if (leftLauncher.getVelocity() > launcherMin) rightLaunchState = LaunchState.LAUNCH;
                break;
            case LAUNCH:
                rightFeeder.setPower(FULL_SPEED);
                rightFeederTimer.reset();
                rightLaunchState = LaunchState.LAUNCHING;
                break;
            case LAUNCHING:
                if (rightFeederTimer.seconds() > FEED_TIME_SECONDS) {
                    rightLaunchState = LaunchState.IDLE;
                    rightFeeder.setPower(STOP_SPEED);
                }
                break;
        }
    }
}
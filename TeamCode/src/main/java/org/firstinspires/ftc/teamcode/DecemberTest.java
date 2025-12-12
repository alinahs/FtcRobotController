package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name="DecemberTest", group="Linear OpMode")
public class DecemberTest extends LinearOpMode {

    private DcMotor LFMotor;
    @Override
    public void runOpMode() {

        LFMotor = hardwareMap.get(DcMotor.class, "LFMotor");
        waitForStart();
        while(opModeIsActive()){
            if(gamepad1.a){LFMotor.setPower(1);}
            if(gamepad1.b){LFMotor.setPower(-1);}
            if(gamepad1.x){LFMotor.setPower(0);}
        }
    }
}

package org.whitneyrobotics.ftc.teamcode.Tests.SubsystemTests;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;

@TeleOp(name="Outtake")
public class OuttakeTest extends OpModeEx {
    private DcMotor o;
    private CRServo tL, tR;
    private Servo flick;
    //change servo positions below after testing
    private static final double DOWN = 0.2;
    private static final double UP = 0.8;
    private boolean outtakeButtonToggle = true;

    @Override
    public void initInternal() {
        o = hardwareMap.get(DcMotor.class, "o");
        tL = hardwareMap.get(CRServo.class, "tL");
        tR = hardwareMap.get(CRServo.class, "tR");
        flick = hardwareMap.get(Servo.class, "flick");
        flick.setPosition(DOWN);
    }

    @Override
    protected void loopInternal() {
        //toggle between joystick and CIRLCE button for the outtake motor
        gamepad1.BUMPER_RIGHT.onPress(() -> {outtakeButtonToggle = !outtakeButtonToggle;});

        if(outtakeButtonToggle){
            gamepad1.CIRCLE.onPress(() -> {
                if(o.getPower() == 1){
                    o.setPower(0);
                } else{
                    o.setPower(1);
                }
            });
        } else{
            o.setPower(-gamepad1.LEFT_STICK_Y.value());
        }

        //TRANGE for transfer
        //cr servos, opposite directions
        gamepad1.TRIANGLE.onPress(() -> {
            if(Math.abs(tL.getPower())==1){
                tL.setPower(0);
                tR.setPower(0);
            } else {
                tL.setPower(1);
                tR.setPower(-1);
            }
        });

        //X for flicker
        gamepad1.X.onPress(() -> {
            if (flick.getPosition() == UP) {
                flick.setPosition(DOWN);
            } else {
                flick.setPosition(UP);
            }
        });

        //telemetry
        telemetry.addData("flywheel power", o.getPower());
        telemetry.addData("position of flicker", flick.getPosition());
        telemetry.update();
    }
}
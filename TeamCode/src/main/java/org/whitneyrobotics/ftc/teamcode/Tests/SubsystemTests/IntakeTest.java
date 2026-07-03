package org.whitneyrobotics.ftc.teamcode.Tests.SubsystemTests;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;

@TeleOp
public class IntakeTest extends OpModeEx {

    public DcMotorEx motor;
    private boolean button = true;

    @Override
    public void initInternal() {
        motor = hardwareMap.get(DcMotorEx.class, "motor");
    }

    @Override
    protected void loopInternal() {
        gamepad1.BUMPER_RIGHT.onPress(() -> button =! button);

        if(!button){
            motor.setPower(-gamepad1.LEFT_STICK_Y.value());
        } else {
            gamepad1.CIRCLE.onPress(() -> {
                if(motor.getPower() == 1){
                    motor.setPower(0);
                } else{
                    motor.setPower(1);
                }
            });
        }

        telemetryPro.addData("button mode is on: ", button);
        telemetryPro.addData("mootor power: ", motor.getPower());
    }
}
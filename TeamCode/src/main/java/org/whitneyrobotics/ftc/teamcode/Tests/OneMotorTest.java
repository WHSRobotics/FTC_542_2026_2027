package org.whitneyrobotics.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;

@TeleOp(name = "One Motor Test", group = "Tests")
public class OneMotorTest extends OpModeEx {
    private DcMotorEx motor;
    private double ticksPerRev;
    private int targetRpm;

    @Override
    public void initInternal() {
        motor = hardwareMap.get(DcMotorEx.class, "fL");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev();
        if (ticksPerRev <= 0) throw new IllegalStateException("Configure motor one with encoder ticks per revolution");
    }

    @Override
    public void startInternal() {
        gamepad1.TRIANGLE.onPress(() -> selectRpm(2000));
        gamepad1.CIRCLE.onPress(() -> selectRpm(2500));
        gamepad1.CROSS.onPress(() -> selectRpm(3000));
        gamepad1.SQUARE.onPress(() -> selectRpm(3500));
    }

    private void selectRpm(int rpm) {
        targetRpm = targetRpm == rpm ? 0 : rpm;
    }

    @Override
    protected void loopInternal() {
        double joystick = gamepad1.LEFT_STICK_Y.value();
        if (Math.abs(joystick) > 0.05) {
            targetRpm = 0;
            motor.setPower(joystick);
        } else if (targetRpm != 0) {
            motor.setVelocity(targetRpm * ticksPerRev / 60);
        } else {
            motor.setPower(0);
        }
        telemetryPro.addData("Target RPM", targetRpm);
        telemetryPro.addData("Actual RPM", motor.getVelocity() * 60 / ticksPerRev);
        telemetryPro.addData("Joystick", joystick);
    }

    @Override
    public void stop() {
        if (motor != null) motor.setPower(0);
        super.stop();
    }
}

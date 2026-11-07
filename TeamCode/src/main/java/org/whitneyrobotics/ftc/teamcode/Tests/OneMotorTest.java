package org.whitneyrobotics.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;

@TeleOp(name = "One Motor Test", group = "Tests")
public class OneMotorTest extends OpModeEx {
    private DcMotorEx motor;
    private double ticksPerRev;
    private int targetRpm = 2000;
    private boolean running;

    @Override
    public void initInternal() {
        motor = hardwareMap.get(DcMotorEx.class, "fL");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev();
        if (ticksPerRev <= 0) throw new IllegalStateException("Configure motor fL with encoder ticks per revolution");
    }

    @Override
    public void startInternal() {
        gamepad1.TRIANGLE.onPress(() -> running = true);
        gamepad1.CROSS.onPress(() -> running = false);
        gamepad1.SQUARE.onPress(() -> targetRpm = Math.max(0, targetRpm - 500));
        gamepad1.CIRCLE.onPress(() -> targetRpm += 500);
    }

    @Override
    protected void loopInternal() {
        double joystick = gamepad1.LEFT_STICK_Y.value();
        if (!running) {
            motor.setPower(0);
        } else if (Math.abs(joystick) > 0.05) {
            motor.setPower(joystick);
        } else if (targetRpm > 0) {
            motor.setVelocity(targetRpm * ticksPerRev / 60);
        } else {
            motor.setPower(0);
        }
        telemetryPro.addData("Target RPM", targetRpm);
        telemetryPro.addData("Actual RPM", motor.getVelocity() * 60 / ticksPerRev);
        telemetryPro.addData("Motor", running ? "On" : "Off");
        telemetryPro.addData("Joystick", joystick);
    }

    @Override
    public void stop() {
        if (motor != null) motor.setPower(0);
        super.stop();
    }
}

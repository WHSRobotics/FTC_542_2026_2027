package org.whitneyrobotics.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Subsystems.Outtake;

@TeleOp(name = "outtakeTest")
public class OuttakeTest extends OpModeEx {
    public Outtake outtake;
    private DcMotorEx fL, bL, fR, bR;
    private int targetRpm;
    private double ticksPerRev;

    @Override
    public void initInternal() {
        outtake = new Outtake(hardwareMap, true);
        outtake.outtake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = outtake.outtake.getMotorType().getTicksPerRev();
        if (ticksPerRev <= 0) throw new IllegalStateException("Configure the outtake motor's encoder ticks per revolution");
        fL = hardwareMap.get(DcMotorEx.class, "fL");
        bL = hardwareMap.get(DcMotorEx.class, "bL");
        fR = hardwareMap.get(DcMotorEx.class, "fR");
        bR = hardwareMap.get(DcMotorEx.class, "bR");
    }

    @Override
    public void startInternal() {
        gamepad1.TRIANGLE.onPress(() -> setRpm(2000));
        gamepad1.CIRCLE.onPress(() -> setRpm(2500));
        gamepad1.CROSS.onPress(() -> setRpm(3000));
        gamepad1.SQUARE.onPress(() -> setRpm(3500));
    }

    private void setRpm(int rpm) {
        targetRpm = targetRpm == rpm ? 0 : rpm;
        if (targetRpm == 0) {
            outtake.stop();
        } else {
            outtake.outtake.setVelocity(targetRpm * ticksPerRev / 60);
        }
    }

    @Override
    protected void loopInternal() {
        double y = gamepad1.LEFT_STICK_Y.value();
        double x = -gamepad1.LEFT_STICK_X.value();
        double rx = -gamepad1.RIGHT_STICK_X.value();
        double scale = Math.max(1, Math.abs(y) + Math.abs(x) + Math.abs(rx));

        fL.setPower((y + x + rx) / scale);
        bL.setPower((y - x + rx) / scale);
        fR.setPower((y - x - rx) / scale);
        bR.setPower((y + x - rx) / scale);

        telemetryPro.addData("Flywheel target RPM", targetRpm);
        telemetryPro.addData("Flywheel actual RPM", outtake.outtake.getVelocity() * 60 / ticksPerRev);
    }

}

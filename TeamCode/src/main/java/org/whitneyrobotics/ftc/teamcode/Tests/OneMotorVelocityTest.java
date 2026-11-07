package org.whitneyrobotics.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@TeleOp(name = "One Motor Velocity Test", group = "Tests")
public class OneMotorVelocityTest extends OpModeEx {
    private DcMotorEx motor;
    private double ticksPerRev;
    private int targetRpm = 2000;
    private boolean running;
    private BufferedWriter log;
    private String logStatus = "Not started";
    private double nextSample;
    private double nextFlush = 1;

    @Override
    public void initInternal() {
        motor = hardwareMap.get(DcMotorEx.class, "fL");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ticksPerRev = motor.getMotorType().getTicksPerRev();
        if (ticksPerRev <= 0) throw new IllegalStateException("Configure motor fL with encoder ticks per revolution");
    }

    @Override
    public void startInternal() {
        try {
            File directory = AppUtil.ROBOT_DATA_DIR;
            if (!directory.isDirectory() && !directory.mkdirs()) throw new IOException("Cannot create data directory");
            File file = File.createTempFile("flywheel-", ".csv", directory);
            log = new BufferedWriter(new FileWriter(file));
            log.write("time_seconds,running,target_rpm,actual_rpm,raw_ticks_per_second,configured_ticks_per_rev\n");
            log.flush();
            logStatus = file.getAbsolutePath();
        } catch (IOException e) {
            logStatus = "Log error: " + e.getMessage();
            closeLog();
        }
        gamepad1.TRIANGLE.onPress(() -> running = true);
        gamepad1.CROSS.onPress(() -> running = false);
        gamepad1.SQUARE.onPress(() -> targetRpm = Math.max(0, targetRpm - 500));
        gamepad1.CIRCLE.onPress(() -> targetRpm += 500);
    }

    @Override
    protected void loopInternal() {
        if (running && targetRpm > 0) motor.setVelocity(targetRpm * ticksPerRev / 60);
        else motor.setPower(0);
        double rawVelocity = motor.getVelocity();
        double actualRpm = rawVelocity * 60 / ticksPerRev;
        double time = getRuntime();
        if (log != null && time >= nextSample) {
            try {
                log.write(time + "," + running + "," + targetRpm + "," + actualRpm + "," + rawVelocity + "," + ticksPerRev + "\n");
                nextSample = time + 0.05;
                if (time >= nextFlush) {
                    log.flush();
                    nextFlush = time + 1;
                }
            } catch (IOException e) {
                logStatus = "Log error: " + e.getMessage();
                closeLog();
            }
        }
        telemetryPro.addData("Motor", running ? "On" : "Off");
        telemetryPro.addData("Target RPM", targetRpm);
        telemetryPro.addData("Actual RPM", actualRpm);
        telemetryPro.addData("RPM error", (running ? targetRpm : 0) - actualRpm);
        telemetryPro.addData("Encoder ticks/rev", ticksPerRev);
        telemetryPro.addData("Encoder ticks/sec", rawVelocity);
        telemetryPro.addData("Log", logStatus);
    }

    @Override
    public void stop() {
        if (motor != null) motor.setPower(0);
        closeLog();
        super.stop();
    }

    private void closeLog() {
        if (log == null) return;
        try {
            log.close();
        } catch (IOException e) {
            logStatus = "Log error: " + e.getMessage();
        }
        log = null;
    }
}

package org.whitneyrobotics.ftc.teamcode.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Config
@TeleOp(name = "Outtake PID Dashboard", group = "Tests")
public class OuttakePidDashboard extends OpMode {
    public static volatile double kP = 100, kI = 0.3, kD = 0, kF = 18;
    public static volatile double targetRpm = 0;
    public static volatile double ticksPerRev = 28;
    public static volatile boolean enabled = false;
    public static volatile double intakePower = 0;

    private DcMotorEx motor;
    private DcMotor intake;
    private VoltageSensor battery;
    private PIDFCoefficients original;
    private double appliedP, appliedI, appliedD, appliedF;
    private BufferedWriter log;
    private String logStatus = "Not started";
    private double nextSample, nextFlush, nextVoltageSample;
    private double voltage = Double.NaN;

    @Override
    public void init() {
        enabled = false;
        intakePower = 0;
        targetRpm = 0;
        ticksPerRev = 28;
        kP = 100; kI = 0.3; kD = 0; kF = 18;
        motor = hardwareMap.get(DcMotorEx.class, "outtake");
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setPower(0);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        original = motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        appliedP = original.p;
        appliedI = original.i;
        appliedD = original.d;
        appliedF = original.f;
        if (hardwareMap.voltageSensor.iterator().hasNext()) battery = hardwareMap.voltageSensor.iterator().next();
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        dashboard.updateConfig();
    }

    @Override
    public void start() {
        resetRuntime();
        try {
            File dir = AppUtil.ROBOT_DATA_DIR;
            if (!dir.isDirectory() && !dir.mkdirs()) throw new IOException("Cannot create data directory");
            File file = File.createTempFile("outtake-pidf-", ".csv", dir);
            log = new BufferedWriter(new FileWriter(file));
            log.write("time_seconds,enabled,target_rpm,actual_rpm,target_ticks_per_second,actual_ticks_per_second,encoder_ticks,ticks_per_rev,kP,kI,kD,kF,intake_power,battery_volts\n");
            logStatus = file.getAbsolutePath();
        } catch (IOException e) {
            logStatus = "Log error: " + e.getMessage();
            closeLog();
        }
    }

    @Override
    public void loop() {
        double p = kP, i = kI, d = kD, f = kF, target = targetRpm, ticks = ticksPerRev;
        boolean valid = Double.isFinite(p) && Double.isFinite(i) && Double.isFinite(d) && Double.isFinite(f)
                && Double.isFinite(target) && Double.isFinite(ticks) && ticks > 0 && p >= 0 && i >= 0 && d >= 0 && f >= 0;
        String status = "Ready";
        if (!valid) {
            enabled = false;
            status = "Invalid PIDF, target, or ticks per rev";
        } else if (p != appliedP || i != appliedI || d != appliedD || f != appliedF) {
            try {
                motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p, i, d, f));
                appliedP = p; appliedI = i; appliedD = d; appliedF = f;
            } catch (RuntimeException e) {
                enabled = false;
                status = "PIDF error: " + e.getMessage();
            }
        }
        double command = enabled && valid ? target * ticks / 60 : 0;
        if (enabled) motor.setVelocity(command);
        else motor.setPower(0);
        double intakeCommand = Double.isFinite(intakePower) ? Math.max(0, Math.min(1, intakePower)) : 0;
        intake.setPower(intakeCommand);
        double time = getRuntime();
        if (time >= nextVoltageSample) {
            try { voltage = battery == null ? Double.NaN : battery.getVoltage(); }
            catch (RuntimeException e) { voltage = Double.NaN; }
            nextVoltageSample = time + 0.2;
        }
        if (time >= nextSample) {
            double velocity = motor.getVelocity();
            double actualRpm = Double.isFinite(ticks) && ticks > 0 ? velocity * 60 / ticks : Double.NaN;
            int position = motor.getCurrentPosition();
            telemetry.addData("Target RPM", enabled ? target : 0);
            telemetry.addData("Actual RPM", actualRpm);
            telemetry.addData("Target ticks per second", command);
            telemetry.addData("Actual ticks per second", velocity);
            telemetry.addData("Encoder ticks", position);
            telemetry.addData("Ticks per revolution", ticks);
            telemetry.addData("Intake power", intakeCommand);
            telemetry.addData("Battery volts", voltage);
            telemetry.addData("Status", status);
            telemetry.addData("Log", logStatus);
            telemetry.update();
            if (log != null) {
                try {
                    log.write(time + "," + enabled + "," + (enabled ? target : 0) + "," + actualRpm + "," + command + "," + velocity + "," + position + "," + ticks + "," + appliedP + "," + appliedI + "," + appliedD + "," + appliedF + "," + intakeCommand + "," + voltage + "\n");
                    if (time >= nextFlush) { log.flush(); nextFlush = time + 1; }
                } catch (IOException e) {
                    logStatus = "Log error: " + e.getMessage();
                    closeLog();
                }
            }
            nextSample = time + 0.05;
        }
    }

    @Override
    public void stop() {
        enabled = false;
        intakePower = 0;
        if (intake != null) intake.setPower(0);
        if (motor != null) {
            motor.setPower(0);
            if (original != null) {
                try { motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, original); }
                catch (RuntimeException ignored) { }
            }
        }
        closeLog();
    }

    private void closeLog() {
        if (log == null) return;
        try { log.close(); } catch (IOException e) { logStatus = "Log error: " + e.getMessage(); }
        log = null;
    }
}

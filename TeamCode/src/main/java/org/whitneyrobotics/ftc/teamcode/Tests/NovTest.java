package org.whitneyrobotics.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@TeleOp(name = "nov test", group = "Tests")
public class NovTest extends PedroDriveOuttakeTest {
    private DcMotorEx outtake, intake;
    private double outtakeTicks, intakeTicks;
    private int outtakeRpm = 2000, intakeRpm = 2000;
    private boolean outtakeOn, intakeOn;
    private BufferedWriter log;
    private String logStatus = "Not started";
    private double nextSample, nextFlush = 1;
    private double nextElectricalSample, voltage = Double.NaN, outtakeAmps = Double.NaN, intakeAmps = Double.NaN;
    private VoltageSensor battery;
    private PIDFCoefficients outtakePid, intakePid;

    @Override
    public void initInternal() {
        super.initInternal();
        outtake = hardwareMap.get(DcMotorEx.class, "outtake");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        outtake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtakeTicks = outtake.getMotorType().getTicksPerRev();
        intakeTicks = intake.getMotorType().getTicksPerRev();
        if (outtakeTicks <= 0 || intakeTicks <= 0) throw new IllegalStateException("Configure encoder motor types for outtake and intake");
        if (hardwareMap.voltageSensor.iterator().hasNext()) battery = hardwareMap.voltageSensor.iterator().next();
        try { outtakePid = outtake.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER); } catch (RuntimeException ignored) { }
        try { intakePid = intake.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER); } catch (RuntimeException ignored) { }
    }

    @Override
    public void startInternal() {
        super.startInternal();
        gamepad2.TRIANGLE.onPress(() -> outtakeOn = true);
        gamepad2.CROSS.onPress(() -> outtakeOn = false);
        gamepad2.SQUARE.onPress(() -> outtakeRpm = Math.max(0, outtakeRpm - 500));
        gamepad2.CIRCLE.onPress(() -> outtakeRpm += 500);
        gamepad2.DPAD_UP.onPress(() -> intakeOn = true);
        gamepad2.DPAD_DOWN.onPress(() -> intakeOn = false);
        gamepad2.DPAD_LEFT.onPress(() -> intakeRpm = Math.max(0, intakeRpm - 500));
        gamepad2.DPAD_RIGHT.onPress(() -> intakeRpm += 500);
        try {
            File dir = AppUtil.ROBOT_DATA_DIR;
            if (!dir.isDirectory() && !dir.mkdirs()) throw new IOException("Cannot create data directory");
            File file = File.createTempFile("nov-test-", ".csv", dir);
            log = new BufferedWriter(new FileWriter(file));
            log.write("time_seconds,battery_volts,drive_mode,imu_yaw_rad,field_reference_rad,gp1_left_x,gp1_left_y,gp1_right_x,gp1_slow,gp1_reset,outtake_on,outtake_target_rpm,outtake_command_ticks_per_second,outtake_actual_rpm,outtake_error_rpm,outtake_ticks_per_second,outtake_position_ticks,outtake_ticks_per_rev,outtake_current_amps,outtake_type,outtake_direction,outtake_pid_p,outtake_pid_i,outtake_pid_d,outtake_pid_f,intake_on,intake_target_rpm,intake_command_ticks_per_second,intake_actual_rpm,intake_error_rpm,intake_ticks_per_second,intake_position_ticks,intake_ticks_per_rev,intake_current_amps,intake_type,intake_direction,intake_pid_p,intake_pid_i,intake_pid_d,intake_pid_f\n");
            log.flush();
            logStatus = file.getAbsolutePath();
        } catch (IOException e) {
            logStatus = "Log error: " + e.getMessage();
            closeLog();
        }
    }

    @Override
    protected void loopInternal() {
        super.loopInternal();
        if (outtakeOn && outtakeRpm > 0) outtake.setVelocity(outtakeRpm * outtakeTicks / 60);
        else outtake.setPower(0);
        if (intakeOn && intakeRpm > 0) intake.setVelocity(intakeRpm * intakeTicks / 60);
        else intake.setPower(0);
        double outtakeRaw = outtake.getVelocity(), intakeRaw = intake.getVelocity();
        double outtakeActual = outtakeRaw * 60 / outtakeTicks, intakeActual = intakeRaw * 60 / intakeTicks;
        double time = getRuntime();
        if (time >= nextElectricalSample) {
            try { voltage = battery == null ? Double.NaN : battery.getVoltage(); } catch (RuntimeException e) { voltage = Double.NaN; }
            try { outtakeAmps = outtake.getCurrent(CurrentUnit.AMPS); } catch (RuntimeException e) { outtakeAmps = Double.NaN; }
            try { intakeAmps = intake.getCurrent(CurrentUnit.AMPS); } catch (RuntimeException e) { intakeAmps = Double.NaN; }
            nextElectricalSample = time + 0.2;
        }
        double yaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        if (log != null && time >= nextSample) {
            try {
                log.write(time + "," + voltage + "," + (fieldCentric ? "field" : "robot") + "," + yaw + "," + startingHeading + "," + gamepad1.LEFT_STICK_X.value() + "," + gamepad1.LEFT_STICK_Y.value() + "," + gamepad1.RIGHT_STICK_X.value() + "," + gamepad1.BUMPER_LEFT.value() + "," + gamepad1.RIGHT_TRIGGER.value()
                        + motorRow(outtake, outtakeOn, outtakeRpm, outtakeTicks, outtakeRaw, outtakeActual, outtakeAmps, outtakePid)
                        + motorRow(intake, intakeOn, intakeRpm, intakeTicks, intakeRaw, intakeActual, intakeAmps, intakePid) + "\n");
                nextSample = time + 0.05;
                if (time >= nextFlush) { log.flush(); nextFlush = time + 1; }
            } catch (IOException e) {
                logStatus = "Log error: " + e.getMessage();
                closeLog();
            }
        }
        telemetryPro.addData("Outtake", (outtakeOn ? "On" : "Off") + " target " + outtakeRpm + " RPM, actual " + Math.round(outtakeActual));
        telemetryPro.addData("Intake", (intakeOn ? "On" : "Off") + " target " + intakeRpm + " RPM, actual " + Math.round(intakeActual));
        telemetryPro.addData("Battery volts", voltage);
        telemetryPro.addData("Outtake encoder", outtakeRaw + " ticks/s; " + outtakeTicks + " ticks/rev; " + outtake.getCurrentPosition() + " ticks");
        telemetryPro.addData("Intake encoder", intakeRaw + " ticks/s; " + intakeTicks + " ticks/rev; " + intake.getCurrentPosition() + " ticks");
        telemetryPro.addData("Motor current (A)", "Outtake " + outtakeAmps + "; intake " + intakeAmps);
        telemetryPro.addData("Motor types", outtake.getMotorType().getName() + " / " + intake.getMotorType().getName());
        telemetryPro.addData("Velocity PIDF", String.valueOf(outtakePid) + " / " + String.valueOf(intakePid));
        telemetryPro.addData("Log", logStatus);
    }

    @Override
    public void stop() {
        if (outtake != null) outtake.setPower(0);
        if (intake != null) intake.setPower(0);
        closeLog();
        super.stop();
    }

    private void closeLog() {
        if (log == null) return;
        try { log.close(); } catch (IOException e) { logStatus = "Log error: " + e.getMessage(); }
        log = null;
    }

    private String motorRow(DcMotorEx motor, boolean on, int rpm, double ticks, double raw, double actual, double amps, PIDFCoefficients pid) {
        String type = motor.getMotorType().getName().replace("\"", "\"\"");
        return "," + on + "," + rpm + "," + (on ? rpm * ticks / 60 : 0) + "," + actual + "," + ((on ? rpm : 0) - actual)
                + "," + raw + "," + motor.getCurrentPosition() + "," + ticks + "," + amps + ",\"" + type + "\"," + motor.getDirection()
                + (pid == null ? ",NaN,NaN,NaN,NaN" : "," + pid.p + "," + pid.i + "," + pid.d + "," + pid.f);
    }
}

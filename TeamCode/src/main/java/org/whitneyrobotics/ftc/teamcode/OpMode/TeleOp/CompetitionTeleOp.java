package org.whitneyrobotics.ftc.teamcode.OpMode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Subsystems.RobotImpl;
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

@TeleOp(name = "Competition TeleOp")
public class CompetitionTeleOp extends OpModeEx {
    // shortcut: Enable after Pinpoint directions, offsets, and localization are verified on the robot.
    private static final boolean USE_PINPOINT = false;
    private RobotImpl robot;
    private BufferedWriter log;
    private String logStatus = "Not started";
    private double nextSample, nextFlush = 1;
    private double nextElectricalSample, voltage = Double.NaN, outtakeAmps = Double.NaN, intakeAmps = Double.NaN;
    private VoltageSensor battery;
    private PIDFCoefficients outtakePid, intakePid;
    private double nextRumble = 15;

    @Override
    public void initInternal() {
        robot = RobotImpl.competitionDrive(hardwareMap, USE_PINPOINT);
        robot.initCompetitionMotors(hardwareMap);
        robot.initOuttakeServo(hardwareMap);
        if (hardwareMap.voltageSensor.iterator().hasNext()) battery = hardwareMap.voltageSensor.iterator().next();
        try { outtakePid = robot.outtakeMotor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER); } catch (RuntimeException ignored) { }
        try { intakePid = robot.intakeMotor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER); } catch (RuntimeException ignored) { }
    }

    @Override
    public void startInternal() {
        nextRumble = 15;
        robot.configureDriveControls(gamepad1);
        robot.configureMatchControls(gamepad2);
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
        if (getRuntime() >= nextRumble) {
            gamepad1.Vibrate(500);
            nextRumble = (Math.floor(getRuntime() / 15) + 1) * 15;
        }
        robot.updateImuDrive(gamepad1);
        telemetryPro.addData("Drive mode", robot.fieldCentric ? "Field-centric" : "Robot-centric");
        telemetryPro.addData("Heading source", USE_PINPOINT ? "Pinpoint" : "Control Hub IMU");
        telemetryPro.addData("Speed mode", gamepad1.BUMPER_LEFT.value() ? "Slow (50%)" : "Normal");
        telemetryPro.addData("Pedro mode", robot.drive.follower.mode());
        telemetryPro.addData("Localization heading", robot.drive.getPose().heading());
        robot.updateMatchMotors(gamepad1, gamepad2);
        double outtakeRaw = robot.outtakeMotor.getVelocity(), intakeRaw = robot.intakeMotor.getVelocity();
        double outtakeActual = outtakeRaw * 60 / robot.outtakeTicks, intakeActual = intakeRaw * 60 / robot.intakeTicks;
        double time = getRuntime();
        if (time >= nextElectricalSample) {
            try { voltage = battery == null ? Double.NaN : battery.getVoltage(); } catch (RuntimeException e) { voltage = Double.NaN; }
            try { outtakeAmps = robot.outtakeMotor.getCurrent(CurrentUnit.AMPS); } catch (RuntimeException e) { outtakeAmps = Double.NaN; }
            try { intakeAmps = robot.intakeMotor.getCurrent(CurrentUnit.AMPS); } catch (RuntimeException e) { intakeAmps = Double.NaN; }
            nextElectricalSample = time + 0.2;
        }
        double yaw = robot.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        if (log != null && time >= nextSample) {
            try {
                log.write(time + "," + voltage + "," + (robot.fieldCentric ? "field" : "robot") + "," + yaw + "," + robot.startingHeading + "," + gamepad1.LEFT_STICK_X.value() + "," + gamepad1.LEFT_STICK_Y.value() + "," + gamepad1.RIGHT_STICK_X.value() + "," + gamepad1.BUMPER_LEFT.value() + "," + gamepad1.CROSS.value()
                        + motorRow(robot.outtakeMotor, robot.outtakeOn, robot.outtakeRpm, robot.outtakeTicks, outtakeRaw, outtakeActual, outtakeAmps, outtakePid)
                        + motorRow(robot.intakeMotor, robot.intakeOn, robot.intakeRpm, robot.intakeTicks, intakeRaw, intakeActual, intakeAmps, intakePid) + "\n");
                nextSample = time + 0.05;
                if (time >= nextFlush) { log.flush(); nextFlush = time + 1; }
            } catch (IOException e) {
                logStatus = "Log error: " + e.getMessage();
                closeLog();
            }
        }
        telemetryPro.addData("Outtake", (robot.outtakeOn ? "On" : "Off") + " target " + robot.outtakeRpm + " RPM, actual " + Math.round(outtakeActual));
        telemetryPro.addData("Intake", (robot.intakeOn ? "On" : "Off") + " target " + robot.intakeRpm + " RPM, actual " + Math.round(intakeActual));
        telemetryPro.addData("Outtake servo", robot.outtakeServo.getPosition());
        telemetryPro.addData("Battery volts", voltage);
        telemetryPro.addData("Outtake encoder", outtakeRaw + " ticks/s; " + robot.outtakeTicks + " ticks/rev; " + robot.outtakeMotor.getCurrentPosition() + " ticks");
        telemetryPro.addData("Intake encoder", intakeRaw + " ticks/s; " + robot.intakeTicks + " ticks/rev; " + robot.intakeMotor.getCurrentPosition() + " ticks");
        telemetryPro.addData("Motor current (A)", "Outtake " + outtakeAmps + "; intake " + intakeAmps);
        telemetryPro.addData("Motor types", robot.outtakeMotor.getMotorType().getName() + " / " + robot.intakeMotor.getMotorType().getName());
        telemetryPro.addData("Velocity PIDF", String.valueOf(outtakePid) + " / " + String.valueOf(intakePid));
        telemetryPro.addData("Log", logStatus);
    }

    @Override
    public void stop() {
        if (robot != null) robot.stopCompetitionMotors();
        closeLog();
        if (robot != null) robot.drive.follower.stop();
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

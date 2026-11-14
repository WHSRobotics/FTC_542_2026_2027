package org.whitneyrobotics.ftc.teamcode.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.pedropathing.math.Pose;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.whitneyrobotics.ftc.teamcode.pedro.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Config
@TeleOp(name = "Auto Pose Recorder", group = "Tests")
public class AutoPoseRecorder extends OpMode {
    // Set these in Dashboard while initialized, before pressing Start.
    public static volatile double startX = 72, startY = 72, startHeadingDegrees = 0;
    // Increase this number in Dashboard to pin the current pose.
    public static volatile int pinPoint = 0;
    // Set a one-based point number and increase removeRequest to remove it.
    public static volatile int removePoint = 1, removeRequest = 0;
    public static volatile boolean intakeOn = false, outtakeOn = false, servoExtended = false;
    public static volatile double intakeRpm = 250, outtakeRpm = 3150;

    private final List<Pose> points = new ArrayList<>();
    private PinpointLocalizer localizer;
    private GoBildaPinpointDriver pinpoint;
    private DcMotorEx intake, outtake;
    private Servo outtakeServo;
    private double intakeTicks, outtakeTicks;
    private File logFile;
    private String logStatus = "Not started";
    private String pinStatus = "No points saved";
    private int lastPinPoint, lastRemoveRequest;
    private boolean lastA, lastB;

    @Override
    public void init() {
        localizer = Constants.localizer(hardwareMap);
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, Constants.localizerConfig.name.get());
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        outtake = hardwareMap.get(DcMotorEx.class, "outtake");
        outtakeServo = hardwareMap.get(Servo.class, "outtakeServo");
        intakeTicks = intake.getMotorType().getTicksPerRev();
        outtakeTicks = outtake.getMotorType().getTicksPerRev();
        if (intakeTicks <= 0 || outtakeTicks <= 0) throw new IllegalStateException("Configure encoder motor types for intake and outtake");
        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intake.setPower(0);
        outtake.setPower(0);
        intakeOn = false;
        outtakeOn = false;
        servoExtended = false;
        outtakeServo.setPosition(0);
        FtcDashboard.getInstance().updateConfig();
        telemetry.addLine("Keep robot still during Pinpoint calibration.");
        telemetry.addLine("Set starting pose in Dashboard before pressing Start.");
        telemetry.update();
    }

    @Override
    public void start() {
        points.clear();
        lastPinPoint = pinPoint;
        lastRemoveRequest = removeRequest;
        lastA = gamepad1.a;
        lastB = gamepad1.b;
        localizer.setPose(new Pose(startX, startY, Math.toRadians(startHeadingDegrees)));
        try {
            File dir = AppUtil.ROBOT_DATA_DIR;
            if (!dir.isDirectory() && !dir.mkdirs()) throw new IOException("Cannot create data directory");
            logFile = File.createTempFile("auto-poses-", ".csv", dir);
            savePoints();
        } catch (IOException e) {
            logStatus = "Log error: " + e.getMessage();
        }
    }

    @Override
    public void loop() {
        double intakeTarget = intakeRpm, outtakeTarget = outtakeRpm;
        if (intakeOn && Double.isFinite(intakeTarget) && intakeTarget > 0) intake.setVelocity(intakeTarget * intakeTicks / 60);
        else intake.setPower(0);
        if (outtakeOn && Double.isFinite(outtakeTarget) && outtakeTarget > 0) outtake.setVelocity(outtakeTarget * outtakeTicks / 60);
        else outtake.setPower(0);
        outtakeServo.setPosition(servoExtended ? 1 : 0);
        localizer.update();
        Pose pose = localizer.pose();
        GoBildaPinpointDriver.DeviceStatus status = pinpoint.getDeviceStatus();
        if (pinPoint != lastPinPoint || (gamepad1.a && !lastA)) {
            if (status != GoBildaPinpointDriver.DeviceStatus.READY || !Double.isFinite(pose.x())
                    || !Double.isFinite(pose.y()) || !Double.isFinite(pose.heading())) {
                pinStatus = "Point not saved: Pinpoint is not ready or pose is invalid";
            } else {
                points.add(new Pose(pose.x(), pose.y(), pose.heading()));
                pinStatus = "Saved point " + points.size();
                savePoints();
            }
        }
        if (removeRequest != lastRemoveRequest || (gamepad1.b && !lastB)) {
            int index = removeRequest != lastRemoveRequest ? removePoint - 1 : points.size() - 1;
            if (index >= 0 && index < points.size()) {
                points.remove(index);
                pinStatus = "Removed point " + (index + 1);
                savePoints();
            } else pinStatus = "No point at that number";
        }
        lastPinPoint = pinPoint;
        lastRemoveRequest = removeRequest;
        lastA = gamepad1.a;
        lastB = gamepad1.b;

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("Pinpoint status", status.toString());
        packet.put("X (in)", pose.x());
        packet.put("Y (in)", pose.y());
        packet.put("Heading (deg)", Math.toDegrees(pose.heading()));
        packet.put("Points", points.size());
        packet.put("Pin status", pinStatus);
        packet.put("Log", logStatus);
        packet.put("Intake", intakeOn ? intakeTarget + " RPM" : "Off");
        packet.put("Outtake", outtakeOn ? outtakeTarget + " RPM" : "Off");
        packet.put("Outtake servo", servoExtended ? 1 : 0);
        packet.put("Pin a point", "Increase pinPoint in Config or press gamepad 1 A");
        packet.put("Remove a point", "Set removePoint and increase removeRequest, or press gamepad 1 B for last point");
        for (int i = 0; i < points.size(); i++) {
            Pose p = points.get(i);
            packet.put("Point " + (i + 1), String.format(Locale.US, "new Pose(%.3f, %.3f, Math.toRadians(%.3f))", p.x(), p.y(), Math.toDegrees(p.heading())));
            packet.fieldOverlay().setFill("#e53935").fillCircle(72 - p.y(), p.x() - 72, 2);
            if (i > 0) {
                Pose previous = points.get(i - 1);
                packet.fieldOverlay().setStroke("#e53935").strokeLine(72 - previous.y(), previous.x() - 72, 72 - p.y(), p.x() - 72);
            }
        }
        double x = 72 - pose.y(), y = pose.x() - 72;
        double heading = pose.heading() + Math.PI / 2;
        packet.fieldOverlay().setStroke("#1976d2").strokeCircle(x, y, 9)
                .strokeLine(x, y, x + 9 * Math.cos(heading), y + 9 * Math.sin(heading));
        FtcDashboard.getInstance().sendTelemetryPacket(packet);

        telemetry.addData("Pinpoint status", status);
        telemetry.addData("Pose (in, in, deg)", "%.2f, %.2f, %.1f", pose.x(), pose.y(), Math.toDegrees(pose.heading()));
        telemetry.addData("Points", points.size());
        telemetry.addData("Pin status", pinStatus);
        telemetry.addData("Log", logStatus);
        telemetry.addLine("A: save point. B: remove last point.");
        telemetry.update();
    }

    private void savePoints() {
        if (logFile == null) return;
        File temporary = null;
        try {
            temporary = File.createTempFile("auto-poses-update-", ".tmp", logFile.getParentFile());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(temporary))) {
                writer.write("point,x_inches,y_inches,heading_degrees\n");
                for (int i = 0; i < points.size(); i++) {
                    Pose p = points.get(i);
                    writer.write(String.format(Locale.US, "%d,%.4f,%.4f,%.4f\n", i + 1, p.x(), p.y(), Math.toDegrees(p.heading())));
                }
            }
            if (!temporary.renameTo(logFile)) throw new IOException("Cannot replace pose file");
            logStatus = logFile.getAbsolutePath();
        } catch (IOException e) {
            logStatus = "Log error: " + e.getMessage();
        } finally {
            if (temporary != null && temporary.exists()) temporary.delete();
        }
    }

    @Override
    public void stop() {
        intakeOn = false;
        outtakeOn = false;
        if (intake != null) intake.setPower(0);
        if (outtake != null) outtake.setPower(0);
    }
}

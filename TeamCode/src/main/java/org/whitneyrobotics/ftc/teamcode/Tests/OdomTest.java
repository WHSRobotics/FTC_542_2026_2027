package org.whitneyrobotics.ftc.teamcode.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.Locale;

@TeleOp(name = "odomTest", group = "Tests")
public class OdomTest extends OpMode {
    private GoBildaPinpointDriver pinpoint;
    private long lastLogNanos;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setOffsets(0, 0, DistanceUnit.MM);
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD);
        pinpoint.resetPosAndIMU();
        telemetry.addLine("Keep the robot still while the Pinpoint calibrates.");
    }

    @Override
    public void init_loop() {
        report();
    }

    @Override
    public void loop() {
        report();
    }

    private void report() {
        pinpoint.update();
        Pose2D pose = pinpoint.getPosition();
        int xTicks = pinpoint.getEncoderX();
        int yTicks = pinpoint.getEncoderY();
        double x = pose.getX(DistanceUnit.INCH);
        double y = pose.getY(DistanceUnit.INCH);
        double heading = pose.getHeading(AngleUnit.DEGREES);
        String status = pinpoint.getDeviceStatus().toString();
        double frequency = pinpoint.getFrequency();

        telemetry.addLine("Keep still during calibration; move robot by hand to test.");
        telemetry.addData("Pinpoint status", status);
        telemetry.addData("Pinpoint frequency (Hz)", frequency);
        telemetry.addData("X pod ticks", xTicks);
        telemetry.addData("Y pod ticks", yTicks);
        telemetry.addData("X position (in)", x);
        telemetry.addData("Y position (in)", y);
        telemetry.addData("Heading (deg)", heading);
        telemetry.addLine("Pod directions and zero offsets are provisional.");
        telemetry.update();

        long now = System.nanoTime();
        if (now - lastLogNanos >= 200_000_000L) {
            RobotLog.ii("odomTest", String.format(Locale.US,
                    "status=%s frequency=%.1f xTicks=%d yTicks=%d xIn=%.3f yIn=%.3f headingDeg=%.2f",
                    status, frequency, xTicks, yTicks, x, y, heading));
            lastLogNanos = now;
        }
    }
}

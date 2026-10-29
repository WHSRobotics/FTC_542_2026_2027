package org.whitneyrobotics.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    // Match the working outtake test's Driver Station motor names.
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("fL");
        c.frontRightName.set("fR");
        c.backLeftName.set("bL");
        c.backRightName.set("bR");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.manualBrakeMode.set(true);
    });

    // Pedro 2 passed forwardPodY then strafePodX to Pinpoint.setOffsets(x, y).
    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-8.50394);
        c.yPodOffset.set(-0.944882);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.offsetUnits.set(DistanceUnit.INCH);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
    });

    // shortcut: Braking starts from the old coast deceleration; replace with Foresight AutoTune results before running paths.
    public static ForesightConfig foresightConfig = new ForesightConfig(c -> {
        c.forwardTranslational.set(Controller.proportional(0.1));
        c.strafeTranslational.set(Controller.proportional(0.1));
        c.headingFeedback.set(Controller.proportional(3.5));
        c.coast.set(Controller.proportionalFeedforward(1.0 / 64.17156694066806));
        c.brake.set(Controller.proportionalFeedforward(1.0 / 64.17156694066806));
        c.linearBrakeCoefficients.set(Matrix.diag(0, 0));
        c.quadraticBrakeCoefficients.set(Matrix.diag(1.0 / (2 * 32.553846162281644), 1.0 / (2 * 42.23239957266778)));
        c.headingBrakeCoefficients.set(Vector2D.cartesian(0, 0));
        c.maxAchievableForwardVelocity.set(64.17156694066806);
        c.maxAchievableStrafeVelocity.set(56.439832792507374);
        c.naturalForwardDeceleration.set(32.553846162281644);
        c.naturalStrafeDeceleration.set(42.23239957266778);
    });

    public static Mecanum drivetrain(HardwareMap h) {
        return new Mecanum(h, drivetrainConfig);
    }

    public static PinpointLocalizer localizer(HardwareMap h) {
        return new PinpointLocalizer(h, localizerConfig);
    }

    public static Foresight foresight() {
        return new Foresight(foresightConfig);
    }

    public static Follower create(HardwareMap h) {
        return new Follower(localizer(h), drivetrain(h), foresight());
    }
}

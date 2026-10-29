package org.whitneyrobotics.ftc.teamcode.pedro;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.Extensions.GamepadEx.GamepadEx;

public class PedroDrive {
    public final Follower follower;
    private boolean robotCentric;
    private double scalar = 1.25;

    public PedroDrive(HardwareMap hardwareMap) {
        follower = Constants.create(hardwareMap);
    }

    public PedroDrive(Follower follower) {
        this.follower = follower;
    }

    public void initialize() {
        follower.update();
    }

    public void update(GamepadEx gamepad) {
        double forward = gamepad.LEFT_STICK_Y.value() * scalar;
        double lateral = gamepad.LEFT_STICK_X.value() * scalar;
        double turn = gamepad.RIGHT_STICK_X.value() * scalar;
        if (robotCentric) {
            follower.manual(forward, lateral, turn);
        } else {
            DrivePowers powers = ManualDrive.fieldCentric(forward, lateral, turn, follower.pose().heading());
            follower.manual(powers);
        }
        follower.update();
    }

    public void driveMode() {
        robotCentric = !robotCentric;
    }

    public boolean getDriveMode() {
        return robotCentric;
    }

    public Pose getPose() {
        return follower.pose();
    }

    public void changeScalar() {
        scalar = scalar == 1.25 ? 0.5 : 1.25;
    }

    public double getScalar() {
        return scalar;
    }

    public void setStartingPose(Pose pose) {
        follower.setPose(pose);
    }
}

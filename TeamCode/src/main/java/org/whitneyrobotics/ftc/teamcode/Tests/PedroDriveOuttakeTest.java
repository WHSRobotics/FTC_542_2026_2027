package org.whitneyrobotics.ftc.teamcode.Tests;

import com.pedropathing.follower.ManualDrive;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Localizer;
import com.pedropathing.localization.MotionState;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Subsystems.RobotImpl;
import org.whitneyrobotics.ftc.teamcode.pedro.Constants;

@TeleOp(name = "Pedro 3 Drive + Outtake Test", group = "Tests")
public class PedroDriveOuttakeTest extends OpModeEx {
    private RobotImpl robot;
    private DcMotorEx flywheel;
    private IMU imu;
    private double startingHeading;
    private double ticksPerRev;
    private boolean fieldCentric;
    private int targetRpm;

    @Override
    public void initInternal() {
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP)));

        // shortcut: IMU supplies heading only; restore Pinpoint when X/Y localization is tuned.
        Localizer headingOnly = new Localizer() {
            private MotionState state = MotionState.zero();
            private double headingOffset;

            @Override
            public void setPose(Pose pose) {
                headingOffset = pose.heading() - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
                state = MotionState.zero().withPose(pose);
            }

            @Override
            public MotionState state() {
                return state;
            }

            @Override
            public void update() {
                Pose pose = state.pose();
                state = MotionState.zero().withPose(new Pose(pose.x(), pose.y(),
                        imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS) + headingOffset));
            }

            @Override
            public void reset() {
                setPose(Pose.zero());
            }
        };
        robot = RobotImpl.driveOnly(hardwareMap,
                new Follower(headingOnly, Constants.drivetrain(hardwareMap), Constants.foresight()));
        robot.drive.setStartingPose(new Pose(0, 0, 0));
        robot.drive.initialize();
        for (String name : new String[]{"fL", "bL", "fR", "bR"}) {
            hardwareMap.get(DcMotorEx.class, name).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

//        flywheel = hardwareMap.get(DcMotorEx.class, "outtake");
//        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        ticksPerRev = flywheel.getMotorType().getTicksPerRev();
//        if (ticksPerRev <= 0) throw new IllegalStateException("Configure the outtake motor encoder ticks per revolution");

    }

    @Override
    public void startInternal() {
        startingHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        gamepad1.BUMPER_RIGHT.onPress(() -> fieldCentric = !fieldCentric);
//        gamepad1.TRIANGLE.onPress(() -> setRpm(2000));
//        gamepad1.CIRCLE.onPress(() -> setRpm(2500));
//        gamepad1.CROSS.onPress(() -> setRpm(3000));
//        gamepad1.SQUARE.onPress(() -> setRpm(3500));
    }

//    private void setRpm(int rpm) {
//        targetRpm = targetRpm == rpm ? 0 : rpm;
//        if (targetRpm == 0) flywheel.setPower(0);
//        else flywheel.setVelocity(targetRpm * ticksPerRev / 60);
//    }

    @Override
    protected void loopInternal() {
        double forward = gamepad1.LEFT_STICK_Y.value();
        double lateral = -gamepad1.LEFT_STICK_X.value();
        double turn = -gamepad1.RIGHT_STICK_X.value();
        double speed = gamepad1.BUMPER_LEFT.value() ? 0.5 : 1.0;
        forward = forward * forward * forward * speed;
        lateral = lateral * lateral * lateral * speed;
        turn = turn * turn * turn * speed;

        if (fieldCentric) {
            double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS) - startingHeading;
            robot.drive.follower.manual(ManualDrive.fieldCentric(forward, lateral, turn, heading));
        } else {
            robot.drive.follower.manual(forward, lateral, turn);
        }
        robot.drive.follower.update();

        telemetryPro.addData("Drive mode", fieldCentric ? "Field-centric (IMU)" : "Robot-centric");
        telemetryPro.addData("Speed mode", gamepad1.BUMPER_LEFT.value() ? "Slow (50%)" : "Normal");
        telemetryPro.addData("Pedro mode", robot.drive.follower.mode());
        telemetryPro.addData("IMU heading", robot.drive.getPose().heading());
        telemetryPro.addData("Flywheel target RPM", targetRpm);
//        telemetryPro.addData("Flywheel actual RPM", flywheel.getVelocity() * 60 / ticksPerRev);
    }

    @Override
    public void stop() {
        if (flywheel != null) flywheel.setPower(0);
        if (robot != null) robot.drive.follower.stop();
        super.stop();
    }
}

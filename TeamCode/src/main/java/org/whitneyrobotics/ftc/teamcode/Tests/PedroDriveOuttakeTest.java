package org.whitneyrobotics.ftc.teamcode.Tests;

import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Subsystems.RobotImpl;

@TeleOp(name = "Pedro 3 Drive + Outtake Test", group = "Tests")
public class PedroDriveOuttakeTest extends OpModeEx {
    private RobotImpl robot;
    private DcMotorEx flywheel;
    private double startingHeading;
    private double ticksPerRev;
    private boolean fieldCentric;
    private int targetRpm;

    @Override
    public void initInternal() {
        robot = RobotImpl.driveOnly(hardwareMap);
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
        startingHeading = robot.drive.getPose().heading();
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
            double heading = robot.drive.getPose().heading() - startingHeading;
            robot.drive.follower.manual(ManualDrive.fieldCentric(forward, lateral, turn, heading));
        } else {
            robot.drive.follower.manual(forward, lateral, turn);
        }
        robot.drive.follower.update();

        telemetryPro.addData("Drive mode", fieldCentric ? "Field-centric (Pinpoint)" : "Robot-centric");
        telemetryPro.addData("Speed mode", gamepad1.BUMPER_LEFT.value() ? "Slow (50%)" : "Normal");
        telemetryPro.addData("Pedro mode", robot.drive.follower.mode());
        telemetryPro.addData("Pinpoint heading", robot.drive.getPose().heading());
        telemetryPro.addData("Pose X", robot.drive.getPose().x());
        telemetryPro.addData("Pose Y", robot.drive.getPose().y());
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

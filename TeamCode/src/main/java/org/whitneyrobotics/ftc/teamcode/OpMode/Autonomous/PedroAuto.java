package org.whitneyrobotics.ftc.teamcode.OpMode.Autonomous;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;

abstract class PedroAuto extends OpModeEx {
    // shortcut: Timed travel is approximate; measure the drive duration on this robot.
    private static final double DRIVE_SECONDS = 1.0;
    private static final double DRIVE_POWER = 0.4;
    private DcMotorEx frontLeft, backLeft, frontRight, backRight;
    private IMU imu;
    private double startHeading;

    @Override
    public void initInternal() {
        frontLeft = hardwareMap.get(DcMotorEx.class, "fL");
        backLeft = hardwareMap.get(DcMotorEx.class, "bL");
        frontRight = hardwareMap.get(DcMotorEx.class, "fR");
        backRight = hardwareMap.get(DcMotorEx.class, "bR");
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        for (DcMotorEx motor : new DcMotorEx[]{frontLeft, backLeft, frontRight, backRight}) {
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP)));
    }

    @Override
    public void startInternal() {
        startHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    @Override
    protected void loopInternal() {
        double error = AngleUnit.normalizeRadians(startHeading - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
        double turn = Range.clip(error * 0.8, -0.2, 0.2);
        double forward = getRuntime() < DRIVE_SECONDS ? DRIVE_POWER : 0;
        if (forward == 0) turn = 0;
        frontLeft.setPower(forward - turn);
        backLeft.setPower(forward - turn);
        frontRight.setPower(forward + turn);
        backRight.setPower(forward + turn);
        telemetryPro.addData("Heading error (deg)", Math.toDegrees(error));
        telemetryPro.addData("Auto", forward == 0 ? "Stopped" : "Leaving wall");
    }

    @Override
    public void stop() {
        if (frontLeft != null) frontLeft.setPower(0);
        if (backLeft != null) backLeft.setPower(0);
        if (frontRight != null) frontRight.setPower(0);
        if (backRight != null) backRight.setPower(0);
        super.stop();
    }
}

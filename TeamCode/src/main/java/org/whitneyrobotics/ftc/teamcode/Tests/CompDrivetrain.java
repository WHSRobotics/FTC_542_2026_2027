package org.whitneyrobotics.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "compDrivetrain", group = "Tests")
public class CompDrivetrain extends PedroDriveOuttakeTest {
    private double nextRumble = 15;

    @Override
    public void initInternal() {
        super.initInternal();
        robot.initOuttakeServo(hardwareMap);
    }

    @Override
    public void startInternal() {
        super.startInternal();
        robot.configureOuttakeServo(gamepad2);
        nextRumble = 15;
    }

    @Override
    protected void loopInternal() {
        super.loopInternal();
        if (getRuntime() >= nextRumble) {
            gamepad1.Vibrate(500);
            nextRumble = (Math.floor(getRuntime() / 15) + 1) * 15;
        }
    }
}

package org.whitneyrobotics.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "intakeTest", group = "Tests")
public class IntakeTest extends PedroDriveOuttakeTest {
    private DcMotorEx intake;

    @Override
    public void initInternal() {
        super.initInternal();
        intake = hardwareMap.get(DcMotorEx.class, "intake");
    }

    @Override
    public void startInternal() {
        super.startInternal();
        gamepad1.TRIANGLE.onPress(() -> intake.setPower(intake.getPower() == 0 ? 0.5 : 0));
    }

    @Override
    public void stop() {
        if (intake != null) intake.setPower(0);
        super.stop();
    }
}

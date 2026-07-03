package org.whitneyrobotics.ftc.teamcode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.pedroPathing.PedroDrive;

@TeleOp(name = "TestTeleop")
public class TestTeleop extends OpModeEx {

    public PedroDrive drive;

    @Override
    public void initInternal() {
        drive = new PedroDrive(hardwareMap);
        drive.initialize();
    }

    @Override
    public void start(){
        drive.startDrive();
    }

    @Override
    protected void loopInternal() {
        gamepad1.BUMPER_RIGHT.onPress(() -> {drive.driveMode();});
        telemetryPro.addData("robotcentric: ", drive.getDriveMode());
        drive.update(gamepad1);
    }
}

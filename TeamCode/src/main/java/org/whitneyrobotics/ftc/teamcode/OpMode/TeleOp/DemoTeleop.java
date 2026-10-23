package org.whitneyrobotics.ftc.teamcode.OpMode.TeleOp;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.pedro.PedroDrive;

@TeleOp(name = "Demo Teleop")
public class DemoTeleop extends OpModeEx {
    private PedroDrive drive;

    @Override
    public void initInternal() {
        drive = new PedroDrive(hardwareMap);
        drive.setStartingPose(new Pose(0, 0, 0));
        drive.driveMode(); // Start robot centric, as this OpMode did before.
        gamepad1.BUMPER_RIGHT.onPress(drive::driveMode);
        gamepad1.BUMPER_LEFT.onPress(drive::changeScalar);
    }

    @Override
    protected void loopInternal() {
        drive.update(gamepad1);
        telemetryPro.addData("robotCentric: ", drive.getDriveMode());
        telemetryPro.addData("speed scalar: ", drive.getScalar());
    }
}

package org.whitneyrobotics.ftc.teamcode.OpMode;

import static org.whitneyrobotics.ftc.teamcode.Extensions.GamepadEx.RumbleEffects.endgame;
import static org.whitneyrobotics.ftc.teamcode.Extensions.GamepadEx.RumbleEffects.matchEnd;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Extensions.TelemetryPro.LineItem;
import org.whitneyrobotics.ftc.teamcode.Subsystems.RobotImpl;

@TeleOp(name = "1DecodeTeleop")
public class DecodeTeleop extends OpModeEx {

    RobotImpl robot;

    @Override
    public void initInternal() {
        robot = RobotImpl.getInstance(hardwareMap);
        gamepad1.SQUARE.onPress(robot::switchAlliance);
        robot.teleOpInit();
        setupNotifications();
    }

    void setupNotifications() {
        addTemporalCallback(resolve -> {
            gamepad1.getEncapsulatedGamepad().runRumbleEffect(endgame);
            gamepad2.getEncapsulatedGamepad().runRumbleEffect(endgame);
            telemetryPro.addLine("Endgame approaching soon!", LineItem.Color.ROBOTICS, LineItem.RichTextFormat.BOLD).persistent();
            resolve.accept(!gamepad1.getEncapsulatedGamepad().isRumbling() &&
                    gamepad2.getEncapsulatedGamepad().isRumbling());
        }, 85000);

        addTemporalCallback(resolve -> {
            telemetryPro.removeLineByCaption("Endgame approaching soon!");
            resolve.accept(true);
        },90000);

        addTemporalCallback(resolve -> {
            //playSound("matchend",100f);
            gamepad1.getEncapsulatedGamepad().runRumbleEffect(matchEnd);
            gamepad2.getEncapsulatedGamepad().runRumbleEffect(matchEnd);
            telemetryPro.addLine("Match ends in 5 seconds!", LineItem.Color.FUCHSIA, LineItem.RichTextFormat.BOLD).persistent();
            resolve.accept(!gamepad1.getEncapsulatedGamepad().isRumbling() &&
                    gamepad2.getEncapsulatedGamepad().isRumbling());
        }, 113000);

        addTemporalCallback(resolve -> {
            //playSound("slay",100f);
            telemetryPro.removeLineByCaption("Match ends in 5 seconds!");
            telemetryPro.addLine("Match has ended.", LineItem.Color.LIME, LineItem.RichTextFormat.ITALICS).persistent();
            resolve.accept(true);
        },120000);
    }

    @Override
    public void start() {
        robot.drive.startDrive();
    }

    @Override
    protected void loopInternal() {
        //drive
        gamepad1.BUMPER_RIGHT.onPress(() -> robot.drive.driveMode());
        gamepad1.BUMPER_LEFT.onPress(() -> robot.drive.changeScalar());
        robot.drive.update(gamepad1);
        telemetryPro.addData("robotCentric: ", robot.drive.getDriveMode());
        telemetryPro.addData("x: ", robot.drive.getPose().getX());
        telemetryPro.addData("y: ", robot.drive.getPose().getY());
        telemetryPro.addData("heading: ", robot.drive.getPose().getHeading());
        telemetryPro.addData("drive scalar: ", robot.drive.getScalar());

        //subsystems
        //transfer
        if(gamepad2.LEFT_TRIGGER.value()>0){
            robot.transfer.run();
        } else{
            robot.transfer.stop();
        }

        //compression (on hold)
//        if (gamepad2.CROSS.value()) {
//            robot.comp.run();
//        } else{
//            robot.comp.stop();
//        }

        //intake (on hold)
        if(gamepad2.RIGHT_TRIGGER.value()>0){
            robot.intake.run();
        } else{
            robot.intake.stop();
        }

        //outtake
        gamepad2.CIRCLE.onPress(() -> {
            robot.outtake.run();
        });
        telemetryPro.addData("outtake speed: ", robot.outtake.getPower());
        gamepad1.SQUARE.onPress(() ->{
            robot.intake.run();
        });
        telemetryPro.update();
    }
}

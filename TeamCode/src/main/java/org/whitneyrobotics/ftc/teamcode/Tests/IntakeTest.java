package org.whitneyrobotics.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Subsystems.Intake;

@TeleOp(name = "intakeTest")
public class IntakeTest extends OpModeEx {
    public Intake intake;
    @Override
    public void initInternal() {
        intake = new Intake(hardwareMap);
    }

    @Override
    protected void loopInternal() {
        gamepad1.A.onPress(() -> {
            if(intake.getPower()==0){
                intake.run(1);
            } else{
                intake.stop();
            }
        });
    }

}

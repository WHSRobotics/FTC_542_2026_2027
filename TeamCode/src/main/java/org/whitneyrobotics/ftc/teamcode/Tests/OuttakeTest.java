package org.whitneyrobotics.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Subsystems.Intake;
import org.whitneyrobotics.ftc.teamcode.Subsystems.Outtake;

@TeleOp(name = "outtakeTest")
public class OuttakeTest extends OpModeEx {
    public Outtake outtake;
    @Override
    public void initInternal() {
        outtake = new Outtake(hardwareMap, true);
    }

    @Override
    protected void loopInternal() {
        gamepad1.B.onPress(() -> {
            if(outtake.getPower()==0){
                outtake.run(1);
            } else{
                outtake.stop();
            }
        });
    }

}

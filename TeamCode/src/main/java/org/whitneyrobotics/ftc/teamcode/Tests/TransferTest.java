package org.whitneyrobotics.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Subsystems.Intake;
import org.whitneyrobotics.ftc.teamcode.Subsystems.Outtake;
import org.whitneyrobotics.ftc.teamcode.Subsystems.Transfer;

@TeleOp(name = "transferTest")
public class TransferTest extends OpModeEx {
    public Transfer transfer;
    @Override
    public void initInternal() {
        transfer = new Transfer(hardwareMap);
    }

    @Override
    protected void loopInternal() {
        gamepad1.DPAD_UP.onPress(() -> {
            if(transfer.getPower()==0){
                transfer.run(1);
            } else{
                transfer.stop();
            }
        });
    }
}

package org.whitneyrobotics.ftc.teamcode.Tests.SubsystemTests;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;

@TeleOp
public class AllTest extends OpModeEx {

    public DcMotorEx intake, outtake;
    public CRServo flicker, tr, tl;

    @Override
    public void initInternal() {
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        outtake = hardwareMap.get(DcMotorEx.class, "outtake");
        flicker = hardwareMap.get(CRServo.class, "flicker");
        tr = hardwareMap.get(CRServo.class, "tr");
        tl = hardwareMap.get(CRServo.class, "tl");
    }

    @Override
    protected void loopInternal() {

        //transfer
        gamepad1.TRIANGLE.onPress(() -> {
            if(Math.abs(tl.getPower())==1){
                tl.setPower(0);
                tr.setPower(0);
            } else {
                tl.setPower(-1);
                tr.setPower(1);
            }
        });

        //flicker
        if(gamepad1.CROSS.value()){
            flicker.setPower(1);
        } else{
            flicker.setPower(0);
        }

        //outtake
        gamepad1.CIRCLE.onPress(() -> {
            if(outtake.getPower() == 1){
                outtake.setPower(0);
            } else{
                outtake.setPower(1);
            }
        });

        //intake
        gamepad1.SQUARE.onPress(() -> {
            if(intake.getPower() == 1){
                intake.setPower(0);
            } else{
                intake.setPower(1);
            }
        });
    }
}

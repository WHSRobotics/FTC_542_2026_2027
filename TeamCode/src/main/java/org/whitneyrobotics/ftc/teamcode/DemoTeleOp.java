package org.whitneyrobotics.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Subsystems.Intake;

@TeleOp(name = "DemeTeleOp")
public class DemoTeleOp extends OpModeEx{

    DcMotorEx fL, bL, fR, bR;
    public Intake intake;

    @Override
    public void initInternal() {
        fL = hardwareMap.get(DcMotorEx.class, "fL");
        bL = hardwareMap.get(DcMotorEx.class, "bL");
        fR = hardwareMap.get(DcMotorEx.class, "fR");
        bR = hardwareMap.get(DcMotorEx.class, "bR");
        fL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake = new Intake(hardwareMap);
    }

    @Override
    protected void loopInternal() {
        double y = gamepad1.LEFT_STICK_Y.value();
        double x = gamepad1.LEFT_STICK_X.value();
        double rx = gamepad1.RIGHT_STICK_X.value();

        fL.setPower(-(y+x+rx));
        bL.setPower(-(y-x+rx));
        fR.setPower(y-x-rx);
        bR.setPower(y+x-rx);

        gamepad1.A.onPress(() -> {
            if(intake.getPower()==0){
                intake.run(1);
            } else{
                intake.stop();
            }
        });

    }
}

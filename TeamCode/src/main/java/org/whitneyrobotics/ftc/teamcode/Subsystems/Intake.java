package org.whitneyrobotics.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public DcMotorEx intake;

    public Intake(HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotorEx.class, "intake");
    }

    public void run(double scalar) {
        intake.setPower(scalar);
    }
    public void stop(){
        intake.setPower(0);
    }
    public double getPower(){
        return intake.getPower();
    }
}
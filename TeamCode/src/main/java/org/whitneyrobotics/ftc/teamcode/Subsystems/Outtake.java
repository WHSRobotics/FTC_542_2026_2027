package org.whitneyrobotics.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Outtake {
    public DcMotorEx outtakeMotor;

    public Outtake(HardwareMap hardwareMap) {
        outtakeMotor = hardwareMap.get(DcMotorEx.class, "outtake");
    }

    public void run() {
        if (outtakeMotor.getPower() == 0) {
            outtakeMotor.setPower(1);
        } else {
            outtakeMotor.setPower(0);
        }
    }

    public double getPower(){
        return outtakeMotor.getPower();
    }
}
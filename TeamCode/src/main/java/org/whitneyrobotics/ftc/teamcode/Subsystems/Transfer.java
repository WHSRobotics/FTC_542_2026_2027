package org.whitneyrobotics.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Transfer {
    DcMotorEx transfer;
    public Transfer(HardwareMap hardwareMap){
        transfer=hardwareMap.get(DcMotorEx.class, "transfer");
    }

    public void run(double scalar){
        transfer.setPower(scalar);
    }

    public void stop(){
        transfer.setPower(0);
    }
    public double getPower(){
        return transfer.getPower();
    }
}

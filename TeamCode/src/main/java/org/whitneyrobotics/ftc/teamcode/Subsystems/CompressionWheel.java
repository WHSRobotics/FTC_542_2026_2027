package org.whitneyrobotics.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class CompressionWheel {
    public CRServo comp;

    public CompressionWheel(HardwareMap hardwareMap) {
        comp = hardwareMap.get(CRServo.class, "comp");
    }

    //control
    public void run() {
        comp.setPower(1);
    }

    public void stop(){
        comp.setPower(0);
    }
}
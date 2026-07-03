package org.whitneyrobotics.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Transfer {
    public CRServo tl, tr;

    public Transfer(HardwareMap hardwareMap) {
        tl = hardwareMap.get(CRServo.class, "tl");
        tr = hardwareMap.get(CRServo.class, "tr");
    }

    public void run() {
        tl.setPower(-1);
        tr.setPower(1);
    }

    public void stop(){
        tl.setPower(0);
        tr.setPower(0);
    }
}

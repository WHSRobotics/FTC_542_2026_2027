package org.whitneyrobotics.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Gate {
    Servo gate;
    double open=1; //replace
    double closed=0; //replace
    boolean isOpen=false;
    public Gate(HardwareMap hardwareMap){
        gate=hardwareMap.get(Servo.class, "gate");

    }

    public void toggle(){
        if (isOpen){
            gate.setPosition(closed);
        }else {
            gate.setPosition(open);
        }
    }
    public void open(){
        gate.setPosition(open);
    }
    public void close(){
        gate.setPosition(closed);
    }
}

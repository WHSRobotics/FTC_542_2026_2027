// Written by: Christopher Gholmieh
// Package:
package org.whitneyrobotics.ftc.teamcode.Subsystems;

// Imports:
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

// Outtake:
public class OuttakeUnofficial {
    // Constants:
    private static final double DOWN = 0.2;
    private static final double UP = 0.8;

    // Variables (Declaration):
    // Motor:
    private DcMotor outtake;

    // Servo:
    private CRServo top_left, top_right;
    private Servo flick;

    // Constructor:
    public OuttakeUnofficial(HardwareMap hardware_map) {
        // Variables (Assignment):
        // Motor:
        outtake = hardware_map.get(DcMotor.class, "o");

        // Servo:
        top_left = hardware_map.get(CRServo.class, "tL");
        top_right = hardware_map.get(CRServo.class, "tR");
        flick = hardware_map.get(Servo.class, "flick");

        // Initialization:
        flick.setPosition(DOWN);
    }

    // Functions:
    public void toggle_outtake_power() {
        if (outtake.getPower() == 1) {
            outtake.setPower(0);
        } else {
            outtake.setPower(1);
        }
    }

    public void transfer() {
        if (Math.abs(top_left.getPower()) == 1) {
            top_left.setPower(0);
            top_right.setPower(0);
        } else {
            top_left.setPower(1);
            top_right.setPower(-1);
        }
    }

    public void flicker() {
        if (flick.getPosition() == UP) {
            flick.setPosition(DOWN);
        } else {
            flick.setPosition(UP);
        }
    }
}
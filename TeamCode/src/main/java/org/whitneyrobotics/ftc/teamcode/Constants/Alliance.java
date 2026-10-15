package org.whitneyrobotics.ftc.teamcode.Constants;

public enum Alliance {
    RED(1, Math.toRadians(90)),
    BLUE(1, Math.toRadians(270));
    public final int allianceCoefficient;
    public final double headingAngle;
    Alliance(int allianceCoefficient, double headingAngle){
        this.allianceCoefficient = allianceCoefficient;
        this.headingAngle = headingAngle;
    }
}

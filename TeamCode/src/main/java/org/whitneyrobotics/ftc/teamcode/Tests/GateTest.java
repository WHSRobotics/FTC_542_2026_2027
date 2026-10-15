package org.whitneyrobotics.ftc.teamcode.Tests;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Subsystems.Gate;

public class GateTest extends OpModeEx {
    public Gate gate;

    @Override
    public void initInternal() {
        gate=new Gate(hardwareMap);
    }

    @Override
    protected void loopInternal() {
        gamepad1.DPAD_DOWN.onPress(()->{gate.toggle();});
    }
}

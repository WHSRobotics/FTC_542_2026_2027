package org.whitneyrobotics.ftc.teamcode.pedro;

import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.whitneyrobotics.ftc.teamcode.pedro.procedures.ForesightTuner;
import org.whitneyrobotics.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.whitneyrobotics.ftc.teamcode.pedro.procedures.PinpointTuner;
import org.whitneyrobotics.ftc.teamcode.pedro.procedures.Tests;

public class Tuning {
    @Tuner
    public static Procedure mecanum() {
        return new MecanumTuner();
    }

    @Tuner
    public static Procedure pinpoint() {
        return new PinpointTuner();
    }

    @Tuner
    public static Procedure foresight() {
        return new ForesightTuner(Constants::localizer, Constants::drivetrain);
    }

    @Tuner
    public static Procedure tests() {
        return new Tests(Constants::drivetrain, Constants::localizer, Constants::foresight);
    }
}

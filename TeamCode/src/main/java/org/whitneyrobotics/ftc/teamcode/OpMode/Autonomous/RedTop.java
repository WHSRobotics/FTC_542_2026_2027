package org.whitneyrobotics.ftc.teamcode.OpMode.Autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Red Top")
public class RedTop extends PedroAuto {
    private static final Pose START = new Pose(9, 132, 0);
    private static final Pose PARK = new Pose(16, 108, 0);

    @Override
    protected Pose startPose() {
        return START;
    }

    @Override
    protected Path parkPath() {
        return Paths.line(START, PARK).constant(START);
    }
}

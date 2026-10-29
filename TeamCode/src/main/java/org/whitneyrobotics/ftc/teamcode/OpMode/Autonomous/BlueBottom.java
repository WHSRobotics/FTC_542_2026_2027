package org.whitneyrobotics.ftc.teamcode.OpMode.Autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Blue Bottom")
public class BlueBottom extends PedroAuto {
    private static final Pose START = new Pose(135, 12, Math.PI);
    private static final Pose PARK = new Pose(128, 36, Math.PI);

    @Override
    protected Pose startPose() {
        return START;
    }

    @Override
    protected Path parkPath() {
        return Paths.line(START, PARK).constant(START);
    }
}

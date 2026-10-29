package org.whitneyrobotics.ftc.teamcode.OpMode.Autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Red Bottom")
public class RedBottom extends PedroAuto {
    private static final Pose START = new Pose(9, 24, 0);
    private static final Pose CLEAR_WALL = new Pose(30, 24, 0);
    private static final Pose APPROACH = new Pose(30, 108, 0);
    private static final Pose PARK = new Pose(16, 108, 0);

    @Override
    protected Pose startPose() {
        return START;
    }

    @Override
    protected Path parkPath() {
        return Paths.path(
                Paths.line(START, CLEAR_WALL),
                Paths.line(CLEAR_WALL, APPROACH),
                Paths.line(APPROACH, PARK)
        ).constant(START);
    }
}

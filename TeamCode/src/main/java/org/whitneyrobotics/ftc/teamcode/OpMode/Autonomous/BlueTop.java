package org.whitneyrobotics.ftc.teamcode.OpMode.Autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Blue Top")
public class BlueTop extends PedroAuto {
    private static final Pose START = new Pose(135, 120, Math.PI);
    private static final Pose CLEAR_WALL = new Pose(114, 120, Math.PI);
    private static final Pose APPROACH = new Pose(114, 36, Math.PI);
    private static final Pose PARK = new Pose(128, 36, Math.PI);

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

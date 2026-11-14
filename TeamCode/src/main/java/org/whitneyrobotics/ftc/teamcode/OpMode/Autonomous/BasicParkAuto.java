package org.whitneyrobotics.ftc.teamcode.OpMode.Autonomous;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.api.Paths.path;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Basic Park Auto")
public class BasicParkAuto extends ParkAuto {
    private final PoseFactory poses = PoseFactory.degrees();
    private final Pose start = poses.of(8.494, 78.7903, 90);
    private final Pose point1 = poses.of(22.2311, 78.3113, 0);
    private final Pose point2 = poses.of(22.8517, 94.3412, 0);
    private final Pose point3 = poses.of(11.5692, 94.4794, 0);

    @Override
    protected Pose startingPose() {
        return start;
    }

    @Override
    protected Path route() {
        return path(line(start, point1).constant(point1),
                line(point1, point2).constant(point2),
                line(point2, point3).constant(point3));
    }
}

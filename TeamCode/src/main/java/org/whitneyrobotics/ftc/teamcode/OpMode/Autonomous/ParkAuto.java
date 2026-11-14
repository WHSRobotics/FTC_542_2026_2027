package org.whitneyrobotics.ftc.teamcode.OpMode.Autonomous;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.api.Paths.path;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.whitneyrobotics.ftc.teamcode.pedro.Constants;

@Autonomous(name = "Park Auto")
public class ParkAuto extends OpMode {
    private final PoseFactory poses = PoseFactory.degrees();
    private final Pose start = poses.of(57.5888, 7.8722, 90);
    private final Pose point1 = poses.of(22.6762, 23.2871, 180);
    private final Pose point2 = poses.of(22.8517, 94.3412, 89.8585);
    private final Pose point3 = poses.of(8.6125, 96.3702, 171.8902);
    private Follower follower;
    private GoBildaPinpointDriver pinpoint;
    private boolean fault;

    protected Pose startingPose() {
        return start;
    }

    protected Path route() {
        return path(line(start, point1).linear(start, point1),
                line(point1, point2).tangent(), line(point2, point3).tangent());
    }

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, Constants.localizerConfig.name.get());
        follower.setPose(startingPose());
        telemetry.addLine("Keep robot still during Pinpoint calibration.");
        telemetry.addData("Start pose", startingPose());
        telemetry.update();
    }

    @Override
    public void start() {
        follower.setPose(startingPose());
        follower.update();
        if (pinpoint.getDeviceStatus() != GoBildaPinpointDriver.DeviceStatus.READY) {
            fault = true;
            return;
        }
        follower.follow(route());
    }

    @Override
    public void loop() {
        if (pinpoint.getDeviceStatus() != GoBildaPinpointDriver.DeviceStatus.READY) fault = true;
        if (fault) follower.stop();
        follower.update();
        telemetry.addData("Pinpoint", pinpoint.getDeviceStatus());
        telemetry.addData("Status", fault ? "Stopped: Pinpoint not ready" : follower.mode());
        telemetry.addData("Pose", follower.pose());
        telemetry.update();
    }

    @Override
    public void stop() {
        if (follower != null) follower.stop();
        for (String name : new String[]{"fL", "bL", "fR", "bR"}) {
            hardwareMap.get(DcMotor.class, name).setPower(0);
        }
    }
}

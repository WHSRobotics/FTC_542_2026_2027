package org.whitneyrobotics.ftc.teamcode.OpMode.Autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Subsystems.RobotImpl;
import org.whitneyrobotics.ftc.teamcode.pedroPathing.Constants;
import org.whitneyrobotics.ftc.teamcode.pedroPathing.PedroDrive;

@Autonomous(name = "Blue Bottom")
public class BlueBottom extends OpModeEx {
    private Follower follower;
    private PedroDrive drive;
    private RobotImpl robot;
    private int currentCallback = 0;
    Pose startPose=new Pose();

    private PathChain path;


    private boolean unstarted;

    private Timer actionTimer = new Timer();

    public void buildPaths() {
        path = follower.pathBuilder()
                .build();
    }


    @Override
    public void initInternal() {
        robot = RobotImpl.getInstance(hardwareMap);
        drive = new PedroDrive(hardwareMap);
        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);
        unstarted=true;
    }

    @Override
    protected void loopInternal() {

        // Handle shooting action
        follower.update();

        if (unstarted) {
            follower.followPath(path, true);
            unstarted = false;
        }

        telemetryPro.addData("x", follower.getPose().getX());
        telemetryPro.addData("y", follower.getPose().getY());
        telemetryPro.addData("Current Callback", currentCallback);
        telemetryPro.addData("Timer", actionTimer.getElapsedTimeSeconds());
    }
}
package org.whitneyrobotics.ftc.teamcode.OpMode.Autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.pedro.Constants;

abstract class PedroAuto extends OpModeEx {
    protected Follower follower;

    // shortcut: Start/park poses assume an 18 in. robot footprint; measure the robot and tune Pinpoint before match use.
    protected abstract Pose startPose();

    protected abstract Path parkPath();

    @Override
    public void initInternal() {
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose());
        follower.update();
    }

    @Override
    public void startInternal() {
        follower.follow(parkPath());
    }

    @Override
    protected void loopInternal() {
        if (getRuntime() >= 28.5) {
            follower.stop();
        } else {
            follower.update();
            if (!follower.isBusy()) follower.stop();
        }
        telemetryPro.addData("x", follower.pose().x());
        telemetryPro.addData("y", follower.pose().y());
        telemetryPro.addData("Pedro mode", follower.mode());
    }

    @Override
    public void stop() {
        if (follower != null) follower.stop();
        super.stop();
    }
}

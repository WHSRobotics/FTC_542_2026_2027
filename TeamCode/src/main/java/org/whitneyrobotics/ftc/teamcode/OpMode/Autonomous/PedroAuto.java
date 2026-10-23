package org.whitneyrobotics.ftc.teamcode.OpMode.Autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;

import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.pedro.Constants;

// shortcut: The four existing autos had empty paths; add measured routes before match use.
abstract class PedroAuto extends OpModeEx {
    protected Follower follower;

    @Override
    public void initInternal() {
        follower = Constants.create(hardwareMap);
        follower.setPose(new Pose(0, 0, 0));
        follower.update();
    }

    @Override
    protected void loopInternal() {
        follower.update();
        telemetryPro.addData("x", follower.pose().x());
        telemetryPro.addData("y", follower.pose().y());
    }
}

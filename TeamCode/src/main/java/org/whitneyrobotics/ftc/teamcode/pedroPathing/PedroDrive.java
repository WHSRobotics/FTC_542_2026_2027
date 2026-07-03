package org.whitneyrobotics.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.whitneyrobotics.ftc.teamcode.Extensions.GamepadEx.GamepadEx;

import java.util.function.Supplier;

public class PedroDrive {

    public Follower follower;
    public boolean robotCentric = false;
    public Supplier<PathChain> pathChain;
    public double scalar = 1;

    public PedroDrive(HardwareMap hardwareMap) {
        follower = Constants.createFollower(hardwareMap);
    }

    public void initialize(){
        follower.setStartingPose(new Pose(0,0,0));
        pathChain = () -> follower.pathBuilder() //Lazy Curve Generation
                .addPath(new Path(new BezierLine(follower::getPose, new Pose(45, 98))))
                .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(follower::getHeading, Math.toRadians(45), 0.8))
                .setBrakingStart(2.5)
                .setBrakingStrength(1)
                .build();
        follower.update();
    }

    public void startDrive(){
        follower.startTeleopDrive();
    }

    public void update(GamepadEx gamepadEx) {
        follower.update();
        follower.setTeleOpDrive(gamepadEx.LEFT_STICK_Y.value() * getScalar(), -gamepadEx.LEFT_STICK_X.value() * getScalar(), -gamepadEx.RIGHT_STICK_X.value() * getScalar(), robotCentric);
    }

    public void updateAuto(){
        follower.update();
    }

    public void driveMode(){
        robotCentric = !robotCentric;
    }

    public boolean getDriveMode(){
        return robotCentric;
    }

    public Pose getPose(){
        return follower.getPose();
    }

    public boolean isBusy(){
        return follower.isBusy();
    }

    public void changeScalar(){
        if(scalar == 1){
            scalar = 0.5;
        } else{
            scalar = 1;
        }
    }

    public double getScalar(){
        return scalar;
    }

    public void setStartingPose(Pose startingPose){
        follower.setStartingPose(startingPose);
    }

    public void followPath(Path path){
        follower.followPath(path);
    }
}
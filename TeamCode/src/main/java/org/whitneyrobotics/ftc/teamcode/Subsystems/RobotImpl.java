package org.whitneyrobotics.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.pedropathing.math.Pose;
import com.pedropathing.follower.Follower;

import org.whitneyrobotics.ftc.teamcode.Constants.Alliance;
import org.whitneyrobotics.ftc.teamcode.pedro.PedroDrive;

public class RobotImpl {

    public static Pose poseMemory = new Pose(0, 0, 0);
    private static RobotImpl instance = null;
    public Alliance alliance = Alliance.RED;
    //devices
    public PedroDrive drive;
    public Intake intake;
    //    public LimelightImpl ll;
//    public Notifier notifier;
    public double outtakev1=1;
    public double outtakev2=0.6;
    public double systemScalar = 1;
    public double targetOuttakeVelocity = 0;


    private RobotImpl(HardwareMap hardwareMap, boolean withIntake, Follower follower) {
        drive = follower == null ? new PedroDrive(hardwareMap) : new PedroDrive(follower);
        //devices
        if (withIntake) intake = new Intake(hardwareMap);
//        ll=new LimelightImpl(hardwareMap);
//        notifier = new Notifier();
    }

    public void switchAlliance(){
        alliance = alliance == Alliance.RED ? Alliance.BLUE : Alliance.RED;
    }

    public void teleOpInit(){
        poseMemory = drive.getPose();
        drive.initialize();
    }

    public static RobotImpl getInstance(){
        return instance;
    }

    public static RobotImpl getInstance(HardwareMap hardwareMap){
        init(hardwareMap);
        return instance;
    }

    public static void init(HardwareMap hardwareMap) {
        instance = new RobotImpl(hardwareMap, true, null);
    }

    public static RobotImpl driveOnly(HardwareMap hardwareMap, Follower follower) {
        return new RobotImpl(hardwareMap, false, follower);
    }

//    public void update(){
//        //update devices
//    }
}

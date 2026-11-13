package org.whitneyrobotics.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.pedropathing.math.Pose;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.localization.Localizer;
import com.pedropathing.localization.MotionState;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import org.whitneyrobotics.ftc.teamcode.Constants.Alliance;
import org.whitneyrobotics.ftc.teamcode.Extensions.GamepadEx.GamepadEx;
import org.whitneyrobotics.ftc.teamcode.pedro.PedroDrive;
import org.whitneyrobotics.ftc.teamcode.pedro.Constants;

public class RobotImpl {

    public static Pose poseMemory = new Pose(0, 0, 0);
    private static RobotImpl instance = null;
    public Alliance alliance = Alliance.RED;
    //devices
    public PedroDrive drive;
    public Intake intake;
    public DcMotorEx outtakeMotor, intakeMotor;
    public Servo outtakeServo;
    private boolean servoExtended;
    private boolean intakeLatched;
    private boolean driverIntakePressed, operatorOuttakePressed;
    public double outtakeTicks, intakeTicks;
    public int outtakeRpm = 2000, intakeRpm = 2000;
    public boolean outtakeOn, intakeOn;
    public boolean intakeReversed, outtakeReversed;
    private boolean intakeTriggerPressed, outtakeTriggerPressed;
    public IMU imu;
    public double startingHeading;
    public boolean fieldCentric;
    private boolean pinpointHeading;
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

    public static RobotImpl imuDriveOnly(HardwareMap hardwareMap) {
        return competitionDrive(hardwareMap, false);
    }

    public static RobotImpl competitionDrive(HardwareMap hardwareMap, boolean usePinpoint) {
        IMU imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP)));
        // shortcut: Heading only; use Pinpoint when position localization is tuned.
        Localizer localizer = usePinpoint ? Constants.localizer(hardwareMap) : new Localizer() {
            private MotionState state = MotionState.zero();
            private double offset;
            public void setPose(Pose pose) {
                offset = pose.heading() - imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
                state = MotionState.zero().withPose(pose);
            }
            public MotionState state() { return state; }
            public void update() {
                Pose pose = state.pose();
                state = MotionState.zero().withPose(new Pose(pose.x(), pose.y(), imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS) + offset));
            }
            public void reset() { setPose(Pose.zero()); }
        };
        RobotImpl robot = driveOnly(hardwareMap, new Follower(localizer, Constants.drivetrain(hardwareMap), Constants.foresight()));
        robot.imu = imu;
        robot.pinpointHeading = usePinpoint;
        robot.drive.setStartingPose(Pose.zero());
        robot.drive.initialize();
        robot.startingHeading = robot.driveHeading();
        for (String name : new String[]{"fL", "bL", "fR", "bR"}) {
            hardwareMap.get(DcMotorEx.class, name).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        return robot;
    }

    public void configureDriveControls(GamepadEx gamepad) {
        gamepad.SQUARE.onPress(() -> fieldCentric = !fieldCentric);
        gamepad.CROSS.onPress(() -> startingHeading = driveHeading());
    }

    private double driveHeading() {
        return pinpointHeading ? drive.getPose().heading() : imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public void updateImuDrive(GamepadEx gamepad) {
        double forward = gamepad.LEFT_STICK_Y.value();
        double lateral = -gamepad.LEFT_STICK_X.value();
        double turn = -gamepad.RIGHT_STICK_X.value();
        double speed = gamepad.BUMPER_LEFT.value() ? 0.5 : 1.0;
        forward = forward * forward * forward * speed;
        lateral = lateral * lateral * lateral * speed;
        turn = turn * turn * turn * speed;
        if (fieldCentric) {
            double heading = driveHeading() - startingHeading;
            drive.follower.manual(ManualDrive.fieldCentric(forward, lateral, turn, heading));
        } else drive.follower.manual(forward, lateral, turn);
        drive.follower.update();
    }

    public void initCompetitionMotors(HardwareMap hardwareMap) {
        outtakeMotor = hardwareMap.get(DcMotorEx.class, "outtake");
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");
        outtakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtakeTicks = outtakeMotor.getMotorType().getTicksPerRev();
        intakeTicks = intakeMotor.getMotorType().getTicksPerRev();
        if (outtakeTicks <= 0 || intakeTicks <= 0) throw new IllegalStateException("Configure encoder motor types for outtake and intake");
    }

    public void initOuttakeServo(HardwareMap hardwareMap) {
        outtakeServo = hardwareMap.get(Servo.class, "outtakeServo");
        servoExtended = false;
        outtakeServo.setPosition(0);
    }

    public void configureOuttakeServo(GamepadEx gamepad) {
        gamepad.BUMPER_RIGHT.onPress(() -> {
            servoExtended = !servoExtended;
            outtakeServo.setPosition(servoExtended ? 1 : 0);
        });
    }

    public void configureMatchControls(GamepadEx gamepad) {
        intakeRpm = 250;
        outtakeRpm = 3150;
        intakeLatched = false;
        outtakeOn = false;
        driverIntakePressed = false;
        operatorOuttakePressed = false;
        configureOuttakeServo(gamepad);
    }

    public void updateMatchMotors(GamepadEx driver, GamepadEx operator) {
        boolean driverIntake = driver.LEFT_TRIGGER.value() > 0.5;
        boolean operatorOuttake = operator.RIGHT_TRIGGER.value() > 0.5;
        if (driverIntake && !driverIntakePressed) intakeLatched = !intakeLatched;
        if (operatorOuttake && !operatorOuttakePressed) outtakeOn = !outtakeOn;
        driverIntakePressed = driverIntake;
        operatorOuttakePressed = operatorOuttake;
        intakeOn = intakeLatched || operator.LEFT_TRIGGER.value() > 0.5;
        updateCompetitionMotors();
    }

    public void updateCompetitionMotors(GamepadEx gamepad) {
        boolean intakePressed = gamepad.LEFT_TRIGGER.value() > 0.5;
        boolean outtakePressed = gamepad.RIGHT_TRIGGER.value() > 0.5;
        if (intakePressed && !intakeTriggerPressed) intakeReversed = !intakeReversed;
        if (outtakePressed && !outtakeTriggerPressed) outtakeReversed = !outtakeReversed;
        intakeTriggerPressed = intakePressed;
        outtakeTriggerPressed = outtakePressed;
        if (outtakeOn && outtakeRpm > 0) outtakeMotor.setVelocity((outtakeReversed ? -1 : 1) * outtakeRpm * outtakeTicks / 60);
        else outtakeMotor.setPower(0);
        if (intakeOn && intakeRpm > 0) intakeMotor.setVelocity((intakeReversed ? -1 : 1) * intakeRpm * intakeTicks / 60);
        else intakeMotor.setPower(0);
    }

    public void updateCompetitionMotors() {
        if (outtakeOn && outtakeRpm > 0) outtakeMotor.setVelocity(outtakeRpm * outtakeTicks / 60);
        else outtakeMotor.setPower(0);
        if (intakeOn && intakeRpm > 0) intakeMotor.setVelocity(intakeRpm * intakeTicks / 60);
        else intakeMotor.setPower(0);
    }

    public void configureCompetitionControls(GamepadEx gamepad) {
        gamepad.TRIANGLE.onPress(() -> outtakeOn = true);
        gamepad.CROSS.onPress(() -> outtakeOn = false);
        gamepad.SQUARE.onPress(() -> outtakeRpm = Math.max(0, outtakeRpm - 500));
        gamepad.CIRCLE.onPress(() -> outtakeRpm += 500);
        gamepad.DPAD_UP.onPress(() -> intakeOn = true);
        gamepad.DPAD_DOWN.onPress(() -> intakeOn = false);
        gamepad.DPAD_LEFT.onPress(() -> intakeRpm = Math.max(0, intakeRpm - 500));
        gamepad.DPAD_RIGHT.onPress(() -> intakeRpm += 500);
    }

    public void stopCompetitionMotors() {
        if (outtakeMotor != null) outtakeMotor.setPower(0);
        if (intakeMotor != null) intakeMotor.setPower(0);
    }

//    public void update(){
//        //update devices
//    }
}

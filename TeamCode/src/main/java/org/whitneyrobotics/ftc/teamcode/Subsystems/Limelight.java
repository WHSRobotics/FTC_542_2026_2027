package org.whitneyrobotics.ftc.teamcode.Subsystems;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Limelight {
    Limelight3A limelight;
    ArrayList<Integer> goodApriltagValues=new ArrayList<>();


    public Limelight(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(60);
        limelight.start();
        goodApriltagValues.add(1);
    }

    //current apriltag pipeline is 0
    public Map<Integer, ArrayList<Double>> getApriltags(int aprilTagPipeline) {
        limelight.pipelineSwitch(aprilTagPipeline);
        LLResult result = limelight.getLatestResult();
        List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
        Map<Integer, ArrayList<Double>> aprilTags = new HashMap<Integer, ArrayList<Double>>();
        for (LLResultTypes.FiducialResult fiducial : fiducials) {
            ArrayList<Double> addList = new ArrayList<Double>();
            addList.add(fiducial.getTargetXDegrees());
            addList.add(fiducial.getTargetYDegrees());
            addList.add(fiducial.getTargetArea());
            aprilTags.put(fiducial.getFiducialId(), addList);
        }
        return aprilTags;
    }

    public ArrayList<Float> getDepotValues(float tx, float ty, float td, float camAngle) {
        ArrayList<Float> values = new ArrayList<>();
        float convFactor = (float) (6.33 / Math.sqrt(td * 307200));
        float robotDistToDepotStraight = (float) (0.5 * (240 * convFactor + 320 * convFactor) * Math.tan(Math.toRadians(65.875)));
        float robotDistToDepot = (float) (robotDistToDepotStraight / Math.cos(Math.toRadians(tx)));
        float robotDistToDepotFixed = (float) (robotDistToDepot * Math.cos(Math.toRadians(ty + camAngle)));
        values.add(tx);
        values.add(robotDistToDepotFixed);
        return values;
    }
    public double getDist(float tx, float ty, float td, float camAngle){
        ArrayList<Float> values = new ArrayList<>();
        float convFactor = (float) (6.33 / Math.sqrt(td * 307200));
        float robotDistToDepotStraight = (float) (0.5 * (240 * convFactor + 320 * convFactor) * Math.tan(Math.toRadians(65.875)));
        float robotDistToDepot = (float) (robotDistToDepotStraight / Math.cos(Math.toRadians(tx)));
        float robotDistToDepotFixed = (float) (robotDistToDepot * Math.cos(Math.toRadians(ty + camAngle)));
        return robotDistToDepotFixed;
    }

    public ArrayList<Float> getDepotVector(float tx, float ty, float td, float camAngle) {
        ArrayList<Float> valuesToDepot=getDepotValues(tx,ty,td,camAngle);
        ArrayList<Float> values=new ArrayList<>();
        float x=(float) (Math.cos(Math.toRadians(valuesToDepot.get(0)))*valuesToDepot.get(1));
        float y=(float) (Math.sin(Math.toRadians(valuesToDepot.get(0)))*valuesToDepot.get(1));
        values.add(x);
        values.add(y);
        return values;
    }

    public double getSpeed(boolean redAlliance, float camAngle) {
        Map<Integer, ArrayList<Double>> aprilTags=getApriltags(0);
        ArrayList<Double> aprilTag=new ArrayList<>();
        float tx;
        float ty;
        float td;
        if (redAlliance){
            aprilTag=aprilTags.get(24);
        }else{
            aprilTag=aprilTags.get(20);
        }
        if (aprilTag==null){
            return -1;
        } else {
            tx=aprilTag.get(0).floatValue();
            ty=aprilTag.get(1).floatValue();
            td=aprilTag.get(2).floatValue();
        }
        float convFactor = (float) (6.33 / Math.sqrt(td * 307200));
        float robotDistToDepotStraight = (float) (0.5 * (240 * convFactor + 320 * convFactor) * Math.tan(Math.toRadians(65.875)));
        float robotDistToDepot = (float) (robotDistToDepotStraight / Math.cos(Math.toRadians(tx)));
        float robotDistToDepotFixed = (float) (robotDistToDepot * Math.cos(Math.toRadians(ty + camAngle)));
        float farZone = 0;
        float distThirdClosest = 69; //PLACEHOLDER, FILL THIS IN
        float distSecondClosest = 54; //PLACEHOLDER, FILL THIS IN
        float distClosest = 35; //PLACEHOLDER, FILL THIS IN
        double speed4 = 1;
        double speed3 = 0.8; //PLACEHOLDER, FILL THIS IN
        double speed2 = 0.7; //PLACEHOLDER, FILL THIS IN
        double speed1 = 0.6; //PLACEHOLDER, FILL THIS IN
        if (robotDistToDepotFixed < distClosest) {
            return speed1;
        } else if (robotDistToDepotFixed < distSecondClosest) {
            return speed2;
        } else if (robotDistToDepotFixed < distThirdClosest) {
            return speed3;
        } else if(robotDistToDepotFixed < farZone){
            return speed4;
        }
        return 1;
    }

    public double getShootingAngle(boolean redAlliance) {
        Map<Integer, ArrayList<Double>> aprilTags=getApriltags(0);
        ArrayList<Double> aprilTag=new ArrayList<>();
        float tx;
        if (redAlliance){
            aprilTag=aprilTags.get(24);
        }else{
            aprilTag=aprilTags.get(20);
        }
        if (aprilTag==null){
            return -9999;
        } else {
            tx=aprilTag.get(0).floatValue();
        }
        return tx;
    }
    public float getSpeedCalculated(boolean redAlliance, float camAngle, float robotHeight, float allianceHeight, float wheelRadius, float buffer){
        Map<Integer, ArrayList<Double>> aprilTags=getApriltags(0);
        ArrayList<Double> aprilTag=new ArrayList<Double>();
        float tx;
        float ty;
        float td;
        if (redAlliance){
            aprilTag=aprilTags.get(24);
        }else{
            aprilTag=aprilTags.get(24);
        }
        if (aprilTag.isEmpty()){
            return -1;
        } else {
            tx=aprilTag.get(0).floatValue();
            ty=aprilTag.get(1).floatValue();
            td=aprilTag.get(2).floatValue();
        }
        float convFactor = (float) (6.33 / Math.sqrt(td * 307200));
        float robotDistToDepotStraight = (float) (0.5 * (240 * convFactor + 320 * convFactor) * Math.tan(Math.toRadians(65.875)));
        float robotDistToDepot = (float) (robotDistToDepotStraight / Math.cos(Math.toRadians(tx)));
        float robotDistToDepotFixed = (float) (robotDistToDepot * Math.cos(Math.toRadians(ty + camAngle)));
        float velocity=(float) (((allianceHeight-(-4.9*Math.pow(robotDistToDepotFixed,2)+robotHeight))/robotDistToDepotFixed)/wheelRadius);
        return velocity*buffer;
    }
}

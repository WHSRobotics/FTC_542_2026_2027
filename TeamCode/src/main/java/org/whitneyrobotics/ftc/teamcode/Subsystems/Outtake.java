package org.whitneyrobotics.ftc.teamcode.Subsystems;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.DoublePredicate;

public class Outtake {
    public DcMotorEx outtake;
    boolean staticVel;
    double totalTicks, rWheel, rNectar, rPollen, mNectar, mPollen, r, m, h0, hF, launchAngle;
    double dragCoeff = 0.5, g = 9.80665, p = 1.225;
    Limelight cam;

    public Outtake(HardwareMap hardwareMap, boolean isNectar, double inhF, double inh0){
        outtake=hardwareMap.get(DcMotorEx.class,"outtake");
        staticVel=true;
        cam=new Limelight(hardwareMap);
        h0=inh0;
        hF=inhF;
        launchAngle=30;
        if (isNectar){
            r=rNectar;
            m=mNectar;
        }else{
            r=rPollen;
            m=mPollen;
        }
    }
    public Outtake(HardwareMap hardwareMap, boolean isNectar){
        outtake=hardwareMap.get(DcMotorEx.class,"outtake");
        staticVel=true;
        // Fixed-power testing does not need the Limelight yet.
        // cam=new Limelight(hardwareMap);
        h0=0.15;
        hF=1.1;
        launchAngle=30;
        if (isNectar){
            r=rNectar;
            m=mNectar;
        }else{
            r=rPollen;
            m=mPollen;
        }
    }
    public void run(double scalar){
        if (staticVel){
            outtake.setPower(scalar);
        }else{
            outtake.setVelocity(calcVel());
        }
    }
    public void stop(){
        outtake.setPower(0);
    }
    public double getPower(){
        return outtake.getPower();
    }
    public double calcVel(){
        Map<Integer, ArrayList<Double>> tags=cam.getApriltags(0);
        ArrayList<Double> values = new ArrayList<Double>();
        for(Map.Entry<Integer, ArrayList<Double>> tag : tags.entrySet()){
            if (cam.goodApriltagValues.contains(tag.getKey())){
                values=tag.getValue();
            }
        }
        double dist=cam.getDist(values.get(0).floatValue(), values.get(1).floatValue(), values.get(2).floatValue(),0);
        double v=launchSpeed(dist);
        double vWheel=(v/0.45)*(60/2*Math.PI*rWheel)*totalTicks;
        return vWheel;
    }
    public double launchSpeed(double d) {
        final double k = 0.5 * p * dragCoeff * Math.PI * r * r / m;
        final double th = Math.toRadians(launchAngle);
        final double dt = 1e-3;

        DoublePredicate reaches = v0 -> {
            double x = 0, y = h0, vx = v0 * Math.cos(th), vy = v0 * Math.sin(th);
            for (double t = 0; t < 600; t += dt) {
                double v1 = Math.hypot(vx, vy);
                double ax1 = -k * v1 * vx, ay1 = -g - k * v1 * vy;

                double vx2 = vx + 0.5 * dt * ax1, vy2 = vy + 0.5 * dt * ay1;
                double v2 = Math.hypot(vx2, vy2);
                double ax2 = -k * v2 * vx2, ay2 = -g - k * v2 * vy2;

                double vx3 = vx + 0.5 * dt * ax2, vy3 = vy + 0.5 * dt * ay2;
                double v3 = Math.hypot(vx3, vy3);
                double ax3 = -k * v3 * vx3, ay3 = -g - k * v3 * vy3;

                double vx4 = vx + dt * ax3, vy4 = vy + dt * ay3;
                double v4 = Math.hypot(vx4, vy4);
                double ax4 = -k * v4 * vx4, ay4 = -g - k * v4 * vy4;

                double nx = x + dt / 6 * (vx + 2 * vx2 + 2 * vx3 + vx4);
                double ny = y + dt / 6 * (vy + 2 * vy2 + 2 * vy3 + vy4);
                vx += dt / 6 * (ax1 + 2 * ax2 + 2 * ax3 + ax4);
                vy += dt / 6 * (ay1 + 2 * ay2 + 2 * ay3 + ay4);

                if (nx >= d) return y + (d - x) / (nx - x) * (ny - y) >= hF;
                if (ny < hF && vy < 0) return false;
                x = nx;
                y = ny;
            }
            return false;
        };

        double lo = 0, hi = 1;
        while (!reaches.test(hi)) {
            lo = hi;
            hi *= 2;
            if (hi > 5000) return Double.NaN;
        }
        while (hi - lo > 1e-6) {
            double mid = 0.5 * (lo + hi);
            if (reaches.test(mid)) hi = mid; else lo = mid;
        }
        return 0.5 * (lo + hi);
    }
}

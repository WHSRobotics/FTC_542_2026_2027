package org.whitneyrobotics.ftc.teamcode.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.whitneyrobotics.ftc.teamcode.Extensions.OpModeEx.OpModeEx;
import org.whitneyrobotics.ftc.teamcode.Subsystems.Outtake;

@Config
@TeleOp(name="Outtake PIDF Tuner", group="Tests")
public class OuttakePIDF extends OpModeEx {
    public static double kp=0, ki=0, kd=0, kf=0;
    public static double targetVel=0;
    public static boolean running=false;

    Outtake outtake;
    Telemetry dash;

    @Override
    public void initInternal(){
        outtake=new Outtake(hardwareMap, kp, ki, kd, kf);
        dash=new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    protected void loopInternal(){
        outtake.kp=kp;
        outtake.ki=ki;
        outtake.kd=kd;
        outtake.kf=kf;

        if (running){
            outtake.runPIDF(targetVel);
        }else{
            outtake.stop();
        }

        dash.addData("target", targetVel);
        dash.addData("velocity", outtake.outtake.getVelocity());
        dash.addData("error", outtake.error);
        dash.addData("pOut", outtake.pOut);
        dash.addData("iOut", outtake.iOut);
        dash.addData("dOut", outtake.dOut);
        dash.addData("fOut", outtake.fOut);
        dash.addData("power", outtake.getPower());
        dash.addData("kp", kp);
        dash.addData("ki", ki);
        dash.addData("kd", kd);
        dash.addData("kf", kf);
        dash.update();
    }
}
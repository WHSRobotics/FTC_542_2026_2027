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

    Outtake outtake;
    Telemetry dash;

    @Override
    public void initInternal(){
        outtake=new Outtake(hardwareMap);
        dash=new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    protected void loopInternal(){

        outtake.runPIDF();

        dash.addData("targetVel", outtake.targetVel);
        dash.addData("velocity", outtake.outtake.getVelocity());
        dash.addData("error", outtake.error);
        dash.addData("pOut", outtake.pOut);
        dash.addData("iOut", outtake.iOut);
        dash.addData("dOut", outtake.dOut);
        dash.addData("fOut", outtake.fOut);
        dash.addData("power", outtake.getPower());
        dash.addData("kp", outtake.kp);
        dash.addData("ki", outtake.ki);
        dash.addData("kd", outtake.kd);
        dash.addData("kf", outtake.kf);
        dash.update();
    }
}
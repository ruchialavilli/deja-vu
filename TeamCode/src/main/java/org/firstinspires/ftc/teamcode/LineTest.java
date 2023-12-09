package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@Autonomous(name="LineTest", group="AutoOpModes")
public class LineTest extends BaseAutoVisionOpMode {
//testing our heading when issues arise
    public void runOpMode() throws InterruptedException {


            SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

            robot.arm = new DejaVuArm();
            robot.arm.init(hardwareMap, true);


            Pose2d startPose = new Pose2d(-63.375, 32, Math.toRadians(0));
            drive.setPoseEstimate(startPose);

            Trajectory traj0 = drive.trajectoryBuilder(startPose.plus(new Pose2d(0,0,Math.toRadians(90))))
                    .lineTo(new Vector2d(-63.375, 33))
                    .build();


            initTfod();

            waitForStart();
            if(isStopRequested()) return;

            telemetry.addLine("Starting to look for stop sign");
            findDroppingPosition(true);

            drive.followTrajectory(traj0);





    }
}


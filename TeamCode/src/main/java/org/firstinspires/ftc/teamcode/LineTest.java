package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@Autonomous(name="LineTest", group="AutoOpModes")
public class LineTest extends BaseAutoOpMode {
//testing our heading when issues arise
    public void runOpMode() throws InterruptedException {


            SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

            robot.arm = new DejaVuArm();
            robot.arm.init(hardwareMap, true);


            Pose2d startPose = new Pose2d(-63.375, 32, Math.toRadians(0));
            drive.setPoseEstimate(startPose);

            Trajectory traj0 = drive.trajectoryBuilder(startPose)
                    .lineTo(new Vector2d(0, 32))
                    .build();


            waitForStart();

            if(isStopRequested()) return;

            drive.followTrajectory(traj0);


           //strafe to nearest pole
                //drive.followTrajectory(traj2);
                //aligning


    }
}


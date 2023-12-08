package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@Autonomous(name="RightRed", group="AutoOpModes")
public class RightRed extends BaseAutoOpMode {
//This code contains the basic chassis movements for the Left Blue Auton Position
    public void runOpMode() throws InterruptedException {

            /*
            * Series of events:
            * 1. Detect pixel ***
            * 2. Drop Pixel in corresponsing location
            * 3. Move to aviod dropped pixel
            * 4. turn 90
            * 5. Move to drop pixel held
            * 6. park
            * */


            SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

            robot.arm = new DejaVuArm();
            robot.arm.init(hardwareMap, true);


            Pose2d startPose = new Pose2d(63.375, 12, Math.toRadians(0));
            drive.setPoseEstimate(startPose);

            Trajectory traj0 = drive.trajectoryBuilder(startPose)
                    .lineTo(new Vector2d(37, 12))
                    .build();

            Trajectory traj1 = drive.trajectoryBuilder(traj0.end().plus(new Pose2d(0, 0, Math.toRadians(278))))
                    .lineTo(new Vector2d(37, 50))
                    .build();

            Trajectory traj2 = drive.trajectoryBuilder(traj1.end())
                    .lineTo(new Vector2d(57.5, 50))
                    .build();

            Trajectory traj3 = drive.trajectoryBuilder(traj2.end().plus(new Pose2d(0, 0, Math.toRadians(-6))))
                    .lineTo(new Vector2d(57.5, 62))
                    .build();



            waitForStart();

            if(isStopRequested()) return;

            //detect pixel + location
            drive.followTrajectory(traj0);
            //now drop purple pixel
            drive.followTrajectory(traj1);
            //now drop yellow on backdrop
            drive.followTrajectory(traj2);
            drive.followTrajectory(traj3);
            //pick pixels and repeat eyeyeyeyey




    }
}


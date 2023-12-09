package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@Autonomous(name="LeftBlue", group="AutoOpModes")
public class LeftBlue extends BaseAutoOpMode {
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
            * 11. fin
            * */


            SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

            robot.arm = new DejaVuArm();
            robot.arm.init(hardwareMap, true);


            Pose2d startPose = new Pose2d(-63.375, 16, Math.toRadians(180));
            drive.setPoseEstimate(startPose);

            Trajectory traj0 = drive.trajectoryBuilder(startPose)
                    .lineTo(new Vector2d(-43, 16))
                    .build();

            Trajectory traj1 = drive.trajectoryBuilder(traj0.end().plus(new Pose2d(0, 0, Math.toRadians(96))))
                    .lineTo(new Vector2d(-43, 50))
                    .build();

            Trajectory traj2 = drive.trajectoryBuilder(traj1.end())
                    .lineTo(new Vector2d(-66, 50))
                    .build();

            Trajectory traj3 = drive.trajectoryBuilder(traj2.end().plus(new Pose2d(0, 0, Math.toRadians(-5))))
                    .lineTo(new Vector2d(-66, 58))
                    .build();



            robot.arm.axon_right.setPosition(SERVO_DOWN);
            robot.arm.hook_right.setPosition(SERVO_UNLIFT);//hold both pixels
            robot.arm.hook_left.setPosition(SERVO_LIFTED-0.33);

            waitForStart();

            if(isStopRequested()) return;

            //detect pixel + location TODO

            drive.followTrajectory(traj0);

            //now drop purple pixel
            robot.arm.moveArmToLevel(3);
            sleep(1000);
            robot.arm.axon_right.setPosition(SERVO_UP);
            sleep(1000);
            robot.arm.hook_right.setPosition(SERVO_LIFTED);

            drive.followTrajectory(traj1);

            //now drop yellow on backdrop
            robot.arm.hook_left.setPosition(SERVO_UNLIFT-0.33);
            sleep(500);
            robot.arm.axon_right.setPosition(SERVO_DOWN);
            sleep(1000);
            robot.arm.moveArmToLevel(0);
            sleep(1000);

            drive.followTrajectory(traj2);
            drive.followTrajectory(traj3);
            //pick pixels and repeat eyeyeyeyey




    }
}


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


            Pose2d startPose = new Pose2d(63, 32, Math.toRadians(0));
            drive.setPoseEstimate(startPose);
//            robot.arm.axon_right.setPosition(SERVO_DOWN);
//            robot.arm.hook_right.setPosition(SERVO_UNLIFT);//hold both pixels
//            robot.arm.hook_left.setPosition(SERVO_LIFTED-0.33);
            telemetry.addLine("Robot ready for run");
            telemetry.update();
//
            Trajectory traj0 = drive.trajectoryBuilder(startPose.plus(new Pose2d(0,0,Math.toRadians(0))))
                    .lineTo(new Vector2d(23, 32))
                    .build();

            waitForStart();
            if(isStopRequested()) return;
//
//            robot.arm.moveArmToLevel(3);
//            sleep(1000);
//            robot.arm.axon_right.setPosition(SERVO_UP);
//            sleep(1000);
//            robot.arm.moveArmToLevel(2);
//            sleep(1000);
//            robot.arm.hook_right.setPosition(SERVO_LIFTED);
//            sleep(500);
//            robot.arm.moveArmToLevel(3);
//            sleep(1000);
//            robot.arm.axon_right.setPosition(SERVO_DOWN);
//            sleep(500);
//            robot.arm.moveArmToLevel(0);
//            sleep(1500);
//
            drive.followTrajectory(traj0);







    }
}


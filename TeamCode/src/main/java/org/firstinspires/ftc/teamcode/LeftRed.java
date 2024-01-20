package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.drive.DriveConstants.TURN_ADD;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous(name="LeftRed", group="AutoOpModes")
public class LeftRed extends BaseAutoVisionOpMode {
//This code contains the basic chassis movements for the Right Red Auton Position
        private String TAG = "LeftRed";
        private ElapsedTime runtime = new ElapsedTime();
        String name = "LeftRed";
        private Thread parkingLocationFinderThread;
        public static String ACTION_GOTO_LEVEL = "goto_level";
        public static String DROP_PIXEL_RIGHT = "release_right";
        public static String DROP_PIXEL_LEFT = "release_left";
        public static String HOLD_BOTH_PIXELS = "hold_pixels";
        public static String BUCKET_AUTON_OUT = "bucket_auton_out";
        public static String BUCKET_OUT = "bucket_out";
        public static String BUCKET_IN = "bucket_in";
        public double angleOffset = 0;

        private HandlerThread mHandlerThread;
        private Handler armHandler;

        // dropping locations
        // left - april tag 1
        protected static Vector2d location1 = new Vector2d(32, 49);
        // center - april tag 2
        protected static Vector2d location2 = new Vector2d(38, 49);
        // right - april tag 3
        protected static Vector2d location3 = new Vector2d(43, 49);

    public void runOpMode() throws InterruptedException {

            initTfod();


            SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

            robot.arm = new DejaVuArm();
            robot.arm.init(hardwareMap, true);
            // initialize handler thread to move robot arm
            mHandlerThread = new HandlerThread("armThread");
            mHandlerThread.start();
            armHandler = new Handler(mHandlerThread.getLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
//                Log.d(TAG, "executing arm action: " + action);
                            switch (msg.what){
                                    case 999:
//                        Log.d(TAG, "executing arm action: Open");
                                            robot.arm.hook_left.setPosition(SERVO_UNLIFT-0.33);
//                        Log.d(TAG, "executing arm action: Open Done");
                                            break;
                                    case 888:
//                        Log.d(TAG, "executing arm action: Close");
                                            robot.arm.hook_right.setPosition(SERVO_LIFTED);
//                        Log.d(TAG, "executing arm action: Close Done");
                                            break;
                                    case 111:
                                            robot.arm.hook_right.setPosition(SERVO_UNLIFT);//hold both pixels
                                            robot.arm.hook_left.setPosition(SERVO_LIFTED-0.33);
                                            break;
                                    case 222:
                                            robot.arm.axon_right.setPosition(SERVO_DOWN);
                                            break;
                                    case 333:
                                            robot.arm.axon_right.setPosition(SERVO_UP);
                                            break;
                                    case 444:
                                            robot.arm.axon_right.setPosition(SERVO_AUTON_UP);
                                            break;
                                    default:
//                        Log.d(TAG, "executing arm action going to level: " + msg.what);
                                            robot.arm.moveArmToLevel(msg.what);
//                        Log.d(TAG, "executing arm action: going to level Done");
                                            break;
                            }

                    }
            };


            //start
            Pose2d startPose = new Pose2d(63.375, -33, Math.toRadians(0));
            drive.setPoseEstimate(startPose);

            //move to drop pixel
            Trajectory traj0 = drive.trajectoryBuilder(startPose)
                    .lineTo(new Vector2d(36, -33))
                    .build();

            //--turn to face one side and drop pixel -- then turn to face normally

            //move back to start
            Trajectory traj1 = drive.trajectoryBuilder(traj0.end())
                    .lineTo(new Vector2d(61, -38))
                    .build();

            //turn to face 270 deg and then move forward to backdrop level
            TrajectorySequence traj2 = drive.trajectorySequenceBuilder(traj1.end().plus(new Pose2d(0, 0, Math.toRadians(0))))
                    .turn(Math.toRadians(-90+TURN_ADD))//todo: do we need to change this for all files?
                    .lineTo(new Vector2d(61, 49))
                    .build();

//            //strafe to backdrop pos
//            Trajectory traj3 = drive.trajectoryBuilder(traj2.end())
//                    .lineTo(new Vector2d(42, 52))
//                    .build();

            //-- drop next pixel and pull down arm

            //strafe back to wall
            Trajectory traj4 = drive.trajectoryBuilder(traj2.end().plus(new Pose2d(0, 0, Math.toRadians(0))))
                    .lineTo(new Vector2d(65, 49))// TODO: 16 for inner park or comment out if other team wants to park too
                    .build();

            //park
//            Trajectory traj5 = drive.trajectoryBuilder(traj4.end().plus(new Pose2d(0, 0, Math.toRadians(0))))
//                    .lineTo(new Vector2d(60, 62))
//                    .build();





            sendMessage(BUCKET_IN);
            sendMessage(HOLD_BOTH_PIXELS);
            telemetry.addData(name, " Robot ready for run");
            telemetry.update();

            /////***********START OPMODE

            waitForStart();
            runtime.reset();

            //starting vision thread
            parkingLocationFinderThread = new Thread(parkingLocationFinderRunnable);
            parkingLocationFinderThread.start();

            // now wait for the threads to finish before returning from this method
//        Log.d(TAG, "waiting for threads to finish...");
            parkingLocationFinderThread.join();
//        Log.d(TAG, "thread joins complete");

            double xOffset = 0,  yOffset = 0;
            double xDropOffset = 0, yDropOffset = 0;
            if(locationToDrop == location1){
                    angleOffset = 90 + TURN_ADD;
                    yOffset = -6;
                    yDropOffset = 3;
            }else if(locationToDrop == location3){
                    angleOffset = -(90 - TURN_ADD);
                    yOffset = 13;
                    yDropOffset = -4;
            }else{
                    angleOffset = 0;
                    xOffset = -8;
                    xDropOffset = 5;
            }

            if (isStopRequested()) {
                    // quit() so we do not process any more messages
                    mHandlerThread.quit();
                    return;
            }

            //move forward to pos

            drive.followTrajectory(traj0);

            TrajectorySequence temp = drive.trajectorySequenceBuilder(traj0.end().plus(new Pose2d(0, 0, Math.toRadians(angleOffset))))
                    .lineTo(new Vector2d(36 + xOffset, -33 + yOffset))
                    .lineTo(new Vector2d(36 + xDropOffset, -33 + yDropOffset))
                    .build();
            drive.followTrajectorySequence(temp);


            //now drop purple pixel
            sendMessage(ACTION_GOTO_LEVEL, 4);
            sleep(1000);
            sendMessage(BUCKET_AUTON_OUT);
            sleep(1000);
            sendMessage(ACTION_GOTO_LEVEL, 0);
            sleep(1000);
            sendMessage(DROP_PIXEL_RIGHT);
            sleep(500);
            sendMessage(BUCKET_OUT);
            sleep(1000);
            sendMessage(ACTION_GOTO_LEVEL, 4);
            sleep(1000);
            sendMessage(BUCKET_IN);
            sleep(500);
            sendMessage(ACTION_GOTO_LEVEL, 0);
            sleep(1500);

            if(locationToDrop == location3){
                    yDropOffset = -5;
            }
            if(locationToDrop == location1){
                    yDropOffset = 1;
            }

            // go back before we return to startpos
            drive.followTrajectorySequence(drive.trajectorySequenceBuilder(temp.end())
                    .lineTo(new Vector2d(36, -33 + yDropOffset))
                    .turn(Math.toRadians(-angleOffset))
                    .build());

            //move to start pos
            drive.followTrajectory(traj1);

            //TODO: sleep to wait for alliance to finish
            //sleep();

            //move to backdrop
            drive.followTrajectorySequence(traj2);
            drive.followTrajectorySequence(drive.trajectorySequenceBuilder(traj2.end().plus(new Pose2d(0, 0, Math.toRadians(0))))
                    .lineTo(locationToDrop)
                    .build());


            //now drop yellow on backdrop
            sendMessage(ACTION_GOTO_LEVEL, 3);
            sleep(1000);
            sendMessage(BUCKET_OUT);
            sleep(1000);
            sendMessage(DROP_PIXEL_LEFT);
            sleep(1000);
            sendMessage(BUCKET_IN);
            sleep(1000);
            sendMessage(ACTION_GOTO_LEVEL, 0);
            sleep(1100);

            //park!
            drive.followTrajectory(traj4);
            //drive.followTrajectory(traj5);

            //quitting thread
            mHandlerThread.quit();


    }

        private void sendMessage(String action) {
                sendMessage(action, -1);
        }
        private void sendMessage(String action, int level) {
                if(DROP_PIXEL_RIGHT.equals(action)){
                        armHandler.sendEmptyMessage(888);
                } else if(DROP_PIXEL_LEFT.equals(action)){
                        armHandler.sendEmptyMessage(999);
                }else if(HOLD_BOTH_PIXELS.equals(action)){
                        armHandler.sendEmptyMessage(111);
                }else if(BUCKET_IN.equals(action)){
                        armHandler.sendEmptyMessage(222);
                }else if(BUCKET_OUT.equals(action)){
                        armHandler.sendEmptyMessage(333);
                }else if(BUCKET_AUTON_OUT.equals(action)){
                        armHandler.sendEmptyMessage(444);
                } else {
                        armHandler.sendEmptyMessage(level);
                }

        }

        private Runnable parkingLocationFinderRunnable = () -> {
                //Find the level in 10 attempts. If not detected set level to 3.
                if (opModeIsActive()) {
                        sendToTelemetry(">", "Detecting parking location using vision");
                        findDroppingPosition(true);
                        switch (droppingPosition) {
                                case 1:
                                        locationToDrop = location1;
                                        break;
                                case 2:
                                        locationToDrop = location2;
                                        break;
                                case 3:
                                        locationToDrop = location3;
                                        break;
                                default:
                                        locationToDrop = location2;
                                        break;
                        }
                        sendToTelemetry("Found parking", locationToDrop.toString());
                        if (locationToDrop != null) {
                                Log.i(TAG, " Found parking =" + locationToDrop);
                        }
                        if (locationToDrop == location1) {
                                telemetry.addLine(" location1");
                        } else if (locationToDrop == location2) {
                                telemetry.addLine(" location2");
                        } else if (locationToDrop == location3) {
                                telemetry.addLine(" location3");
                        } else {
                                telemetry.addLine(" UNKNOWN - defaulting to location2");
                                locationToDrop = location2;
                        }
                } else {
                        sendToTelemetry(">", "Could not init vision code - defaulting to TOP");
                        locationToDrop = location2;
                }
//        Log.d(TAG, "Thread 1 finishing up");
        };
}


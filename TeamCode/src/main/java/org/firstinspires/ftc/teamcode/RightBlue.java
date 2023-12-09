package org.firstinspires.ftc.teamcode;

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

@Autonomous(name="RightBlue", group="AutoOpModes")
public class RightBlue extends BaseAutoVisionOpMode {
//This code contains the basic chassis movements for the Right Red Auton Position
        private String TAG = "RightBlue";
        private ElapsedTime runtime = new ElapsedTime();
        String name = "RightBlue";
        private Thread parkingLocationFinderThread;
        public static String ACTION_GOTO_LEVEL = "goto_level";
        public static String DROP_PIXEL_RIGHT = "release_right";
        public static String DROP_PIXEL_LEFT = "release_left";
        public static String HOLD_BOTH_PIXELS = "hold_pixels";
        public static String BUCKET_OUT = "bucket_out";
        public static String BUCKET_IN = "bucket_in";
        public int x = 0;

        private HandlerThread mHandlerThread;
        private Handler armHandler;

        // dropping locations
        // left - april tag 1
        protected static Vector2d location1 = new Vector2d(-48, 52);
        // center - april tag 2
        protected static Vector2d location2 = new Vector2d(-38, 50);
        // right - april tag 3
        protected static Vector2d location3 = new Vector2d(-28, 52);

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
                                    default:
//                        Log.d(TAG, "executing arm action going to level: " + msg.what);
                                            robot.arm.moveArmToLevel(msg.what);
//                        Log.d(TAG, "executing arm action: going to level Done");
                                            break;
                            }

                    }
            };


            //start
            Pose2d startPose = new Pose2d(-63.375, -36, Math.toRadians(180));
            drive.setPoseEstimate(startPose);

            //move to drop pixel
            Trajectory traj0 = drive.trajectoryBuilder(startPose)
                    .lineTo(new Vector2d(-43, -36))
                    .build();


            //--turn to face one side and drop pixel -- then turn to face normally

            //move back to start
            Trajectory traj1 = drive.trajectoryBuilder(traj0.end())
                    .lineTo(new Vector2d(-60, -36))//60 might be too far
                    .build();

            //turn to face 270 deg and then move forward to backdrop level
            Trajectory traj2 = drive.trajectoryBuilder(traj1.end().plus(new Pose2d(0, 0, Math.toRadians(90))))
                    .lineTo(new Vector2d(-52, 52))
                    .build();

            //strafe to backdrop pos
            Trajectory traj3 = drive.trajectoryBuilder(traj2.end())
                    .lineTo(new Vector2d(-42, 52))
                    .build();

            //-- drop next pixel and pull down arm

            //strafe back to wall
            Trajectory traj4 = drive.trajectoryBuilder(traj2.end().plus(new Pose2d(0, 0, Math.toRadians(-4))))
                    .lineTo(new Vector2d(-62, 52))
                    .build();

            //park
            Trajectory traj5 = drive.trajectoryBuilder(traj4.end().plus(new Pose2d(0, 0, Math.toRadians(0))))
                    .lineTo(new Vector2d(-59, 62))
                    .build();





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

            if(locationToDrop == location1){
                    x = 93;
            }else if(locationToDrop == location3){
                    x = -81;
            }else{
                    x = 0;
            }

            if (isStopRequested()) {
                    // quit() so we do not process any more messages
                    mHandlerThread.quit();
                    return;
            }

            //move forward to pos
            drive.followTrajectory(traj0);
            drive.followTrajectory(drive.trajectoryBuilder(traj0.end().plus(new Pose2d(0, 0, Math.toRadians(x))))
                    .lineTo(new Vector2d(43, 15))
                    .build());


            //now drop purple pixel
            sendMessage(ACTION_GOTO_LEVEL, 3);
            sleep(1000);
            sendMessage(BUCKET_OUT);
            sleep(1000);
            sendMessage(ACTION_GOTO_LEVEL, 2);
            sleep(1000);
            sendMessage(DROP_PIXEL_RIGHT);
            sleep(500);
            sendMessage(ACTION_GOTO_LEVEL, 3);
            sleep(1000);
            sendMessage(BUCKET_IN);
            sleep(500);
            sendMessage(ACTION_GOTO_LEVEL, 0);
            sleep(1500);


            //move to start pos
            drive.followTrajectory(traj1);

            //TODO: sleep to wait for alliance to finish
            //sleep();

            //move to backdrop
            drive.followTrajectory(traj2);
            drive.followTrajectory(drive.trajectoryBuilder(traj2.end().plus(new Pose2d(0, 0, Math.toRadians(0))))
                    .lineTo(locationToDrop)
                    .build());


            //now drop yellow on backdrop
            sendMessage(ACTION_GOTO_LEVEL, 3);
            sleep(1000);
            sendMessage(BUCKET_OUT);
            sleep(1000);
            sendMessage(DROP_PIXEL_LEFT);
            sleep(500);
            sendMessage(BUCKET_IN);
            sleep(1000);
            sendMessage(ACTION_GOTO_LEVEL, 0);
            sleep(1100);
//
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


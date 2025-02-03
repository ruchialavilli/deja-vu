package org.firstinspires.ftc.teamcode;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "BasicAuton", group = "Autonomous")
public class BasicAuton extends BaseAutoVisionOpMode{

    private String TAG = "BasicAuton";

    public static String PITCH_TOP = "pitch_top_bucket";
    public static String PITCH_PICK = "pitch_pick";
    public static String OUTTAKE = "outtake";
    public static String INTAKE = "intake";
    public static String ARM_UP = "arm_up";
    public static String ARM_DOWN = "arm_down";
    public static String ARM_EXTEND = "arm_extend";

    private ElapsedTime runtime = new ElapsedTime();
    String name = "BucketAuton";

    // instantiate your MecanumDrive at a particular pose.

    private HandlerThread mHandlerThread;
    private Handler armHandler;

    public class Arm {

        //for scoring
        public class ArmScore implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                robot.arm.moveArmToLevel(3, robot.arm.armRotation, 0.4);
                sleep(500);
                robot.arm.pitchServo.setPosition(0.7);
                sleep(1000);
                robot.arm.moveArmToLevel(6, robot.arm.armRotation, 0.6);
                sleep(500);
                robot.arm.moveArmToLevel(2, robot.arm.armExtension, 1);
                sleep(800);
                robot.arm.intakeLeft.setPower(-1);
                robot.arm.intakeRight.setPower(1);
                sleep(500);
                robot.arm.intakeLeft.setPower(0);
                robot.arm.intakeRight.setPower(0);
                robot.arm.pitchServo.setPosition(0.9);
                robot.arm.moveArmToLevel(0, robot.arm.armExtension, 1);
                sleep(1000);
                robot.arm.moveArmToLevel(4, robot.arm.armRotation, 0.5);
                sleep(1000);
                robot.arm.moveArmToLevel(5, robot.arm.armRotation, 0.7);
                sleep(500);
                robot.arm.pitchServo.setPosition(0.6);
                robot.arm.intakeLeft.setPower(1);
                robot.arm.intakeRight.setPower(-1);




                return false;
            }
        }
        public Action scoreBlock() {
            return new ArmScore();
        }

        public class ArmPick implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                sleep(400);
                robot.arm.intakeLeft.setPower(0);
                robot.arm.intakeRight.setPower(0);
                robot.arm.pitchServo.setPosition(0.9);


                return false;
            }
        }
        public Action pickBlock() {
            return new ArmPick();
        }


    }


    @Override
    public void runOpMode() throws InterruptedException {

        Pose2d initialPose = new Pose2d(0, 0, Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        Arm score = new Arm();


        robot.arm = new DejaVuArm();
        robot.arm.init(hardwareMap, true);

        mHandlerThread = new HandlerThread("armThread");
        mHandlerThread.start();

        armHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
//                Log.d(TAG, "executing arm action: " + action);
                switch (msg.what){
                    case 999:
                        //TODO score top bucket - pitch servo
//                        Log.d(TAG, "executing arm action: Open");
                        robot.arm.pitchServo.setPosition(0.5);
//                        Log.d(TAG, "executing arm action: Open Done");
                        break;
                    case 888:
                        //TODO pick up block- pitch servo
//                        Log.d(TAG, "executing arm action: Close");
                        robot.arm.pitchServo.setPosition(0.4);
//                        Log.d(TAG, "executing arm action: Close Done");
                        break;
                    case 111:
                        //TODO intake out
                        robot.arm.intakeLeft.setPower(-1);
                        robot.arm.intakeRight.setPower(1);
                        break;
                    case 222:
                        //TODO intake in
                        robot.arm.intakeLeft.setPower(1);
                        robot.arm.intakeRight.setPower(-1);
                        break;
                    case 333:
                        //TODO arm up rotation
                        robot.arm.moveArmToLevel(3, robot.arm.armRotation, 0.4);
                        sleep(1000);
                        robot.arm.moveArmToLevel(6, robot.arm.armRotation, 0.4);                        break;
                    case 444:
                        //TODO arm down rotation
//                        robot.arm.moveArmToLevel(4, robot.arm.armRotation, 0.5);
//                        sleep(1000);
                        robot.arm.moveArmToLevel(5, robot.arm.armRotation, 0.5);
                        break;
                    default:
                        //TODO arm extension levels
//                        Log.d(TAG, "executing arm action going to level: " + msg.what);
                        robot.arm.moveArmToLevel(msg.what, robot.arm.armExtension, 1);
//                        Log.d(TAG, "executing arm action: going to level Done");
                        break;
                }

            }
        };




//        Action test1 = drive.actionBuilder(initialPose)
//                .lineToX(10)
//                .setTangent(Math.PI/6)
//                .build();





//        TrajectoryActionBuilder test1 = drive.actionBuilder(initialPose)
//                .lineToXSplineHeading(10, Math.toRadians(0))
//                .setTangent(Math.PI/6);
//
////        TrajectoryActionBuilder tab3 = drive.actionBuilder(initialPose)
////                .lineToYSplineHeading(33, Math.toRadians(180))
////                .waitSeconds(2)
////                .strafeTo(new Vector2d(46, 30))
////                .waitSeconds(3);

//        Action move1 = drive.actionBuilder(initialPose)
//                .strafeTo(new Vector2d(5, 24))
//                .build();












        telemetry.addData(name, " Robot ready for run");
        telemetry.update();

        /////***********START OPMODE

        waitForStart();
        runtime.reset();

        sendMessage(ARM_DOWN);


        if (isStopRequested()) {
            // quit() so we do not process any more messages
            mHandlerThread.quit();
            return;
        }

        //Action test3 = test1.build();

//        Actions.runBlocking(
//                drive.actionBuilder(initialPose)
//                        .lineToX(20)
//                        .turn(Math.toRadians(-55))
//                        .lineToX(10)
//                        .build());

        Actions.runBlocking(
                drive.actionBuilder(initialPose)
                        .strafeTo(new Vector2d(5, -24))
                        .build());





    }

    private void sendMessage(String action) {
        sendMessage(action, -1);
    }
    private void sendMessage(String action, int level) {
        if(PITCH_PICK.equals(action)){
            armHandler.sendEmptyMessage(888);
        } else if(PITCH_TOP.equals(action)){
            armHandler.sendEmptyMessage(999);
        }else if(OUTTAKE.equals(action)){
            armHandler.sendEmptyMessage(111);
        }else if(INTAKE.equals(action)){
            armHandler.sendEmptyMessage(222);
        }else if(ARM_UP.equals(action)){
            armHandler.sendEmptyMessage(333);
        }else if(ARM_DOWN.equals(action)){
            armHandler.sendEmptyMessage(444);
        } else {
            armHandler.sendEmptyMessage(level);
        }

    }






}

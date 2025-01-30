package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


@TeleOp(name="GamePadOpMode", group="Linear OpMode")
public class GamePadOpMode extends LinearOpMode {
    private static final String TAG = "GamePadOpMode";
    public final float threshholdConst = .1f;
    DejaVuBot robot = new DejaVuBot();
    private ElapsedTime runtime = new ElapsedTime();
    private Thread gamepad1Thread; //chassis
    private Thread gamepad2Thread; //armExtension
    private Thread g2RotateThread; //armRotate
    private Thread pickUpThread; //pitch movements
    private Thread intakeThread; //intake wheel movements



    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap,false);
        robot.chassisEncoderOff();
        robot.arm.setTelemetry(telemetry);
        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready for gamepad run");
        telemetry.update();

        // Wait for the game to start (set pitchServo position)
        robot.arm.pitchServo.setPosition(0.8);
        robot.arm.moveArmToLevel(5, robot.arm.armRotation, 0.5);

        waitForStart();

        runtime.reset();

        Log.d(TAG, "starting thread 1");
        gamepad1Thread = new Thread(gp1Runnable);
        gamepad1Thread.start();

        Log.d(TAG, "starting thread 2");
        gamepad2Thread = new Thread(gp2Runnable);
        gamepad2Thread.start();

        Log.d(TAG, "starting thread 3");
        g2RotateThread = new Thread(g2RotateRunnable);
        g2RotateThread.start();

        Log.d(TAG, "starting thread 4");
        pickUpThread = new Thread(pickupRunnable);
        pickUpThread.start();

        Log.d(TAG, "starting thread 5");
        intakeThread = new Thread(intakeRunnable);
        intakeThread.start();



        // now wait for the threads to finish before returning from this method
        Log.d(TAG, "waiting for threads to finish...");
        gamepad1Thread.join();
        gamepad2Thread.join();
        g2RotateThread.join();
        pickUpThread.join();
        intakeThread.join();
        Log.d(TAG, "thread joins complete");

        // Step 4:  Stop and close the claw.
        robot.stopRobot();
//        robot.arm.setArmMotorPowers(0);
//        robot.arm.armMotor1.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
//        robot.arm.armMotor2.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        telemetry.addData("Gamepad", "Threads Complete");
        telemetry.update();
    }

    private Runnable gp1Runnable = new Runnable() {
        @Override
        public void run() {
            double leftPower, rightPower;
            double drive;
            double turn;
            double turnPower;

            while (opModeIsActive()) {
                drive = (-gamepad1.left_stick_y);
                turn = (gamepad1.left_stick_x);
                leftPower = Range.clip(drive + turn, -1.0, 1.0);
                rightPower = Range.clip(drive - turn, -1.0, 1.0);
                turnPower = Range.clip((gamepad1.right_stick_x) * 0.90, -1.0, 1.0);

//                telemetry.addData("GP1 drive set to:", "" + drive);
//                telemetry.addData("GP1 turn set to:", "" + turn);

                if (gamepad1.left_bumper) {
                    leftPower = leftPower *0.4;
                    rightPower = rightPower *0.4;
                    turnPower = turnPower *0.4;
                }

                if (gamepad1.right_stick_x != 0) {
                    robot.turnRobot(turnPower);
                } else {
                    robot.leftFrontMotor.setPower(leftPower);
                    robot.rightFrontMotor.setPower(rightPower);
                    robot.rightBackMotor.setPower(rightPower);
                    robot.leftBackMotor.setPower(leftPower);
                }


//                telemetry.addData("GP1 Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
//                telemetry.addData("GP1 GamePadOpMode", "Leg 1: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            } //end of while loop
            Log.d(TAG, "Thread 1 finishing up");
            telemetry.addData("GP1 Status", "Completed");
        }
    };

    private Runnable gp2Runnable = new Runnable() {
        public void run() {
            float extendPower;

            while (opModeIsActive()) {


                //EXTENDING ARM when picking up


                extendPower = gamepad2.left_stick_y;

                robot.arm.armExtension.setPower(extendPower);


//                //TODO do we want fixed arm-levels? if so, where to place on gamepad?
//                //just for fixing arm extension when it goes out too much
//                if (gamepad1.y) { //pick
//                    telemetry.addData("GP2 Input", "Y");
//                    telemetry.addData("GP2 Input level", "Pick Specimen");
//                    robot.arm.moveArmToLevel(0, robot.arm.armExtension, 1);
//
//                }
//                if (gamepad1.x) { //drop middle
//                    telemetry.addData("GP2 Input", "X");
//                    telemetry.addData("GP2 Input level", "Lower Bucket");
//                    robot.arm.moveArmToLevel(1, robot.arm.armExtension, 1);
//                }
//                if (gamepad1.b) { //drop top
//                    telemetry.addData("GP2 Input", "B");
//                    telemetry.addData("GP2 Input level", "Upper Bucket");
//                    robot.arm.moveArmToLevel(2, robot.arm.armExtension, 1);
//                }


                telemetry.addData("GP2 armMotor armExtension value", robot.arm.armExtension.getCurrentPosition());
                Log.d(TAG, "armExtension current reach:" + robot.arm.armExtension.getCurrentPosition());
                //telemetry.addData("GP2 GamePadOpMode", "Leg 1: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            } //end of while loop
            Log.d(TAG, "Thread 2 finishing up");
            telemetry.addData("GP2 Status", "Completed");
        }
    };

    //to be used for the servos for dropping the pixels
    private Runnable g2RotateRunnable = new Runnable() {
        public void run() {
            while (opModeIsActive()) {

                //THIS IS FOR CONTROLLING armRotation (just angles to pick and drop)

                telemetry.addData("GP2 armRotation value", robot.arm.armRotation.getCurrentPosition());

                if(gamepad2.right_bumper){
                    telemetry.addData("GP2 Input", "Right Bumper");
                    telemetry.addData("GP2 Input level", "Arm Up");
                    Log.d(TAG, "armRotation current angle:" + robot.arm.armRotation.getCurrentPosition());
                    robot.arm.moveArmToLevel(3, robot.arm.armRotation, 0.4);
                    sleep(1000);
                    robot.arm.moveArmToLevel(6, robot.arm.armRotation, 0.4);

                }else if(gamepad2.left_bumper){
                    telemetry.addData("GP2 Input", "Left Bumper");
                    telemetry.addData("GP2 Input level", "Arm Down");
                    robot.arm.moveArmToLevel(4, robot.arm.armRotation, 0.5);
                    sleep(1000);
                    robot.arm.moveArmToLevel(5, robot.arm.armRotation, 0.5);

                }


                telemetry.update();
            } //end of while loop
            Log.d(TAG, "Thread 3 finishing up");
        }
    };

    private Runnable pickupRunnable = new Runnable() {
        public void run() {
            while (opModeIsActive()) {

                //THIS IS FOR SCORING THE BLOCKS (pitch)

                if (gamepad2.dpad_down) { //pick
                    telemetry.addData("GP2 Input", "D-Pad Down");
                    telemetry.addData("GP2 Input level", "Pick Specimen");
                    robot.arm.pitchServo.setPosition(0.4);
                }
                if (gamepad2.dpad_left) { //drop middle
                    telemetry.addData("GP2 Input", "D-Pad Right");
                    telemetry.addData("GP2 Input level", "Lower Bucket");
                    robot.arm.pitchServo.setPosition(0.8);
                }
                if (gamepad2.dpad_up) { //drop top
                    telemetry.addData("GP2 Input", "D-Pad Up");
                    telemetry.addData("GP2 Input level", "Upper Bucket");
                    robot.arm.pitchServo.setPosition(0.5);

                }



                telemetry.update();
            } //end of while loop
            Log.d(TAG, "Thread 4 finishing up");
        }
    };

    private Runnable intakeRunnable = new Runnable() {
        public void run() {
            while (opModeIsActive()) {

                if (gamepad2.y) {  //outtake
                    robot.arm.intakeLeft.setPower(-1);
                    robot.arm.intakeRight.setPower(1);
                }
                else if (gamepad2.a) {  //intake
                    robot.arm.intakeLeft.setPower(1);
                    robot.arm.intakeRight.setPower(-1);
                }
                else if (gamepad2.b){ //stops moving
                    robot.arm.intakeLeft.setPower(0);
                    robot.arm.intakeRight.setPower(0);
                }

                telemetry.update();
            } //end of while loop
            Log.d(TAG, "Thread 5 finishing up");
        }
    };

}

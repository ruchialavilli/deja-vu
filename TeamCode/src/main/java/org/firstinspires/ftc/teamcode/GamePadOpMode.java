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
    //look below at isBlue variable every gamepad run
    public static final double SERVO_DOWN = 0.48;
    public static final double SERVO_UP = 0.04;
    public static final double SERVO_LIFTED = 0.62;
    public static final double SERVO_UNLIFT = 1;
    private Thread gamepad1Thread;
    private Thread gamepad2Thread;
    private Thread pickUpThread;
    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap,false);
        robot.chassisEncoderOff();
        robot.arm.setTelemetry(telemetry);
        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready for gamepad run");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        robot.arm.axon_right.setPosition(SERVO_DOWN);
        robot.arm.hook_right.setPosition(SERVO_LIFTED); //release both pixels
        robot.arm.hook_left.setPosition(SERVO_UNLIFT-0.33);

        waitForStart();

        runtime.reset();

        Log.d(TAG, "starting thread 1");
        gamepad1Thread = new Thread(gp1Runnable);
        gamepad1Thread.start();

        Log.d(TAG, "starting thread 2");
        gamepad2Thread = new Thread(gp2Runnable);
        gamepad2Thread.start();

        Log.d(TAG, "starting thread 3");
        pickUpThread = new Thread(pickupRunnable);
        pickUpThread.start();



        // now wait for the threads to finish before returning from this method
        Log.d(TAG, "waiting for threads to finish...");
        gamepad1Thread.join();
        gamepad2Thread.join();
        pickUpThread.join();
        Log.d(TAG, "thread joins complete");

        // Step 4:  Stop and close the claw.
        robot.stopRobot();
//        robot.arm.setArmMotorPowers(0);
//        robot.arm.armMotor1.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
//        robot.arm.armMotor2.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        telemetry.addData("Gamepad", "Threads for 1 & 2 Complete");
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
                drive = (-gamepad1.left_stick_y)*0.9;
                turn = (gamepad1.left_stick_x)*0.9;
                leftPower = Range.clip(drive + turn, -1.0, 1.0);
                rightPower = Range.clip(drive - turn, -1.0, 1.0);
                turnPower = Range.clip((gamepad1.right_stick_x)*0.85, -1.0, 1.0);

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
                //telemetry.addData("GP1 GamePadOpMode", "Leg 1: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            } //end of while loop
            Log.d(TAG, "Thread 1 finishing up");
            telemetry.addData("GP1 Status", "Completed");
        }
    };

    private Runnable gp2Runnable = new Runnable() {
        public void run() {
            float slideD, slideU;
            int con = 10;

            while (opModeIsActive()) {
                //TODO: if necessary, make dpad on gp2 move slides to pos 0

                slideD = gamepad2.left_trigger;
                slideU = gamepad2.right_trigger;

                while(gamepad2.left_trigger < threshholdConst && gamepad2.right_trigger < threshholdConst){
                    robot.arm.armMotor1.setPower(0);
                    robot.arm.armMotor2.setPower(0);
                }

                if (gamepad2.left_trigger < threshholdConst) slideD = 0;

                if (gamepad2.left_trigger > 1 - threshholdConst){
                    slideD = 1;
                    telemetry.addData("GP2 Input", "Left Trigger");
                    telemetry.addData("GP2 Input level", "Slides Down");
                }

                if (gamepad2.right_trigger < threshholdConst) slideU = 0;

                if (gamepad2.right_trigger > 1 - threshholdConst){
                    slideU = 1;
                    telemetry.addData("GP2 Input", "Right Trigger");
                    telemetry.addData("GP2 Input level", "Slides Up");
                }


                robot.arm.armMotor1.setPower(slideD*con);
                robot.arm.armMotor2.setPower(slideD*con);
                robot.arm.armMotor1.setPower(-slideU*con);
                robot.arm.armMotor2.setPower(-slideU*con);

//possible breaking strat                if(robot.arm.armMotor1.getCurrentPosition() <= 300 && robot.arm.armMotor2.getCurrentPosition() <= 300){
//                    robot.arm.armMotor1.setPower(0);
//                    robot.arm.armMotor2.setPower(0);
//                }


                telemetry.addData("GP2 left armMotor encoder value", robot.arm.armMotor1.getCurrentPosition());
                telemetry.addData("GP2 right armMotor encoder value", robot.arm.armMotor2.getCurrentPosition());
                Log.d(TAG, "motor1 current height:" + robot.arm.armMotor1.getCurrentPosition());
                Log.d(TAG, "motor2 current height:" + robot.arm.armMotor2.getCurrentPosition());
                //telemetry.addData("GP2 GamePadOpMode", "Leg 1: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            } //end of while loop
            Log.d(TAG, "Thread 2 finishing up");
            telemetry.addData("GP2 Status", "Completed");
        }
    };

    //to be used for the servos for dropping the pixels
    private Runnable pickupRunnable = new Runnable() {
        public void run() {
            while (opModeIsActive()) {

                //Intake controls
                if(gamepad1.right_trigger != 0){
                    telemetry.addData("GP1 Input", "Right Trigger");
                    telemetry.addData("GP1 Input level", "Intake");
                    robot.arm.intakeMotor.setPower(-0.9);
                }else if(gamepad1.left_trigger != 0){
                    telemetry.addData("GP1 Input", "Left Trigger");
                    telemetry.addData("GP1 Input level", "Outtake");
                    robot.arm.intakeMotor.setPower(0.9);
                }else{
                    robot.arm.intakeMotor.setPower(0);
                }
                if(gamepad2.right_bumper){
                    telemetry.addData("GP2 Input", "Right Bumper");
                    telemetry.addData("GP2 Input level", "Bucket Out");
                    robot.arm.axon_right.setPosition(SERVO_UP);

                } else if (gamepad2.left_bumper){
                    telemetry.addData("GP2 Input", "Left Bumper");
                    telemetry.addData("GP2 Input level", "Bucket Back");
                    robot.arm.axon_right.setPosition(SERVO_DOWN);
                }

                if(gamepad2.a) {
                    telemetry.addData("GP2 Input", "A");
                    telemetry.addData("GP2 Input level", "Hold Right Pixel");
                    robot.arm.hook_right.setPosition(SERVO_UNLIFT);//hold pixel right

                }
                if (gamepad2.y){
                    telemetry.addData("GP2 Input", "Y");
                    telemetry.addData("GP2 Input level", "Release Right Pixel");
                    robot.arm.hook_right.setPosition(SERVO_LIFTED);//release pixel right
                }
                if(gamepad2.dpad_left) {
                    telemetry.addData("GP2 Input", "Dpad - Left");
                    telemetry.addData("GP2 Input level", "Release Pixel Left");
                    robot.arm.hook_left.setPosition(SERVO_UNLIFT-0.33);//release pixel left

                }
                if (gamepad2.dpad_right){
                    telemetry.addData("GP2 Input", "Dpad - Right");
                    telemetry.addData("GP2 Input level", "Hold Left Pixel");
                    robot.arm.hook_left.setPosition(SERVO_LIFTED-0.33);//hold pixel left
                }

                if (gamepad2.dpad_up){
                    telemetry.addData("GP2 Input", "Dpad - Up");
                    telemetry.addData("GP2 Input level", "Release Pixels");
                    robot.arm.hook_right.setPosition(SERVO_LIFTED); //release both pixels
                    robot.arm.hook_left.setPosition(SERVO_UNLIFT-0.33);

                }
                if (gamepad2.dpad_down){
                    telemetry.addData("GP2 Input", "Dpad - Dowm");
                    telemetry.addData("GP2 Input level", "Hold Pixels");
                    robot.arm.hook_right.setPosition(SERVO_UNLIFT);//hold both pixels
                    robot.arm.hook_left.setPosition(SERVO_LIFTED-0.33);
                }

                telemetry.update();
            } //end of while loop
            Log.d(TAG, "Thread 3 finishing up");
        }
    };

}

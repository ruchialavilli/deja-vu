package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;

public class DejaVuArm {
    /* Public OpMode members. */
    public DcMotorEx armExtension = null; //left
    public DcMotorEx armRotation = null; //right
    public Servo pitchServo = null;
    public CRServo intakeLeft = null;
    public CRServo intakeRight = null;
    static final double PULSES_PER_REVOLUTION = 751.8;
    private Telemetry telemetry;
    double  MIN_POSITION = 0, MAX_POSITION = 1;

    //max rpm for our arm motor is 1,850, here we're using 1750 rpm
    public static double SLIDER_TPS = 2500.0; //5959.5 MAX
    public static double SLIDER_TPS_DOWN = -2500.0;
    static HashMap<Integer, Integer> level_map = new HashMap<>();
    private String TAG = "DejaVuArm";
    private boolean isAuton;
    private HardwareMap hwMap = null;
    private int currentLevel = 0;
    {
        //100 = 1 inch
        level_map.put(0, 0 );//ground
        level_map.put(1, 50);//5 inches off the ground (pick up)//need to lower/test
        level_map.put(2, 100);//16 inches - level 1
        level_map.put(3, 3000);// to be 26 inches - level 2
        level_map.put(4, 1600);//to be 36 inches - level 3 (auton)

    }

    public DejaVuArm() {    }

    //Initialize the arm
    public void init(HardwareMap hMap, boolean isAuton) {
        this.isAuton = isAuton;
        this.hwMap = hMap;

        this.armExtension = hwMap.get(DcMotorEx.class, "armExtension");
        this.armRotation = hwMap.get(DcMotorEx.class, "armRotation");
        this.pitchServo = hwMap.servo.get("intakePitch");
        this.intakeLeft = hwMap.crservo.get("intakeLeft");
        this.intakeRight = hwMap.crservo.get("intakeRight");

        armRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armRotation.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        armExtension.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        armExtension.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        armRotation.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);


//        if(isAuton == false){
//            armExtension.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
//            armRotation.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
//
//        }else if(isAuton){
//            armExtension.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//            armRotation.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//
//        }


    }

    private void resetDCMotor(DcMotorEx a){
        Log.d(TAG, "Resetting DC Motor state");
        a.resetDeviceConfigurationForOpMode();
    }

    public void setArmMotorPowers(double num){
        while(armExtension.getCurrentPosition() <= 2000 && armRotation.getCurrentPosition() <= 2000 && armExtension.getCurrentPosition() >= 0 && armRotation.getCurrentPosition() >= 0){
            sendToTelemetry("LeftArmMotor currentPosition:" + armExtension.getCurrentPosition());
            sendToTelemetry("RightArmMotor currentPosition:" + armRotation.getCurrentPosition());
            armExtension.setVelocity(SLIDER_TPS);
            armRotation.setVelocity(SLIDER_TPS);
            armExtension.setPower(num);
            armRotation.setPower(num);


        }


    }



    public void moveArmToLevel(int level) {

        sendToTelemetry("moveArmToLevel:" + level);
        if(level != currentLevel) {
            //GO ONE LEVEL DOWN AT FULL speed\
            int height = level_map.get(level);
            // set the zero power behavior
//            if(level == 0){
//                armMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
//            } else {
            //armMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//            }
            //checking if going up
            sendToTelemetry("currentPosition:" + armExtension.getCurrentPosition());
            sendToTelemetry("currentPosition:" + armRotation.getCurrentPosition());

            sendToTelemetry("setting to height:" + height);
            armExtension.setTargetPosition(height);
            armRotation.setTargetPosition(height);

            //setting the armMotor's target
            sendToTelemetry("starting motor");
            armExtension.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            armRotation.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

            armExtension.setVelocity(SLIDER_TPS);
            armRotation.setVelocity(SLIDER_TPS);


            sendToTelemetry("turning on motor power");
            while (armExtension.isBusy() && armRotation.isBusy()) {

//                armMotor1.setPower(1);
//                armMotor2.setPower(1);

                Log.d(TAG, "motor1 going to level (" + level + ") expected height ("
                        + height + ") current height:" + armExtension.getCurrentPosition());
                Log.d(TAG, "motor2 going to level (" + level + ") expected height ("
                        + height + ") current height:" + armRotation.getCurrentPosition());
//                if(armMotor1.getCurrentPosition() == armMotor1.getTargetPosition() || armMotor2.getCurrentPosition() == armMotor2.getTargetPosition()){
//                    break;
//                }
//                if(level ==0 && armMotor.getCurrentPosition() < 10) {
//                    armMotor.setPower(0);
//                    Log.d(TAG, "reached 0 position - turning off power");
//                    try {
//                        sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
            sendToTelemetry("motor completed to level (" + level + ") current height:" + armExtension.getCurrentPosition());
            Log.d(TAG, "motor completed to level (" + level + ") expected height ("
                    + height + ") current height:" + armExtension.getTargetPosition());
            //motor done/break
            sendToTelemetry("Applying Brakes!");
//            if(level == 0){
//                armMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
//            } else {
//                armMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//            }
            sendToTelemetry("turning off motor power");
//            armMotor.setPower(0);


            //this is to auto-correct if we went beyond the level we need to go - MIGHT NOT NEED IT
//            if (armMotor1.getCurrentPosition() != level_map.get(level) && armMotor1.getCurrentPosition() != level_map.get(level)) {
//                armMotor1.setTargetPosition(level_map.get(level));
//                armMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                armMotor2.setTargetPosition(level_map.get(level));
//                armMotor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                while (armMotor1.isBusy() && armMotor2.isBusy()) {
//                    armMotor1.setVelocity(SLIDER_TPS/4);
//                    armMotor2.setVelocity(SLIDER_TPS/4);
//                    armMotor1.setPower(1);
//                    armMotor2.setPower(1);
//
//                }
//            }

            currentLevel = level;
//            if(level == 0){
//                armMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//                armMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//                sendToTelemetry("Reset Motor" );
//                Log.d(TAG, "Reset Motor");
//            }

            armExtension.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            armExtension.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            armRotation.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            armRotation.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

            sendToTelemetry("Breaking" );
            Log.d(TAG, "Breaking");
        } else {
            sendToTelemetry("already at level:" + level);
        }
    }



    public void setTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    private void sendToTelemetry(String msg){
        if(telemetry != null){
            telemetry.addData("DejaVuArm", msg);
            telemetry.update();
//            try {
//                sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}


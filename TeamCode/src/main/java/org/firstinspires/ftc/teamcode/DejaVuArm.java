package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DejaVuArm {
    /* Public OpMode members. */
    public DcMotorEx armMotor1 = null; //left
    public DcMotorEx armMotor2 = null; //right
    public DcMotorEx intakeMotor = null;
    public Servo axon_right = null;
    public Servo hook_right = null;


    public Servo gripperServo = null;
    static final double PULSES_PER_REVOLUTION = 751.8;
    private Telemetry telemetry;
    double  MIN_POSITION = 0, MAX_POSITION = 1;

    //max rpm for our arm motor is 1,850, here we're using 1750 rpm
    public static double SLIDER_TPS = 2500.0; //5959.5 MAX
    public static double SLIDER_TPS_DOWN = 2500.0;
    private String TAG = "DejaVuArm";
    private boolean isAuton;
    private HardwareMap hwMap = null;


    public DejaVuArm() {    }

    //Initialize the arm
    public void init(HardwareMap hMap, boolean isAuton) {
        this.isAuton = isAuton;
        this.hwMap = hMap;

        this.armMotor1 = hwMap.get(DcMotorEx.class, "leftArmMotor");
        this.armMotor2= hwMap.get(DcMotorEx.class, "rightArmMotor");
        this.intakeMotor = hwMap.get(DcMotorEx.class, "intakeMotor");
        this.axon_right = hwMap.servo.get("axonRight");
        this.hook_right = hwMap.servo.get("hookRight");

        armMotor2.setDirection(DcMotorSimple.Direction.REVERSE);

        armMotor1.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        armMotor2.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        armMotor1.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        armMotor2.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void resetDCMotor(DcMotorEx a){
        Log.d(TAG, "Resetting DC Motor state");
        a.resetDeviceConfigurationForOpMode();
    }

    public void setArmMotorPowers(double num){
        while(armMotor1.getCurrentPosition() <= 2000 && armMotor2.getCurrentPosition() <= 2000 && armMotor1.getCurrentPosition() >= 0 && armMotor2.getCurrentPosition() >= 0){
            sendToTelemetry("LeftArmMotor currentPosition:" + armMotor1.getCurrentPosition());
            sendToTelemetry("RightArmMotor currentPosition:" + armMotor2.getCurrentPosition());
            armMotor1.setVelocity(SLIDER_TPS);
            armMotor2.setVelocity(SLIDER_TPS);
            armMotor1.setPower(num);
            armMotor2.setPower(num);


        }


    }
//
//
//    public void moveArmToLevel(int level) {
//
//        sendToTelemetry("moveArmToLevel:" + level);
//        if(level != currentLevel) {
//            //GO ONE LEVEL DOWN AT FULL speed\
//            int height = level_map.get(level);
//            if(level < 4 && (level < currentLevel)) {
//                height+=20;
//            }
//            // set the zero power behavior
////            if(level == 0){
////                armMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
////            } else {
//                armMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
////            }
//            //checking if going up
//            sendToTelemetry("currentPosition:" + armMotor.getCurrentPosition());
//            sendToTelemetry("setting to height:" + height);
//            armMotor.setTargetPosition(height);
//            //setting the armMotor's target
//            sendToTelemetry("starting motor");
//            this.armMotor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
//            sendToTelemetry("turning off motor power");
//            while (armMotor.isBusy()) {
//                if (level > currentLevel) {
//                    armMotor.setVelocity(SLIDER_TPS);
//                }else{
//                    armMotor.setVelocity(SLIDER_TPS_DOWN);
//                }
//                armMotor.setPower(1);
//                Log.d(TAG, "motor going to level (" + level + ") expected height ("
//                        + height + ") current height:" + armMotor.getCurrentPosition());
////                if(level ==0 && armMotor.getCurrentPosition() < 10) {
////                    armMotor.setPower(0);
////                    Log.d(TAG, "reached 0 position - turning off power");
////                    try {
////                        sleep(100);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                }
//            }
//            sendToTelemetry("motor completed to level (" + level + ") current height:" + armMotor.getCurrentPosition());
//            Log.d(TAG, "motor completed to level (" + level + ") expected height ("
//                    + height + ") current height:" + armMotor.getTargetPosition());
//            //motor done/break
//            sendToTelemetry("Applying Brakes!");
//            if(level == 0){
//                armMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
//            } else {
//                armMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//            }
//            sendToTelemetry("turning off motor power");
//            armMotor.setPower(0);
//
//
//            //this is to auto-correct if we went beyond the level we need to go - MIGHT NOT NEED IT
//            if (armMotor.getCurrentPosition() != level_map.get(level)) {
//                armMotor.setTargetPosition(level_map.get(level));
//                armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                while (armMotor.isBusy()) {
//                    armMotor.setVelocity(SLIDER_TPS/4);
//                }
//                armMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
//            }
//            currentLevel = level;
//            if(level == 0){
//                resetDCMotor();
//            }
//        } else {
//            sendToTelemetry("already at level:" + level);
//        }
//    }


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


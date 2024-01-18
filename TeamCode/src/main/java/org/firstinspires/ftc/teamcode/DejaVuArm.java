package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;

public class DejaVuArm {
    /* Public OpMode members. */
    public DcMotorEx armMotor1 = null; //left
    public DcMotorEx armMotor2 = null; //right
    public DcMotorEx intakeMotor = null;
    public Servo axon_right = null;
    public Servo hook_right = null;
    public Servo hook_left = null;



    public Servo gripperServo = null;
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
        level_map.put(3, 2700);// to be 26 inches - level 2
        level_map.put(4, 1600);//to be 36 inches - level 3 (auton)

        level_map.put(9, 2900);//to be 36 inches - level 3
        //level_map.put(4, 2950);//to be 36 inches - level 3 (auton)

        level_map.put(5, 707);//getting first cone on stack (auton)
        level_map.put(6, 457);//first cone on stack (auton)
        level_map.put(7, 350);//second cone on stack (auton)
        level_map.put(8, 600);//getting second cone on stack (auton)

        level_map.put(10, 25 );//1 inch off ground

    }

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
        this.hook_left = hwMap.servo.get("hookLeft");

        armMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        armMotor1.setDirection(DcMotorSimple.Direction.REVERSE);
        armMotor2.setDirection(DcMotorSimple.Direction.FORWARD);

        armMotor1.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        armMotor2.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        intakeMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);


        if(isAuton == false){
            armMotor1.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
            armMotor2.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        }else if(isAuton){
            armMotor1.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            armMotor2.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            intakeMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        }


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
            sendToTelemetry("currentPosition:" + armMotor1.getCurrentPosition());
            sendToTelemetry("currentPosition:" + armMotor2.getCurrentPosition());

            sendToTelemetry("setting to height:" + height);
            armMotor1.setTargetPosition(height);
            armMotor2.setTargetPosition(height);

            //setting the armMotor's target
            sendToTelemetry("starting motor");
            armMotor1.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            armMotor2.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

            armMotor1.setVelocity(SLIDER_TPS);
            armMotor2.setVelocity(SLIDER_TPS);


            sendToTelemetry("turning on motor power");
            while (armMotor1.isBusy() && armMotor2.isBusy()) {

//                armMotor1.setPower(1);
//                armMotor2.setPower(1);

                Log.d(TAG, "motor1 going to level (" + level + ") expected height ("
                        + height + ") current height:" + armMotor1.getCurrentPosition());
                Log.d(TAG, "motor2 going to level (" + level + ") expected height ("
                        + height + ") current height:" + armMotor2.getCurrentPosition());
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
            sendToTelemetry("motor completed to level (" + level + ") current height:" + armMotor1.getCurrentPosition());
            Log.d(TAG, "motor completed to level (" + level + ") expected height ("
                    + height + ") current height:" + armMotor1.getTargetPosition());
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

            armMotor1.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            armMotor1.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            armMotor2.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            armMotor2.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

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


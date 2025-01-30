package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp
public class GOATEDNEW extends LinearOpMode {
    public final float threshholdConst = .1f;
    public final Servo intakePitch = null;
    public final Servo intakeLeft = null;
    public final Servo intakeRight = null;
    private int armTargetPosition = 0;

    public void runOpMode() throws InterruptedException {

        DcMotor frontLeft = hardwareMap.dcMotor.get("frontLeft");
        DcMotor backLeft = hardwareMap.dcMotor.get("backLeft");
        DcMotor frontRight = hardwareMap.dcMotor.get("frontRight");
        DcMotor backRight = hardwareMap.dcMotor.get("backRight");
        DcMotor armRotation = hardwareMap.dcMotor.get("armRotation");
        DcMotor armExtension = hardwareMap.dcMotor.get("armExtension");
        Servo intakePitch = hardwareMap.servo.get("intakePitch");
        CRServo intakeLeft = hardwareMap.crservo.get("intakeLeft");
        CRServo intakeRight = hardwareMap.crservo.get("intakeRight");

        armRotation.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armRotation.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y - x - rx) / denominator;
            double backLeftPower = (y + x - rx) / denominator;
            double frontRightPower = (y + x + rx) / denominator;
            double backRightPower = (y - x + rx) / denominator;

            frontLeft.setPower(frontLeftPower);
            backLeft.setPower(backLeftPower);
            frontRight.setPower(frontRightPower);
            backRight.setPower(backRightPower);

            float gp1AdjL = gamepad1.left_trigger;
            float gp1AdjR = gamepad1.right_trigger;
            if (gamepad1.left_trigger < threshholdConst) gp1AdjL = 0;
            if (gamepad1.left_trigger > 1 - threshholdConst) gp1AdjL = 1;
            if (gamepad1.right_trigger < threshholdConst) gp1AdjR = 0;
            if (gamepad1.right_trigger > 1 - threshholdConst) gp1AdjR = 1;
            armExtension.setPower(gp1AdjL*2);
            armExtension.setPower(-gp1AdjR*2);




            armExtension.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            if (gamepad2.right_bumper) {
                armTargetPosition += 10;
                armRotation.setTargetPosition(armTargetPosition);
                armRotation.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                armRotation.setPower(0.6);
            } else if (gamepad2.left_bumper) {
                armTargetPosition -= 10;
                armRotation.setTargetPosition(armTargetPosition);
                armRotation.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                armRotation.setPower(-0.6);
            } else {
                armRotation.setPower(0);
            }

            while (gamepad1.right_bumper) {
                armExtension.setPower(1);
            }
            while (gamepad1.left_bumper) {
                armExtension.setPower(-1);
            }    // Arm extension control

            if (gamepad1.dpad_down) {
                intakePitch.setPosition(0.5);
            }
            if (gamepad1.dpad_up) {
                intakePitch.setPosition(0.7);
            }
            if (gamepad1.y) {
                intakeLeft.setPower(-1);
                intakeRight.setPower(1);
            }
            if (gamepad1.x) {
                intakeLeft.setPower(1);
                intakeRight.setPower(-1);
            }
            if (gamepad1.b) {
                intakeLeft.setPower(0);
                intakeRight.setPower(0);
            }
        }
    }
}

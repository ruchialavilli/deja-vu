/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

/**
 * This class represents the autonomous run from Red 2  position with webcam integration
 */
@Autonomous(name = "BaseAutoVisionOpMode", group = "AutoOpModes")
@Disabled

public class BaseAutoVisionOpMode extends BaseAutoOpMode {
    private String TAG = "BaseAutoVisionOpMode";
    private boolean LIVE_GAME = true;
    protected static final String TFOD_MODEL_ASSET  = "ssd_mobilenet_v1_1_metadata_1.tflite";
    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera


    protected static final String[] LABELS = {
            "person","bicycle","car","motorcycle","airplane","bus","train","truck","boat","traffic light","fire hydrant","???","stop sign","parking meter","bench","bird","cat","dog","horse","sheep","cow","elephant","bear","zebra","giraffe","???","backpack","umbrella","???","???","handbag","tie","suitcase","frisbee","skis","snowboard","sports ball","kite","baseball bat","baseball glove","skateboard","surfboard","tennis racket","bottle","???","wine glass","cup","fork","knife","spoon","bowl","banana","apple","sandwich","orange","broccoli","carrot","hot dog","pizza","donut","cake","chair","couch","potted plant","bed","???","dining table","???","???","toilet","???","tv","laptop","mouse","remote","keyboard","cell phone","microwave","oven","toaster","sink","refrigerator","???","book","clock","vase","scissors","teddy bear","hair drier","toothbrush"
    };


    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private TfodProcessor tfod;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;

    // we have 3 dropping position - 1,2,3. Default is always 2.
    protected int droppingPosition = 1;
    //switch location 1 and 3 for red1

    protected static Vector2d locationToDrop;

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    protected void initTfod() {
        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

                // With the following lines commented out, the default TfodProcessor Builder
                // will load the default model for the season. To define a custom model to load,
                // choose one of the following:
                //   Use setModelAssetName() if the custom TF Model is built in as an asset (AS only).
                //   Use setModelFileName() if you have downloaded a custom team model to the Robot Controller.
                .setModelAssetName(TFOD_MODEL_ASSET)
                //.setModelFileName(TFOD_MODEL_FILE)

                // The following default settings are available to un-comment and edit as needed to
                // set parameters for custom models.
                .setModelLabels(LABELS)
                .setIsModelTensorFlow2(true)
                .setIsModelQuantized(true)
                .setModelInputSize(360)
                .setUseObjectTracker(true)
                .setModelAspectRatio(16.0 / 9.0)

                .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        //builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Set confidence threshold for TFOD recognitions, at any time.
        tfod.setMinResultConfidence(0.3f);

        // Disable or re-enable the TFOD processor at any time.
        visionPortal.setProcessorEnabled(tfod, true);
    }

    /**
     * Add telemetry about TensorFlow Object Detection (TFOD) recognitions.
     */
    public void telemetryTfod() {

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2 ;
            double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;

            telemetry.addData(""," ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
        }   // end for() loop

    }   // end method telemetryTfod()

    protected void findDroppingPosition(boolean red){
        String detectSign = "remote";
        if(red){
            detectSign = "stop sign";
        }
        int rightCount = 0;
        int centerCount = 0;
        int leftCount = 0;
        for(int i = 0; i < 10; i++) {
            List<Recognition> updatedRecognitions = tfod.getRecognitions();//tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null && updatedRecognitions.size() > 0) {
                sendToTelemetry("# Objects Detected", updatedRecognitions.size());
                // step through the list of recognitions and display image position/size information for each one
                // Note: "Image number" refers to the randomized image orientation/number
                for (Recognition recognition : updatedRecognitions) {
                    double col = (recognition.getLeft() + recognition.getRight()) / 2;
                    double row = (recognition.getTop() + recognition.getBottom()) / 2;
                    double width = Math.abs(recognition.getRight() - recognition.getLeft());
                    double height = Math.abs(recognition.getTop() - recognition.getBottom());

                    if (recognition.getLabel().equals(detectSign)) {
                        Log.d(TAG, "////************* " + i + ":");
                        Log.d(TAG, String.format("Image %s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100));
                        Log.d(TAG, String.format("- Position (Row/Col): %.0f / %.0f", row, col));
                        Log.d(TAG, String.format("- Size (Width/Height): %.0f / %.0f", width, height));
                        Log.d(TAG, "*************////");
                        if(row >= 130 && row <= 312){
                            centerCount++;
                        }else if(row > 312 && row <= 550){
                            rightCount++;
                        }else{
                            leftCount++;
                        }
                    } else {
                        Log.d(TAG, String.format("Found %s - not interested", recognition.getLabel()));
                    }
                }
                sendToTelemetry("Vision Result", String.format("Found %s(%d)",detectSign, centerCount+rightCount));
            } else {
//                Log.d(TAG, "No more recognitions found!");
            }
        }

        if(centerCount > 0){
            Log.d(TAG, "found stop sign - location 2");
            telemetry.addLine("found stop sign - location 2");
            droppingPosition = 2;

        }else if(rightCount > 0){
            Log.d(TAG, "found stop sign - location 3");
            telemetry.addLine("found stop sign - location 3");
            droppingPosition = 3;

        } else if (leftCount > 0){
            Log.d(TAG, "found stop sign - location 1");
            telemetry.addLine("found stop sign - location 1");
            droppingPosition = 1;

        }else {
            Log.d(TAG, "unable to determine dropping location - defaulting to 1");
            telemetry.addLine("unable to determine dropping location - defaulting to 1");
            droppingPosition = 1;
        }

        // test test - DELETE TODO
        droppingPosition = 3;
    }

    protected void sendToTelemetry(String key, int value){
        if(!LIVE_GAME) {
            sendToTelemetry(key, String.format("%d", value));
        }
    }
    protected void sendToTelemetry(String key, String value){
        if(!LIVE_GAME) {
//            Log.d(TAG, key + " - " + value);
            telemetry.addData(key, value);
            telemetry.update();
        }
    }
}

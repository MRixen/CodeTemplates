package rixen.manleo.dev.mytemplatelibrary;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dev on 1/1/15.
 */
public class MySensorData extends Thread implements SensorEventListener {

    private final Activity activity;
    private final Context context;
    public Handler handler;
    private SensorManager sManager;
    private Handler threadHandler;
    private Thread sThread;
    private int sDelay, sType;
    private int[] sensorTransformation;
    private final double g = 10;
    private float gz;
    private double alpha;

    public final int ORIGINAL = 0;
    public final int NO_DIRECTION = 1;
    public final int POS_DIRECTION = 2;
    public final int NEG_DIRECTION = 3;

    private boolean calibrationFlag;
    private boolean calculationFlag;

    public MySensorData(Context context){
        this.context = context;
        activity = new Activity();
    }



    public void run() {
        // Get sensor object
        sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // Check if sensor is available
        // TODO Add other sensor types
        Sensor sensor = sManager.getDefaultSensor(sType);
        if (sensor != null) {
            // Start loop for execution
            Looper.prepare();
            // Handler to cancel the message loop
            threadHandler = new Handler();

            // handler to receive message from other thread
            handler = new Handler() {
                public void handleMessage(Message msg) {
                    Bundle data = msg.getData();
                    if(data != null){
                        if(data.containsKey("transformation")){
                            sensorTransformation = data.getIntArray("transformation");
                            unregisterSensorListener();
                        }
                    }

                }
            };

            // Register sensor listener
            sManager.registerListener(MySensorData.this, sensor, sDelay, threadHandler);
            Looper.loop();
        } else Toast.makeText(activity, "Sensor isn't available", Toast.LENGTH_SHORT).show();

    }

    private void unregisterSensorListener() {
        sManager.unregisterListener(MySensorData.this);
        // Set flags to provide calibration and calculation
        calibrationFlag = true;
        calculationFlag = true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == sType) {
            float[] sensorTempData = event.values;
            float[] sensorData = new float[3];

            // Get original sensor values
            if(sensorTransformation[0] == ORIGINAL){
                for(int i = 0; i < sensorTempData.length; i++){
                    sensorData[i] = sensorTempData[i];
                }
            }
            // Get sensor values for landscape mode
            if(sensorTransformation[0] == POS_DIRECTION){
                if(calibrationFlag){
                    // TODO
                }
            }
            // ---- CALIBRATION ----
            // Get sensor values for reverse landscape mode
            if(sensorTransformation[0] == NEG_DIRECTION){
                if(calibrationFlag){
                    gz = sensorTempData[2];
                    alpha = -(Math.PI / 2) + Math.acos(gz / g);
                    calibrationFlag = false;
                }

                // Stop calculation if the device isn't in correct calibration area
                if(calculationFlag) {
                    // ---- CALCULATION ----
                    double[] Ry = {-Math.sin(alpha), 0, Math.cos(alpha)};

                    for (int i = 0; i < sensorTempData.length; i++) {
                        // Calculate z value only
                        sensorData[1] += Ry[i] * sensorTempData[i];
                    }
                    sensorData[0] = -sensorTempData[1];
                }

                // ---- VALIDATION ----
                // Check sensorTransformation area
                if (Math.abs(gz) > g || ( sensorData[0] > 0 || sensorData[2] < 0 )){
                    toastInMainThread("Not in correct calibration area");
                    calculationFlag = false;
                }
                else{
                    toastInMainThread("Calibration success");

                    // TODO Send sensor data to other (callback method?)
                }

            }
        }
    }

    private void toastInMainThread(final String toast) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                    }
                });
            }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void startSensorData(int sDelay, int sType) {
        // Set sensor delay and sensor type
        this.sDelay = sDelay;
        this.sType = sType;

        // Start sensor thread
        if(sThread == null){
            sThread = new Thread(this);
            sThread.start();
        }
    }

    public void stopSensorData() {
        unregisterSensorListener();
        try {
            threadHandler.getLooper().quit();
        }catch(NullPointerException e){
            Log.d("MySensorData->stopSensorData:", String.valueOf(e));
        }

    }
}

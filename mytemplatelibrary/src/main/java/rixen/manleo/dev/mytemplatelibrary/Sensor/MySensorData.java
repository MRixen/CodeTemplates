package rixen.manleo.dev.mytemplatelibrary.Sensor;

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
public class MySensorData extends Thread implements SensorEventListener, MySensorEventListener{

    public final int ORIGINAL = 0;
    public final int NO_DIRECTION = 1;
    public final int POS_DIRECTION = 2;
    public final int NEG_DIRECTION = 3;

    private final Activity activity;
    private final Context context;
    private final MySensorEventListener mySensorEventListener;
    public Handler inMsgHandler, threadHandler;
    private SensorManager sManager;
    private Thread sThread;
    private int sDelay, sType;
    private int[] sTransform;
    private final double g = 10;
    private float gz;
    private double alpha;
    private boolean caliFlag, calcFlag;

    public MySensorData(Context context, MySensorEventListener mySensorEventListener){
        this.context = context;
        this.mySensorEventListener = mySensorEventListener;
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

            // inMsgHandler to receive message from other thread
            inMsgHandler = new Handler() {
                public void handleMessage(Message msg) {
                    Bundle data = msg.getData();
                    if(data != null){
                        if(data.containsKey("transformation")){
                            sTransform = data.getIntArray("transformation");
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
        caliFlag = true;
        calcFlag = true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == sType) {
            float[] sensorTempData = event.values;
            float[] sensorData = new float[3];

            // Get original sensor values
            if(sTransform[0] == ORIGINAL){
                for(int i = 0; i < sensorTempData.length; i++){
                    sensorData[i] = sensorTempData[i];
                }
            }
            // Get sensor values for landscape mode
            if(sTransform[0] == POS_DIRECTION){
                if(caliFlag){
                    // TODO
                }
            }
            // ---- CALIBRATION ----
            // Get sensor values for reverse landscape mode
            if(sTransform[0] == NEG_DIRECTION){
                if(caliFlag){
                    gz = sensorTempData[2];
                    alpha = -(Math.PI / 2) + Math.acos(gz / g);
                    caliFlag = false;
                }

                // ---- CALCULATION ----
                // Stop calculation if the device isn't in correct calibration area
                if(calcFlag) {
                    double[] Ry = {-Math.sin(alpha), 0, Math.cos(alpha)};

                    for (int i = 0; i < sensorTempData.length; i++) {
                        // Calculate z value only
                        sensorData[1] += Ry[i] * sensorTempData[i];
                    }
                    sensorData[0] = -sensorTempData[1];
                }

                // ---- VALIDATION ----
                // Check sTransform area
                if (Math.abs(gz) > g || ( sensorData[0] > 0 || sensorData[2] < 0 )){
                    toastInMainThread("Not in correct calibration area");
                    calcFlag = false;
                }
                else{
                    toastInMainThread("Calibration success");
                    mySensorEventListener.onNewSensorEvent();
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

    @Override
    public void onNewSensorEvent() {

    }
}

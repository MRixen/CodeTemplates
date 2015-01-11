package rixen.manleo.dev.mytemplates;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import rixen.manleo.dev.mytemplatelibrary.Data;
import rixen.manleo.dev.mytemplatelibrary.Utilities.Movement;
import rixen.manleo.dev.mytemplatelibrary.Sensor.MySensorData;
import rixen.manleo.dev.mytemplatelibrary.Sensor.MySensorEventListener;
import rixen.manleo.dev.mytemplatelibrary.MySurfaceView;


public class MainActivity extends Activity implements MySensorEventListener {

    private MySurfaceView mySurfaceView;
    private MySensorData mySensorData;
    private Movement movement;
    private SurfaceView surface;
    private int sDelay, sType;
    private MySensorEventListener mySensorEventListener;
    private int speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        // ------------------
        // ----- SENSOR -----
        // ------------------
        // Set sensor type and delay
        // type-> Accelerometer: 1
        // delay-> Fastest: 0, Game: 1, UI: 2, Normal: 3
        sType = 1; sDelay = 3;
        mySensorEventListener = this;
        // New instance of sensor data class
        mySensorData = new MySensorData(this, mySensorEventListener);
        // ------------------

        // ------------------
        // ----- SURFACE ----
        // ------------------
        // Create reference to surface view
        surface = (SurfaceView) findViewById(R.id.surface_view);
        // New instance of surface view class
        mySurfaceView = new MySurfaceView(this);
        // ------------------

        // ------------------
        // ----- MOTOR ----
        // ------------------
        // New instance of movement class
        movement = new Movement(this); // Start communication thread
        // ------------------
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_calibration:
                Message msg = new Message();
                Bundle bundle = new Bundle();

                int[] transformation = {mySensorData.ORIGINAL, mySensorData.NO_DIRECTION};
                bundle.putIntArray("transformation", transformation);
                msg.setData(bundle);
                // Send message to sensor thread to calibrate sensor
                if(mySensorData.inMsgHandler != null) mySensorData.inMsgHandler.sendMessage(msg);
                else Toast.makeText(this, "Handler is null", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_startMotor:
                movement.moveStepper(Data.Type.go, speed, Data.StepResolution.half);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ------------------
        // ----- SENSOR -----
        // ------------------
        // Start sensor acquisition
        mySensorData.startSensorData(sDelay, sType);
        // ------------------

        // ------------------
        // ----- SURFACE ----
        // ------------------
        // Start drawing on the surface
        mySurfaceView.startSurfaceView(surface);
        // ------------------
    }

    @Override
    protected void onPause() {
        super.onPause();

        // ------------------
        // ----- SENSOR -----
        // ------------------
        // Stop sensor acquisition
        mySensorData.stopSensorData();
        // ------------------

        // ------------------
        // ----- SURFACE ----
        // ------------------
        mySurfaceView.stopSurfaceView();
        // ------------------
    }

    @Override
    public void onNewSensorEvent() {

    }
}

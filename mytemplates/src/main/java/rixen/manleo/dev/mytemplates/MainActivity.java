package rixen.manleo.dev.mytemplates;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import rixen.manleo.dev.mytemplatelibrary.MySensorData;
import rixen.manleo.dev.mytemplatelibrary.MySurfaceView;


public class MainActivity extends Activity {

    private MySurfaceView mySurfaceView;
    private MySensorData mySensorData;
    private SurfaceView surface;
    private int sDelay, sType;

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
        // New instance of sensor data class
        mySensorData = new MySensorData(this);
        // ------------------

        // ------------------
        // ----- SURFACE ----
        // ------------------
        // Create reference to surface view
        surface = (SurfaceView) findViewById(R.id.surface_view);
        // New instance of surface view class
        mySurfaceView = new MySurfaceView(this);
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
                if(mySensorData.handler != null) mySensorData.handler.sendMessage(msg);
                else Toast.makeText(this, "Handler is null", Toast.LENGTH_SHORT).show();
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
}

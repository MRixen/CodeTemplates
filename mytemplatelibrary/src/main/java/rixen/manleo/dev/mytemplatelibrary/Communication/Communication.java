package rixen.manleo.dev.mytemplatelibrary.Communication;

import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Created by dev on 1/11/15.
 */
public class Communication implements Runnable{

    private Handler threadHandler;
    private Client server;
    public Handler inMsgHandler;
    private Thread sThread;

    @Override
    public void run() {
        Looper.prepare();
        threadHandler = new Handler();

        // inMsgHandler to receive message from other thread
        inMsgHandler = new Handler() {
            public void handleMessage(Message msg) {
                Bundle messageData = msg.getData();
                if(messageData != null){
                    if(messageData.containsKey("MovementData")){
                        int[] data = messageData.getIntArray("MovementData");

                        // Convert int array to byte array
                        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4); // Integer has 4 byte
                        IntBuffer intBuffer = byteBuffer.asIntBuffer();
                        intBuffer.put(data);

                        byte[] dataArray = byteBuffer.array();
                        try
                        {
                            server.send(dataArray);

                        } catch (IOException e)
                        {
                            Log.e("sendToArduino", "Problem sending TCP message", e);
                        }
                    }
                }

            }
        };


        Looper.loop();
    }


    public void startCommunicationThread() {
        // Start sensor thread
        if(sThread == null){
            sThread = new Thread(this);
            sThread.start();
        }
    }

    public void stopCommunicationThread() {
        try {
            threadHandler.getLooper().quit();
        }catch(NullPointerException e){
            Log.d("Communication->stopCommunicationThread:", String.valueOf(e));
        }

    }

}

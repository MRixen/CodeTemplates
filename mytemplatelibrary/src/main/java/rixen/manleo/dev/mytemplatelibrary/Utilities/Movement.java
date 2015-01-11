package rixen.manleo.dev.mytemplatelibrary.Utilities;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import rixen.manleo.dev.mytemplatelibrary.Communication.Communication;
import rixen.manleo.dev.mytemplatelibrary.Data;

/**
 * Created by dev on 1/10/15.
 */
public class Movement {

    private final Communication communication;
    private final Context context;
    private int rotType, rotQuantity, utilityContext, sleep, enable, reset, stepResolution;

    public Movement(Context context) {
        this.context = context;
        // Start communication thread
        communication= new Communication();
        communication.startCommunicationThread();
    }

    public void moveServo(Data.Type rotType, int rotQuantity){
        this.rotType = rotType.ordinal();
        this.rotQuantity = rotQuantity;
        this.stepResolution = 0;
        this.utilityContext = Data.Context.motor.ordinal();
        this.sleep = 0;
        this.enable = 0;
        this.reset = 0;

        sendToArduino();
    }


    public void moveStepper(Data.Type rotType, int rotQuantity, Data.StepResolution stepResolution){
        this.rotType = rotType.ordinal();
        this.rotQuantity = rotQuantity;
        this.stepResolution = stepResolution.ordinal();
        this.utilityContext = Data.Context.motor.ordinal();
        this.sleep = 0;
        this.enable = 0;
        this.reset = 0;

        sendToArduino();
    }

/*    public void initServo(){
        sendToArduino();
    }*/

    public void initStepper(Data.Init option){
        this.rotType = 0;
        this.rotQuantity = 0;
        this.stepResolution = 0;
        this.utilityContext = Data.Context.motor.ordinal();
        if(option.ordinal() == Data.Init.sleep.ordinal()) this.sleep = 1; else this.sleep = 0;
        if(option.ordinal() == Data.Init.enable.ordinal()) this.enable = 1; else this.enable = 0;
        if(option.ordinal() == Data.Init.reset.ordinal()) this.reset = 1; else this.reset = 0;

        sendToArduino();
    }

    public void sendToArduino(){
        Message msg = new Message();
        Bundle bundle = new Bundle();

        int[] movementData = {this.rotType, this.rotQuantity, this.stepResolution, this.utilityContext, this.sleep, this.enable, this.reset};
        bundle.putIntArray("MovementData", movementData);
        msg.setData(bundle);
        // Send message to sensor thread to calibrate sensor
        if(communication.inMsgHandler != null) communication.inMsgHandler.sendMessage(msg);
        else Toast.makeText(context, "Handler is null", Toast.LENGTH_SHORT).show();
    }
}

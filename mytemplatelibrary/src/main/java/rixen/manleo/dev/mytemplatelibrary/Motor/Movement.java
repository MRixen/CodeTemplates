package rixen.manleo.dev.mytemplatelibrary.Motor;

import android.os.Bundle;
import android.os.Message;

import rixen.manleo.dev.mytemplatelibrary.Data;

/**
 * Created by dev on 1/10/15.
 */
public class Movement {

    private int rotType;
    private int rotQuantity;
    private int context;
    private int sleep;
    private int enable;
    private int reset;
    private int stepResolution;

    public Movement() {

    }

    public void moveServo(Data.Type rotType, int rotQuantity){
        this.rotType = rotType.ordinal();
        this.rotQuantity = rotQuantity;
        this.stepResolution = 0;
        this.context = Data.Context.motor.ordinal();
        this.sleep = 0;
        this.enable = 0;
        this.reset = 0;

        prepareMessage();
    }


    public void moveStepper(Data.Type rotType, int rotQuantity, Data.StepResolution stepResolution){
        this.rotType = rotType.ordinal();
        this.rotQuantity = rotQuantity;
        this.stepResolution = stepResolution.ordinal();
        this.context = Data.Context.motor.ordinal();
        this.sleep = 0;
        this.enable = 0;
        this.reset = 0;

        prepareMessage();
    }

/*    public void initServo(){
        prepareMessage();
    }*/

    public void initStepper(Data.Init option){
        this.rotType = 0;
        this.rotQuantity = 0;
        this.stepResolution = 0;
        this.context = Data.Context.motor.ordinal();
        if(option.ordinal() == Data.Init.sleep.ordinal()) this.sleep = 1; else this.sleep = 0;
        if(option.ordinal() == Data.Init.enable.ordinal()) this.enable = 1; else this.enable = 0;
        if(option.ordinal() == Data.Init.reset.ordinal()) this.reset = 1; else this.reset = 0;

        prepareMessage();
    }

    public void prepareMessage(){
        Message msg = new Message();
        Bundle bundle = new Bundle();

        // TODO Send byte array with given parameters to server (microbridge)

    }
}

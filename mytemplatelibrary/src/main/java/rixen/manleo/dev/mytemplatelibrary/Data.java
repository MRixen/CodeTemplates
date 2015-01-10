package rixen.manleo.dev.mytemplatelibrary;

/**
 * Created by dev on 1/10/15.
 */
public class Data {
    public enum Type{
        go,
        incremental
    }

    public enum StepResolution{
        full,
        half,
        quarter,
        eighth
    }

    public enum Context{
        light,
        motor
    }

    public enum Init{
        sleep,
        enable,
        reset
    }
}

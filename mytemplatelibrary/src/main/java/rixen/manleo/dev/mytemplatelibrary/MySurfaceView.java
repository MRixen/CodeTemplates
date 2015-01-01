package rixen.manleo.dev.mytemplatelibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by mr on 20.05.14.
 *
 */
public class MySurfaceView extends SurfaceView implements Runnable {

    private Context context;
    private Thread drawThread;
    private Handler threadHandler;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint svfPaint;
    private int pxWidth, pxHeight;
    private float factorHeight, factorWidth;
    private float rectLength;
    private float rectHeight;
    private int backgroundColor;
    private SurfaceView surface;

    public MySurfaceView(Context context) {
        super(context);
        this.context = context;
        drawThread = null;

        // Dimension for rect to draw (in px)
        // Normally, you need to set it in dp unit and transform it to px unit
        rectLength = 60;
        rectHeight = 25;
        backgroundColor = context.getResources().getColor(R.color.White);
        calculateDimensions();
    }

    public void calculateDimensions(){
        // Calculate display size
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        pxWidth = displayMetrics.widthPixels;
        pxHeight = displayMetrics.heightPixels;
        float dpWidth = pxWidth / displayMetrics.density;
        float dpHeight = pxHeight / displayMetrics.density;

        // Factor dp to px
        factorHeight = (pxHeight / dpHeight);
        factorWidth = (pxWidth / dpWidth);
    }

    public void surfaceInit() {
        // Set draw color and other options
        svfPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        svfPaint.setStyle(Paint.Style.FILL);
        svfPaint.setColor(context.getResources().getColor(R.color.Orange));
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    public void run() {
        long timeElapsed;
        long startTime = System.nanoTime();

        while (!surfaceHolder.getSurface().isValid()) {
            timeElapsed = (System.nanoTime() - startTime) / context.getResources().getInteger(R.integer.DIVIDER);
            if (timeElapsed >= context.getResources().getInteger(R.integer.dTimeSurfaceValidation)) {
                break;
            }
        }

        Looper.prepare();
        threadHandler = new Handler();

        try {
            canvas = surfaceHolder.lockCanvas();
            drawLayout(rectLength, rectHeight);
            surfaceHolder.unlockCanvasAndPost(canvas);
        } catch (Exception e) {
            Log.e("@MySurfaceView-run: ", "Exception: " + e);
        }

        Looper.loop();
    }

    public void drawLayout(float length, float height){
        canvas.drawColor(backgroundColor);

        // Draw rect
        canvas.drawRect(surface.getRight()/3, (surface.getBottom()-surface.getTop())/2-height, (surface.getRight()/3)*2, ( surface.getBottom()-surface.getTop() )/2+height, svfPaint);
    }

    public void stopSurfaceView(){
            try {
                threadHandler.getLooper().quit();
                threadHandler = null;
                // Blocks drawThread until all operations are finished
                drawThread.join();
            }catch(Exception e){
                Log.d("@MySurfaceView-stopSurfaceView: ", String.valueOf(e));
            }
        drawThread = null;
    }

    public void startSurfaceView(SurfaceView surface) {
        // Stop old drawThread to provide new theme settings
        stopSurfaceView();

        this.surface = surface;
        // Set surfaceHolder
        surface.setZOrderOnTop(false);
        surfaceHolder = surface.getHolder();

        // Set paint options
        surfaceInit();

        // Stop last drawThread and execute a new one
        if (drawThread == null) {
            drawThread = new Thread(this);
            drawThread.start();
        }
    }
}

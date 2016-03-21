package com.example.jayzhang.doodler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;

/**
 * Created by jayzhang on 3/6/16.
 */
public class DoodleView extends View {

    Paint paintDoodle;
    ArrayList<Float> strokeWidths;
    ArrayList<Integer> strokeOpacity;
    ArrayList<Integer> colors;
    ArrayList<Path> paths;
    Bitmap bitmap;
    // Path path = new Path();
    float strokeWidth = 1;
    int currentOpacity = 0;
    int currentColor = 0;

    public DoodleView(Context context) {
        super(context);
        init(null, 0);
    }

    public DoodleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {
        paths = new ArrayList<Path>();
        strokeWidths = new ArrayList<Float>();
        strokeOpacity = new ArrayList<Integer>();
        colors = new ArrayList<Integer>();
        bitmap = null;

        paintDoodle = new Paint();
        paintDoodle.setColor(currentColor);
        paintDoodle.setAntiAlias(true);
        paintDoodle.setStyle(Paint.Style.STROKE);
        paintDoodle.setStrokeWidth(strokeWidth);
        paintDoodle.setAlpha(currentOpacity);

        paths.add(new Path());
        colors.add(new Integer(currentColor));
        strokeWidths.add(new Float(strokeWidth));
        strokeOpacity.add(new Integer(currentOpacity));
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bitmap != null)
            canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.DITHER_FLAG));

        for (int i = 0; i < paths.size(); i++) {
            paintDoodle.setColor(colors.get(i));
            paintDoodle.setStrokeWidth(strokeWidths.get(i));
            paintDoodle.setAlpha(strokeOpacity.get(i));

            canvas.drawPath(paths.get(i), paintDoodle);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        if (paintDoodle.getStrokeWidth() != strokeWidth
                || paintDoodle.getAlpha() != currentOpacity
                || paintDoodle.getColor() != currentColor) {
            strokeWidth = paintDoodle.getStrokeWidth();
            currentOpacity = paintDoodle.getAlpha();
            currentColor = paintDoodle.getColor();

            paths.add(new Path());
            strokeWidths.add(new Float(strokeWidth));
            strokeOpacity.add(new Integer(currentOpacity));
            colors.add(new Integer(currentColor));
        }

        /*
        Log.d("strokeWidth: ", String.valueOf(paintDoodle.getStrokeWidth()));
        Log.d("strokeOpacity: ", String.valueOf(paintDoodle.getAlpha()));
        Log.d("currentColor: ", String.valueOf(paintDoodle.getColor()));
        */

        Path path = paths.get(paths.size() - 1);
        if (motionEvent.getAction() == motionEvent.ACTION_DOWN) {
            path.moveTo(touchX, touchY);
        } else if (motionEvent.getAction() == motionEvent.ACTION_MOVE) {
            path.lineTo(touchX, touchY);
        }

        invalidate();
        return true;
    }
}

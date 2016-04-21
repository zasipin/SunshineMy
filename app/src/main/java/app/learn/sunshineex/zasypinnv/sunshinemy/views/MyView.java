package app.learn.sunshineex.zasypinnv.sunshinemy.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ZasypinNV on 20.04.2016.
 */
public class MyView extends View {

    Paint mPaint;
    float mPaintHeight;

    public MyView(Context context)
    {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defaultStyle)
    {
        super(context, attrs, defaultStyle);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawCircle(10, 10, 30, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int myHeight = defineMeasure(heightMeasureSpec);
        int myWidth = defineMeasure(widthMeasureSpec);
        setMeasuredDimension(myHeight, myWidth);
    }

    private int defineMeasure(int measure)
    {
        int measureSpecMode = MeasureSpec.getMode(measure);
        int measureSpecSize = MeasureSpec.getSize(measure);
        int myMeaseure = measureSpecSize;

        if (measureSpecMode == MeasureSpec.EXACTLY)
            myMeaseure = measureSpecSize;
        else if (measureSpecMode == MeasureSpec.AT_MOST)
            // wrap content
            myMeaseure = measureSpecSize;
        return myMeaseure;
    }

    private void init()
    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mPaintHeight);
    }
}

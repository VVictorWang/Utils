package com.victor.utils.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by victor on 7/14/17.
 * email: chengyiwang@hustunique.com
 * blog: www.victorwang.science                                            #
 */

public class CircleProgressView extends View {
    private float mSweepValue = 75;
    private int length;
    private int showTextsize = 30;
    private int cicleXY;

    public CircleProgressView(Context context) {
        super(context);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        length = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint ciclepaint = new Paint();
        ciclepaint.setColor(Color.RED);
        ciclepaint.setStyle(Paint.Style.FILL);
        Paint arcPaint = new Paint();
        arcPaint.setColor(Color.BLUE);
        arcPaint.setStyle(Paint.Style.STROKE);
        int strokesize = length / 8;
        arcPaint.setStrokeWidth(strokesize);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(showTextsize);
        cicleXY = length / 2;
        float radius = (float) (length * 0.5 / 2);
        RectF mRect = new RectF((float) (length * 0.1), (float) (length * 0.1), (float) (length * 0.9), (float) (length * 0.9));
        canvas.drawCircle(cicleXY, cicleXY, radius, ciclepaint);
        float mSweepAngle = mSweepValue / 100 * 360;
        canvas.drawArc(mRect, 270, mSweepAngle, false, arcPaint);
        String showText = String.valueOf(mSweepValue) + "%";
        canvas.drawText(showText, 0, showText.length(), (float) (cicleXY - showTextsize), cicleXY + (showTextsize / 4), textPaint);

    }

    public void setSweepValue(float sweepValue) {
        if (sweepValue != 0) {
            mSweepValue = sweepValue;
        } else
            mSweepValue = 25;
        this.invalidate();
    }
}

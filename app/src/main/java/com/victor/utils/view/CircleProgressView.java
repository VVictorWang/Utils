package com.victor.utils.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.victor.utils.R;

/**
 * Created by victor on 7/14/17.
 * email: chengyiwang@hustunique.com
 * blog: www.victorwang.science                                            #
 */

//超级粗略的一个CircleProgressbar
public class CircleProgressView extends View {
    private int cirlceColor, progressColor;
    private int circleWidth, progressWidth;
    private int center, radius;


    private Paint circlePaint, proGressPaint;

    private int mProgress = 0, maxProgress = 100;

    private ValueAnimator.AnimatorUpdateListener mAnimatorListener;//动画更新监听
    private ValueAnimator mProgressAnimator;// 进度动画
    private int mAnimatorProgress = 0;//动画进度
    private float mAnimatorProgressAngle = 0;// 动画进度角度
    private long mProgressDuration = 1000;// 进度动画时长
    private final ValueAnimator mLoadingAnimator = ValueAnimator.ofFloat(0f, 1f);// 载入动画

    private OnProgressChange mOnProgressChange;

    public CircleProgressView(Context context) {
        super(context);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

        mAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                if (animator == mProgressAnimator) {
                    mAnimatorProgress = (int) animator.getAnimatedValue();
                    mAnimatorProgressAngle = 360 * (((float) mAnimatorProgress) /
                            maxProgress);
                    if (mOnProgressChange != null) {
                        mOnProgressChange.onProgressChange(mAnimatorProgress);
                    }
                    invalidate();
                } else if (animator == mLoadingAnimator) {
                    invalidate();
                }
            }
        };
        mLoadingAnimator.addUpdateListener(mAnimatorListener);
        mLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mLoadingAnimator.setRepeatMode(ValueAnimator.RESTART);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        cirlceColor = a.getColor(R.styleable.CircleProgressView_circle_color, Color.BLACK);
        circleWidth = (int) a.getDimension(R.styleable.CircleProgressView_circle_width, 10);
        circlePaint = new Paint();
        circlePaint.setColor(cirlceColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(circleWidth);
        circlePaint.setAntiAlias(true);

        progressColor = a.getColor(R.styleable.CircleProgressView_progress_color, Color.GREEN);
        progressWidth = (int) a.getDimension(R.styleable.CircleProgressView_progress_width, 10);
        proGressPaint = new Paint();
        proGressPaint.setColor(progressColor);
        proGressPaint.setStyle(Paint.Style.STROKE);
        proGressPaint.setStrokeWidth(progressWidth);
        proGressPaint.setStrokeCap(Paint.Cap.ROUND);

        mProgress = a.getInt(R.styleable.CircleProgressView_current_progress, 0);
        maxProgress = a.getInt(R.styleable.CircleProgressView_max_progress, 100);

        mProgressDuration = a.getInt(R.styleable.CircleProgressView_animate_duration, 1000);

        animationToProgress(0, mProgress);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredWidth = 300;
        int desiredHeight = 300;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);

        center = getWidth() / 2;

        //圆环的半径 ，此处必须是progressWidth与circleWidth中较大的一个
        if (progressWidth > circleWidth)
            radius = (center - progressWidth / 2);
        else {
            radius = (center - circleWidth / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(center, center, radius, circlePaint);

        RectF mRect = new RectF(center - radius, center - radius, center
                + radius, center + radius);
        canvas.drawArc(mRect, 270, mAnimatorProgressAngle, false,
                proGressPaint);
    }

    public void setProgress(int progress) {
        animationToProgress(mProgress, progress);
        mProgress = progress;

    }

    public void setProgressChangeListenner(OnProgressChange onProgressChange) {
        mOnProgressChange = onProgressChange;
    }

    public int getProgress() {
        return mProgress;
    }


    /**
     * 创建进度动画
     */
    private void makeProgressAnimation(int start, int end) {

        if (mProgressAnimator != null && mProgressAnimator.isRunning())
            mProgressAnimator.end();
        mProgressAnimator = ValueAnimator.ofInt(start, end);
        mProgressAnimator.setDuration(mProgressDuration);
        mProgressAnimator.addUpdateListener(mAnimatorListener);
        mProgressAnimator.start();
    }

    /**
     * 动画到某一进度
     */
    public void animationToProgress(int start, int progress) {
        mProgress = progress > maxProgress ? maxProgress : progress;
        makeProgressAnimation(start, progress > maxProgress ? maxProgress : progress);
    }

    public interface OnProgressChange {
        void onProgressChange(int progress);
    }

}

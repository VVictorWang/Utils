package com.victor.utils.modular;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.victor.utils.R;

/**
 * Created by victor on 7/13/17.
 * email: chengyiwang@hustunique.com
 * blog: www.victorwang.science                                            #
 */

public class TopBar extends RelativeLayout {
    private Button mLeftButton, mRightButton;
    private TextView mTitleTextView;
    private int mLeftTextColor, mRightTextColor, mTitleTextColor;
    private float mTitleTextsize;
    private Drawable mLeftBackGround, mRightBackGround;
    private String mTitle, mLeftText, mRightText;
    private topBarClickListenner mtopBarClickListenner;

    public TopBar(Context context) {
        super(context);
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //获取属性值
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
        mLeftTextColor = ta.getColor(R.styleable.TopBar_leftTextColor, 0);
        mLeftBackGround = ta.getDrawable(R.styleable.TopBar_leftBackGround);
        mLeftText = ta.getString(R.styleable.TopBar_leftText);
        mRightTextColor = ta.getColor(R.styleable.TopBar_rightTextColor, 0);
        mRightBackGround = ta.getDrawable(R.styleable.TopBar_rightBackGround);
        mRightText = ta.getString(R.styleable.TopBar_rightText);
        mTitle = ta.getString(R.styleable.TopBar_title);
        mTitleTextColor = ta.getColor(R.styleable.TopBar_titltTextColor, 0);
        mTitleTextsize = ta.getDimension(R.styleable.TopBar_titleTextSize, 10);
        //获取属性值后，要回收TypedArray
        ta.recycle();
        initView(context);

    }

    private void initView(Context context) {
        mLeftButton = new Button(context);
        mRightButton = new Button(context);
        mTitleTextView = new TextView(context);
        mLeftButton.setText(mLeftText);
        mLeftButton.setTextColor(mLeftTextColor);
        mLeftButton.setBackground(mLeftBackGround);
        mRightButton.setText(mRightText);
        mRightButton.setTextColor(mRightTextColor);
        mRightButton.setBackground(mRightBackGround);
        mTitleTextView.setText(mTitle);
        mTitleTextView.setTextSize(mTitleTextsize);
        mTitleTextView.setTextColor(mTitleTextColor);
        mTitleTextView.setGravity(Gravity.CENTER);
        LayoutParams leftlayoutparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        leftlayoutparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
        addView(mLeftButton, leftlayoutparams);
        LayoutParams rightlayoutparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rightlayoutparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
        addView(mRightButton, rightlayoutparams);
        LayoutParams titlelayoutparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        titlelayoutparams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
        addView(mTitleTextView, titlelayoutparams);
        mLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mtopBarClickListenner.leftClick();
            }
        });
        mRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mtopBarClickListenner.rightClick();
            }
        });
    }

    public interface topBarClickListenner {
        void leftClick();

        void rightClick();
    }

    public void setOntopBarClickListenner(topBarClickListenner topbarlistenner) {
        mtopBarClickListenner = topbarlistenner;
    }
}

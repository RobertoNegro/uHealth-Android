package com.negroroberto.uhealth.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.negroroberto.uhealth.R;

import java.util.Date;

public class CircleFrameLayout extends FrameLayout {
    private Path clippingPath;

    private int mShadowRadius = 12;
    private int mShadowX = 2;
    private int mShadowY = mShadowRadius / 4;

    private int mShadowRadiusSelected = 14;
    private int mShadowXSelected = 4;
    private int mShadowYSelected = mShadowRadiusSelected / 2;

    private boolean mIsDragging;
    private long mStartMotionDown;

    private Bitmap mShadow;

    private boolean mFirstInit = true;

    private Paint mPaintBg;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mFirstInit) {
            mFirstInit = false;
            View parent = (View) getParent();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
            params.leftMargin = (int) parent.getMeasuredWidth() - getMeasuredWidth() - 10;
            params.topMargin = (int) parent.getMeasuredHeight() - getMeasuredHeight() - 10;
            setLayoutParams(params);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            int shadowX = Math.max(mShadowX, mShadowXSelected);
            int shadowY = Math.max(mShadowY, mShadowYSelected);
            int shadowRadius = Math.max(mShadowRadius, mShadowRadiusSelected);

            int radius = Math.min(w - shadowX - shadowRadius * 2, h - shadowY - shadowRadius * 2) / 2;
            clippingPath = new Path();
            clippingPath.addCircle(w / 2, h / 2, radius, Path.Direction.CW);
            drawShadow(mIsDragging);
        }
    }

    private void drawShadow(boolean selected) {
        mShadow = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tmp = new Canvas(mShadow);
        tmp.clipPath(clippingPath, Region.Op.DIFFERENCE);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (selected)
            paint.setShadowLayer(mShadowRadiusSelected, mShadowXSelected, mShadowYSelected, Color.argb(180, 0, 0, 0));
        else
            paint.setShadowLayer(mShadowRadius, mShadowX, mShadowY, Color.argb(180, 0, 0, 0));

        tmp.drawPath(clippingPath, paint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int count = canvas.save();
        canvas.clipPath(clippingPath);
        canvas.drawPaint(mPaintBg);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(count);

        canvas.drawBitmap(mShadow, 0, 0, null);
    }


    public CircleFrameLayout(@NonNull Context context) {
        super(context);
        mPaintBg = new Paint();
        mPaintBg.setColor(ResourcesCompat.getColor(getResources(), R.color.darkPrimary, null));
        mPaintBg.setStyle(Paint.Style.FILL);
    }

    public CircleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaintBg = new Paint();
        mPaintBg.setColor(ResourcesCompat.getColor(getResources(), R.color.darkPrimary, null));
        mPaintBg.setStyle(Paint.Style.FILL);
    }

    public CircleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaintBg = new Paint();
        mPaintBg.setColor(ResourcesCompat.getColor(getResources(), R.color.darkPrimary, null));
        mPaintBg.setStyle(Paint.Style.FILL);
    }

    public CircleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaintBg = new Paint();
        mPaintBg.setColor(ResourcesCompat.getColor(getResources(), R.color.darkPrimary, null));
        mPaintBg.setStyle(Paint.Style.FILL);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //Debug.Log(this, "onInterceptTouchEvent " + event.toString());
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mStartMotionDown = new Date().getTime();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!mIsDragging && new Date().getTime() - mStartMotionDown > 300) {
                    mIsDragging = true;
                    drawShadow(mIsDragging);
                    invalidate();
                }

                if (mIsDragging) {
                    int[] location = new int[2];
                    ((View) getParent()).getLocationOnScreen(location);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                    params.leftMargin = (int) event.getRawX() - location[0] - getWidth() / 2;
                    params.topMargin = (int) event.getRawY() - location[1] - getHeight() / 2;

                    if (params.leftMargin < 0)
                        params.leftMargin = 0;

                    if (params.topMargin < 0)
                        params.topMargin = 0;

                    if (params.leftMargin > ((View) getParent()).getWidth() - getWidth())
                        params.leftMargin = ((View) getParent()).getWidth() - getWidth();

                    if (params.topMargin > ((View) getParent()).getHeight() - getHeight())
                        params.topMargin = ((View) getParent()).getHeight() - getHeight();

                    setLayoutParams(params);
                }
                return true;
            }
            case MotionEvent.ACTION_UP: {
                if (mIsDragging) {
                    mIsDragging = false;
                    drawShadow(mIsDragging);
                    invalidate();
                } else {

                    if (new Date().getTime() - mStartMotionDown < 150)
                        performClick();
                }
                return true;
            }
        }
        return false;
    }


}

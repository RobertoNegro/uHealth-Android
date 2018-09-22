package com.negroroberto.uhealth.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.negroroberto.uhealth.R;

public class CircularGraphView extends View {
    private double mActualValue;
    private double mTotalValue;
    private double mPercValue;
    private String mPercString;

    private int mTextPosX;
    private int mTextPosY;

    private int mStrokeWidth;

    private Paint mActualPaint;
    private Paint mTotalPaint;
    private Paint mOverPaint;

    private Paint mTextPaint;

    private int mSize;

    public CircularGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularGraphView, 0, 0);
        mActualValue = a.getInt(R.styleable.CircularGraphView_valueActual, 0);
        mTotalValue = a.getInt(R.styleable.CircularGraphView_valueTotal, 0);
        updatePerc();

        mStrokeWidth = a.getInt(R.styleable.CircularGraphView_strokeWidth, 10);

        int actualColor = a.getColor(R.styleable.CircularGraphView_actualColor, Color.rgb(15, 15, 225));
        int totalColor = a.getColor(R.styleable.CircularGraphView_totalColor, Color.rgb(208, 208, 208));
        int overColor = a.getColor(R.styleable.CircularGraphView_overColor, Color.rgb(239, 54, 54));
        int textColor = a.getColor(R.styleable.CircularGraphView_textColor, Color.rgb(166, 166, 166));

        a.recycle();

        mActualPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mActualPaint.setColor(actualColor);
        mActualPaint.setStrokeWidth(mStrokeWidth);
        mActualPaint.setStyle(Paint.Style.STROKE);
        mActualPaint.setStrokeCap(Paint.Cap.ROUND);

        mTotalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTotalPaint.setColor(totalColor);
        mTotalPaint.setStrokeWidth(mStrokeWidth);
        mTotalPaint.setStyle(Paint.Style.STROKE);
        mTotalPaint.setStrokeCap(Paint.Cap.ROUND);

        mOverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOverPaint.setColor(overColor);
        mOverPaint.setStrokeWidth(mStrokeWidth);
        mOverPaint.setStyle(Paint.Style.STROKE);
        mOverPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        onSizeChanged(this.getWidth(), this.getHeight(), 0, 0);
    }

    public double getActualValue() {
        return mActualValue;
    }

    public void setActualValue(double actualValue) {
        this.mActualValue = actualValue;
        updatePerc();
    }

    public double getTotalValue() {
        return mTotalValue;
    }

    public void setTotalValue(double totalValue) {
        this.mTotalValue = totalValue;
        updatePerc();
    }

    private void updatePerc() {
        if (mTotalValue != 0)
            mPercValue = mActualValue / mTotalValue;
        else
            mPercValue = 1;

        mPercString = (int) (mPercValue * 100f) + "%";
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int paddingLeft = Math.max(this.getPaddingLeft(), mStrokeWidth / 2 + 1);
        int paddingTop = Math.max(this.getPaddingTop(), mStrokeWidth / 2 + 1);
        int paddingRight = Math.max(this.getPaddingRight(), mStrokeWidth / 2 + 1);
        int paddingBottom = Math.max(this.getPaddingBottom(), mStrokeWidth / 2 + 1);

        mSize = Math.min(w - paddingLeft - paddingRight, h - paddingTop - paddingBottom);

        mTextPosX = paddingLeft + (mSize / 2);
        mTextPosY = paddingTop + (int) ((mSize / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));

        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width > height) {
            size = height;
        } else {
            size = width;
        }
        setMeasuredDimension(size, size);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = Math.max(this.getPaddingLeft(), mStrokeWidth / 2 + 1);
        int paddingTop = Math.max(this.getPaddingTop(), mStrokeWidth / 2 + 1);

        if (mPercValue < 1) {
            double degreesAct = 360f * mPercValue;
            canvas.drawArc(paddingLeft, paddingTop, mSize + paddingLeft, mSize + paddingTop, (int) (270d + degreesAct), (int) (360d - degreesAct), false, mTotalPaint);
            canvas.drawArc(paddingLeft, paddingTop, mSize + paddingLeft, mSize + paddingTop, 270, (int) degreesAct, false, mActualPaint);
        } else if (mPercValue < 2) {
            double degreesOver = 360f * (mPercValue - 1f);
            canvas.drawArc(paddingLeft, paddingTop, mSize + paddingLeft, mSize + paddingTop, (int) (270d + degreesOver), (int) (360d - degreesOver), false, mActualPaint);
            canvas.drawArc(paddingLeft, paddingTop, mSize + paddingLeft, mSize + paddingTop, 270, (int) degreesOver, false, mOverPaint);
        } else {
            canvas.drawArc(paddingLeft, paddingTop, mSize + paddingLeft, mSize + paddingTop, 270, 360, false, mOverPaint);
        }

        canvas.drawText(mPercString, mTextPosX, mTextPosY, mTextPaint);
    }
}

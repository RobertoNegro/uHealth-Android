package com.negroroberto.uhealth.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.TextureView;

import com.negroroberto.uhealth.R;

import java.util.ArrayList;

public class WaterGlassView extends TextureView {
    private static final int FPS = 60;
    private static final int N_BUBBLES_PAINT = 5;
    private static final float WH_RATIO = 1.2f;
    private static final float ANGULAR_RATIO = 0.23f;

    private DrawingThread mDrawingThread;

    private Paint mWaterPaint;
    private Paint mWaterTopperPaint;
    private Paint mGlassPaint;
    private Paint mGlassTopperPaint;
    private Paint mTextPaint;
    private ArrayList<Paint> mBubblePaints;

    public WaterGlassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOpaque(false);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaterGlassView, 0, 0);
        int actualValue = a.getInt(R.styleable.WaterGlassView_valueActual, 0);
        int totalValue = a.getInt(R.styleable.WaterGlassView_valueTotal, 0);

        int waterColor = a.getColor(R.styleable.WaterGlassView_waterColor, Color.rgb(187, 217, 248));
        int waterStrokeColor = a.getColor(R.styleable.WaterGlassView_waterStrokeColor, Color.rgb(210, 232, 255));
        int glassColor = a.getColor(R.styleable.WaterGlassView_glassColor, Color.rgb(240, 240, 240));
        int glassStrokeColor = a.getColor(R.styleable.WaterGlassView_glassStrokeColor, Color.rgb(255, 255, 255));
        int bubbleColor = a.getColor(R.styleable.WaterGlassView_bubbleColor, Color.rgb(255, 255, 255));
        int textColor = a.getColor(R.styleable.WaterGlassView_textColor, Color.rgb(255, 255, 255));

        a.recycle();

        mWaterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWaterPaint.setColor(waterColor);
        mWaterPaint.setStyle(Paint.Style.FILL);

        mWaterTopperPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWaterTopperPaint.setColor(waterStrokeColor);
        mWaterTopperPaint.setStyle(Paint.Style.STROKE);
        mWaterTopperPaint.setAlpha(180);

        mGlassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGlassPaint.setColor(glassColor);
        mGlassPaint.setStyle(Paint.Style.FILL);

        mGlassTopperPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGlassTopperPaint.setColor(glassStrokeColor);
        mGlassTopperPaint.setStyle(Paint.Style.STROKE);
        mGlassTopperPaint.setAlpha(180);

        mBubblePaints = new ArrayList<>(N_BUBBLES_PAINT);
        for (int i = 0; i < N_BUBBLES_PAINT; i++) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(bubbleColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(25 + i * (100 / (N_BUBBLES_PAINT - 1)));
            mBubblePaints.add(paint);
        }

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mDrawingThread = new DrawingThread();
        mDrawingThread.setValues(actualValue, totalValue);
        setSurfaceTextureListener(new SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mDrawingThread.start();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                mDrawingThread.interrupt();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    private double lerp(double point1, double point2, double alpha) {
        return point1 + alpha * (point2 - point1);
    }

    public void setActualValue(double actualValue) {
        mDrawingThread.setActualValue(actualValue);
    }

    public void setTotalValue(double totalValue) {
        mDrawingThread.setTotalValue(totalValue);
    }

    public double getActualValue() {
        return mDrawingThread.getActualValue();
    }

    public double getTotalValue() {
        return mDrawingThread.getTotalValue();
    }

    public void pauseAnimation() {
        mDrawingThread.pauseAnimation();
    }

    public void resumeAnimation() {
        mDrawingThread.resumeAnimation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawingThread.updateSize(w, h, getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthResult;
        int heightResult;

        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            widthResult = widthSize;
            heightResult = heightSize;
        } else if (widthMode == MeasureSpec.UNSPECIFIED || (widthMode == MeasureSpec.AT_MOST && (widthSize * WH_RATIO) >= heightSize)) {
            widthResult = (int) ((heightSize - getPaddingTop() - getPaddingBottom()) * (1f / WH_RATIO));
            heightResult = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED || (heightMode == MeasureSpec.AT_MOST && (widthSize * WH_RATIO) < heightSize)) {
            widthResult = widthSize;
            heightResult = (int) ((widthSize - getPaddingLeft() - getPaddingRight()) * WH_RATIO);
        } else {
            widthResult = widthSize;
            heightResult = heightSize;
        }

        setMeasuredDimension(widthResult, heightResult);
    }

    private class DrawingThread extends Thread {
        private boolean mIsPaused = false;

        private Rect mDrawingArea = new Rect();
        private Point mDrawingAreaCenter = new Point();

        private int mSizeOnTop = 0;
        private int mSizeOnBottom = 0;

        private Path mGlassPath = new Path();
        private RectF mGlassTopEllipse = new RectF();
        private RectF mGlassBottomEllipse = new RectF();

        private Path mWaterPath = new Path();
        private RectF mWaterTopEllipse = new RectF();
        private RectF mWaterBottomEllipse = new RectF();

        private int mBubblesNumber = 0;
        private int mMaxBubbleSize = 0;
        private ArrayList<Bubble> mBubbles = new ArrayList<>();

        private double mActualValue = 0;
        private double mTotalValue = 0;
        private double mPercValue = 0;
        private String mPercString = "0%";
        private int mStrokeWidth = 0;

        private boolean mIsResized = false;
        private boolean mIsDrawing = false;

        private Handler mHandler = new Handler();

        public void setValues(double actualValue, double totalValue) {
            if(totalValue < 0)
                totalValue = 0;
            if(actualValue > totalValue)
                actualValue = totalValue;
            if(actualValue < 0)
                actualValue = 0;

            mActualValue = actualValue;
            mTotalValue = totalValue;
            updatePerc();
        }

        public void setActualValue(double actualValue) {
            if(actualValue > mTotalValue)
                actualValue = mTotalValue;
            if(actualValue < 0)
                actualValue = 0;

            mActualValue = actualValue;
            updatePerc();
        }

        public void setTotalValue(double totalValue) {
            if(totalValue < 0)
                totalValue = 0;
            if(mActualValue > totalValue)
                mActualValue = totalValue;

            mTotalValue = totalValue;
            updatePerc();
        }

        public double getActualValue() {
            return mActualValue;
        }

        public double getTotalValue() {
            return mTotalValue;
        }

        public void pauseAnimation() {
            mIsPaused = true;
        }

        public void resumeAnimation() {
            mIsPaused = false;
        }

        private void updatePerc() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    while (isDrawing(null)) ;
                    if (mTotalValue != 0)
                        mPercValue = mActualValue / mTotalValue;
                    else
                        mPercValue = 1;

                    mBubblesNumber = (int) (mPercValue * 20);

                    mPercString = (int) (mPercValue * 100f) + "%";

                    int glassThickness = (int) ((mDrawingArea.right - mDrawingArea.left) * 0.02d);
                    double cappedPerc = mPercValue * 0.85;

                    int percentedSizeOnTop = (int) (lerp(mSizeOnBottom - glassThickness * 2, mSizeOnTop - glassThickness * 2, cappedPerc));
                    int percentedVerticalSizeOnTop = (int) (lerp(mSizeOnBottom * ANGULAR_RATIO, mSizeOnTop * ANGULAR_RATIO, cappedPerc));
                    int percentedSizeOnBottom = mSizeOnBottom - glassThickness * 2;

                    mMaxBubbleSize = percentedSizeOnBottom / 12;

                    float topTop = (float) lerp(mDrawingArea.bottom - glassThickness - mSizeOnBottom * ANGULAR_RATIO, mDrawingArea.top + glassThickness, cappedPerc);
                    mWaterTopEllipse.set(mDrawingAreaCenter.x - percentedSizeOnTop / 2, topTop, mDrawingAreaCenter.x + percentedSizeOnTop / 2, topTop + percentedVerticalSizeOnTop);
                    mWaterBottomEllipse.set(mDrawingAreaCenter.x - percentedSizeOnBottom / 2, mDrawingArea.bottom - glassThickness - mSizeOnBottom * ANGULAR_RATIO, mDrawingAreaCenter.x + percentedSizeOnBottom / 2, mDrawingArea.bottom - glassThickness);

                    mWaterPath.reset();
                    mWaterPath.moveTo(mDrawingAreaCenter.x - percentedSizeOnTop / 2, topTop + percentedVerticalSizeOnTop / 2);
                    mWaterPath.lineTo(mDrawingAreaCenter.x + percentedSizeOnTop / 2, topTop + percentedVerticalSizeOnTop / 2);
                    mWaterPath.lineTo(mDrawingAreaCenter.x + percentedSizeOnBottom / 2, mDrawingArea.bottom - glassThickness - mSizeOnBottom * ANGULAR_RATIO / 2);
                    mWaterPath.lineTo(mDrawingAreaCenter.x - percentedSizeOnBottom / 2, mDrawingArea.bottom - glassThickness - mSizeOnBottom * ANGULAR_RATIO / 2);

                    mDrawingThread.isResized(true);
                }
            });
        }

        private void updateSize(final int w, final int h, final int pLeft, final int pTop, final int pRight, final int pBottom) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    while (isDrawing(null)) ;

                    int paddingLeft = Math.max(pLeft, mStrokeWidth / 2 + 1);
                    int paddingTop = Math.max(pTop, mStrokeWidth / 2 + 1);
                    int paddingRight = Math.max(pRight, mStrokeWidth / 2 + 1);
                    int paddingBottom = Math.max(pBottom, mStrokeWidth / 2 + 1);

                    int paddedW = w - paddingLeft - paddingRight;
                    int paddedH = h - paddingTop - paddingBottom;

                    int ratedW;
                    int ratedH;

                    if (paddedW * WH_RATIO < paddedH) {
                        ratedW = paddedW;
                        ratedH = (int) (paddedW * WH_RATIO);
                    } else {
                        ratedW = (int) (paddedH * (1f / WH_RATIO));
                        ratedH = paddedH;
                    }

                    int halfRatedW = ratedW / 2;
                    int halfRatedH = ratedH / 2;

                    int centerX = (paddingLeft + (w - paddingRight)) / 2;
                    int centerY = (paddingTop + (h - paddingBottom)) / 2;

                    mDrawingArea.set(centerX - halfRatedW, centerY - halfRatedH, centerX + halfRatedW, centerY + halfRatedH);
                    mDrawingAreaCenter.set((mDrawingArea.right + mDrawingArea.left) / 2, (mDrawingArea.bottom + mDrawingArea.top) / 2);

                    mSizeOnTop = (mDrawingArea.right - mDrawingArea.left);
                    mSizeOnBottom = (int) ((mDrawingArea.right - mDrawingArea.left) * 0.73f);
                    mStrokeWidth = (int) ((mDrawingArea.right - mDrawingArea.left) * 0.01d);

                    mWaterTopperPaint.setStrokeWidth(mStrokeWidth);
                    mGlassTopperPaint.setStrokeWidth(mStrokeWidth);

                    mGlassTopEllipse.set(mDrawingAreaCenter.x - mSizeOnTop / 2, mDrawingArea.top, mDrawingAreaCenter.x + mSizeOnTop / 2, mDrawingArea.top + mSizeOnTop * ANGULAR_RATIO);
                    mGlassBottomEllipse.set(mDrawingAreaCenter.x - mSizeOnBottom / 2, mDrawingArea.bottom - mSizeOnBottom * ANGULAR_RATIO, mDrawingAreaCenter.x + mSizeOnBottom / 2, mDrawingArea.bottom);

                    mGlassPath.reset();

                    mGlassPath.moveTo(mDrawingAreaCenter.x - mSizeOnTop / 2, mDrawingArea.top + mSizeOnTop * ANGULAR_RATIO / 2);
                    mGlassPath.lineTo(mDrawingAreaCenter.x + mSizeOnTop / 2, mDrawingArea.top + mSizeOnTop * ANGULAR_RATIO / 2);
                    mGlassPath.lineTo(mDrawingAreaCenter.x + mSizeOnBottom / 2, mDrawingArea.bottom - mSizeOnBottom * ANGULAR_RATIO / 2);
                    mGlassPath.lineTo(mDrawingAreaCenter.x - mSizeOnBottom / 2, mDrawingArea.bottom - mSizeOnBottom * ANGULAR_RATIO / 2);

                    mGlassPath.close();

                    updatePerc();
                }
            });
        }

        synchronized boolean isResized(Boolean newValue) {
            if (newValue != null) {
                mIsResized = newValue;
                return mIsResized;
            } else {
                boolean val = mIsResized;
                mIsResized = false;
                return val;
            }
        }

        synchronized boolean isDrawing(Boolean newValue) {
            if (newValue != null)
                mIsDrawing = newValue;

            return mIsDrawing;
        }

        private void safeSleep(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }

        public void run() {
            long startTime;
            long endTime;

            while (!isInterrupted()) {
                if(mIsPaused) {
                    mHandler.removeCallbacksAndMessages(null);
                    while (mIsPaused) ;
                }

                startTime = System.currentTimeMillis();

                final Canvas canvas = lockCanvas();

                if (canvas == null) {
                    safeSleep(1);
                } else {
                    isDrawing(true);
                    update();
                    draw(canvas);
                    isDrawing(false);
                    unlockCanvasAndPost(canvas);

                    endTime = System.currentTimeMillis();

                    long sleepTime = (long) (1000f / FPS - (endTime - startTime));
                    if (sleepTime > 0)
                        safeSleep(sleepTime);
                }
            }
            mHandler.removeCallbacksAndMessages(null);
        }

        private void update() {
            if (isResized(null)) {
                int newPopPositionOnResizing = (int) ((mWaterTopEllipse.bottom + mWaterBottomEllipse.top) / 2);
                for (int i = mBubbles.size() - 1; i >= 0; i--) {
                    Bubble b = mBubbles.get(i);
                    if (b.getPosition().y < newPopPositionOnResizing && !b.isDying())
                        b.die();
                }
            }

            while (mBubbles.size() < mBubblesNumber)
                createBubble();

            for (int i = mBubbles.size() - 1; i >= 0; i--) {
                Bubble b = mBubbles.get(i);
                b.update();

                b.setPositionY(b.getPosition().y - b.mSpeed);

                if (b.getPosition().y < b.getPopPosition() && !b.isDying())
                    b.die();

                if (b.isDead())
                    mBubbles.remove(b);
            }
        }

        private void draw(Canvas canvas) {
            if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                canvas.drawPath(mGlassPath, mGlassPaint);
                canvas.drawOval(mGlassBottomEllipse, mGlassPaint);
                canvas.drawOval(mGlassTopEllipse, mGlassPaint);

                canvas.drawPath(mWaterPath, mWaterPaint);
                canvas.drawOval(mWaterBottomEllipse, mWaterPaint);
                canvas.drawOval(mWaterTopEllipse, mWaterPaint);

                canvas.save();
                canvas.clipPath(mWaterPath);
                for (Bubble b : mBubbles) {
                    int halfSize = (int) (b.getSize() * b.getSizeFade() / 2);
                    float x = b.getPosition().x;
                    float y = b.getPosition().y;
                    int paintIndex = b.getPaintIndex();

                    canvas.drawOval(x - halfSize, y - halfSize, x + halfSize, y + halfSize, mBubblePaints.get(paintIndex));
                }
                canvas.restore();


                canvas.drawOval(mWaterTopEllipse, mWaterTopperPaint);
                canvas.drawOval(mGlassTopEllipse, mGlassTopperPaint);
            }
        }

        private void createBubble() {
            float x = (float) ((mWaterBottomEllipse.left + mMaxBubbleSize / 2) + Math.random() * ((mWaterBottomEllipse.right - mMaxBubbleSize / 2) - (mWaterBottomEllipse.left + mMaxBubbleSize / 2)));
            float y = (float) (mWaterBottomEllipse.top + Math.random() * ((mWaterBottomEllipse.bottom) - (mWaterBottomEllipse.top)));
            int size = (int) (((1d / 2d) * mMaxBubbleSize) + Math.random() * ((1d / 2d) * mMaxBubbleSize));
            float speed = (float) (3 + Math.random() * 5);
            int opacity = (int) (Math.random() * mBubblePaints.size());
            int popPosition = (int) (((mWaterTopEllipse.top + mWaterTopEllipse.bottom) / 2 + size) + Math.random() * ((mWaterTopEllipse.bottom * 2 + mWaterBottomEllipse.top) / 3 - ((mWaterTopEllipse.top + mWaterTopEllipse.bottom) / 2 + size)));
            mBubbles.add(new Bubble(new PointF(x, y), size, 0, speed, opacity, popPosition));
        }
    }

    private class Bubble {
        private PointF mPosition;
        private int mSize;
        private int mPaintIndex;
        private float mSpeed;
        private float mSizeFade;
        private float mPopPosition;

        private boolean mIsFadingIn;

        private boolean mIsDying;
        private boolean mIsDead;

        Bubble(PointF position, int size, float sizeFade, float speed, int paintIndex, float popPosition) {
            this.mPosition = position;
            this.mSize = size;
            this.mSizeFade = sizeFade;
            this.mSpeed = speed;
            this.mPaintIndex = paintIndex;
            this.mPopPosition = popPosition;

            this.mIsFadingIn = true;
            this.mIsDying = false;
            this.mIsDead = false;
        }

        public void update() {
            if (mIsDying) {
                if (mSizeFade > 0)
                    mSizeFade -= 0.05f;
                else {
                    mSizeFade = 0;
                    mIsDying = false;
                    mIsDead = true;
                }
            } else if (mIsFadingIn) {
                if (mSizeFade < 1)
                    mSizeFade += 0.05f;
                else {
                    mSizeFade = 1;
                    mIsFadingIn = false;
                }
            }
        }

        public void die() {
            mIsDying = true;
        }

        public boolean isDying() {
            return mIsDying;
        }

        public boolean isDead() {
            return mIsDead;
        }

        public float getPopPosition() {
            return mPopPosition;
        }

        public void setPopPosition(float popPosition) {
            this.mPopPosition = popPosition;
        }

        public int getPaintIndex() {
            return mPaintIndex;
        }

        public void setPaintIndex(int paintIndex) {
            this.mPaintIndex = paintIndex;
        }

        public PointF getPosition() {
            return mPosition;
        }

        public void setPosition(PointF position) {
            this.mPosition = position;
        }

        public void setPosition(float x, float y) {
            this.mPosition.x = x;
            this.mPosition.y = y;
        }

        public void setPositionX(float x) {
            this.mPosition.x = x;
        }

        public void setPositionY(float y) {
            this.mPosition.y = y;
        }

        public int getSize() {
            return mSize;
        }

        public void setSize(int size) {
            this.mSize = size;
        }

        public float getSizeFade() {
            return mSizeFade;
        }

        public void setSizeFade(float sizeFade) {
            this.mSizeFade = sizeFade;
        }

        public float getSpeed() {
            return mSpeed;
        }

        public void setSpeed(float speed) {
            this.mSpeed = speed;
        }
    }
}

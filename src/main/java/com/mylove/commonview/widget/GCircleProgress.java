package com.mylove.commonview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.mylove.commonview.R;


/**
 * TODO: document your custom view class.
 */
public class GCircleProgress extends View {

    private int mBackgroundColor = Color.argb(150,0,0,0);
    private int mProgressBackgroundColor = Color.argb(60,255,255,255);
    private int mProgressColor = Color.argb(40,0,0,0);
    private int mTextColor = Color.WHITE;
    private float mTextSize = 0f;
    private int mMaxProgress = 100;
    private int mCurrentProgress = 0;
    private float mRoundSize = 0f;

    private String mSuffix = "%";
    private String mPrefix = "";

    private RectF mBackgroundRectF = new RectF(0, 0, 0, 0);
    private RectF mProgressBackgroundF = new RectF(0, 0, 0, 0);

    private Paint mBackgroundPaint;
    private Paint mProgressBackgroundPaint;
    private Paint mProgressPaint;
    private TextPaint mTextPaint;

    private OnProgressBarListener mOnProgressBarListener;
	private String mCurrentDrawText;
	private float mDrawTextWidth,mDrawTextHeight;

    public interface OnProgressBarListener {
        void onProgressChange(int current, int max);
    }
    public void setOnProgressBarListener(OnProgressBarListener mOnProgressBarListener) {
        this.mOnProgressBarListener = mOnProgressBarListener;
    }

    public GCircleProgress(Context context) {
        super(context);
        init(null, 0);
    }

    public GCircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GCircleProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mTextSize = sp2px(16);
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.GCircleProgress, defStyle, 0);

        mBackgroundColor = a.getColor(R.styleable.GCircleProgress_circle_background,mBackgroundColor);
        mProgressBackgroundColor = a.getColor(R.styleable.GCircleProgress_circle_progressBackground,mProgressBackgroundColor);
        mProgressColor = a.getColor(R.styleable.GCircleProgress_circle_progressColor,mProgressColor);
        mTextColor = a.getColor(R.styleable.GCircleProgress_circle_textColor,mTextColor);
        mTextSize = a.getDimension(R.styleable.GCircleProgress_circle_textSize, mTextSize);
        mRoundSize = a.getFloat(R.styleable.GCircleProgress_circle_round_size, mRoundSize);

        setProgress(a.getInt(R.styleable.GCircleProgress_circle_progress_current, 0));
        setMax(a.getInt(R.styleable.GCircleProgress_circle_progress_max, 100));

        a.recycle();

        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColor);

        mProgressBackgroundPaint = new Paint();
        mProgressBackgroundPaint.setAntiAlias(true);
        mProgressBackgroundPaint.setColor(mProgressBackgroundColor);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(mProgressColor);

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int)sp2px(100);
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return (int)sp2px(100);
    }

    public int getDefaultSize(int measureSpec,boolean isWidth) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            if (isWidth){
                result = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
            }else{
                result = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();
            }
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(widthMeasureSpec,true);
        int height = getDefaultSize(heightMeasureSpec,false);
        setMeasuredDimension(width,height);

        mBackgroundRectF = new RectF(0,0,width,height);
        float size = Math.min(width,height);
        size = size / 3;
        float x = mBackgroundRectF.centerX();
        float y = mBackgroundRectF.centerY();

        mProgressBackgroundF.left = x - size;
        mProgressBackgroundF.top = y - size;
        mProgressBackgroundF.right = x + size;
        mProgressBackgroundF.bottom = y + size;
    }

    public void setSuffix(String suffix) {
        if (suffix == null) {
            mSuffix = "";
        } else {
            mSuffix = suffix;
        }
    }

    public String getSuffix() {
        return mSuffix;
    }

    public void setPrefix(String prefix) {
        if (prefix == null)
            mPrefix = "";
        else {
            mPrefix = prefix;
        }
    }

    public String getPrefix() {
        return mPrefix;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setProgressTextColor(int textColor) {
        this.mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public void setProgress(int progress) {
        if (progress <= getMax() && progress >= 0) {
            this.mCurrentProgress = progress;
            invalidate();
        }
    }

    public void setMax(int maxProgress) {
        if (maxProgress > 0) {
            this.mMaxProgress = maxProgress;
            invalidate();
        }
    }

    public void incrementProgressBy(int by) {
        if (by > 0) {
            setProgress(getProgress() + by);
        }
        if(getProgress() == getMax()){
            this.setVisibility(View.GONE);
        }
        if(mOnProgressBarListener != null){
            mOnProgressBarListener.onProgressChange(getProgress(), getMax());
        }
    }

    public int getMax(){
        return mMaxProgress;
    }

    public int getProgress() {
        return mCurrentProgress;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        calculateText();
        
        canvas.drawRoundRect(mBackgroundRectF,mRoundSize,mRoundSize,mBackgroundPaint);
        canvas.drawCircle(mProgressBackgroundF.centerX(),mProgressBackgroundF.centerY(),mProgressBackgroundF.width()/2,mProgressBackgroundPaint);

        float sweepAngle = 360 * mCurrentProgress / mMaxProgress;
        canvas.drawArc(mProgressBackgroundF,-90,sweepAngle,true,mProgressPaint);
        
        canvas.drawText(mCurrentDrawText, mBackgroundRectF.width()/2 - mDrawTextWidth/2 , mBackgroundRectF.height()/2 + mDrawTextHeight/2, mTextPaint);
    }
    
    public void calculateText(){
    	mCurrentDrawText = String.format("%d", getProgress() * 100 / getMax());
        mCurrentDrawText = mPrefix + mCurrentDrawText + mSuffix;
//        mDrawTextWidth = mTextPaint.measureText(mCurrentDrawText);
        
        Rect textRect = new Rect();
        mTextPaint.getTextBounds(mCurrentDrawText, 0, mCurrentDrawText.length(), textRect);
        mDrawTextWidth = textRect.width();
        mDrawTextHeight = textRect.height();
        System.out.println("mDrawTextWidth = "+mDrawTextWidth);
        System.out.println("mDrawTextHeight = "+mDrawTextHeight);
        
    }
    
    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}

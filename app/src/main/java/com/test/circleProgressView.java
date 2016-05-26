package com.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/5/25.
 */
public class circleProgressView extends View {

    /**
     * 进度的风格，实心或者空心
     */
    private  int style;
    /**
     * 是否显示中间的进度
     */
    private  boolean textIsDisplayable;
    private  float roundWidth;
    private  int currentProgress;
    private  int max;
    private  float textSize;
    private  int textColor;
    private  int circleProgressColor;
    private  int circleColor;
    private String text;


    private Paint paint;
    public static final int STROKE = 0;
    public static final int FILL = 1;

    public circleProgressView(Context context) {
        super(context);
    }

    public circleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public circleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();

        //获取自定义属性和默认值
        TypedArray typeArr = context.obtainStyledAttributes(attrs, R.styleable.circleProgressView);
        circleColor = typeArr.getColor(R.styleable.circleProgressView_circleColor, getResources().getColor(R.color.color_ababab));
        circleProgressColor = typeArr.getColor(R.styleable.circleProgressView_circleProgressColor, getResources().getColor(R.color.f585858));
        textColor = typeArr.getColor(R.styleable.circleProgressView_textColor, getResources().getColor(R.color.black_p50));
        textSize = typeArr.getDimension(R.styleable.circleProgressView_textSize, getResources().getDimension(R.dimen.text_font_size));
        text = typeArr.getString(R.styleable.circleProgressView_text);
        max = typeArr.getInteger(R.styleable.circleProgressView_max, 100);
        roundWidth = typeArr.getDimension(R.styleable.circleProgressView_roundWidth, 5);
        currentProgress = typeArr.getInteger(R.styleable.circleProgressView_currentProgress, 0);
        textIsDisplayable  = typeArr.getBoolean(R.styleable.circleProgressView_textIsDisplayable , true);
        style = typeArr.getInt(R.styleable.circleProgressView_style, 0);

        typeArr.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画最外层的大圆环
         */
        int center = getWidth()/2;//view的宽度,圆的中心坐标
        int radius = (int)((center-roundWidth)/2);//获取圆环的半径
        paint.setColor(circleColor);//设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(roundWidth);//外层圆环的宽度
        paint.setAntiAlias(true);//消除锯齿

        canvas.drawCircle(center,center,radius,paint);//画出圆环


        /**
         * 画进度百分比的文字
         */
        paint.setStrokeWidth(0);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTypeface(Typeface.DEFAULT);//设置字体
        int percent = (int) (((float)currentProgress/(float)max)*100);//中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = paint.measureText(percent+"%");//测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        if(textIsDisplayable && percent !=0 && style == STROKE){
            canvas.drawText(percent+"%",center - textWidth / 2, center + textSize/2, paint);
        }

        /**
         * 圆圈中的文字
         */
        paint.setStrokeWidth(0);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTypeface(Typeface.DEFAULT);//设置字体
        float textWidth2 = paint.measureText(text);//测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        if(!textIsDisplayable && style == STROKE) {
            canvas.drawText(text, center - textWidth2 / 2, center + textSize / 2, paint);
        }

        /**
         * 画圆弧 ，画圆环的进度
         */
        paint.setStyle(Paint.Style.STROKE);//设置进度是实心还是空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setColor(circleProgressColor);//设置进度的颜色
        RectF oval = new RectF(center-radius,center-radius,center+radius,center+radius);//用于定义的圆弧的形状和大小的界限

        switch (style) {
            case STROKE:
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval,0,360 * percent/max,false,paint);
                break;
            case FILL:
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if(currentProgress !=0)
                    canvas.drawArc(oval,0,360 * percent/max,true,paint);
                break;
        }
    }


    /**
     * 获取进度.需要同步
     * @return
     */
    public synchronized  int getCurrentProgress() {
        return currentProgress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     * @param currentProgress
     */
    public void setCurrentProgress(int currentProgress) {
        if(currentProgress < 0){
            throw new IllegalArgumentException("progress not less than 0");
        }
        if(currentProgress > max){
            currentProgress = max;
        }
        if(currentProgress <= max){
            this.currentProgress = currentProgress;
            postInvalidate();
        }
    }

    public synchronized  int getMax() {
        return max;
    }
    /**
     * 设置进度的最大值
     * @param max
     */
    public synchronized void setMax(int max) {
        if(max < 0){
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getCircleProgressColor() {
        return circleProgressColor;
    }

    public void setCircleProgressColor(int circleProgressColor) {
        this.circleProgressColor = circleProgressColor;
    }

    public int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public boolean isTextIsDisplayable() {
        return textIsDisplayable;
    }

    public void setTextIsDisplayable(boolean textIsDisplayable) {
        this.textIsDisplayable = textIsDisplayable;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

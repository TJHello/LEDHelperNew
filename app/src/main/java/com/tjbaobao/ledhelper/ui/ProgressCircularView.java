package com.tjbaobao.ledhelper.ui;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

/**
 * 下载进度条 加载动画 组件
 * @author TJbaobao
 *
 */
public class ProgressCircularView extends View{
	private int radioON = 0,radioStart=270;
	private int progress=0;
	private int animStart = 0;
	private boolean isLoding = false;
	public static final float RADIUS = 50f; 
	private boolean isStart  = false;
	public ProgressCircularView(Context context) {
		super(context);
		
	}
	public class MyInterploator implements TimeInterpolator {
	    @Override
	    public float getInterpolation(float input) {
	        return 1-input;
	    }
	}
	@Override
	public void draw(Canvas canvas) {
		if(isStart)
		{
			Paint paint = new Paint();
			paint.setAntiAlias(true);// 消除锯齿效果
			int WIDTH = 120,HEIGHT = 120;
			RectF oval = new RectF(10, 10, 110, 110);
			//加载动画
			for(int i=0;i<3;i++)
			{
				RectF ovalAnim = new RectF(0, 0, 120, 120);
				paint.setColor(Color.parseColor("#1ca9f0"));
				canvas.drawArc(ovalAnim, animStart+(i*60), 60, true, paint);
				animStart=(animStart+3)%360;
			}
			
			paint.setColor(Color.parseColor("#dadada"));
	        canvas.drawArc(oval, radioStart, 360, true, paint);
	        if(!isLoding)
	        {
	        	paint.setColor(Color.RED);
		        canvas.drawArc(oval, radioStart, radioON, true, paint);
	        }
	        paint.setColor(Color.WHITE);
	        RectF oval2 = new RectF(14, 14, 106, 106);
	        canvas.drawArc(oval2, radioStart, 360, true, paint);
			
	        
	        //绘画进度文本
	        paint.setColor(Color.BLACK);
	        paint.setTextSize(22);
	        paint.setTextAlign(Paint.Align.LEFT);  
	        
	        String progressText;
	        if(isLoding)
	        	progressText = "loading";
	        else
	        	progressText = progress+"%";
	        
	        //完全居中
	        Rect bounds = new Rect();  
	        paint.getTextBounds(progressText, 0, progressText.length(), bounds);  
	        FontMetricsInt fontMetrics = paint.getFontMetricsInt();  
	        int baseline = (HEIGHT - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;  
	        canvas.drawText(progressText,WIDTH / 2 - bounds.width() / 2, baseline, paint);
	        
	        postInvalidateDelayed(3);
		}
		super.draw(canvas);
	}
	public void setProgress(int progress)
	{
		
		if(progress<=100)
		{
			this.progress = progress;
			radioON = progress*360/100;
		}
		if(!isStart)
		{
			isStart = true;
			this.postInvalidate();
		}
		
	}
	public void setLoding(boolean loding)
	{
		this.isLoding = loding;
		isStart = true;
		postInvalidateDelayed(1);
	}
	public void animStop()
	{
		isStart = false;
	}

}

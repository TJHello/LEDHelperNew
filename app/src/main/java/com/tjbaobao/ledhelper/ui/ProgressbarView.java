package com.tjbaobao.ledhelper.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.tjbaobao.framework.ui.base.BaseUI;
import com.tjbaobao.framework.util.Tools;

public class ProgressbarView extends BaseUI {

	private int bgColor = Color.parseColor("#dcdcdc");//背景颜色
	private int bgOutColor = Color.parseColor("#ffffff");//背景颜色
	private int progressColor = Color.parseColor("#b60000");//进度颜色
	public static final int MAX_PROGRESS = 100;
	private int progress = 10;
	private int progressTotal = MAX_PROGRESS;
	private int circleBgColor = Color.parseColor("#b60000");//圆圈颜色
	private int circleEdgeBgColor = Color.parseColor("#ffffff");//圆圈外圈背景颜色
	private int circleEdgeLineColor = Color.parseColor("#6a6a6a") ;//圆圈外圈背景线颜色
	private int bgTop = Tools.dpToPx(5),bgLeft = Tools.dpToPx(5),bgRight=Tools.dpToPx(5),bgBottom = Tools.dpToPx(5);
	
	private OnProgressListener onProgressListener ;
	
	public ProgressbarView(Context context) {
		super(context);
	}
	public ProgressbarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
        //canvas.drawColor(bgOutColor);
		drawProgressbar(canvas);
		drawCircle(canvas);
	}
	private void drawProgressbar(Canvas canvas)
	{
		Paint paint = new Paint();
		paint.setAntiAlias(true);// 消除锯齿效果
		paint.setColor(bgColor);
		if(bgTop<=0)
		{
			bgTop = 10;
		}
		if(bgBottom<=0)
		{
			bgBottom = 10;
		}
		canvas.drawRect(bgLeft, bgTop, viewWidth-bgRight, viewHeight-bgBottom, paint);
		paint.setColor(progressColor);
		int progressWidth = (viewWidth-bgLeft-bgRight)*progress/progressTotal ;
		canvas.drawRect(bgLeft, bgTop, progressWidth-bgLeft, viewHeight-bgBottom, paint);
	}
	private void drawCircle(Canvas canvas)
	{
		int rCircleLeft = 5 ;
		Paint paint = new Paint();
		paint.setAntiAlias(true);// 消除锯齿效果
		paint.setColor(circleEdgeLineColor);
		paint.setStyle(Style.STROKE);
		int progressWidth = (viewWidth-bgLeft-bgRight)*progress/progressTotal ;
		
		if(progressWidth>viewWidth-viewHeight/2)
		{
			progressWidth = viewWidth-viewHeight/2;
		}
		else if(progressWidth<=viewHeight/2)
		{
			progressWidth = viewHeight/2;
		}
		canvas.drawCircle(progressWidth, viewHeight/2, viewHeight/2-1, paint);
		
		paint.setColor(circleEdgeBgColor);
		paint.setStyle(Style.FILL);
		canvas.drawCircle(progressWidth, viewHeight/2, viewHeight/2-1, paint);
		
		paint.setColor(circleBgColor);
		paint.setStyle(Style.FILL);
		canvas.drawCircle(progressWidth, viewHeight/2, viewHeight/2-rCircleLeft, paint);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()== MotionEvent.ACTION_DOWN||event.getAction()== MotionEvent.ACTION_MOVE)
		{
			float pointX = event.getX();
			int progress =(int) (pointX/(float)viewWidth*progressTotal);
			if(progress>=0&&progress<=progressTotal)
			{
				setProgress(progress);
			}
		}
		else if(event.getAction()== MotionEvent.ACTION_UP)
		{
			if(onProgressListener!=null)
			{
				onProgressListener.onUp();
			}
		}
		return true;
	}
	
	public interface OnProgressListener
	{
		public void onChange(int progress);
		public void onUp();
	}
	public void setOnProgressListener(OnProgressListener onProgressListener) {
		this.onProgressListener = onProgressListener;
	}
	//------------属性设置读取--------------
	public int getBgColor() {
		return bgColor;
	}
	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
		this.postInvalidate();
	}
	public int getProgressColor() {
		return progressColor;
	}
	public void setProgressColor(int progressColor) {
		this.progressColor = progressColor;
		this.postInvalidate();
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
		this.postInvalidate();
		if(onProgressListener!=null)
		{
			onProgressListener.onChange(progress);
		}
	}
	public int getProgressTotal() {
		return progressTotal;
	}
	public void setProgressTotal(int progressTotal) {
		this.progressTotal = progressTotal;
		this.postInvalidate();
	}
	public int getCircleBgColor() {
		return circleBgColor;
	}
	public void setCircleBgColor(int circleBgColor) {
		this.circleBgColor = circleBgColor;
		this.postInvalidate();
	}
	public int getCircleEdgeBgColor() {
		return circleEdgeBgColor;
	}
	public void setCircleEdgeBgColor(int circleEdgeBgColor) {
		this.circleEdgeBgColor = circleEdgeBgColor;
		this.postInvalidate();
	}
	public int getCircleEdgeLineColor() {
		return circleEdgeLineColor;
	}
	public void setCircleEdgeLineColor(int circleEdgeLineColor) {
		this.circleEdgeLineColor = circleEdgeLineColor;
		this.postInvalidate();
	}
	public int getBgTop() {
		return bgTop;
	}
	public void setBgTop(int bgTop) {
		this.bgTop = bgTop;
		this.postInvalidate();
	}
	public int getBgLeft() {
		return bgLeft;
	}
	public void setBgLeft(int bgLeft) {
		this.bgLeft = bgLeft;
		this.postInvalidate();
	}
	public int getBgRight() {
		return bgRight;
	}
	public void setBgRight(int bgRight) {
		this.bgRight = bgRight;
		this.postInvalidate();
	}
	public int getBgBottom() {
		return bgBottom;
	}
	public void setBgBottom(int bgBottom) {
		this.bgBottom = bgBottom;
		this.postInvalidate();
	}
	
}

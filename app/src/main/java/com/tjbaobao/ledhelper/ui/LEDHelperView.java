package com.tjbaobao.ledhelper.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.tjbaobao.framework.ui.base.BaseUI;
import com.tjbaobao.framework.util.DateTimeUtil;
import com.tjbaobao.framework.util.Tools;
import com.tjbaobao.ledhelper.R;
import com.tjbaobao.ledhelper.engine.anim.AnimComputeEngine;
import com.tjbaobao.ledhelper.engine.anim.AnimComputeEngine.AnimInfo;
import com.tjbaobao.ledhelper.entity.data.LedAnimData;
import com.tjbaobao.ledhelper.entity.ui.LEDAnimInfo;
import com.tjbaobao.ledhelper.entity.ui.LEDEditHistoryInfo;
import com.tjbaobao.ledhelper.entity.ui.LEDPXInfo;
import com.tjbaobao.ledhelper.utils.BluetoothServer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LEDHelperView extends BaseUI {

	//==========画图
	private int ledWidth = 0,ledHeight = 0,ledTop = 0,ledLeft = 0;
	private int ledPaintWidth = 0,ledPaintHeight = 0;
	private int ledPxWidth = 0,ledPxHeight = 0;
	private LEDAnimInfo ledAnimInfo= null;
	//==========画笔============
	private Paint paintLedBgLine = new Paint();
	private int ledBlockStrokeWidth = 1;
	private Paint paintLedBg = new Paint();
	private Paint paintLedBlock = new Paint();
	private ArrayList<LEDEditHistoryInfo> ledHistoryList = new ArrayList<LEDEditHistoryInfo>();
	//==========动画============\
	private int FRAMERATE = 16;
	private Timer animTimer = null;
	private AnimTimerTask animTimerTask = null;
	private LEDAnimInfo oldLedAnimInfo = null;
	private boolean isPlaying = false;
	//=========工具==========
	private boolean paintON_OFF = true;//true画出白色，false擦除
	
	//==========监听器==========
	private OnLEDLinstener onLedLinstener ;
	
	
	public LEDHelperView(Context context) {
		super(context);
		initPaint();
	}
	public LEDHelperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}
	
	/**
	 * 加载数据
	 * @param ledAnimInfo
	 */
	public void loadData(LEDAnimInfo ledAnimInfo)
	{
		if(ledAnimInfo!=null)
		{
			this.ledAnimInfo = ledAnimInfo;
			ledWidth = ledAnimInfo.getWidth();
			ledHeight = ledAnimInfo.getHeight();
			if(oldLedAnimInfo==null)
			{
				oldLedAnimInfo = new LEDAnimInfo().initLEDPxs(ledWidth, ledHeight);
			}
		}
	}
	
	/**
	 * 初始化画笔
	 */
	private void initPaint()
	{
		paintLedBgLine.setColor(Color.parseColor("#686868"));
		paintLedBgLine.setAntiAlias(true);
		paintLedBgLine.setStrokeWidth(ledBlockStrokeWidth);
		paintLedBg.setColor(Color.BLACK);
		paintLedBlock.setAntiAlias(true);
		
		if(ledAnimInfo==null)
		{
			loadData(new LEDAnimInfo().initLEDPxs(32, 32));
		}
	}

	private Bitmap bitmapIndex = null;
	private Canvas canvasIndex = null;
	@Override
	protected void onDraw(Canvas canvas) {
		if(bitmapIndex==null)
		{
			bitmapIndex = Bitmap.createBitmap(viewWidth,viewHeight, Bitmap.Config.ARGB_8888);
			canvasIndex = new Canvas(bitmapIndex);
		}
		initValue();
		if(!isSave)
		{
			drawLedBg(canvas);
			isSave = false;
		}
		drawLedBlock(canvas);
	}
	
	/**
	 * 初始化长宽高等等数值
	 */
	private void initValue()
	{
		ledPxWidth = (viewWidth-(ledBlockStrokeWidth*(ledWidth+1)))/ledWidth;
		ledPxHeight = (viewHeight-(ledBlockStrokeWidth*(ledHeight+1)))/ledHeight;
		if(ledPxWidth<ledPxHeight)
		{
			ledPxHeight = ledPxWidth;
		}
		else
		{
			ledPxWidth = ledPxHeight;
		}
		ledPaintWidth =  (ledPxWidth+ledBlockStrokeWidth)*ledWidth+ledBlockStrokeWidth;
		ledPaintHeight = (ledPxHeight+ledBlockStrokeWidth)*ledHeight+ledBlockStrokeWidth;
		ledLeft = (viewWidth-ledPaintWidth)/2;
		ledTop =(viewHeight-ledPaintHeight)/2;
		//viewWidth = ledPaintWidth ;
		//viewHeight = ledPaintHeight ;
		if(ledPxWidth==0)
		{
			//setMeasuredDimension(ledPaintWidth, ledPaintHeight);
		}
	}
	
	/**
	 * 画方格背景
	 * @param canvas
	 */
	private void drawLedBg(Canvas canvas)
	{
		canvas.drawRect(ledLeft, ledTop, ledLeft+ledPaintWidth, ledTop+ledPaintHeight, paintLedBg);
//		for(int i=0;i<ledWidth+1;i++)
//		{
//			float startX = ledLeft+(i*(ledPxWidth+1));
//			float startY = ledTop;
//			float stopX = startX ;
//			float stopY = ledPaintHeight + ledTop;
//			canvas.drawLine(startX, startY, stopX, stopY, paintLedBgLine);
//		}
//		for(int j=0;j<ledHeight+1;j++)
//		{
//			float startX = ledLeft;
//			float startY = ledTop+(j*(ledPxHeight+1));
//			float stopX = ledPaintWidth+ledLeft ;
//			float stopY = startY;
//			canvas.drawLine(startX, startY, stopX, stopY, paintLedBgLine);
//		}
	}
	
	/**
	 * 画LED灯的方块
	 * @param canvas
	 */
	RectF oval = new RectF();
	private void drawLedBlock(Canvas canvas)
	{
		for(int i=0;i<ledWidth;i++)
		{
			for(int j=0;j<ledHeight;j++)
			{
				LEDPXInfo info = ledAnimInfo.getLedPxs()[i][j];
				if(!isSave||info.getColor()==LEDPXInfo.ColorOn)
				{

					paintLedBlock.setColor(info.getColor());
					paintLedBlock.setAlpha(255*info.getBrighter()/LEDPXInfo.maxBrighter);
					if(isSave)
					{
						paintLedBlock.setColor(getColorForRes(R.color.app_theme_color));
					}
					float left = ledLeft+(ledPxWidth+1)*i;
					float top = ledTop+(ledPxHeight+1)*j;
					float right = left+ledPxWidth;
					float bottom = top+ledPxHeight;
					oval.set(left, top, right, bottom);
					if(info.getColor()==LEDPXInfo.ColorOn)
					{
						paintLedBlock.setStyle(Paint.Style.FILL);
					}
					else
					{
						paintLedBlock.setColor(Color.parseColor("#8e8e8e"));
						paintLedBlock.setStyle(Paint.Style.STROKE);
					}
					canvas.drawRoundRect(oval, ledPxWidth, ledPxWidth, paintLedBlock);//第二个参数是x半径，第三个参数是y半径
				}
			}
		}
		
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE)
		{
			float x = event.getX();
			float y = event.getY();
			LEDPXInfo ledPxInfo = getLedPxInfo(x, y);
			if(ledPxInfo!=null)
			{
				if(!isPlaying)
				{
					int col = getLedCol(x);
					int row =getLedRow(y);
					addHistory(col, row, ledPxInfo);
					if(paintON_OFF)
					{
						ledPxInfo.setColor(LEDPXInfo.ColorOn);
					}
					else
					{
						ledPxInfo.setColor(LEDPXInfo.ColorOff);
						ledPxInfo.setBrighter(LEDPXInfo.maxBrighter);
					}
					oldLedAnimInfo.getLedPxs()[col][row]=ledPxInfo;
					if(onLedLinstener!=null)
					{
						onLedLinstener.onClick(col, row, ledPxInfo);
					}
					invalidate();
				}
				else
				{
					Tools.showToast("如需编辑请停止播放动画");
				}
			}
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	private class AnimTimerTask extends TimerTask
	{
		private int animType = 0;
		private int nowCol = 0,nowRow =0;
		private AnimComputeEngine animComputeEngine;
		private int round = 0;
		public AnimTimerTask(int animType) {
			this.animType = animType;
			animComputeEngine = new AnimComputeEngine(0, ledWidth, 0, ledHeight);
		}
		@Override
		public void run() {
			LEDAnimInfo ledAnimInfoTemp = new LEDAnimInfo() ;
			ledAnimInfoTemp.initLEDPxs(ledWidth, ledHeight);
			ledAnimInfoTemp.setAnimType(animType);
			round++;
			for(int i=0;i<ledWidth;i++)
			{
				for(int j=0;j<ledHeight;j++)
				{
					LEDPXInfo pxInfo = ledAnimInfo.getLedPxs()[i][j];
					if(pxInfo==null)
					{
						continue;
					}
					if(pxInfo.getColor()==LEDPXInfo.ColorOn)
					{
						AnimInfo positon =animComputeEngine.toAnimByType(animType, i, j,pxInfo.getBrighter(),round);
						pxInfo.setBrighter(positon.getBrighter());
						ledAnimInfoTemp.getLedPxs()[ positon.getX()][positon.getY()]=pxInfo;
					}
				}
			}
			if(isPlaying)
			{
				ledAnimInfo.setLedPxs(ledAnimInfoTemp.getLedPxs());
				BluetoothServer.write(new LedAnimData(ledAnimInfo.getLedPxs()).getBytesData());
			}
			postInvalidate();
		}
	}
	
	//============内部方法=========
	/**
	 * 根据点击位置获取LEDPX
	 * @param x
	 * @param y
	 * @return
	 */
	private LEDPXInfo getLedPxInfo(float x,float y)
	{
		int col = getLedCol(x);
		int row =getLedRow(y);
		LEDPXInfo ledPxInfo = getLedPxInfo(col,row);
		return ledPxInfo;
	}
	
	private int getLedRow(float y)
	{
		return (int) ((y-ledTop)/(ledPxHeight+1));
	}
	private int getLedCol(float x)
	{
		return (int) ((x-ledLeft)/(ledPxWidth+1));
	}
	
	private void addHistory(int col,int row,LEDPXInfo info)
	{
		LEDEditHistoryInfo ledHistoryInfo = new LEDEditHistoryInfo();
		ledHistoryInfo.setLedPxInfo(info);
		ledHistoryInfo.setX(col);
		ledHistoryInfo.setY(row);
		ledHistoryList.add(ledHistoryInfo);
		positon = ledHistoryList.size()-1;
	}
	
	//============外部方法============
	public LEDPXInfo getLedPxInfo(int col,int row)
	{
		if(col>-1&&row>-1&&col<ledWidth&&row<ledHeight)
		{
			return ledAnimInfo.getLedPxs()[col][row];
		}
		return null;
	}
	/**
	 * LED灯编辑组件事件监听器
	 * @author TJbaobao
	 * @time 2017年5月27日
	 */
	public interface OnLEDLinstener
	{
		/**
		 * 点击监听器
		 * @param info
		 */
		public void onClick(int col, int row, LEDPXInfo info);
		
		/**
		 * 动画播放完成
		 */
		public void onAnimStop();
		
		
	}

	
	
	public OnLEDLinstener getOnLedLinstener() {
		return onLedLinstener;
	}
	public void setOnLedLinstener(OnLEDLinstener onLedLinstener) {
		this.onLedLinstener = onLedLinstener;
	}
	public void setPxColor(int col,int row,boolean on_off)
	{
		LEDPXInfo info = getLedPxInfo(col, row);
		if(info!=null)
		{
			if(on_off)
			{
				info.setColor(LEDPXInfo.ColorOn);
			}
			else
			{
				info.setColor(LEDPXInfo.ColorOff);
			}
			this.postInvalidate();
		}
	}
	public void setPxBrighter(int col,int row,int brighter)
	{
		LEDPXInfo info = getLedPxInfo(col, row);
		if(info!=null)
		{
			info.setBrighter(brighter);
			this.postInvalidate();
		}
	}
	public void setAnimType(int animType)
	{
		ledAnimInfo.setAnimType(animType);
	}

	public void startAnimTask(int animType,int fps)
	{
		FRAMERATE = fps;
		oldLedAnimInfo.copyLEDAnim(ledAnimInfo) ;
		stopAnimTask();
		isPlaying = true ;
		animTimer = new  Timer();
		animTimerTask = new AnimTimerTask(animType);
		animTimer.schedule(animTimerTask, 0, 1000/FRAMERATE);
	}
	public void stopAnimTask()
	{
		isPlaying = false;
		if(animTimerTask!=null)
		{
			animTimerTask.cancel();
			animTimerTask = null;
		}
		if(animTimer!=null)
		{
			animTimer.cancel();
			animTimer = null;
		}
	}
	public void stopAnim()
	{
		stopAnimTask();
		ledAnimInfo.copyLEDAnim(oldLedAnimInfo) ;
		postInvalidate();
	}
	
	public void setPaint(boolean paintON_OFF)
	{
		this.paintON_OFF = paintON_OFF;
	}
	
	private int positon = 0;
	public void revoke()
	{
		positon--;
		if(positon>=0&&positon<ledHistoryList.size())
		{
			LEDEditHistoryInfo info = ledHistoryList.get(positon);
			ledAnimInfo.getLedPxs()[info.getX()][info.getY()] = info.getLedPxInfo();
			this.postInvalidate();
		}
	}

	public void forward()
	{
		if(positon<ledHistoryList.size())
		{
			LEDEditHistoryInfo info = ledHistoryList.get(positon);
			ledAnimInfo.getLedPxs()[info.getX()][info.getY()] = info.getLedPxInfo();
			positon++;
			this.postInvalidate();
		}
	}

	public LEDAnimInfo getLedAnimInfo()
	{
		ledAnimInfo.setTime(DateTimeUtil.getNowTime("yyyy-MM-dd HH:mm"));
		return ledAnimInfo;
	}
	private boolean isSave = false;
	public Bitmap getBitmapIndex()
	{
		isSave = true;
		this.draw(canvasIndex);
		return bitmapIndex;
	}

}

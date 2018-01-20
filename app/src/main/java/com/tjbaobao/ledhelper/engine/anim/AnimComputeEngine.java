package com.tjbaobao.ledhelper.engine.anim;

import java.util.Random;


public class AnimComputeEngine {
	private int left,right,top,bottom;
	private static final int FRAMERATE = 16;
	private int round;
	public class AnimType
	{
		public static final int LEFT_TO_RIGHT = 0;
		public static final int RIGHT_TO_LEFT = 1;
		public static final int TOP_TO_BOTTOM = 2;
		public static final int BOTTOM_TO_TOP = 3;
		public static final int STARS_FLASHING = 4;
		public static final int FLASHING_SLOW = 5;
		public static final int FLASHING = 6;
		public static final int SHADOW_FOLLOWS_TO_RIGHT = 7;
	}
	public enum AnimTypeEnum
	{
		LEFT_TO_RIGHT(AnimType.LEFT_TO_RIGHT,"从左到右"),
		RIGHT_TO_LEFT(AnimType.RIGHT_TO_LEFT,"从右到左"),
		TOP_TO_BOTTOM(AnimType.TOP_TO_BOTTOM,"从上到下"),
		BOTTOM_TO_TOP(AnimType.BOTTOM_TO_TOP,"从下到上"),
		STARS_FLASHING(AnimType.STARS_FLASHING,"繁星闪闪"),
		FLASHING_SLOW(AnimType.FLASHING_SLOW,"淡入淡出"),
		FLASHING(AnimType.FLASHING,"闪烁"),
		SHADOW_FOLLOWS_TO_RIGHT(AnimType.SHADOW_FOLLOWS_TO_RIGHT,"如影随形")
		;
		public int animType;
		public String animName;
		AnimTypeEnum(int animType,String animName)
		{
			this.animType = animType;
			this.animName = animName;
		}
		public static String getAnimName(int animType)
		{
			for(AnimTypeEnum anim : values())
			{
				if(anim.animType==animType)
				{
					return anim.animName ;
				}
			}
			return "";
		}
	}
	
	public AnimComputeEngine(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		round = 0;
	}
	
	public class AnimInfo
	{
		int x;
		int y;
		int brighter = 100;
		int color ;
		public int getX() {
			return x;
		}
		public AnimInfo setX(int x) {
			this.x = x;
			return this;
		}
		public int getY() {
			return y;
		}
		public AnimInfo setY(int y) {
			this.y = y;
			return this;
		}
		public int getBrighter() {
			return brighter;
		}
		public AnimInfo setBrighter(int brighter) {
			this.brighter = brighter;
			return this;
		}
		public int getColor() {
			return color;
		}
		public void setColor(int color) {
			this.color = color;
		}
	}
	
	public AnimInfo toAnimByType(int type,int x,int y,int brighter,int round)
	{
		switch(type)
		{
		case AnimType.LEFT_TO_RIGHT:
			return leftToRight(x).setY(y);
		case AnimType.RIGHT_TO_LEFT:
			return rightToLeft(x).setY(y);
		case AnimType.TOP_TO_BOTTOM:
			return topToBottom(y).setX(x);
		case AnimType.BOTTOM_TO_TOP:
			return bottomToTop(y).setX(x);
		case AnimType.STARS_FLASHING:
			return starsFlashing().setX(x).setY(y);
		case AnimType.FLASHING_SLOW:
			return flashingSlow(brighter,round).setX(x).setY(y);
		case AnimType.FLASHING:
			return flashing(brighter, round).setX(x).setY(y);
		case AnimType.SHADOW_FOLLOWS_TO_RIGHT:
			return shadowFollowsToRight(x).setY(y);
		}
		return new AnimInfo().setX(x).setY(y).setBrighter(brighter);
	}
	
	public AnimInfo leftToRight(int x)
	{
		AnimInfo position = new AnimInfo();
		position.setX( (++x)%right+left);
		return position;
	}

	public AnimInfo rightToLeft(int x)
	{
		AnimInfo position = new AnimInfo();
		x = --x<0?right-1:x ;
		position.setX(x+left);
		return position;
	}

	public AnimInfo topToBottom(int y)
	{
		AnimInfo position = new AnimInfo();
		position.setY( (++y)%bottom+top);
		return position;
	}

	public AnimInfo bottomToTop(int y)
	{
		AnimInfo position = new AnimInfo();
		y = --y<0?bottom-1:y ;
		position.setY(y+top);
		return position;
	}

	/**
	 * 常亮
	 * @return
	 */
	public AnimInfo starsFlashing()
	{
		Random random = new Random();
		int brighter = random.nextInt(100);
		return new AnimInfo().setBrighter(brighter);
	}

	/**
	 * 淡入淡出
	 * @param brighter
	 * @return
	 */
	boolean flashingIn = true;
	public AnimInfo flashingSlow(int brighter,int round)
	{
		int flashingLong = 4;//闪动时长秒
		if(flashingIn)
		{
			brighter+=100/FRAMERATE/flashingLong*2;
			if(brighter>=100&&round!=this.round)
			{
				brighter = 100;
				flashingIn = false;
			}
		}
		else
		{
			brighter-=100/FRAMERATE/flashingLong*2;
			if(brighter<=0&&round!=this.round)
			{
				brighter = 0;
				flashingIn = true;
			}
		}
		this.round = round;
		return new AnimInfo().setBrighter(brighter);
	}
	
	/**
	 * 闪烁
	 * @param brighter
	 * @param round
	 * @return
	 */
	public AnimInfo flashing(int brighter,int round)
	{
		int flashingLong = 2;//闪动间隔
		if(round/FRAMERATE%flashingLong==0)
		{
			brighter = 100;
		}
		else
		{
			brighter = 0;
		}
		return new AnimInfo().setBrighter(brighter);
	}
	
	
	public AnimInfo shadowFollowsToRight(int x)
	{
		AnimInfo info = leftToRight(x);
		int brighter = 100*info.getX()/right;
		return info.setBrighter(brighter);
	}
	
	public void initValue(int left,int right,int top,int bottom)
	{
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	public static int getFPSNum(int type,int screenRowNum,int screenColNum)
	{
		switch(type)
		{
			case AnimType.LEFT_TO_RIGHT:
				return screenColNum ;
			case AnimType.RIGHT_TO_LEFT:
				return screenColNum;
			case AnimType.TOP_TO_BOTTOM:
				return screenRowNum;
			case AnimType.BOTTOM_TO_TOP:
				return screenRowNum;
			case AnimType.STARS_FLASHING:
				return 1;
			case AnimType.FLASHING_SLOW:
				return 13 ;
			case AnimType.FLASHING:
				return 4;
			case AnimType.SHADOW_FOLLOWS_TO_RIGHT:
				return screenColNum;
		}
		return 0;
	}
}

package com.tjbaobao.ledhelper.entity.ui;

import android.graphics.Color;

import java.io.Serializable;

public class LEDPXInfo implements Serializable{
	private static final long serialVersionUID = -7092819968284446786L;

	public static final int ColorOff = Color.BLACK;
	public static final int ColorOn = Color.WHITE;
	public static final int minBrighter = 0;
	public static final int maxBrighter = 100;

	private int color = ColorOff;//颜色
	private int  brighter = maxBrighter;//亮度,最大值100，最小值0
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getBrighter() {
		return brighter;
	}
	public void setBrighter(int brighter) {
		this.brighter = brighter;
	}

	public LEDPXInfo copy(LEDPXInfo ledpxInfo)
	{
		this.color = ledpxInfo.getColor();
		this.brighter = ledpxInfo.getBrighter();
		return this;
	}
	
	
}

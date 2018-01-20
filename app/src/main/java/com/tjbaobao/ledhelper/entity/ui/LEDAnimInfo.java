package com.tjbaobao.ledhelper.entity.ui;

import com.tjbaobao.ledhelper.engine.anim.AnimComputeEngine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LEDAnimInfo implements Serializable{
	private static final long serialVersionUID = -4670916831766650882L;

	private int width;
	private int height ;
	private int animType ;
	private LEDPXInfo[][] ledPxs ;
	private String title;
	private String time ;
	private String indexImage ;
	private String code ;
	private String configPath ;
	private int position ;
	private int rowNum,colNum ;
	private boolean isCollection = false;
	private int ledHelperType = -1;
	private String text ;
	private List<String> images ;
	private List<LEDPXInfo[][]> ledPxFpsList = new ArrayList<>();
	private boolean isSelect = false;
	private int speed = 16;
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getAnimType() {
		return animType;
	}
	public void setAnimType(int animType) {
		this.animType = animType;
	}
	public LEDPXInfo[][] getLedPxs() {
		return ledPxs;
	}
	public void setLedPxs(LEDPXInfo[][] ledPxs) {
		this.ledPxs = ledPxs;
	}
	public LEDAnimInfo initLEDPxs(int width,int height)
	{
		rowNum = width;
		colNum = height;
		this.width = width;
		this.height = height;
		this.animType = AnimComputeEngine.AnimTypeEnum.LEFT_TO_RIGHT.animType ;
		this.ledPxs = new LEDPXInfo[width][height];
		for(int i=0;i<width;i++)
		{
			for(int j=0;j<height;j++)
			{
				ledPxs[i][j] = new LEDPXInfo();
			}
		}
		return this;
	}
	public LEDAnimInfo copyLEDAnim(LEDAnimInfo info)
	{
		this.width = info.getWidth();
		this.height = info.getHeight();
		this.animType = info.getAnimType() ;
		this.ledPxs = new LEDPXInfo[width][height];
		this.code = info.getCode();
		this.configPath = info.getConfigPath();
		this.colNum = info.getColNum();
		this.rowNum = info.getRowNum();
		this.text = info.getText();
		this.title = info.getTitle();
		this.ledHelperType = info.getLedHelperType();
		this.isSelect = info.isSelect();
		this.speed = info.getSpeed();

		for(LEDPXInfo[][] ledpxInfos:info.getLedPxFpsList())
		{
			LEDPXInfo[][] ledpxInfosTemp = new LEDPXInfo[width][height];
			for(int i=0;i<width;i++) {
				for (int j = 0; j < height; j++) {
					LEDPXInfo pxInfo = new LEDPXInfo();
					pxInfo.setBrighter(ledpxInfos[i][j].getBrighter());
					pxInfo.setColor(ledpxInfos[i][j].getColor());
					ledpxInfosTemp[i][j] = pxInfo;
				}
			}
			this.ledPxFpsList.add(ledpxInfosTemp);
		}
		this.ledPxFpsList = ledPxFpsList ;
		for(int i=0;i<width;i++)
		{
			for(int j=0;j<height;j++)
			{
				LEDPXInfo pxInfo = new LEDPXInfo();
				pxInfo.setBrighter(info.getLedPxs()[i][j].getBrighter());
				pxInfo.setColor(info.getLedPxs()[i][j].getColor());
				this.ledPxs[i][j] = pxInfo;
			}
		}
		return this;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getIndexImage() {
		return indexImage;
	}

	public void setIndexImage(String indexImage) {
		this.indexImage = indexImage;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public int getColNum() {
		return colNum;
	}

	public void setColNum(int colNum) {
		this.colNum = colNum;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setCollection(boolean collection) {
		isCollection = collection;
	}

	public int getLedHelperType() {
		return ledHelperType;
	}

	public void setLedHelperType(int ledHelperType) {
		this.ledHelperType = ledHelperType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public List<LEDPXInfo[][]> getLedPxFpsList() {
		if(ledPxFpsList==null)
		{
			ledPxFpsList = new ArrayList<>();
		}
		return ledPxFpsList;
	}

	public void setLedPxFpsList(List<LEDPXInfo[][]> ledPxFpsList) {
		this.ledPxFpsList = ledPxFpsList;
	}

	public void addLedPxFps(LEDPXInfo[][] ledpxInfos)
	{
		ledPxFpsList.add(ledpxInfos);
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public int getSpeed() {
		if(speed==0)
		{
			speed = 16;
		}
		return speed;
	}

	public void setSpeed(int speed) {

		this.speed = speed;
	}
}

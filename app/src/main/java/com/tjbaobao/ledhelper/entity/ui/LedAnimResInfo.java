package com.tjbaobao.ledhelper.entity.ui;

import java.util.List;

public class LedAnimResInfo {
	private int width;
	private int height ;
	private int animType ;
	private String title;
	private String indexImage ;
	private String code ;
	private int ledHelperType = -1;
	private String text ;
	private List<String> images ;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	
	
	
}

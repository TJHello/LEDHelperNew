package com.tjbaobao.ledhelper.entity.ui;

/**
 * Created by TJbaobao on 2017/9/20.
 */

public class PoPMenuListInfo {
    private int imageId ;
    private String text ;

    public PoPMenuListInfo()
    {

    }

    public PoPMenuListInfo(int imageId, String text) {
        this.imageId = imageId;
        this.text = text;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

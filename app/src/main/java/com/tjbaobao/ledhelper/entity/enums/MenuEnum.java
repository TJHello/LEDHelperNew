package com.tjbaobao.ledhelper.entity.enums;

import com.tjbaobao.ledhelper.R;

/**
 * Created by TJbaobao on 2018/1/17.
 */

public enum MenuEnum {

    BLUETOOTH(R.drawable.bluetooth_connect,"蓝牙设备"),
    SETTING(R.drawable.index_bottom_setting,"设置")
    ;

    public int imageId ;
    public String name ;
    MenuEnum(int imageId, String name) {
        this.imageId = imageId;
        this.name = name;
    }
}

package com.tjbaobao.ledhelper.entity.data;

import com.tjbaobao.ledhelper.entity.ui.LEDPXInfo;

/**
 * Created by TJbaobao on 2017/11/28.
 */

public class LedAnimData extends LedBaseData {



    public LedAnimData(LEDPXInfo[][] ledPxInfos) {
        super(new byte[14+ledPxInfos[0].length*ledPxInfos.length/2]);
        addBytes(BYTES_HEAD, 1).addBytes(BYTES_TITLE, 2);//帧头-标志
        for(int i=0;i<ledPxInfos.length;i++)
        {
            for(int j=0;j<ledPxInfos[0].length;j+=2)
            {
                LEDPXInfo ledPxInfo1 = ledPxInfos[i][j];
                LEDPXInfo ledPxInfo2 = ledPxInfos[i][j+1];
                int position = 14+(i*ledPxInfos[0].length+j+2)/2;
                addBytes(getByteByColor(ledPxInfo1.getColor(),ledPxInfo2.getColor()),position);
            }
        }
    }

}

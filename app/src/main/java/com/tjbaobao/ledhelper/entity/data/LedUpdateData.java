package com.tjbaobao.ledhelper.entity.data;

import com.tjbaobao.ledhelper.entity.ui.LEDPXInfo;

/**
 * Created by TJbaobao on 2017/12/12.
 */

public class LedUpdateData extends LedBaseData {

    public LedUpdateData(LEDPXInfo[][] ledPxInfos,int position) {
        super(new byte[14+2+ledPxInfos[0].length*ledPxInfos.length/2+2]);

        addBytes(BYTES_HEAD, 1)//帧头
                .addBytes(BYTES_TITLE, 2)//帧头-标志
                .addBytes(getH(position),15)
                .addBytes(getL(position),16);
        int colSize = ledPxInfos[0].length;
        for(int i=0;i<ledPxInfos.length;i++)
        {
            for(int j=0;j<colSize;j+=2)
            {
                LEDPXInfo ledPxInfo1 = ledPxInfos[i][j];
                LEDPXInfo ledPxInfo2 = ledPxInfos[i][j+1];
                int index = 16+(i*colSize+j+2)/2;
                addBytes(getByteByColor(ledPxInfo1.getColor(),ledPxInfo2.getColor()),index);
            }
        }
        byte checkH = getCheckH(15,getBytesData().length-2);
        addBytes(checkH,getBytesData().length-1);
        addBytes(getCheckL(15,getBytesData().length-2),getBytesData().length);
    }
}

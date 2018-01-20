package com.tjbaobao.ledhelper.entity.data;

import com.tjbaobao.ledhelper.entity.ui.LEDPXInfo;

/**
 * Created by TJbaobao on 2017/11/28.
 */

public class LedBaseData {

    protected static final byte[] BYTES_HEAD = new byte[]{0};
    protected static final byte[] BYTES_TITLE= new byte[]{115, 101, 101, 107, 119, 97, 121, 119, 115, 50, 48, 49, 55};
    protected static final byte byteOff = 0x0;
    protected static final byte byteOn = 0xf;
    private byte[] bytesData = null ;

    public LedBaseData(byte[] bytesData)
    {
        this.bytesData = bytesData ;
    }


    public byte[] getBytesData() {
        return bytesData;
    }

    public void setBytesData(byte[] bytesData) {
        this.bytesData = bytesData;
    }

    protected LedBaseData addBytes(byte[] bytes, int position)
    {
        for(int i=position-1,readSize = 0;readSize<bytes.length;i++,readSize++)
        {
            bytesData[i] = bytes[readSize];
        }
        return this;
    }
    protected LedBaseData addBytes(byte b, int position)
    {
        int index = position-1;
        bytesData[index]= b;
        return this;
    }
    protected LedBaseData addBytes(int bInt, int position)
    {
        int index = position-1;
        byte b = (byte) bInt;
        bytesData[index]= b;
        return this;
    }

    protected byte getCheckH(int index,int end)
    {
        int  result = 0;
        for(int i=index-1;i<=end-1;i++)
        {
            byte b = bytesData[i];
            int mNum = ((int)b >= 0) ? (int)b : ((int)b + 256);
            result+=mNum;
        }
        return getH(result);
    }

    protected byte getCheckL(int index,int end)
    {
        int  result = 0;
        for(int i=index-1;i<=end-1;i++)
        {
            byte b = bytesData[i];
            int mNum = ((int)b >= 0) ? (int)b : ((int)b + 256);
            result+=mNum;
        }
        return getL(result);
    }

    protected static byte getH(int data)
    {
        return (byte) ((data>>8)&0xff);
    }

    protected static byte getL(int data)
    {
        return (byte) (data&0xff);
    }

    protected byte getByteByColor(int color1,int color2)
    {

        return (byte) (getByteByColor(color1)<<4|getByteByColor(color2));
    }

    protected byte getByteByColor(int color)
    {
        if(color== LEDPXInfo.ColorOff)
        {
            return byteOff;
        }
        else
        {
            return byteOn;
        }
    }
}

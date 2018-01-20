package com.tjbaobao.ledhelper.entity.data;

/**
 * Created by TJbaobao on 2017/12/20.
 */

public class LedReturnData{
    private byte[] bytesData = null ;
    protected int resultType ;
    protected int resultComment ;

    public static final int OK = 1;
    public static final int ERROR = 0;

    public static final int  UPDATE_ERROR = 0;
    public static final int  UPDATE_OUT_TIME = 1;

    private static final LedReturnData ledReturnData = new LedReturnData();

    public static LedReturnData utilData(byte[] bytes)
    {
        if(bytes==null)
        {
            ledReturnData.resultType = ERROR;
            ledReturnData.resultComment = UPDATE_OUT_TIME;
            return ledReturnData;
        }
        ledReturnData.bytesData = bytes ;
        byte byteType = bytes[14];
        byte byteComment = bytes[15];
        ledReturnData.resultType =byteType;
        ledReturnData.resultComment = byteComment;
        return ledReturnData;
    }

    public byte[] getBytesData() {
        return bytesData;
    }
    public int getResultType() {
        return resultType;
    }
    public int getResultComment() {
        return resultComment;
    }

}

package com.tjbaobao.ledhelper.entity.data;

/**
 * Created by TJbaobao on 2017/11/26.
 */

public class LedControlInfo extends LedBaseData{

    private static final byte[] BYTES_RESERVE = new byte[]{0};
    private static final int[] CHECK_POSITON = new int[]{15,22};
    public static final int TYPE_SCREEN_512 = 1;
    public static final int TYPE_SCREEN_1024 = 0;

    public static final int TYPE_RUN_STOP =0 ;
    public static final int TYPE_RUN_CONSTANTLY=1 ;
    public static final int TYPE_RUN_BUILT = 2;
    public static final int TYPE_RUN_UPDATE = 3;
    public static final int TYPE_RUN_CHECK = 4;
    public static final int DEF_SPEED = 5;
    public static final int DEF_0 = 0;
    public static  final int DEF_1 = 1;

    //播放，停止
    private LedControlInfo(int screenType, int runType)
    {
        this(screenType,runType,DEF_0,DEF_1,DEF_0,DEF_0,DEF_1,DEF_SPEED);
    }

    private LedControlInfo(int screenType,int runType,int builtPosition,int speed)
    {
        this(screenType,runType,builtPosition,DEF_1,DEF_0,DEF_0,DEF_1,speed);
    }

    private LedControlInfo(int screenType,int runType,int updatePosition,int fpsH,int fpsL,int number,int speed)
    {
        this(screenType,runType,DEF_0,updatePosition,fpsH,fpsL,number,speed);
    }


    private LedControlInfo(int screenType,int runType,int builtPosition,int updatePosition,int fpsH,int fpsL,int number,int speed)
    {
        super(new byte[24]);
        addBytes(BYTES_HEAD, 1)//帧头
                .addBytes(BYTES_TITLE, 2)//帧头-标志
                .addBytes(screenType, 15)//软屏类型
                .addBytes(runType, 16)//运行模式
                .addBytes(builtPosition, 17)//内置效果
                .addBytes(updatePosition, 18)//更新效果
                .addBytes(fpsH, 19)//帧数高8位
                .addBytes(fpsL, 20)//帧数低8位
                .addBytes(number, 21)//更新效果的总数
                .addBytes(speed, 22)//速度0-99
                .addBytes(getCheckH(CHECK_POSITON[0], CHECK_POSITON[1]), 23)//检验和H
                .addBytes(getCheckL(CHECK_POSITON[0], CHECK_POSITON[1]), 24)//检验和L
        ;
    }

    public static LedControlInfo getPlayControl(int screenType)
    {
        return new LedControlInfo(screenType,TYPE_RUN_CONSTANTLY);
    }
    public static LedControlInfo getStopControl(int screenType)
    {
        return new LedControlInfo(screenType,TYPE_RUN_STOP);
    }

    public static LedControlInfo getUpdateControl(int screenType,int updatePosition,int fps,int number,int speed)
    {
        return new LedControlInfo(screenType,TYPE_RUN_UPDATE,updatePosition,getH(fps),getL(fps),number,speed);
    }

    public static LedControlInfo getBuiltControl(int screenType,int runType,int builtPosition,int speed)
    {
        return new LedControlInfo(screenType,runType,builtPosition,speed);
    }
}

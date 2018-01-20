package com.tjbaobao.ledhelper.utils;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.tjbaobao.framework.util.BaseTimerTask;
import com.tjbaobao.framework.util.HexConvertTools;
import com.tjbaobao.framework.util.Tools;
import com.tjbaobao.ledhelper.entity.info.BlueToothInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Created by TJbaobao on 2017/11/26.
 */

public class BluetoothServer {

    private static BluetoothServer bluetoothServer = null;

    public static final BluetoothServer getInstance(Context context) {
        if (bluetoothServer == null) {
            synchronized (BroadcastReceiver.class) {
                if (bluetoothServer == null) {
                    bluetoothServer = new BluetoothServer(context);
                }
            }
        }
        bluetoothServer.registerReceiver();
        return bluetoothServer;
    }

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final int STATE_INIT = 0;
    public static final int STATE_SEARCH_ING = 11;
    public static final int STATE_SEARCH_SUCCESS = 12;
    public static final int STATE_SEARCH_FAIL = 13;
    public static final int STATE_CONNECT_ING = 21;
    public static final int STATE_CONNECT_SUCCESS = 22;
    public static final int STATE_CONNECT_FAIL = 23;

    private static int server_state = STATE_INIT;

    private static BluetoothAdapter mBluetoothAdapter = null;
    private static BluetoothServerSocket mBluetoothServerSocket = null;
    private static MyBroadcastReceiver mBroadcastReceiver = null;
    private static BlueToothInfo connectBlueToothInfo = new BlueToothInfo();
    private MyLeScanCallback mLeScanCallback = new MyLeScanCallback();
    private MyHandler mHandler = new MyHandler();
    private OnBltSearchListener onBltSearchListener;
    private OnBltConnectListener onBltConnectListener;
    private OnBltServerListener onBltServerListener ;

    private static ArrayList<BlueToothInfo> deviceList = new ArrayList<BlueToothInfo>();

    private class Handler_What {
        private static final int SEARCH_ING = 11;
        private static final int SEARCH_SUCCESS = 12;
        private static final int SEARCH_FAIL = 13;
        private static final int SEARCH_SUCCESS_ONE = 14;
        private static final int CONNECT_INT = 21 ;
        private static final int CONNECT_SUCCESS = 22;//连接成功
        private static final int CONNECT_FAIL = 23;
        private static final int SERVER_ING = 31;
        private static final int SERVER_SUCCESS = 32;
        private static final int SERVER_FAIL = 33;
    }

    private Context context;
    private BluetoothServer(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        //获取BluetoothAdapter
        if (bluetoothManager != null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        mBroadcastReceiver = new MyBroadcastReceiver();
        createServer();
    }

    private void registerReceiver()
    {
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);//搜索发现设备
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式改变了
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生了变化
        context.registerReceiver(mBroadcastReceiver, intent);
    }

    //region======================搜索设备=======================

    public void search() {
        if(mBluetoothAdapter==null)
        {
            return ;
        }
        getOffLineBltList();
        //开始搜索设备，当搜索到一个设备的时候就应该将它添加到设备集合中，保存起来
        checkBleDevice(context);
        //如果当前发现了新的设备，则停止继续扫描，当前扫描到的新设备会通过广播推向新的逻辑

        //开始搜索
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (onBltSearchListener != null) {
                onBltSearchListener.onSearchIng();
            }
            //mBluetoothAdapter.startLeScan(mLeScanCallback);
            boolean result = mBluetoothAdapter.startDiscovery();
        }
        else
        {
            boolean result = mBluetoothAdapter.startDiscovery();
            if(!result)
            {
                if (onBltSearchListener != null) {
                    onBltSearchListener.onSearchFail();
                }
            }
        }
    }

    /**
     * 获得系统保存的配对成功过的设备，并尝试连接
     */
    public void getOffLineBltList() {
        if (mBluetoothAdapter == null) return;
        //获得已配对的远程蓝牙设备的集合
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (Iterator<BluetoothDevice> it = devices.iterator(); it.hasNext(); ) {
                BluetoothDevice device = it.next();
                BlueToothInfo info = new BlueToothInfo();
                info.setName(device.getName());
                info.setAddress(device.getAddress());
                deviceList.add(info);
                sendMessage(Handler_What.SEARCH_SUCCESS_ONE,info);
                //自动连接已有蓝牙设备
                //createBond(device, null);
            }
        }
    }

    //endregion

    //region==========================连接设备===============================

    /**
     * 输入mac地址进行自动配对
     * 前提是系统保存了该地址的对象
     *
     * @param address
     */
    public void connect(final String address) {
        if(mBluetoothAdapter==null) return ;
        for(BlueToothInfo info:deviceList)
        {
            if(info.getState()==STATE_CONNECT_ING)
            {
                info.setState(STATE_INIT);
            }
        }
        new Thread() {
            public void run() {
                if (mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.cancelDiscovery();
                BluetoothDevice btDev = mBluetoothAdapter.getRemoteDevice(address);
                connectBltDevice(btDev);
            }
        }.start();
    }

    /**
     * 尝试连接一个设备，子线程中完成，因为会线程阻塞
     *
     * @param bltDev 蓝牙设备对象
     * @return
     */
    private synchronized void connectBltDevice(BluetoothDevice bltDev) {
        BlueToothInfo info = getBluetoothInfo(bltDev.getAddress());
        BluetoothSocket mBluetoothSocket = info.getmBluetoothSocket();
        try {
            if(mBluetoothAdapter==null) return ;

            if (mBluetoothSocket != null&&mBluetoothSocket.isConnected()) {
                return ;
            }
            //通过和服务器协商的uuid来进行连接
            if (mBluetoothSocket == null) {
                server_state = STATE_CONNECT_ING;
                info.setState(STATE_CONNECT_ING);
                sendMessage(Handler_What.CONNECT_INT,null);
                mBluetoothSocket = bltDev.createRfcommSocketToServiceRecord(uuid);
                mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("led",uuid);
            }
            if (mBluetoothSocket == null) {
                server_state = STATE_CONNECT_FAIL;
                sendMessage(Handler_What.CONNECT_FAIL,null);
                return;
            }
            //在建立之前调用
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            //如果当前socket处于非连接状态则调用连接
            if (!mBluetoothSocket.isConnected()) {
                //你应当确保在调用connect()时设备没有执行搜索设备的操作。
                // 如果搜索设备也在同时进行，那么将会显著地降低连接速率，并很大程度上会连接失败。
                mBluetoothSocket.connect();
            }
            if (mBluetoothSocket.isConnected()) {
                if(info!=null)
                {
                    info.setState(STATE_CONNECT_SUCCESS);
                    info.setmBluetoothSocket(mBluetoothSocket);
                    Tools.printLog("连接成功"+bltDev.getAddress());
                }
                server_state = STATE_CONNECT_SUCCESS;
                sendMessage(Handler_What.CONNECT_SUCCESS, bltDev);
            }
        } catch (Exception e) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            server_state = STATE_CONNECT_FAIL;
            info.setState(STATE_CONNECT_FAIL);
            sendMessage(Handler_What.CONNECT_FAIL, bltDev);
        }
    }

    private BlueToothInfo getBluetoothInfo(String address)
    {
        for(BlueToothInfo info:deviceList)
        {
            if(info.getAddress().equals(address))
            {
                return info;
            }
        }
        return null;
    }

    //endregion

    //region=======================创建蓝牙服务器========================
        private ServerTimerTask serverTimerTask ;
        private void createServer()
        {
            try {
                if(mBluetoothServerSocket==null)
                {
                    sendMessage(Handler_What.SERVER_ING,null);
                    if(mBluetoothAdapter!=null)
                    {
                        mBluetoothServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("LEDServer",uuid);
                        sendMessage(Handler_What.SERVER_SUCCESS,null);
                    }
                }
                if(mBluetoothServerSocket!=null)
                {
                    if(serverTimerTask==null)
                    {
                        serverTimerTask = new ServerTimerTask();
                    }
                    serverTimerTask.startTimer();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private class ServerTimerTask extends BaseTimerTask
        {
            private BluetoothSocket mBluetoothSocket ;
            @Override
            public void run() {
                try {
                    while (true)
                    {
                        if(mBluetoothServerSocket!=null)
                        {
                            mBluetoothSocket = mBluetoothServerSocket.accept();
                        }
                        if(mBluetoothSocket!=null&&mBluetoothSocket.isConnected())
                        {
                            InputStream inputStream = mBluetoothSocket.getInputStream();
                            int len = 0;
                            while (len == 0) {
                                len = inputStream.available();
                            }
                            byte[] bytes = new byte[len];
                            inputStream.read(bytes);
                            Tools.printLog("接收到数据:"+ HexConvertTools.bytesToHex(bytes));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    //endregion

    //region============================数据操作=================================

    public static synchronized void write(byte[] data) {
        OutputStream outStream = null;
        try {
            for(BlueToothInfo info:deviceList)
            {
                BluetoothSocket mBluetoothSocket = info.getmBluetoothSocket();
                if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
                    outStream = mBluetoothSocket.getOutputStream();
                    outStream.write(data);
                    outStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(outStream!=null)
            {
                try {
                    outStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static final long ReadOutTime = 1000*15;
    public static synchronized byte[] writeAndRead(byte[] data) {
        OutputStream outStream = null;
        InputStream inputStream = null;
        try {
            for(BlueToothInfo info:deviceList)
            {
                BluetoothSocket mBluetoothSocket = info.getmBluetoothSocket();
                if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
                    outStream = mBluetoothSocket.getOutputStream();
                    outStream.write(data);
                    outStream.flush();
                }
                if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
                    inputStream = mBluetoothSocket.getInputStream();
                    int len = 0;
                    long firstTime = System.currentTimeMillis();
                    while (len == 0) {
                        len = inputStream.available();
                        Tools.sleep(10);
                        if(System.currentTimeMillis()-firstTime>ReadOutTime)
                        {
                            return null;
                        }
                    }
                    byte[] bytes = new byte[len];
                    inputStream.read(bytes);
                    if(onBltDataListener!=null)
                    {
                        onBltDataListener.onReceive(bytes);
                    }
                    return bytes;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(outStream!=null)
            {
                try {
                    outStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }


    //endregion

    //region=================外部方法=====================

    /**
     * 判断是否支持蓝牙，并打开蓝牙
     * 获取到BluetoothAdapter之后，还需要判断是否支持蓝牙，以及蓝牙是否打开。
     * 如果没打开，需要让用户打开蓝牙。
     */
    public void checkBleDevice(Context context) {
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(enableBtIntent);
            }
        } else {
            Tools.showToast("您的手机不支持蓝牙");
        }
    }

    //endregion

    //region========================回调处理===============================

    /**
     * 处理蓝牙搜索结果
     *
     * @param device
     * @param rssi
     * @param scanRecord
     */
    private void handlerLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        BlueToothInfo info = new BlueToothInfo();
        info.setName(device.getName());
        info.setAddress(device.getAddress());
        Tools.printLog("找到设备"+info.getAddress());
        boolean isHave = false;
        for (BlueToothInfo blueInfo : deviceList) {
            if (blueInfo.getAddress().equals(info.getAddress())) {
                isHave = true;
            }
        }
        if (!isHave) {
            deviceList.add(info);
            sendMessage(Handler_What.SEARCH_SUCCESS_ONE,info);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        sendMessage(Handler_What.SEARCH_SUCCESS,null);
    }

    private void handlerBlueBrodcast(Context context, Intent intent)
    {
        String action = intent.getAction();
        BluetoothDevice device;
        // 搜索发现设备时，取得设备的信息；注意，这里有可能重复搜索同一设备
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BlueToothInfo info = new BlueToothInfo();
            info.setName(device.getName());
            info.setAddress(device.getAddress());
            boolean isHave = false;
            for (BlueToothInfo blueInfo : deviceList) {
                if (blueInfo.getAddress().equals(info.getAddress())) {
                    isHave = true;
                }
            }
            Tools.printLog("找到设备:"+device.getAddress());
            if(!isHave)
            {
                deviceList.add(info);
                sendMessage(Handler_What.SEARCH_SUCCESS_ONE,info);
            }
        }
        //状态改变时
        else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (device.getBondState()) {
                case BluetoothDevice.BOND_BONDING://正在配对
                    Tools.printLog("正在配对......");
                    break;
                case BluetoothDevice.BOND_BONDED://配对结束
                    Tools.printLog("完成配对");
                    break;
                case BluetoothDevice.BOND_NONE://取消配对/未配对
                    Tools.printLog("取消配对");
                default:
                    break;
            }
        }
    }

    private void handlerMsg(Message msg)
    {
        switch (msg.what) {
            case Handler_What.CONNECT_INT:
                if (onBltConnectListener != null) {
                    onBltConnectListener.onConnectIng();
                    connectBlueToothInfo.setState(STATE_CONNECT_ING);
                }
                break;
            case Handler_What.CONNECT_SUCCESS:
                BluetoothDevice bltDev = (BluetoothDevice) msg.obj;
                if(bltDev!=null)
                {
                    connectBlueToothInfo.setState(STATE_CONNECT_SUCCESS);
                    connectBlueToothInfo.setAddress(bltDev.getAddress());
                    connectBlueToothInfo.setName(bltDev.getName());
                    if (onBltConnectListener != null) {
                        onBltConnectListener.onConnectSuccess(bltDev);
                    }
                }
                break;
            case Handler_What.CONNECT_FAIL:
                connectBlueToothInfo.setState(STATE_CONNECT_FAIL);
                if (onBltConnectListener != null) {
                    onBltConnectListener.onConnectFail();
                }
                break;
            case Handler_What.SEARCH_ING:
                if (onBltSearchListener != null) {
                    onBltSearchListener.onSearchIng();
                }
                break;
            case Handler_What.SEARCH_SUCCESS:
                if (onBltSearchListener != null) {
                    onBltSearchListener.onSearchSuccess(deviceList);
                }
                break;
            case Handler_What.SEARCH_SUCCESS_ONE:
                BlueToothInfo blueToothInfo = (BlueToothInfo) msg.obj;
                if(connectBlueToothInfo.getState()==STATE_CONNECT_SUCCESS)
                {
                    if(blueToothInfo.getAddress().equals(connectBlueToothInfo.getAddress()))
                    {
                        blueToothInfo.setState(STATE_CONNECT_SUCCESS);
                    }
                }
                if (onBltSearchListener != null) {
                    onBltSearchListener.onSearchSuccessOne(blueToothInfo);
                }
                break;
            case Handler_What.SEARCH_FAIL:
                if (onBltSearchListener != null) {
                    onBltSearchListener.onSearchFail();
                }
                break;
        }
    }

    //endregion

    //region=========================回调以及接口==========================
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private class MyLeScanCallback implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            handlerLeScan(device, rssi, scanRecord);
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Tools.printLog("接收到广播");
            handlerBlueBrodcast(context,intent);
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handlerMsg(msg);
        }
    }

    private void sendMessage(int what, Object obj) {
        Message message = mHandler.obtainMessage();
        message.what = what;
        message.obj = obj;
        mHandler.sendMessage(message);
    }

    public interface OnBltSearchListener {
        public void onSearchIng();

        public void onSearchSuccessOne(BlueToothInfo blueToothInfo);

        public void onSearchSuccess(ArrayList<BlueToothInfo> blueToothInfoList);

        public void onSearchFail();
    }

    public interface OnBltConnectListener {
        public void onConnectIng();

        public void onConnectSuccess(BluetoothDevice bltDev);

        public void onConnectFail();

    }

    public interface OnBltServerListener{
        public void onCreateIng();

        public void onCreateSuccess();

        public void onCreateFail();

        public void onReceiving(byte dataByte);
    }

    public interface OnBltDataListener{
        public void onReceive(byte[] bytes);
    }

    public OnBltSearchListener getOnBltSearchListener() {
        return onBltSearchListener;
    }

    public void setOnBltSearchListener(OnBltSearchListener onBltSearchListener) {
        this.onBltSearchListener = onBltSearchListener;
    }

    public OnBltConnectListener getOnBltConnectListener() {
        return onBltConnectListener;
    }

    public void setOnBltConnectListener(OnBltConnectListener onBltConnectListener) {
        this.onBltConnectListener = onBltConnectListener;
    }

    public OnBltServerListener getOnBltServerListener() {
        return onBltServerListener;
    }

    public void setOnBltServerListener(OnBltServerListener onBltServerListener) {
        this.onBltServerListener = onBltServerListener;
    }

    public static OnBltDataListener onBltDataListener ;

    public static void setOnBltDataListener(OnBltDataListener onBltDataListener) {
        BluetoothServer.onBltDataListener = onBltDataListener;
    }

    public static void cleanDataListener()
    {
        BluetoothServer.onBltDataListener = null;
    }

    //endregion

    public static int getServerState()
    {
        return server_state;
    }

    public static BlueToothInfo getConnectDevice()
    {
        return connectBlueToothInfo ;
    }

    public void destroy()
    {
        try {
            context.unregisterReceiver(mBroadcastReceiver);
        }
        catch (Exception e)
        {

        }
    }
}

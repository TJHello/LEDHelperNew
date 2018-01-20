package com.tjbaobao.ledhelper.activity.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tjbaobao.framework.base.BaseAdapter;
import com.tjbaobao.framework.util.Tools;
import com.tjbaobao.ledhelper.R;
import com.tjbaobao.ledhelper.base.AppActivity;
import com.tjbaobao.ledhelper.entity.info.BlueToothInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class DeviceListActivityBackup extends AppActivity implements OnClickListener {

    private BluetoothAdapter mBluetoothAdapter;
    private TextView tv_search;
    private ListView listView;
    private MyAdapter adapter;
    private ArrayList<BlueToothInfo> deviceList = new ArrayList<BlueToothInfo>();
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mBluetoothSocket = null;
    private EditText et_data;
    private Button bt_sent;
    private String address = null;
    private RadioButton rb_roop, rb_one;
    private EditText et_loop_time;
    private String loopTime = "0";
    private String sendData = "";



    @Override
    protected void onInitValues(Bundle savedInstanceState) {

    }

    @Override
    protected void onInitView() {
        setContentView(R.layout.device_test_layout);
        setTempleTitle("我的设备");
        listView = (ListView) this.findViewById(R.id.listView);
        adapter = new MyAdapter(context, deviceList, R.layout.device_list_item);
        listView.setAdapter(adapter);
        et_data = (EditText) this.findViewById(R.id.et_data);
        bt_sent = (Button) this.findViewById(R.id.bt_sent);
        bt_sent.setOnClickListener(this);
        rb_roop = (RadioButton) this.findViewById(R.id.rb_loop);
        rb_one = (RadioButton) this.findViewById(R.id.rb_one);
        et_loop_time = ((EditText) this.findViewById(R.id.et_loop_time));
    }

    @Override
    protected void onInitBaseView() {

    }

    @Override
    protected void onLoadData() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        //获取BluetoothAdapter
        if (bluetoothManager != null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        getBltList();
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);//搜索发现设备
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式改变了
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生了变化
        context.registerReceiver(searchDevices, intent);
        startSearthBltDevice(context);
    }

    @Override
    protected void onTemplateRightClick() {
        startSearthBltDevice(context);
    }

    /**
     * 蓝牙接收广播
     */
    private BroadcastReceiver searchDevices = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            Object[] lstName = b.keySet().toArray();

            // 显示所有收到的消息及其细节
            for (int i = 0; i < lstName.length; i++) {
                String keyName = lstName[i].toString();
                Tools.printLog(keyName + ">>>" + String.valueOf(b.get(keyName)));
            }
            BluetoothDevice device;
            // 搜索发现设备时，取得设备的信息；注意，这里有可能重复搜索同一设备
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BlueToothInfo info = new BlueToothInfo();
                info.setName(device.getName());
                info.setAddress(device.getAddress());
                deviceList.add(info);
                adapter.notifyDataSetChanged();
                Tools.printLog("发现设备" + device.getName());
            }
            //状态改变时
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState())
                {
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
    };

    /**
     * 反注册广播取消蓝牙的配对
     *
     * @param context
     */
    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(searchDevices);
        if (mBluetoothAdapter != null)
            mBluetoothAdapter.cancelDiscovery();
    }

    /**
     * 判断是否支持蓝牙，并打开蓝牙
     * 获取到BluetoothAdapter之后，还需要判断是否支持蓝牙，以及蓝牙是否打开。
     * 如果没打开，需要让用户打开蓝牙：
     */
    public void checkBleDevice(Context context) {
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(enableBtIntent);
            }
        } else {
            Log.i("blueTooth", "该手机不支持蓝牙");
        }
    }

    /**
     * 搜索蓝牙设备
     * 通过调用BluetoothAdapter的startLeScan()搜索BLE设备。
     * 调用此方法时需要传入 BluetoothAdapter.LeScanCallback参数。
     * 因此你需要实现 BluetoothAdapter.LeScanCallback接口，BLE设备的搜索结果将通过这个callback返回。
     * <p/>
     * 由于搜索需要尽量减少功耗，因此在实际使用时需要注意：
     * 1、当找到对应的设备后，立即停止扫描；
     * 2、不要循环搜索设备，为每次搜索设置适合的时间限制。避免设备不在可用范围的时候持续不停扫描，消耗电量。
     * <p/>
     * 如果你只需要搜索指定UUID的外设，你可以调用 startLeScan(UUID[], BluetoothAdapter.LeScanCallback)方法。
     * 其中UUID数组指定你的应用程序所支持的GATT Services的UUID。
     * <p/>
     * 注意：搜索时，你只能搜索传统蓝牙设备或者BLE设备，两者完全独立，不可同时被搜索。
     */
    private boolean startSearthBltDevice(Context context) {
        //开始搜索设备，当搜索到一个设备的时候就应该将它添加到设备集合中，保存起来
        checkBleDevice(context);
        //如果当前发现了新的设备，则停止继续扫描，当前扫描到的新设备会通过广播推向新的逻辑
        //开始搜索

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter.startLeScan(new LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    BlueToothInfo info = new BlueToothInfo();
                    info.setName(device.getName());
                    info.setAddress(device.getAddress());
                    boolean isHave = false;
                    for (BlueToothInfo blueInfo : deviceList) {
                        if (blueInfo.getAddress().equals(info.getAddress())) {
                            isHave = true;
                        }
                    }
                    if (!isHave) {
                        deviceList.add(info);
                        adapter.notifyDataSetChanged();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        mBluetoothAdapter.stopLeScan(this);
                    }
                }
            });
        }
        //if(mBluetoothAdapter.startDiscovery())
        {
            Tools.printLog("开始搜索");
        }
        //这里的true并不是代表搜索到了设备，而是表示搜索成功开始。
        return true;
    }

    /**
     * 获得系统保存的配对成功过的设备，并尝试连接
     */
    public void getBltList() {
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
                //自动连接已有蓝牙设备
                //createBond(device, null);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 输入mac地址进行自动配对
     * 前提是系统保存了该地址的对象
     *
     * @param address
     */
    public void autoConnect(final String address) {
        loopTime = et_loop_time.getText().toString();
        sendData = et_data.getText().toString();
        new Thread() {
            public void run() {
                if (mBluetoothAdapter.isDiscovering()) mBluetoothAdapter.cancelDiscovery();
                BluetoothDevice btDev = mBluetoothAdapter.getRemoteDevice(address);
                connect(btDev);
            }

            ;
        }.start();
    }

    /**
     * 这个操作应该放在子线程中，因为存在线程阻塞的问题
     */
    public void createSocket() {
        //服务器端的bltsocket需要传入uuid和一个独立存在的字符串，以便验证，通常使用包名的形式
        BluetoothServerSocket bluetoothServerSocket = null;
        while (true) {
            try {
                bluetoothServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("com.tjbaobao.led", uuid);
                //注意，当accept()返回BluetoothSocket时，socket已经连接了，因此不应该调用connect方法。
                //这里会线程阻塞，直到有蓝牙设备链接进来才会往下走
                BluetoothSocket socket = bluetoothServerSocket.accept();
                if (socket != null) {
                    //回调结果通知
                    Message message = new Message();
                    message.what = 3;
                    message.obj = socket.getRemoteDevice();
                    handler.sendMessage(message);
                    //如果你的蓝牙设备只是一对一的连接，则执行以下代码
                    //bluetoothServerSocket.close();
                    //如果你的蓝牙设备是一对多的，则应该调用break；跳出循环
                    //break;
                }
            } catch (IOException e) {
                try {
                    bluetoothServerSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 尝试连接一个设备，子线程中完成，因为会线程阻塞
     *
     * @param btDev 蓝牙设备对象
     * @return
     */
    private void connect(BluetoothDevice btDev) {
        try {
            //通过和服务器协商的uuid来进行连接
            if (mBluetoothSocket == null)
                mBluetoothSocket = btDev.createRfcommSocketToServiceRecord(uuid);
            if (mBluetoothSocket != null)
                //全局只有一个bluetooth，所以我们可以将这个socket对象保存在appliaction中
                //通过反射得到bltSocket对象，与uuid进行连接得到的结果一样，但这里不提倡用反射的方法
                //mBluetoothSocket = (BluetoothSocket) btDev.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(btDev, 1);
                Log.d("blueTooth", "开始连接...");
            //在建立之前调用
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            //如果当前socket处于非连接状态则调用连接
            if (!mBluetoothSocket.isConnected()) {
                //你应当确保在调用connect()时设备没有执行搜索设备的操作。
                // 如果搜索设备也在同时进行，那么将会显著地降低连接速率，并很大程度上会连接失败。
                mBluetoothSocket.connect();
                //Tools.showToast("链接成功");
            }
            OutputStream outStream = mBluetoothSocket.getOutputStream();
            for (int i = 0; i < 10000 && rb_roop.isChecked() || (i < 1 && rb_one.isChecked()); i++) {
                long nowTime = System.currentTimeMillis();
                byte[] bytes = et_data.getText().toString().getBytes();
                outStream.write(bytes);
                outStream.flush();
                long myTime = Long.parseLong(et_loop_time.getText().toString());
                long endTime = System.currentTimeMillis();
                try {
                    if(endTime-nowTime>myTime)
                    {
                        Thread.sleep(myTime);
                    }
                    else
                    {
                        Thread.sleep(myTime-(endTime-nowTime));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            //outStream.close();
            Log.d("blueTooth", "已经链接");
            if (handler == null) return;
            //结果回调
            Message message = new Message();
            message.what = 4;
            message.obj = btDev;
            handler.sendMessage(message);
        } catch (Exception e) {
            Log.e("blueTooth", "...链接失败");
            try {
                mBluetoothSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleMessage(Message msg, int what, Object obj) {
        if (what == 3) {
            Tools.printLog("建立连接了");
        } else if (what == 4) {
            Tools.printLog("已经连接");
        }
        super.onHandleMessage(msg, what, obj);
    }

    public class MyAdapter extends BaseAdapter {
        public MyAdapter(Context context, ArrayList<?> arrayList, int resId) {
            super(context, arrayList, resId);
        }

        class Holder {
            TextView tv_name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(context).inflate(resId, parent, false);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            BlueToothInfo info = (BlueToothInfo) arrayList.get(position);
            holder.tv_name.setText(info.getName());
            convertView.setOnClickListener(new MyOnclickListener(info.getAddress()));
            return super.getView(position, convertView, parent);
        }

        public class MyOnclickListener implements OnClickListener {
            private String address;

            public MyOnclickListener(String address) {
                this.address = address;
            }

            @Override
            public void onClick(View v) {
                DeviceListActivityBackup.this.address = address;
                autoConnect(address);
            }
        }
    }

    /**
     * 尝试配对和连接
     *
     * @param btDev
     */
    public void createBond(BluetoothDevice btDev, Handler handler) {
        if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
            //如果这个设备取消了配对，则尝试配对
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                btDev.createBond();
            }
        } else if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
            //如果这个设备已经配对完成，则尝试连接
            connect(btDev);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(context);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sent:
                if (address == null) {
                    Tools.showToast("请先选择一个设备建立连接");
                    return;
                }
                autoConnect(address);
                break;
        }
    }
}

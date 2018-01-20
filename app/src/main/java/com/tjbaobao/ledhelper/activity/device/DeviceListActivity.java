package com.tjbaobao.ledhelper.activity.device;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tjbaobao.framework.base.BaseAdapter;
import com.tjbaobao.ledhelper.R;
import com.tjbaobao.ledhelper.base.AppActivity;
import com.tjbaobao.ledhelper.entity.info.BlueToothInfo;
import com.tjbaobao.ledhelper.utils.BluetoothServer;

import java.util.ArrayList;

public class DeviceListActivity extends AppActivity {

    private static final int REQUEST_BLT_PERMISSIONS = 11;

    private ArrayList<BlueToothInfo> blueToothInfoList = new ArrayList<>();
    private ListView listView ;
    private MyAdapter adapter ;
    private BluetoothServer bluetoothServer ;

    @Override
    protected void onInitValues(Bundle savedInstanceState) {

    }

    @Override
    protected void onInitView() {
        setContentView(R.layout.device_list_layout);
        listView = this.findViewById(R.id.listView);
        adapter = new MyAdapter(context,blueToothInfoList,R.layout.device_list_item);
        listView.setAdapter(adapter);
        bluetoothServer = BluetoothServer.getInstance(context);
        bluetoothServer.setOnBltSearchListener(new MyOnBltSearchListener());
        bluetoothServer.setOnBltConnectListener(new MyOnBltConnectListener());
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_BLT_PERMISSIONS);
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                //Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onInitBaseView() {
        setTempleTitle("设备列表");
        setTempleRightRes(R.drawable.ic_import_revoke);

    }

    @Override
    protected void onLoadData() {
        adapter.notifyDataSetChanged();
        bluetoothServer.search();
    }

    @Override
    protected void onTemplateLeftClick() {
        super.onTemplateLeftClick();
        activityBack();
    }

    @Override
    protected void onTemplateRightClick() {
        super.onTemplateRightClick();
        bluetoothServer.search();
    }

    private void activityBack()
    {
        if(BluetoothServer.getServerState()==BluetoothServer.STATE_CONNECT_SUCCESS)
        {
            setResult(RESULT_OK);
        }
        else
        {
            setResult(RESULT_CANCELED);
        }
        this.finish();
    }

    private class MyAdapter extends BaseAdapter
    {
        public MyAdapter(Context context, ArrayList<?> arrayList, int resId) {
            super(context, arrayList, resId);
        }

        class Holder {
            TextView tv_name,tv_state;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyAdapter.Holder holder;
            if (convertView == null) {
                holder = new MyAdapter.Holder();
                convertView = LayoutInflater.from(context).inflate(resId, parent, false);
                holder.tv_name = convertView.findViewById(R.id.tv_name);
                holder.tv_state = convertView.findViewById(R.id.tv_state);
                convertView.setTag(holder);
            } else {
                holder = (MyAdapter.Holder) convertView.getTag();
            }
            BlueToothInfo info = (BlueToothInfo) arrayList.get(position);
            holder.tv_name.setText(info.getName().equals("")?info.getAddress():info.getName());
            if(info.getState()==BluetoothServer.STATE_CONNECT_ING)
            {
                holder.tv_state.setText("连接中");
            }
            else if(info.getState()==BluetoothServer.STATE_CONNECT_SUCCESS)
            {
                holder.tv_state.setText("连接成功");
            }
            else if(info.getState()==BluetoothServer.STATE_CONNECT_FAIL)
            {
                holder.tv_state.setText("连接失败");
            }
            else if(info.getState()==BluetoothServer.STATE_INIT)
            {
                holder.tv_state.setText("");
            }
            convertView.setOnClickListener(new MyAdapter.MyOnclickListener(info));
            return super.getView(position, convertView, parent);
        }

        public class MyOnclickListener implements View.OnClickListener {
            private BlueToothInfo blueToothInfo ;

            public MyOnclickListener(BlueToothInfo blueToothInfo) {
                this.blueToothInfo = blueToothInfo;
            }

            @Override
            public void onClick(View v) {
                if(blueToothInfo.getState()==BluetoothServer.STATE_CONNECT_SUCCESS)
                {
                    return ;
                }
                bluetoothServer.connect(blueToothInfo.getAddress());
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        activityBack();
    }

    private class MyOnBltSearchListener implements BluetoothServer.OnBltSearchListener
    {
        @Override
        public void onSearchIng() {
            blueToothInfoList.clear();
        }

        @Override
        public void onSearchSuccessOne(BlueToothInfo blueToothInfo) {
            blueToothInfoList.add(blueToothInfo);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onSearchSuccess(ArrayList<BlueToothInfo> blueToothInfoList) {

        }

        @Override
        public void onSearchFail() {

        }
    }

    private class MyOnBltConnectListener implements BluetoothServer.OnBltConnectListener
    {
        @Override
        public void onConnectIng() {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onConnectSuccess(BluetoothDevice bltDev) {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onConnectFail() {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothServer.destroy();
    }


}

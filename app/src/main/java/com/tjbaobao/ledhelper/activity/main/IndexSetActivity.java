package com.tjbaobao.ledhelper.activity.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tjbaobao.framework.base.BaseAdapter;
import com.tjbaobao.framework.util.BaseTimerTask;
import com.tjbaobao.framework.util.FileUtil;
import com.tjbaobao.framework.util.ImageUtil;
import com.tjbaobao.framework.util.Tools;
import com.tjbaobao.framework.util.ValueTools;
import com.tjbaobao.ledhelper.R;
import com.tjbaobao.ledhelper.activity.device.DeviceListActivity;
import com.tjbaobao.ledhelper.activity.index.AnimEditActivity;
import com.tjbaobao.ledhelper.base.AppActivity;
import com.tjbaobao.ledhelper.dialog.LoadingDialog;
import com.tjbaobao.ledhelper.dialog.YesNoDialog;
import com.tjbaobao.ledhelper.engine.anim.AnimComputeEngine;
import com.tjbaobao.ledhelper.entity.data.LedAnimData;
import com.tjbaobao.ledhelper.entity.data.LedControlInfo;
import com.tjbaobao.ledhelper.entity.data.LedReturnData;
import com.tjbaobao.ledhelper.entity.data.LedUpdateData;
import com.tjbaobao.ledhelper.entity.info.BlueToothInfo;
import com.tjbaobao.ledhelper.entity.ui.LEDAnimInfo;
import com.tjbaobao.ledhelper.entity.ui.LEDPXInfo;
import com.tjbaobao.ledhelper.entity.ui.LedAnimResInfo;
import com.tjbaobao.ledhelper.entity.ui.PoPMenuListInfo;
import com.tjbaobao.ledhelper.popwindow.MenuPopupWindow;
import com.tjbaobao.ledhelper.utils.AppConstantUtil;
import com.tjbaobao.ledhelper.utils.BluetoothServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class IndexSetActivity extends AppActivity {

	private TextView tv_device_name ;
	private View ll_device_info ;
	private static final int REQUEST_DEVICE = 10;

	private SwipeMenuListView listView;
	private MyAdapter adapter ;
	private ArrayList<LEDAnimInfo> ledAnimInfoList = new ArrayList<>();

	private static String[] PERMISSIONS_STORAGE = {
			"android.permission.READ_EXTERNAL_STORAGE",
			"android.permission.WRITE_EXTERNAL_STORAGE",
	};
	private static final int REQUEST_EXTERNAL_STORAGE = 1;

	private int ledHelperType ;
	@Override
	protected void onInitValues(Bundle savedInstanceState) {
		ledHelperType = this.getIntent().getIntExtra("ledHelperType",IndexActivity.SET_TYPE_IMAGE);
		try {
			//检测是否有写的权限
			int permission = ActivityCompat.checkSelfPermission(activity,
					"android.permission.WRITE_EXTERNAL_STORAGE");
			if (permission != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onInitView() {
		setContentView(R.layout.activity_main);

		tv_device_name = this.findViewById(R.id.tv_device_name);
		listView = this.findViewById(R.id.listView);
		adapter = new MyAdapter(context,ledAnimInfoList,R.layout.led_anim_local_list_item_layout);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new MyOnItemClickListener());
		ll_device_info = this.findViewById(R.id.ll_device_info);
		ll_device_info.setOnClickListener(this);
		View bt_connect = this.findViewById(R.id.bt_connect);
		bt_connect.setOnClickListener(this);

		checkDeviceState();

		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				if(menu.getViewType()==IndexActivity.SET_TYPE_ANIM)
				{
					return ;
				}
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				openItem.setBackground(new ColorDrawable(getColorById(R.color.app_theme_color)));
				// set item width
				openItem.setWidth(Tools.dpToPx(90));
				// set item title
				openItem.setIcon(R.drawable.ic_edit);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(Tools.dpToPx(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_trash);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		listView.setMenuCreator(creator);

		listView.setOnMenuItemClickListener(new MyOnMenuItemClickListener());
	}

	@Override
	protected void onInitBaseView() {
		setTempleRightRes(R.drawable.ic_new);
		setTempleLeftRes(R.drawable.ic_back);
		switch (ledHelperType)
		{
			case IndexActivity.SET_TYPE_IMAGE:
				setTempleTitle("图像");
				break;
			case IndexActivity.SET_TYPE_TEXT:
				setTempleTitle("文字");
				break;
			case IndexActivity.SET_TYPE_ANIM:
				setTempleTitle("动画");
				hideTempleRight();
				break;
		}
	}


	@Override
	protected void onLoadData() {
		ledAnimInfoList.clear();
		File filePath = new File(AppConstantUtil.getAppConfigPath());
		if(filePath.exists())
		{
			for(File file:filePath.listFiles())
			{
				if(file.getName().contains("config"))
				{
					String path = AppConstantUtil.getAppConfigPath()+file.getName();
					LEDAnimInfo info = (LEDAnimInfo) ValueTools.getObject(path);
					if(info!=null)
					{
						if(info.getLedHelperType()==-1)
						{
							info.setLedHelperType(IndexActivity.SET_TYPE_IMAGE);
						}
						if(info.getCode()==null)
						{
							FileUtil.delFileIfExists(path);
							continue;
						}
						info.setConfigPath(path);
						if(info.getLedHelperType()==ledHelperType)
						{
							ledAnimInfoList.add(info);
						}
					}
				}
			}
		}
		InputStream inputStream = Tools.getAssetsInputSteam("config_local.config");
		List<LedAnimResInfo> infoList = new Gson().fromJson(FileUtil.Reader.readTextByInputSteam(inputStream),new TypeToken<List<LedAnimResInfo>>() {
		}.getType());
		if(infoList!=null)
		{
			for(LedAnimResInfo info:infoList)
			{
				String code = info.getCode();
				File file = new File(AppConstantUtil.getAppConfigPath()+code+".config");
				if(!file.exists())
				{
					FileUtil.createFolder(AppConstantUtil.getAppConfigPath()+code+File.separator);
					String indexImagePath = AppConstantUtil.getAppConfigPath()+code+".png";
					copyAssetsToDisk(code+File.separator+info.getIndexImage(),indexImagePath);

					LEDAnimInfo animInfo = new LEDAnimInfo();
					animInfo.initLEDPxs(info.getWidth(),info.getHeight());

					animInfo.setTitle(info.getTitle());
					animInfo.setText(info.getText());
					animInfo.setLedHelperType(info.getLedHelperType());
					animInfo.setAnimType(info.getAnimType());
					animInfo.setCode(info.getCode());
					animInfo.setWidth(info.getWidth());
					animInfo.setHeight(info.getHeight());
					animInfo.setImages(info.getImages());
					animInfo.setIndexImage(indexImagePath);
					animInfo.setColNum(info.getWidth());
					animInfo.setRowNum(info.getHeight());
					animInfo.setConfigPath(AppConstantUtil.getAppConfigPath()+code+".config");
					for(String url:info.getImages())
					{
						Bitmap bitmap = ImageUtil.getBitmapByAssets(code+File.separator+url);
						textToData(animInfo,bitmap);
					}

					ValueTools.saveObject(AppConstantUtil.getAppConfigPath()+code+".config",animInfo);
					if(animInfo.getLedHelperType()==ledHelperType)
					{
						ledAnimInfoList.add(animInfo);
					}
				}
			}
		}
		adapter.notifyDataSetChanged();
	}

	private void copyAssetsToDisk(String pathAssets,String path)
	{
		InputStream inputStream = Tools.getAssetsInputSteam(pathAssets);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
			byte[] bytes = new byte[1024];
			try {
				while (inputStream.read(bytes)>0)
                {
					fileOutputStream.write(bytes);
                }
                inputStream.close();
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onTemplateLeftClick() {
		super.onTemplateLeftClick();
		this.finish();
	}

	@Override
	protected void onTemplateRightClick() {
		if(adapter.getCount()>=16)
		{
			Tools.showToast("存储空间已满，请清空后再增加");
		}
		else
		{
			Intent intent = new Intent(IndexSetActivity.this, AnimEditActivity.class);
			intent.putExtra("ledHelperType", ledHelperType);
			startActivity(intent);
		}
	}

	private void checkDeviceState()
	{
		BlueToothInfo blueToothInfo = BluetoothServer.getConnectDevice();
		if(blueToothInfo.getState()==BluetoothServer.STATE_CONNECT_SUCCESS)
		{
			tv_device_name.setText(blueToothInfo.getName());
			ll_device_info.setVisibility(View.GONE);
		}
		else
		{
			tv_device_name.setText("设备未连接");
		}
	}

	private LoadingDialog loadingDialog = null ;
	private class MyAdapter extends BaseAdapter
	{
		int itemWidth = 0;
		public MyAdapter(Context context, ArrayList<?> arrayList, int resId) {
			super(context, arrayList, resId);
			itemWidth = Tools.dpToPx(85);
		}

		private class Holder
		{
			private ImageView iv_image ;
			private TextView tv_title ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Holder holder ;
			if(convertView==null)
			{
				holder = new Holder();
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.led_anim_local_list_item_layout,null);
				holder.iv_image = convertView.findViewById(R.id.iv_image);
				holder.tv_title = convertView.findViewById(R.id.tv_title);
				convertView.setTag(holder);
			}
			else
			{
				holder = (Holder) convertView.getTag();
			}
			LEDAnimInfo info = ledAnimInfoList.get(position);
			info.setPosition(position+1);
			String imagePath = info.getIndexImage();
			Glide.with(context)
					.load("file://"+imagePath)
					.into(holder.iv_image);
			holder.tv_title.setText(info.getTitle());

			if(info.isSelect())
			{
				convertView.setBackgroundColor(Color.parseColor("#1101b1dd"));
			}
			else
			{
				convertView.setBackgroundColor(Color.parseColor("#ffffff"));
			}
			return convertView;
		}

		private class MenuOnclickListener implements View.OnClickListener
		{
			private LEDAnimInfo ledAnimInfo ;
			private MenuPopupWindow menuPopupWindow ;
			private ArrayList<PoPMenuListInfo> menuInfoList = new ArrayList<>();
			public MenuOnclickListener(LEDAnimInfo ledAnimInfo) {
				this.ledAnimInfo = ledAnimInfo;
				menuInfoList.add(new PoPMenuListInfo(R.drawable.ic_rate_2,"收藏"));
				if(ledAnimInfo.getPosition()!=0)
				{
					menuInfoList.add(new PoPMenuListInfo(R.drawable.led_anim_play,"播放"));
				}
			}

			@Override
			public void onClick(View iv_menu) {
				menuPopupWindow = new MenuPopupWindow(context, iv_menu);
				menuPopupWindow.setData(menuInfoList);
				menuPopupWindow.showAsDropDown(iv_menu, -Tools.dpToPx(95), 0);
				menuPopupWindow.setOnMenuClickListener(new MenuPopupWindow.OnMenuClickListener() {
					@Override
					public void onMenuClick(int position) {
						if(position==0)
						{
							collection(ledAnimInfo);
						}
						else if(position==1)
						{
//							playAnimSub(ledAnimInfo);
						}

					}
				});
			}
			private void collection(final LEDAnimInfo ledAnimInfo)
			{
				if(loadingDialog==null)
				{
					loadingDialog = new LoadingDialog(context);
				}
				if(BluetoothServer.getServerState()==BluetoothServer.STATE_CONNECT_SUCCESS)
				{
					loadingDialog.show();
					new Thread(new Runnable() {
						@Override
						public void run() {
							int fpsNum = AnimComputeEngine.getFPSNum(ledAnimInfo.getAnimType(),ledAnimInfo.getRowNum(),ledAnimInfo.getColNum());
							byte[] bytes = BluetoothServer.writeAndRead(LedControlInfo.getUpdateControl(LedControlInfo.TYPE_SCREEN_1024,
									ledAnimInfo.getPosition(),
									fpsNum,
									LedControlInfo.DEF_1,
									LedControlInfo.DEF_SPEED
									).getBytesData()
							);
							final LedReturnData ledReturnData = LedReturnData.utilData(bytes);
							if(ledReturnData.getResultType()==ledReturnData.OK)
							{
								sendAnimSub(ledAnimInfo,fpsNum);
							}
							else
							{
								IndexSetActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if(ledReturnData.getResultComment()==LedReturnData.UPDATE_OUT_TIME)
										{
											Tools.showToast("请求超时");
										}
										else
										{
											Tools.showToast("发生错误，请重新尝试");
										}
										loadingDialog.dismiss();
									}
								});

							}
							BluetoothServer.cleanDataListener();
						}
					}).start();
				}
				else
				{
					Tools.showToast("请先连接设备");
				}
			}
			boolean isError = false;
			private void sendAnimSub(LEDAnimInfo ledAnimInfo,int fpsNum)
			{
				AnimComputeEngine animComputeEngine = new AnimComputeEngine(0,ledAnimInfo.getRowNum(),0,ledAnimInfo.getColNum());
				byte[] result = BluetoothServer.writeAndRead(new LedUpdateData(ledAnimInfo.getLedPxs(),0).getBytesData());
				LedReturnData ledReturnData = LedReturnData.utilData(result);
				if(ledReturnData.getResultType()!=ledReturnData.OK)
				{
					isError = true;
					IndexSetActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Tools.showToast("发生错误，请重新尝试");
						}
					});
				}
				if(isError)
				{
					return ;
				}
				LEDAnimInfo ledAnimInfoBase = new LEDAnimInfo().copyLEDAnim(ledAnimInfo) ;
				LEDAnimInfo ledAnimInfoTemp = new LEDAnimInfo() ;
				ledAnimInfoTemp.copyLEDAnim(ledAnimInfo);
				for(int round=1;round<fpsNum;round++)
				{
					for(int i=0;i<ledAnimInfo.getRowNum();i++)
					{
						for(int j=0;j<ledAnimInfo.getColNum();j++)
						{
							LEDPXInfo pxInfo = ledAnimInfoBase.getLedPxs()[i][j];
							AnimComputeEngine.AnimInfo animInfo =animComputeEngine.toAnimByType(ledAnimInfo.getAnimType(), i, j,pxInfo.getBrighter(),round);
							pxInfo.setBrighter(animInfo.getBrighter());
							ledAnimInfoTemp.getLedPxs()[ animInfo.getX()][animInfo.getY()].copy(pxInfo);
						}
					}
					ledAnimInfoBase.copyLEDAnim(ledAnimInfoTemp);
					result = BluetoothServer.writeAndRead(new LedUpdateData(ledAnimInfoTemp.getLedPxs(),round).getBytesData());
					ledReturnData = LedReturnData.utilData(result);
					if(ledReturnData.getResultType()!=ledReturnData.OK)
					{
						isError = true;
						IndexSetActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Tools.showToast("发生错误，请重新尝试");
							}
						});
						break;
					}
					else
					{
						isError = false;
					}
				}
				if(!isError) {
					ledAnimInfo.setCollection(true);
					String configPath = AppConstantUtil.getAppConfigPath()+ ledAnimInfo.getCode()+".config";
					ValueTools.saveObject(configPath,ledAnimInfo);
				}
				BluetoothServer.cleanDataListener();
				IndexSetActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						loadingDialog.dismiss();
						if(!isError) {
							Tools.showToast("收藏成功");
							adapter.notifyDataSetChanged();
						}
					}
				});
			}


		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			LEDAnimInfo info = ledAnimInfoList.get(position);
			return info.getLedHelperType();
		}
	}

	private void delete()
	{
		new YesNoDialog(context,"确认清除所有内容吗?"){
			@Override
			public void onContinueClick() {
				super.onContinueClick();
				for(LEDAnimInfo ledAnimInfo:ledAnimInfoList)
				{
					FileUtil.delFileIfExists(ledAnimInfo.getConfigPath());
					FileUtil.delFileIfExists(ledAnimInfo.getIndexImage());
				}
				ledAnimInfoList.clear();
				adapter.notifyDataSetChanged();
				Tools.showToast("清除所有内容成功！");
			}

			@Override
			public void onCancel() {
				super.onCancel();
			}
		}.show();
	}

	private MyTimerTask timerTask ;

	private class MyOnItemClickListener implements AdapterView.OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			LEDAnimInfo info = ledAnimInfoList.get(position);
			for(LEDAnimInfo infoTemp:ledAnimInfoList)
			{
				infoTemp.setSelect(false);
			}
			if(!info.isSelect())
			{
				info.setSelect(true);
				adapter.notifyDataSetChanged();
				if(timerTask!=null)
				{
					timerTask.stopTimer();
				}
				timerTask = new MyTimerTask(info);
				timerTask.startTimer(0,1000/info.getSpeed());
			}
		}

		private void playAnimSub(final LEDAnimInfo ledAnimInfo)
		{
			if(loadingDialog==null)
			{
				loadingDialog = new LoadingDialog(context);
			}
			if(BluetoothServer.getServerState()==BluetoothServer.STATE_CONNECT_SUCCESS)
			{
				loadingDialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						byte[] bytes = BluetoothServer.writeAndRead(
								LedControlInfo.getBuiltControl(LedControlInfo.TYPE_SCREEN_1024,LedControlInfo.TYPE_RUN_BUILT,ledAnimInfo.getPosition(),LedControlInfo.DEF_SPEED)
										.getBytesData()
						);
						final LedReturnData ledReturnData = LedReturnData.utilData(bytes);
						if(ledReturnData.getResultType()==ledReturnData.OK)
						{
							IndexSetActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									loadingDialog.dismiss();
								}
							});
						}
						else
						{
							IndexSetActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if(ledReturnData.getResultComment()==LedReturnData.UPDATE_OUT_TIME)
									{
										Tools.showToast("请求超时");
									}
									else
									{
										Tools.showToast("发生错误，请重新尝试");
									}
									ledAnimInfo.setSelect(false);
									loadingDialog.dismiss();
									adapter.notifyDataSetChanged();
								}
							});
						}
					}
				}).start();
			}
			else
			{
				Tools.showToast("请先连接设备");
				ledAnimInfo.setSelect(false);
			}

		}
	}

	private class MyTimerTask extends BaseTimerTask
	{
		int round = 0;
		private LEDAnimInfo ledAnimInfo ;
		private LEDAnimInfo baseLedAnimInfo ;
		private AnimComputeEngine animComputeEngine;

		public MyTimerTask(LEDAnimInfo ledAnimInfo) {
			this.ledAnimInfo = new LEDAnimInfo();
			this.ledAnimInfo.copyLEDAnim(ledAnimInfo);
			baseLedAnimInfo = ledAnimInfo ;
			animComputeEngine = new AnimComputeEngine(0, ledAnimInfo.getWidth(), 0, ledAnimInfo.getHeight());
		}

		@Override
		public void run() {
			LEDAnimInfo ledAnimInfoTemp = new LEDAnimInfo() ;
			ledAnimInfoTemp.initLEDPxs(ledAnimInfo.getWidth(), ledAnimInfo.getHeight());
			ledAnimInfoTemp.setAnimType(ledAnimInfo.getAnimType());
			round++;
			for(int i=0;i<ledAnimInfo.getWidth();i++)
			{
				for(int j=0;j<ledAnimInfo.getHeight();j++)
				{
					if(ledAnimInfoTemp.getLedHelperType()!=IndexActivity.SET_TYPE_ANIM)
					{
						LEDPXInfo pxInfo = ledAnimInfo.getLedPxs()[i][j];
						if(pxInfo==null)
						{
							continue;
						}
						if(pxInfo.getColor()==LEDPXInfo.ColorOn)
						{
							AnimComputeEngine.AnimInfo animInfo =animComputeEngine.toAnimByType(ledAnimInfo.getAnimType(), i, j,pxInfo.getBrighter(),round);
							pxInfo.setBrighter(animInfo.getBrighter());
							ledAnimInfoTemp.getLedPxs()[ animInfo.getX()][animInfo.getY()]=pxInfo;
						}
					}
					else
					{
						LEDPXInfo pxInfo = ledAnimInfoTemp.getLedPxFpsList().get(round%3)[i][j];
						ledAnimInfoTemp.getLedPxs()[i][j]=pxInfo;
					}
				}
			}
			ledAnimInfo.setLedPxs(ledAnimInfoTemp.getLedPxs());
			if(BluetoothServer.getServerState()==BluetoothServer.STATE_CONNECT_SUCCESS)
			{
				byte[] bytes = BluetoothServer.writeAndRead(new LedAnimData(ledAnimInfo.getLedPxs()).getBytesData());
				final LedReturnData ledReturnData = LedReturnData.utilData(bytes);
				if(ledReturnData.getResultType()==ledReturnData.OK)
				{
					IndexSetActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							baseLedAnimInfo.setSelect(false);
							adapter.notifyDataSetChanged();
						}
					});
					stopTimer();
				}
				else
				{
					IndexSetActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(ledReturnData.getResultComment()==LedReturnData.UPDATE_OUT_TIME)
							{
								Tools.showToast("请求超时");
							}
							else
							{
								Tools.showToast("发生错误，请重新尝试");
							}
							baseLedAnimInfo.setSelect(false);
							adapter.notifyDataSetChanged();
						}
					});
					stopTimer();
				}
			}
			else
			{
				IndexSetActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						baseLedAnimInfo.setSelect(false);
						adapter.notifyDataSetChanged();
						Tools.showToast("请先连接设备");
					}
				});
				stopTimer();
			}



		}
	}

	private class MyOnMenuItemClickListener implements SwipeMenuListView.OnMenuItemClickListener
	{

		@Override
		public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {

			final LEDAnimInfo info = ledAnimInfoList.get(position);
			if(index==0)
			{

				Intent intent = new Intent(IndexSetActivity.this,AnimEditActivity.class);
				intent.putExtra("ledHelperType",ledHelperType);
				intent.putExtra("ledAnimCode",info.getCode());
				startActivity(intent);
			}
			else
			{
				new YesNoDialog(context,"确认删除该内容吗?"){
					@Override
					public void onContinueClick() {
						super.onContinueClick();
						FileUtil.delFileIfExists(info.getConfigPath());
						FileUtil.delFileIfExists(info.getIndexImage());
						ledAnimInfoList.remove(position);
						adapter.notifyDataSetChanged();
						Tools.showToast("删除成功！");
					}
				}.show();

			}
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId())
		{
			case R.id.ll_device_info:case R.id.bt_connect:
				startActivityForResult(DeviceListActivity.class,REQUEST_DEVICE);
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_DEVICE)
		{
			if(resultCode==RESULT_OK)
			{
				checkDeviceState();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		onLoadData();
	}

	private void textToData(LEDAnimInfo ledAnimInfo,Bitmap bitmap)
	{
		int white = Color.WHITE;
		LEDPXInfo[][] ledpxInfos = new LEDPXInfo[ledAnimInfo.getRowNum()][ledAnimInfo.getColNum()];
		for(int i=0;i<ledAnimInfo.getRowNum();i++)
		{
			for(int j=0;j<ledAnimInfo.getColNum();j++)
			{
				ledpxInfos[i][j]=new LEDPXInfo();
				if(bitmap.getPixel(i,j)==white)
				{
					ledpxInfos[i][j].setColor(LEDPXInfo.ColorOn);
					ledAnimInfo.getLedPxs()[i][j].setColor(LEDPXInfo.ColorOn);
				}
				else
				{
					ledpxInfos[i][j].setColor(LEDPXInfo.ColorOff);
					ledAnimInfo.getLedPxs()[i][j].setColor(LEDPXInfo.ColorOff);
				}
			}
		}
		ledAnimInfo.addLedPxFps(ledpxInfos);
	}
}

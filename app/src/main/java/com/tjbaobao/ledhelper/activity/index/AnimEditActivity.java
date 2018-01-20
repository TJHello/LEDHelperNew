package com.tjbaobao.ledhelper.activity.index;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tjbaobao.framework.base.BaseAdapter;
import com.tjbaobao.framework.util.ConstantUtil;
import com.tjbaobao.framework.util.ImageUtil;
import com.tjbaobao.framework.util.Tools;
import com.tjbaobao.framework.util.ValueTools;
import com.tjbaobao.ledhelper.R;
import com.tjbaobao.ledhelper.activity.main.IndexActivity;
import com.tjbaobao.ledhelper.base.AppActivity;
import com.tjbaobao.ledhelper.dialog.SaveDialog;
import com.tjbaobao.ledhelper.engine.anim.AnimComputeEngine;
import com.tjbaobao.ledhelper.engine.anim.AnimComputeEngine.AnimTypeEnum;
import com.tjbaobao.ledhelper.entity.data.LedControlInfo;
import com.tjbaobao.ledhelper.entity.data.LedReturnData;
import com.tjbaobao.ledhelper.entity.enums.EditToolsEnum;
import com.tjbaobao.ledhelper.entity.ui.LEDAnimInfo;
import com.tjbaobao.ledhelper.entity.ui.LEDPXInfo;
import com.tjbaobao.ledhelper.entity.ui.Tab;
import com.tjbaobao.ledhelper.ui.ClickTabbar;
import com.tjbaobao.ledhelper.ui.ImageOnOffView;
import com.tjbaobao.ledhelper.ui.LEDHelperView;
import com.tjbaobao.ledhelper.ui.LEDHelperView.OnLEDLinstener;
import com.tjbaobao.ledhelper.ui.OnTabListener;
import com.tjbaobao.ledhelper.ui.PoPBL02Window;
import com.tjbaobao.ledhelper.ui.ProgressbarView;
import com.tjbaobao.ledhelper.utils.AppConstantUtil;
import com.tjbaobao.ledhelper.utils.BluetoothServer;

import java.util.ArrayList;
import java.util.UUID;

public class AnimEditActivity extends AppActivity {

	private ImageOnOffView iv_switch ;
	private ProgressbarView progressbarView;
	private EditText et_row,et_col;
	private LEDHelperView ledHelperView;
	private int nowRow= 0,nowCol = 0;

	//=====下拉框--动画类型选择
	private TextView tv_animName ;
	private ImageView iv_animSwitch;
	private MyAdapter adapter ;
	private PoPBL02Window popBLWindow ;
	private EditText et_fps;
	private EditText et_text ;
	private Button bt_apply ;
	private ArrayList<AnimTypeEnum> animTypeList = new ArrayList<AnimTypeEnum>();
	//=====动画
	private int animType = AnimComputeEngine.AnimType.LEFT_TO_RIGHT;
	private ImageView iv_animPlay ,iv_animStop;
	private boolean isPlay = false;

	private int ledHelperType ;
	//====工具
	private ClickTabbar imgListLayout;

	private String configPath = null;
	private String ledAnimCode = null;


	@Override
	protected void onInitValues(Bundle savedInstanceState) {
		ledHelperType = this.getIntent().getIntExtra("ledHelperType", IndexActivity.SET_TYPE_IMAGE);
		ledAnimCode =  this.getIntent().getStringExtra("ledAnimCode");
		if(ledAnimCode==null)
		{
			ledAnimCode = UUID.randomUUID().toString();

		}
		configPath = AppConstantUtil.getAppConfigPath()+ ledAnimCode+".config";
	}

	@Override
	protected void onInitView() {
		setContentView(R.layout.anim_edit_activity_layout);
		et_row = (EditText) this.findViewById(R.id.et_row);
		et_col = (EditText) this.findViewById(R.id.et_col);
		progressbarView = (ProgressbarView) this.findViewById(R.id.progressbarView);
		iv_switch = (ImageOnOffView) this.findViewById(R.id.iv_switch);
		ledHelperView = (LEDHelperView) this.findViewById(R.id.ledHelperView);
		tv_animName= (TextView) this.findViewById(R.id.tv_animName);
		iv_animSwitch = (ImageView) this.findViewById(R.id.iv_animSwitch);
		iv_animPlay = (ImageView) this.findViewById(R.id.iv_animPlay);
		iv_animStop = (ImageView) this.findViewById(R.id.iv_animStop);
		et_fps = this.findViewById(R.id.et_fps);
		imgListLayout = (ClickTabbar) this.findViewById(R.id.imgClickTabbar);
		et_text = this.findViewById(R.id.et_text);
		bt_apply = this.findViewById(R.id.bt_apply);
		bt_apply.setOnClickListener(this);
		//添加监听器
		ledHelperView.setOnLedLinstener(new MyOnLedLinstener());
		iv_switch.setOnCheckedLinstener(new MyOnCheckedLinstener());
		progressbarView.setOnProgressListener(new MyOnProgressListener());
		et_row.addTextChangedListener(new MyTextWatcher());
		et_col.addTextChangedListener(new MyTextWatcher());
		iv_animSwitch.setOnClickListener(this);
		tv_animName.setOnClickListener(this);
		iv_animStop.setOnClickListener(this);
		iv_animPlay.setOnClickListener(this);
		imgListLayout.setOnTabListener(new MyOnTabListener());
		adapter = new MyAdapter(context, animTypeList, R.layout.anim_edit_activity_animtype_item);

		View ll_text = this.findViewById(R.id.ll_text);
		if(ledHelperType==IndexActivity.SET_TYPE_TEXT)
		{
			ll_text.setVisibility(View.VISIBLE);
		}
		else
		{
			ll_text.setVisibility(View.GONE);
		}
		//初始化内容


		BluetoothServer bluetoothServer = BluetoothServer.getInstance(context);
	}

	@Override
	protected void onInitBaseView() {
		setTempleTitle("LED灯编辑");
		setTempleRightRes(R.drawable.ic_puzzle_save);
	}

	@Override
	protected void onLoadData() {
		LEDAnimInfo info = (LEDAnimInfo) ValueTools.getObject(configPath);
		if(info==null)
		{
			info =new LEDAnimInfo().initLEDPxs(32, 32);
			info.setCode(ledAnimCode);
			info.setConfigPath(configPath);
			info.setAnimType(0);
			info.setLedHelperType(ledHelperType);
		}
		if(info.getLedHelperType()==-1)
		{
			info.setLedHelperType(ledHelperType);
		}
		if(info.getLedHelperType()==IndexActivity.SET_TYPE_TEXT)
		{
			info.setTitle(info.getText());
		}
		et_text.setText(info.getText());
		ledHelperView.loadData(info);
		animType = info.getAnimType();
		ledHelperView.setAnimType(info.getAnimType());
		tv_animName.setText(AnimTypeEnum.getAnimName(info.getAnimType()));
		//添加动画列表
		AnimTypeEnum[] animTypeEnums = AnimTypeEnum.values();
		animTypeList.clear();
		for(AnimTypeEnum animTypeEnum:animTypeEnums)
		{
			animTypeList.add(animTypeEnum);
		}
		adapter.notifyDataSetChanged();
		imgListLayout.setDefaultTab(0);
		for(EditToolsEnum toolsEnum:EditToolsEnum.values())
		{
			Tab tab = new Tab();
			tab.setImgOffId(toolsEnum.imgOffId);
			tab.setImgOnId(toolsEnum.imgOnId);
			tab.setTag(toolsEnum.tag);
			tab.setText("");
			imgListLayout.addTab(tab);
		}
	}

	@Override
	protected void onTemplateRightClick() {
		save(ledHelperView.getLedAnimInfo());
		Tools.showToast("保存成功");
	}

	private void save(LEDAnimInfo ledAnimInfo)
	{
		final String pathImage = AppConstantUtil.getAppConfigPath()+ledAnimCode+".png";
		Bitmap bitmapIndex = ledHelperView.getBitmapIndex();
		ImageUtil.saveBitmap(bitmapIndex,pathImage);
		ledAnimInfo.setIndexImage(pathImage);
		ValueTools.saveObject(configPath,ledAnimInfo);
	}

	/**
	 * 将bitmap中的某种颜色值替换成新的颜色
	 * @param oldBitmap
	 * @param oldColor
	 * @param newColor
	 * @return
	 */
	public static Bitmap replaceBitmapColor(Bitmap oldBitmap,int oldColor,int newColor)
	{
		//相关说明可参考 http://xys289187120.blog.51cto.com/3361352/657590/
		Bitmap mBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
		//循环获得bitmap所有像素点
		int mBitmapWidth = mBitmap.getWidth();
		int mBitmapHeight = mBitmap.getHeight();
		int mArrayColorLengh = mBitmapWidth * mBitmapHeight;
		int[] mArrayColor = new int[mArrayColorLengh];
		int count = 0;
		for (int i = 0; i < mBitmapHeight; i++) {
			for (int j = 0; j < mBitmapWidth; j++) {
				int color = mBitmap.getPixel(j, i);
				//将颜色值存在一个数组中 方便后面修改
				if (color == oldColor) {
					mBitmap.setPixel(j, i, newColor);  //将白色替换成透明色
				}

			}
		}
		oldBitmap.recycle();
		return mBitmap;
	}

	private boolean isNullInfo(LEDAnimInfo ledAnimInfo)
	{
		LEDPXInfo[][] ledpxInfos = ledAnimInfo.getLedPxs();
		for(LEDPXInfo[] ledpxInfos1:ledpxInfos)
		{
			for(LEDPXInfo ledpxInfo:ledpxInfos1)
			{
				if(ledpxInfo.getColor()==LEDPXInfo.ColorOn)
				{
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected void onTemplateLeftClick() {

		LEDAnimInfo info = ledHelperView.getLedAnimInfo();
		if(!isNullInfo(info))
		{
			SaveDialog saveDialog = new SaveDialog(context,"是否保存？"){
				@Override
				public void onContinueClick() {
					super.onContinueClick();

				}

				@Override
				public void onSave(String text) {
					LEDAnimInfo info = ledHelperView.getLedAnimInfo();
					if(info.getLedHelperType()!=IndexActivity.SET_TYPE_TEXT)
					{
						info.setTitle(text);
					}
					save(info);
					AnimEditActivity.this.finish();
				}

				@Override
				public void onCancel() {
					super.onCancel();
					AnimEditActivity.this.finish();
				}
			};
			if(info.getLedHelperType()==IndexActivity.SET_TYPE_TEXT)
			{
				saveDialog.setShowEt(false);
			}
			else
			{
				saveDialog.setShowEt(true);
				saveDialog.setText(info.getTitle());
			}
			saveDialog.show();
		}
		else
		{
			AnimEditActivity.this.finish();
		}
	}

	/**
	 * LED动画组件监听器
	 * @author TJbaobao
	 * @time 2017年5月27日
	 */
	private class MyOnLedLinstener implements OnLEDLinstener
	{
		@Override
		public void onClick(int col, int row, LEDPXInfo info) {
			et_col.setText(col+"");
			et_row.setText(row+"");
		}
		@Override
		public void onAnimStop() {
			
		}
	}
	
	/**
	 * 开关选择监听器
	 * @author TJbaobao
	 * @time 2017年5月27日
	 */
	private class MyOnCheckedLinstener implements ImageOnOffView.OnCheckedLinstener
	{
		@Override
		public void onChecked(boolean checked) {
			ledHelperView.setPxColor(nowCol,nowRow, checked);
		}
	}
	
	/**
	 * 亮度调节监听器
	 * @author TJbaobao
	 * @time 2017年5月27日
	 */
	private class MyOnProgressListener implements ProgressbarView.OnProgressListener
	{
		@Override
		public void onChange(int progress) {
			ledHelperView.setPxBrighter(nowCol,nowRow, LEDPXInfo.maxBrighter*progress/ProgressbarView.MAX_PROGRESS);
		}

		@Override
		public void onUp() {
			
		}
		
	}
	
	/**
	 * 文本框编辑监听器
	 * @author TJbaobao
	 * @time 2017年5月27日
	 */
	private class MyTextWatcher implements TextWatcher
	{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			try {
				String strCol = et_col.getText().toString();
				String strRow = et_row.getText().toString();
				if(!strRow.equals("")&&!strCol.equals(""))
				{
					nowCol= Integer.parseInt(strCol);
					nowRow = Integer.parseInt(strRow);
					LEDPXInfo info = ledHelperView.getLedPxInfo( nowCol,nowRow);
					if(info!=null)
					{
						initView(info);
						et_row.setTextColor(getColorById(R.color.app_black_left));
						et_col.setTextColor(getColorById(R.color.app_black_left));
					}
					else
					{
						et_row.setTextColor(getColorById(R.color.app_red_off));
						et_col.setTextColor(getColorById(R.color.app_red_off));
					}
				}
			} catch (Exception e) {
			}
		}
		
	}
	
	/**
	 * 动画类型列表适配器
	 * @author TJbaobao
	 * @time 2017年5月27日
	 */
	private class MyAdapter extends BaseAdapter
	{
		int itemHeight = 0;
		public MyAdapter(Context context, ArrayList<?> arrayList, int resId) {
			super(context, arrayList, resId);
		}
		
		private class Holder{
			TextView tv_name ;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Holder holder = null;
			if(convertView==null)
			{
				holder = new Holder();
				convertView = LayoutInflater.from(context).inflate(resId, parent, false);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			}
			else
			{
				holder = (Holder) convertView.getTag();
			}
			convertView.measure(0, 0);
			itemHeight = convertView.getHeight();
			int size = adapter.getCount();
			if(size>5)
			{
				popBLWindow.setListMaxHeight(5*itemHeight);
			}
			AnimTypeEnum animTypeEnum = (AnimTypeEnum) arrayList.get(position);
			holder.tv_name.setText(animTypeEnum.animName );
			return super.getView(position, convertView, parent);
		}

		public int getItemHeight() {
			return itemHeight;
		}
		
	}
	
	/**
	 * 动画类型Item点击监听器
	 * @author TJbaobao
	 * @time 2017年5月27日
	 */
	private class MyOnItemClickLinstener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AnimTypeEnum animTypeEnum = animTypeList.get(position);
			tv_animName.setText(animTypeEnum.animName);
			animType = animTypeEnum.animType;
			ledHelperView.setAnimType(animType);
			popBLWindow.dismiss();
		}
	}
	
	private class MyOnTabListener implements OnTabListener
	{
		@Override
		public void onTabClick(String tag, int position) {
			if(tag.equals(EditToolsEnum.Paint.tag))
			{
				ledHelperView.setPaint(true);
			}
			else if(tag.equals(EditToolsEnum.Eraser.tag))
			{
				ledHelperView.setPaint(false);
			}
			else if(tag.equals(EditToolsEnum.Revoke.tag))
			{
				ledHelperView.revoke();
			}
			else if(tag.equals(EditToolsEnum.Forward.tag))
			{
				ledHelperView.forward();
			}
		}
	}
	/**
	 * 通过选取的LEDPX初始化组件
	 * @param info
	 */
	private void initView(LEDPXInfo info)
	{
		if(info.getColor()==LEDPXInfo.ColorOn)
		{
			iv_switch.setChecked(true);
		}
		else
		{
			iv_switch.setChecked(false);
		}
		progressbarView.setProgress(info.getBrighter()*ProgressbarView.MAX_PROGRESS/LEDPXInfo.maxBrighter);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		LEDAnimInfo info = ledHelperView.getLedAnimInfo();
		if(!isNullInfo(info))
		{
			SaveDialog saveDialog = new SaveDialog(context,"是否保存？"){
				@Override
				public void onContinueClick() {
					super.onContinueClick();

				}

				@Override
				public void onSave(String text) {
					LEDAnimInfo info = ledHelperView.getLedAnimInfo();
					if(info.getLedHelperType()!=IndexActivity.SET_TYPE_TEXT)
					{
						info.setTitle(text);
					}
					save(info);
					AnimEditActivity.this.finish();
				}

				@Override
				public void onCancel() {
					super.onCancel();
					AnimEditActivity.this.finish();
				}
			};
			if(info.getLedHelperType()==IndexActivity.SET_TYPE_TEXT)
			{
				saveDialog.setShowEt(false);
			}
			else
			{
				saveDialog.setShowEt(true);
				saveDialog.setText(info.getTitle());
			}
			saveDialog.show();
		}
		else
		{
			AnimEditActivity.this.finish();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus)
		{
			this.findViewById(R.id.ll_index).setPadding(0, getBarHeight(), 0, 0);
			et_row.setText(nowRow+"");
			et_col.setText(nowCol+"");
			popBLWindow = new PoPBL02Window(context,tv_animName.getWidth()+iv_animSwitch.getWidth(),-2);
			popBLWindow.setAdapter(adapter);
			popBLWindow.setOnItemClickListener(new MyOnItemClickLinstener());
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId())
		{
		case R.id.iv_animSwitch:
			popBLWindow.show(tv_animName, Gravity.BOTTOM);
			break;
		case R.id.iv_animPlay:
			play();
			break;
		case R.id.iv_animStop:
			//BluetoothServer.write(new LedControlInfo(LedControlInfo.TYPE_SCREEN_512,LedControlInfo.TYPE_RUN_STOP).getBytesData());
			ledHelperView.stopAnim();
			iv_animPlay.setImageResource(R.drawable.anim_edit_px_anim_play);
			isPlay = false;
			break;
		case R.id.tv_animName:
			popBLWindow.show(tv_animName, Gravity.BOTTOM);
			break;
			case R.id.bt_apply:
				textApply();
				break;
		}
	}

	private void play()
	{
		if(!isPlay)
		{
			new Thread(new Runnable() {
				@Override
				public void run() {
					final byte[] bytes = BluetoothServer.writeAndRead(LedControlInfo.getPlayControl(LedControlInfo.TYPE_SCREEN_1024).getBytesData());
					AnimEditActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							LedReturnData ledReturnData = LedReturnData.utilData(bytes);
							int fps = Integer.valueOf(et_fps.getText().toString());
							ledHelperView.startAnimTask(animType,fps);
							if(ledReturnData.getResultType()==ledReturnData.OK)
							{
								try{
									iv_animPlay.setImageResource(R.drawable.anim_edit_px_anim_pause);
									isPlay = true;
								}
								catch (Exception e)
								{
									Tools.printLog("未知错误");
								}

							}
							else
							{
								if(ledReturnData.getResultComment()==LedReturnData.UPDATE_OUT_TIME)
								{
									Tools.showToast("请求超时");
								}
								else
								{
									Tools.showToast("发生错误，请重新尝试");
								}
							}
						}
					});
				}
			}).start();
		}
		else
		{
			ledHelperView.stopAnimTask();
			iv_animPlay.setImageResource(R.drawable.anim_edit_px_anim_play);
			isPlay = false;
		}
	}

	private void textApply()
	{
		String text = et_text.getText().toString();
		LEDAnimInfo ledAnimInfo = ledHelperView.getLedAnimInfo();
		ledAnimInfo.setText(text);
		if(ledAnimInfo.getLedHelperType()==IndexActivity.SET_TYPE_TEXT)
		{
			ledAnimInfo.setTitle(text);
		}
		Bitmap bitmap = Bitmap.createBitmap(ledAnimInfo.getColNum(),ledAnimInfo.getRowNum(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawTextCentent(canvas,text,ledAnimInfo);
		ImageUtil.saveBitmap(bitmap, ConstantUtil.getFileImagePath()+ledAnimInfo.getCode()+".png");
		textToData(ledAnimInfo,bitmap);
		ledHelperView.invalidate();
	}

	private void drawTextCentent(Canvas canvas, String text,LEDAnimInfo ledAnimInfo)
	{
		int padding =2;
		Rect rect = new Rect(padding,padding,ledAnimInfo.getColNum()-padding,ledAnimInfo.getRowNum()-padding);//画一个矩形
		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize((ledAnimInfo.getColNum()-padding)/(text.length()));
		textPaint.setStyle(Paint.Style.FILL);
		float width = textPaint.measureText(text);
		while (width<rect.width())
		{
			textPaint.setTextSize(textPaint.getTextSize()+1);
			width = textPaint.measureText(text);
		}
		//该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center
		textPaint.setTextAlign(Paint.Align.CENTER);
		Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
		float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
		float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

		int baseLineY = (int) (rect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
		canvas.drawText(text,rect.centerX(),baseLineY,textPaint);
	}

	private void textToData(LEDAnimInfo ledAnimInfo,Bitmap bitmap)
	{
		int white = Color.WHITE;
		for(int i=0;i<ledAnimInfo.getRowNum();i++)
		{
			for(int j=0;j<ledAnimInfo.getColNum();j++)
			{
				if(bitmap.getPixel(i,j)==white)
				{
					ledAnimInfo.getLedPxs()[i][j].setColor(LEDPXInfo.ColorOn);
				}
				else
				{
					ledAnimInfo.getLedPxs()[i][j].setColor(LEDPXInfo.ColorOff);
				}
			}
		}
	}
}

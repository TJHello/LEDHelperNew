package com.tjbaobao.ledhelper.ui;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tjbaobao.framework.util.Tools;
import com.tjbaobao.ledhelper.R;

/**
 * 弹窗 框型 01号封装类
 * @author TJbaobao
 *
 */
public class PoPBB01Window extends PopupWindow{
	private Context context;
	
	private static final int WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
	private static final int MATCH_PARENT= RelativeLayout.LayoutParams.MATCH_PARENT;
	
	LinearLayout.LayoutParams lParams ;
	RelativeLayout.LayoutParams rParams;
	
	private int backGroundDrawableId = R.drawable.sys_pop_bb01_bg;
	private int boxBackgroundColor = Color.parseColor("#aa000000");
	private boolean bMain = false;
	private int parentId = -1;
	private int animationStyleId = -1;
	
	private String title_text="温馨提示";
	private int title_text_size=18;
	private int title_text_color=Color.parseColor("#ffffff");
	private int title_bgColor = Color.parseColor("#1ca9f0");
	private int title_bgImage = -1;
	
	private boolean isProgress = false,isLoding=false;
	private int progress = 0;
	private String contentText = "这是一句提示语";
	private int contentTextColor = Color.parseColor("#707070");
	private ProgressCircularView pcv ;
	
	
	private TextView tv_content ;
	
	private static boolean isInput = false;
	private EditText etInput ;
	private String etHintStr = "";
	
	private static String txtButtonOk = "确定",txtButtonCancel="取消";
	private Button bt_ok ,bt_cancel;
	
	private int bt_bgId = R.drawable.sys_btn_white_gray_selector;
	
	/**
	 * 具备 确定 取消按钮 
	 */
	public PoPBB01Window(Context context,int parentId,String title)
	{
		this(context,parentId,title,txtButtonOk,txtButtonCancel,false,"",isInput,MATCH_PARENT,MATCH_PARENT);
	}
	public PoPBB01Window(Context context,int parentId,String title,int WIDTH,int HEIGHT)
	{
		this(context,parentId,title,txtButtonOk,txtButtonCancel,false,"",false,WIDTH,HEIGHT);
	}
	
	/**
	 * 具备 确定 取消 按钮 中心内容 
	 */
	public PoPBB01Window(Context context,int parentId,String title,String contentText,boolean isProgress)
	{
		this(context,parentId,title,txtButtonOk,txtButtonCancel,isProgress,contentText,false,MATCH_PARENT,MATCH_PARENT);
	}
	
	/**
	 * 具备 进度条
	 */
	public PoPBB01Window(Context context,int parentId,String title,boolean isProgress,String content)
	{
		this(context,parentId,title,txtButtonOk,txtButtonCancel,isProgress,content,false,MATCH_PARENT,MATCH_PARENT);
	}
	public PoPBB01Window(Context context,int parentId,String title,boolean isProgress,String content,int WIDTH,int HEIGHT)
	{
		this(context,parentId,title,txtButtonOk,txtButtonCancel,isProgress,content,false,WIDTH,HEIGHT);
	}
	
	/**
	 * 具备 确定按钮 自定义按钮名称 null为不显示
	 */
	public PoPBB01Window(Context context,int parentId,String title,String ok)
	{
		this(context,parentId,title,ok,null,false,"",false,MATCH_PARENT,MATCH_PARENT);
	}
	public PoPBB01Window(Context context,int parentId,String title,String ok,int WIDTH,int HEIGHT)
	{
		this(context,parentId,title,ok,null,false,"",false,WIDTH,HEIGHT);
	}

	/**
	 * 具备 确定 取消 按钮 和进度条 自定义按钮名称 null为不显示(该方法暂无效果)
	 */
	public PoPBB01Window(Context context,int parentId,String title,String ok,String cancel,boolean isProgress,String content)
	{
		this(context,parentId,title,ok,cancel,isProgress,content,false,MATCH_PARENT,MATCH_PARENT);
	}
	
	public PoPBB01Window(Context context,int parentId,String title,boolean isInput)
	{
		
		this(context, parentId, title,txtButtonOk,txtButtonCancel,false,"",isInput,MATCH_PARENT,MATCH_PARENT);
	}
	
	/**
	 * 
	 * @param context 上下文
	 * @param parentId 父组件id
	 * @param title 弹窗标题
	 * @param ok 确认按钮显示文本 null为不显示
	 * @param cancel 取消按钮显示文本 null为不显示
	 * @param isProgress 是否具有进度条 
	 * @param WIDTH 弹窗的宽度
	 * @param HEIGHT 弹窗的高度
	 */
	public PoPBB01Window(Context context,int parentId,String title,String ok,String cancel,boolean isProgress,String content,boolean isInput,int WIDTH,int HEIGHT)
	{
		super(WIDTH , HEIGHT);
		this.context = context;
		this.parentId = parentId;
		this.txtButtonOk = ok;
		this.txtButtonCancel = cancel;
		this.title_text = title;
		this.isProgress = isProgress;
		this.isInput = isInput;
		this.contentText = content;
		this.setOutsideTouchable(true);//可外部点击
		this.setFocusable(true);//可获取焦点
		this.setTouchable(true);
		if(isProgress)
		{
			this.setTouchInterceptor(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
						String tag = (String) v.getTag();
						if(tag!=null)
						{
							//Tools.showToast("点击了按钮");
						}
						return true;
				}
			});
		}
		
		this.setBackgroundDrawable(context.getResources().getDrawable(backGroundDrawableId));
		
	}
	
	public void show(){show(Gravity.CENTER,0,0);}
	
	public void show(int gravity){show(gravity,0,0);}
	
	/**
	 * @param gravity 显示方式，Gravity类的布局常量(BOTTOM,TOP,LEFT,RIGHT,CENTER等)
	 * @param x 横坐标偏移量
	 * @param y 纵坐标偏移量
	 */
	public void show(int gravity, int x, int y)
	{
		if(!bMain)
		{
			this.setContentView(makeView());
			bMain = true;
		}
		;
	    if(animationStyleId!=-1)
	    	this.setAnimationStyle(animationStyleId);
	    View parent = ((Activity)context).findViewById(parentId);
	    this.showAtLocation(parent,gravity, x, y);
	    parent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {				
				return true;
			}
		});
	}
	
	/**
	 * 生成弹窗窗体
	 * @return View
	 */
	private View makeView()
	{
		 View parent = ((Activity)context).findViewById(parentId);
		RelativeLayout rlIndex = new RelativeLayout(context);
		parent.getLayoutParams().width = MATCH_PARENT;
		parent.getLayoutParams().height = MATCH_PARENT;
		
		LinearLayout ll_index = new LinearLayout(context);//
		rParams = new RelativeLayout.LayoutParams(dipToPx(300),WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		ll_index.setLayoutParams(rParams);
		ll_index.setOrientation(LinearLayout.VERTICAL);
		ll_index.setTag("index");
		if(!isLoding)
		{
			ll_index.addView(makeTitleView());
		}
		if(isProgress)
		{
			ll_index.addView(makeContentView());
		}
		else
		{
			if(contentText!=null&&!contentText.equals(""))
			{
				ll_index.addView(makeContentTextView());
			}
			if(isInput)
			{
				ll_index.addView(makeEditTextView());
			}
			ll_index.addView(makeButtonView());
		}
		rlIndex.addView(ll_index);
		
		return rlIndex;
	}
	
	/**
	 * 生成窗体标题栏部分
	 * @return
	 */
	private View makeTitleView()
	{
		RelativeLayout rl_title = new RelativeLayout(context);
		lParams = new LinearLayout.LayoutParams(MATCH_PARENT, Tools.dpToPx(50));
		rl_title.setLayoutParams(lParams);
		rl_title.setGravity(Gravity.CENTER);
		if(title_bgImage==-1)
			rl_title.setBackgroundColor(title_bgColor);
		else
			rl_title.setBackgroundResource(title_bgImage);
		TextView tv_title = new TextView(context);
		rParams = new RelativeLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
		tv_title.setLayoutParams(rParams);
		tv_title.setText(title_text);
		tv_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,title_text_size);
		tv_title.setTextColor(title_text_color);
		tv_title.setGravity(Gravity.CENTER);
		rl_title.addView(tv_title);
		return rl_title;
	}
	
	/**
	 * 生成进度条部分View
	 * @return
	 */
	private View makeContentView()
	{
		RelativeLayout rl_content = new RelativeLayout(context);
		if(isLoding)
		{
			lParams = new LinearLayout.LayoutParams(MATCH_PARENT,dipToPx(80));
			rl_content.setBackgroundColor(boxBackgroundColor);
		}
		else
		{
			lParams = new LinearLayout.LayoutParams(MATCH_PARENT,dipToPx(180));
			rl_content.setBackgroundColor(Color.WHITE);
		}
		lParams.setMargins(0, 0, 0, 0);
		rl_content.setLayoutParams(lParams);
		rl_content.setGravity(Gravity.CENTER);
		
		
		LinearLayout ll_box = new LinearLayout(context);
		rParams = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		ll_box.setLayoutParams(rParams);
		ll_box.setOrientation(LinearLayout.HORIZONTAL);
		ll_box.setGravity(Gravity.CENTER_VERTICAL);
		
		pcv = new ProgressCircularView(context);
		lParams = new LinearLayout.LayoutParams(120,120);
		lParams.setMargins(Tools.dpToPx(10), Tools.dpToPx(10), Tools.dpToPx(10), Tools.dpToPx(10));
		pcv.setLayoutParams(lParams);
		pcv.setLoding(isLoding);
		ll_box.addView(pcv);
		if(!isLoding)
		{
			tv_content = new TextView(context);
			lParams = new LinearLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
			lParams.setMargins(dipToPx(20), 0, 0, 0);
			tv_content.setLayoutParams(rParams);
			tv_content.setText(contentText);
			tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP,title_text_size);
			tv_content.setTextColor(Color.BLACK);
			ll_box.addView(tv_content);
		}
		rl_content.addView(ll_box);
		return rl_content;
	}
	
	/**
	 * 生成中间文本框部分View
	 */
	private View makeContentTextView()
	{
		RelativeLayout rl_title = new RelativeLayout(context);
		lParams = new LinearLayout.LayoutParams(WRAP_CONTENT,dipToPx(200));
		rl_title.setBackgroundColor(Color.WHITE);
		rl_title.setLayoutParams(lParams);
		rl_title.setGravity(Gravity.CENTER);
		
		TextView tv_title = new TextView(context);
		rParams = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		tv_title.setLayoutParams(rParams);
		tv_title.setText(Html.fromHtml(contentText));
		tv_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,title_text_size);
		tv_title.setTextColor(contentTextColor);
		tv_title.setGravity(Gravity.CENTER);
		rl_title.addView(tv_title);
		
		return rl_title;
	}
	
	/**
	 * 生成文本输入框部分View
	 */
	private View makeEditTextView()
	{
		RelativeLayout rlIndex = new RelativeLayout(context);
		lParams = new LinearLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
		rlIndex.setBackgroundColor(Color.WHITE);
		rlIndex.setLayoutParams(lParams);
		rlIndex.setGravity(Gravity.LEFT);
		rlIndex.setPadding(0, dipToPx(5), 0, dipToPx(5));
		etInput = new EditText(context);
		etInput.setLayoutParams(rParams);
		etInput.setTextSize(TypedValue.COMPLEX_UNIT_SP,title_text_size);
		etInput.setTextColor(contentTextColor);
		etInput.setGravity(Gravity.CENTER);
		rlIndex.addView(etInput);
		return rlIndex;
	}
	
	/**
	 * 生成按钮部分View
	 * @return
	 */
 	private View makeButtonView()
	{
		LinearLayout ll_index = new LinearLayout(context);
		lParams = new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
		
		ll_index.setLayoutParams(lParams);
		ll_index.setOrientation(LinearLayout.HORIZONTAL);
		ll_index.setGravity(Gravity.CENTER_VERTICAL);
		if(txtButtonOk!=null)
		{
			Tools.printLog("生成按钮");
			LinearLayout ll_box = new LinearLayout(context);
			lParams = new LinearLayout.LayoutParams(0,WRAP_CONTENT);
			lParams.weight=1;
			ll_box.setLayoutParams(lParams);
			ll_box.setOrientation(LinearLayout.VERTICAL);
			ll_box.setGravity(Gravity.CENTER_HORIZONTAL);
			
			bt_ok = new Button(context);
			lParams = new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
			bt_ok.setLayoutParams(lParams);
			bt_ok.setText(txtButtonOk);
			bt_ok.setTag("ok");
			bt_ok.setBackgroundResource(bt_bgId);
			bt_ok.setOnClickListener(new BTOnclick());
			ll_box.addView(bt_ok);
			ll_index.addView(ll_box);
		}
		if(txtButtonCancel!=null)
		{
			Tools.printLog("生成按钮");
			LinearLayout ll_box = new LinearLayout(context);
			lParams = new LinearLayout.LayoutParams(0,WRAP_CONTENT);
			lParams.weight=1;
			ll_box.setLayoutParams(lParams);
			ll_box.setOrientation(LinearLayout.VERTICAL);
			
			bt_cancel = new Button(context);
			lParams = new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
			bt_cancel.setLayoutParams(lParams);
			bt_cancel.setText(txtButtonCancel);
			bt_cancel.setTag("cancel");
			bt_cancel.setOnClickListener(new BTOnclick());
			bt_cancel.setBackgroundResource(bt_bgId);
			ll_box.addView(bt_cancel);
			ll_index.addView(ll_box);
		}
		return ll_index;
	}
	
	private class BTOnclick implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			if(v.getTag().equals("ok"))
			{
				okOnclickListener(v);
			}
			else if(v.getTag().equals("cancel"))
			{
				cancelOnclickListener(v);
			}
		}
	}
	public void okOnclickListener(View v){}
	public void cancelOnclickListener(View v){this.dismiss();}
	public int getBackGroundDrawableId() {
		return backGroundDrawableId;
	}
	public boolean isbMain() {
		return bMain;
	}
	public int getParentId() {
		return parentId;
	}
	public int getAnimationStyleId() {
		return animationStyleId;
	}
	public String getTitle_text() {
		return title_text;
	}
	public int getTitle_text_size() {
		return title_text_size;
	}
	public int getTitle_text_color() {
		return title_text_color;
	}
	public int getTitle_bgColor() {
		return title_bgColor;
	}
	public int getTitle_bgImage() {
		return title_bgImage;
	}
	public Button getBt_ok() {
		return bt_ok;
	}
	public Button getBt_cancel() {
		return bt_cancel;
	}
	public String getTxtButtonOk(){
		return txtButtonOk;
	}
	public String getTxtButtonCancel() {
		return txtButtonCancel;
	}
	public boolean isProgress() {
		return isProgress;
	}
	public int getProgress() {
		return progress;
	}
	public void setBackGroundDrawableId(int backGroundDrawableId) {
		this.backGroundDrawableId = backGroundDrawableId;
	}

	public void setbMain(boolean bMain) {
		this.bMain = bMain;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public void setAnimationStyleId(int animationStyleId) {
		this.animationStyleId = animationStyleId;
	}
	public void setTitle_text(String title_text) {
		this.title_text = title_text;
	}
	public void setTitle_text_size(int title_text_size) {
		this.title_text_size = title_text_size;
	}
	public void setTitle_text_color(int title_text_color) {
		this.title_text_color = title_text_color;
	}
	public void setTitle_bgColor(int title_bgColor) {
		this.title_bgColor = title_bgColor;
	}
	public void setTitle_bgImage(int title_bgImage) {
		this.title_bgImage = title_bgImage;
	}
	public void setBt_ok(Button bt_ok) {
		this.bt_ok = bt_ok;
	}
	public void setBt_cancel(Button bt_cancel) {
		this.bt_cancel = bt_cancel;
	}
	public void setTxtButtonOk(String textOk) {
		txtButtonOk = textOk;
		if(bt_ok!=null)
		{
			bt_ok.setText(textOk);
		}
	}
	public String getContentText() {
		return contentText;
	}
	public void setContentText(String contentText) {
		this.contentText = contentText;
		if(tv_content!=null)
		{
			tv_content.setText(contentText);
		}
	}
	public void setTxtButtonCancel(String textCancel) {
		txtButtonCancel = textCancel;
		if(bt_cancel!=null)
		{
			bt_cancel.setText(textCancel);
		}
	}
	public void setProgress(boolean isProgress) {
		this.isProgress = isProgress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
		if(isShowing()&&isProgress&&pcv!=null)
		{
			pcv.setProgress(progress);
		}
	}
	public void setLoding(boolean loding)
	{
		this.isLoding = loding;
	}
	public void setETValue(String value)
	{
		if(etInput!=null&&value!=null)
			etInput.setText(value);
	}
	public String getETValue()
	{
		if(etInput!=null)
			return etInput.getText().toString();
		else
			return "";
	}
	
	/**
	 * 将dip数值转化为px数值
	 * @param dip
	 * @return
	 */
	private int dipToPx(int dip)
	{
		final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dip * scale + 0.5f);  
	}
	@Override
	public void dismiss() {
		if(pcv!=null)
		{
			pcv.animStop();
		}
		super.dismiss();
	}
}

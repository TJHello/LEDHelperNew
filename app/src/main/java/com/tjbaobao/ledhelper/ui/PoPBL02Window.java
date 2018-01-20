package com.tjbaobao.ledhelper.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tjbaobao.framework.util.Tools;

public class PoPBL02Window extends PopupWindow{
	private static final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
	
	private Context  context ;
	private ListView listview ;
	private boolean bMain = false;
	private int listMaxHeight = Tools.dpToPx(100);
	//
	private TextView tv_title ;
	private String titleText = null;
	private int titleTextColor = Color.parseColor("#ffffff");
	private int titleTextSize = 16 ;
	private int titleBackgroundColor = Color.parseColor("#4697fc");
	
	private int listBackgroundColor = Color.parseColor("#ffffff");
	
	public PoPBL02Window(Context context) 
	{
		super(WRAP_CONTENT, WRAP_CONTENT);
		this.context = context;
		this.setOutsideTouchable(true);
		this.setFocusable(true);
		initView();
	}
	public PoPBL02Window(Context context,int width,int height)
	{
		super(width, height);
		this.context = context;
		this.setOutsideTouchable(true);
		this.setFocusable(true);
		initView();
	}
	private void initView()
	{
		listview = new ListView(context);
	}
	private View makeView()
	{
		LinearLayout ll_index = new LinearLayout(context);
		ll_index.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		ll_index.setLayoutParams(lParams);
		if(titleText!=null)
		{
			ll_index.addView(makeTitleView());
		}
		ll_index.addView(makeLine());
		ll_index.addView(makeListView());
		ll_index.addView(makeLine());
		return ll_index;
	}
	private View makeTitleView()
	{
		LinearLayout ll_title = new LinearLayout(context);
		ll_title.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		lParams.gravity = Gravity.CENTER_VERTICAL;
		ll_title.setPadding(0, Tools.dpToPx(10), 0, Tools.dpToPx(10));
		ll_title.setLayoutParams(lParams);
		if(titleBackgroundColor!=-1)
		{
			ll_title.setBackgroundColor(titleBackgroundColor);
		}
		
		tv_title = new TextView(context);
		LinearLayout.LayoutParams lParamsTV = new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
		tv_title.setLayoutParams(lParamsTV);
		tv_title.setText(titleText);
		tv_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,titleTextSize);
		tv_title.setTextColor(titleTextColor);
		tv_title.setGravity(Gravity.CENTER);
		ll_title.addView(tv_title);
		
		return ll_title;
	}
	
	private View makeListView()
	{
		if(listMaxHeight==0)
		{
			listMaxHeight = -2;
		}
		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(MATCH_PARENT,listMaxHeight);
		listview.setLayoutParams(lParams);
		listview.setBackgroundColor(listBackgroundColor);
		return listview;
	}
	/**
	 * 生成窗体分割线
	 * @return
	 */
	private View makeLine()
	{
		TextView tv_line = new TextView(context);
		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(MATCH_PARENT,1);
		tv_line.setLayoutParams(lParams);
		tv_line.setBackgroundColor(Color.parseColor("#DBDBDB"));
		return tv_line;
	}
	@SuppressLint("NewApi")
	public void show(View parent,int gravity)
	{
		if(!bMain)
		{
			this.setContentView(makeView());
			bMain = true ;
		}
		this.showAsDropDown(parent, 0, 0, gravity);
	}
	
	public void setAdapter(BaseAdapter adapter)
	{
		listview.setAdapter(adapter);
	}
	public void setOnItemClickListener(OnItemClickListener onItemClickListener)
	{
		listview.setOnItemClickListener(onItemClickListener);
	}
	
	
	public String getTitleText() {
		return titleText;
	}
	public void setTitleText(String titleText) {
		if(tv_title!=null)
		{
			tv_title.setText(titleText);
		}
		this.titleText = titleText;
	}
	public int getTitleTextColor() {
		return titleTextColor;
	}
	public void setTitleTextColor(int titleTextColor) {
		if(tv_title!=null)
		{
			tv_title.setTextColor(titleTextColor);
		}
		this.titleTextColor = titleTextColor;
	}
	public int getTitleTextSize() {
		return titleTextSize;
	}
	public void setTitleTextSize(int titleTextSize) {
		this.titleTextSize = titleTextSize;
	}
	public int getTitleBackgroundColor() {
		return titleBackgroundColor;
	}
	public void setTitleBackgroundColor(int titleBackgroundColor) {
		this.titleBackgroundColor = titleBackgroundColor;
	}
	public int getListMaxHeight() {
		return listMaxHeight;
	}
	public void setListMaxHeight(int listMaxHeight) {
		this.listMaxHeight = listMaxHeight;
		LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(MATCH_PARENT,listMaxHeight);
		listview.setLayoutParams(lParams);
	}
	
}

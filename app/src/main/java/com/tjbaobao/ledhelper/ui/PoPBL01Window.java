package com.tjbaobao.ledhelper.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tjbaobao.framework.util.Tools;
import com.tjbaobao.ledhelper.R;

import java.util.ArrayList;
/**
 * 弹窗_底部_列表 弹窗代码封装
 * @author czm
 *
 */
public abstract class PoPBL01Window extends PopupWindow{
	Context context;
	View parent;
	RelativeLayout bt_weixin,bt_ali;
	private String title_text="";
	private int title_text_size=18;
	private int title_text_color=Color.parseColor("#ffffff");
	private int title_bgColor = Color.parseColor("#1ca9f0");
	private int title_bgImage = -1;
	private int contentTextColor = Color.parseColor("#000000");
	private int itemBackground = -1;
	private int boxBackgroundColor =  Color.parseColor("#ffffff");
	private int boxBackground = -1 ;
	
	private int arrowIconId = R.drawable.sys_arrow_right_icon;
	
	private int animationStyleId = -1;
	private boolean bMain = false;
	int R_WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
	int R_MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
	int L_WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
	int L_MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
	private int itemWidth = L_MATCH_PARENT ,itemHeight=L_WRAP_CONTENT ;
	LinearLayout.LayoutParams lParams ;
	RelativeLayout.LayoutParams rParams;
	
	
	ArrayList<PoPItemInfo> popList = new ArrayList<PoPItemInfo>();
	/**
	 * 子列数据类
	 * @author czm
	 *
	 */
	public class PoPItemInfo 
	{
		private int iconId;//图标id
		private String content;//文本内容
		public PoPItemInfo(int iconId,String content)
		{
			this.iconId = iconId;
			this.content = content;
		}
	}
	/**
	 * 构造函数
	 * @param context 上下文
	 * @param gravityId 容器组件Id，窗体会显示在该容器组件的底部
	 * @param backGroundDrawableId 弹窗背景id
	 */
	public PoPBL01Window(Context context,int gravityId,int backGroundDrawableId) {
		super(LayoutParams.MATCH_PARENT , LayoutParams.WRAP_CONTENT);
		this.context = context;
		initData(popList);
		this.setOutsideTouchable(true);
		this.setFocusable(true);
		this.setBackgroundDrawable(context.getResources().getDrawable(backGroundDrawableId));
		parent = ((Activity) context).findViewById(gravityId);
	}
	public PoPBL01Window(Context context,int gravityId,int backGroundDrawableId,int width,int height)
	{
		super(width, height);
		this.context = context;
		initData(popList);
		this.setOutsideTouchable(true);
		this.setFocusable(true);
		this.setBackgroundDrawable(context.getResources().getDrawable(backGroundDrawableId));
		parent = ((Activity) context).findViewById(gravityId);
	}
	/**
	 * 初始化数据
	 * @param popList 子列数据类对象动态列表
	 */
	public abstract void initData(ArrayList<PoPItemInfo> popList);
	/**
	 * 生成弹窗窗体
	 * @return View
	 */
	private View makeView()
	{
		RelativeLayout rl = new RelativeLayout(context);
		lParams = new LinearLayout.LayoutParams(L_MATCH_PARENT,L_MATCH_PARENT);
		rl.setLayoutParams(lParams);
		rl.setBackgroundResource(R.drawable.sys_transparent_55_black_shape);
		LinearLayout ll_index = new LinearLayout(context);
		rParams = new RelativeLayout.LayoutParams(R_WRAP_CONTENT,R_WRAP_CONTENT);
		rParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		ll_index.setLayoutParams(rParams);
		ll_index.setOrientation(LinearLayout.VERTICAL);
		if(boxBackground!=-1)
		{
			ll_index.setBackgroundDrawable(context.getResources().getDrawable(boxBackground));
		}
		else
		{
			ll_index.setBackgroundColor(boxBackgroundColor);
		}
		
		if(!title_text.equals(""))
		{
			ll_index.addView(makeTitleView());
		}
		
		int i=0;
		for(PoPItemInfo info:popList)
		{
			ll_index.addView(makeItemView(info,i));
			ll_index.addView(makeLine());
			i++;
		}
		rl.addView(ll_index);
		return rl;
	}
	/**
	 * 生成窗体标题栏部分
	 * @return
	 */
	private View makeTitleView()
	{
		RelativeLayout rl_title = new RelativeLayout(context);
		lParams = new LinearLayout.LayoutParams
				(LinearLayout.LayoutParams.MATCH_PARENT,dipToPx(50));
		rl_title.setLayoutParams(lParams);
		rl_title.setGravity(Gravity.CENTER);
		if(title_bgImage==-1)
			rl_title.setBackgroundColor(title_bgColor);
		else
			rl_title.setBackgroundResource(title_bgImage);
		TextView tv_title = new TextView(context);
		rParams = new RelativeLayout.LayoutParams(R_WRAP_CONTENT,R_WRAP_CONTENT);
		tv_title.setLayoutParams(rParams);
		tv_title.setText(title_text);
		tv_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,title_text_size);
		tv_title.setTextColor(title_text_color);
		rl_title.addView(tv_title);
		return rl_title;
	}
	/**
	 * 生成窗体Item子列
	 * @param info 子列数据对象
	 * @param position 子列顺序标识
	 * @return
	 */
	private View makeItemView(PoPItemInfo info,int position)
	{
		LinearLayout ll = new LinearLayout(context);
		lParams = new LinearLayout.LayoutParams(itemWidth,dipToPx(60));
		ll.setLayoutParams(lParams);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setGravity(Gravity.CENTER_VERTICAL);
		ll.setPadding(dipToPx(10), dipToPx(5), dipToPx(5), dipToPx(10));
		ll.setTag(position);
		if(itemBackground!=-1)
		{
			ll.setBackgroundDrawable(context.getResources().getDrawable(itemBackground));
		}
		ll.setOnClickListener(new ItemOnclick());
		TextView tv_content = new TextView(context);
		lParams = new LinearLayout.LayoutParams(L_MATCH_PARENT,L_WRAP_CONTENT);
		lParams.weight=1;
		lParams.setMargins(dipToPx(5), 0, 0, 0);
		tv_content.setLayoutParams(lParams);
		tv_content.setText(info.content);
		tv_content.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
		tv_content.setTextColor(contentTextColor);
		tv_content.setGravity(Gravity.CENTER);
		ll.addView(tv_content);
		
		if(info.iconId!=0)
		{
			ImageView iv_icon = new ImageView(context);
			lParams = new LinearLayout.LayoutParams(dipToPx(30),dipToPx(30));
			lParams.setMargins(dipToPx(30), 0, 0, 0);
			iv_icon.setLayoutParams(lParams);
			iv_icon.setBackgroundDrawable(context.getResources().getDrawable(info.iconId));
			ll.addView(iv_icon);
		}
		
		/*ImageView iv_arrow = new ImageView(context);
		lParams = new android.widget.LinearLayout.LayoutParams(L_WRAP_CONTENT,L_MATCH_PARENT);
		lParams.setMargins(dipToPx(10), 0, 0, 0);
		iv_arrow.setLayoutParams(lParams);
		iv_arrow.setScaleType(ScaleType.FIT_CENTER);
		iv_arrow.setBackgroundDrawable(context.getResources().getDrawable(arrowIconId));
		ll.addView(iv_arrow);*/
		
		return ll;
	}
	/**
	 * 生成窗体分割线
	 * @return
	 */
	private View makeLine()
	{
		TextView tv_line = new TextView(context);
		lParams = new LinearLayout.LayoutParams(L_MATCH_PARENT,dipToPx(1));
		tv_line.setLayoutParams(lParams);
		tv_line.setBackgroundColor(Color.parseColor("#DBDBDB"));
		return tv_line;
	}
	/**
	 * Item子列点击事件(重写执行)
	 * @param v View
	 * @param position 子列顺序标识
	 */
	public void onClickListener(View v,int position)
	{
		
	}
	
	private class ItemOnclick implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			onClickListener(v,(Integer)(v.getTag()));
		}
	}
	/**
	 * 
	 * @param x 横坐标偏移量
	 * @param y 纵坐标偏移量
	 */
	public void show( int x, int y)
	{
		if(!bMain)
		{
			this.setContentView(makeView());
			bMain = true;
		}
		
	    if(animationStyleId!=-1)
	    	this.setAnimationStyle(animationStyleId);
	    this.showAtLocation(parent,Gravity.BOTTOM, x, y);
	}
	@SuppressLint("NewApi")
	public void show()
	{
		show(Gravity.CENTER);
	    
	}
	public void show(int gravity)
	{
		if(!bMain)
		{
			this.setContentView(makeView());
			bMain = true;
		}
		
	    if(animationStyleId!=-1)
	    	this.setAnimationStyle(animationStyleId);
	    this.showAtLocation(parent,gravity, 0, 0);
	}
	@SuppressLint("NewApi")
	public void myShowAsDropDown()
	{
		if(!bMain)
		{
			Tools.showToast("显示");
			this.setContentView(makeView());
			bMain = true;
		}
		
	    if(animationStyleId!=-1)
	    	this.setAnimationStyle(animationStyleId);
	    this.showAsDropDown(parent, 0, 0, Gravity.LEFT);
	}
	public void setTitle(String title){this.title_text = title;}
	public void setTitleSizeBySp(int sp){this.title_text_size = sp;}
	public void setTitleColor(String colorString){this.title_text_color = Color.parseColor(colorString);}
	public void setTitleColor(int color){this.title_text_color = color;}
	public void setAnimationStyleId(int styleId){this.animationStyleId=styleId;}
	public void setItemBackground(int itemBackground)
	{
		this.itemBackground = itemBackground;
	}
	public void setBoxBackgroundColor(int color)
	{
		this.boxBackgroundColor = color ;
	}
	public void setBoxBackground(int boxBackground)
	{
		this.boxBackground = boxBackground;
	}
	public void setContentTextColor(int contentTextColor) {
		this.contentTextColor = contentTextColor;
	}
	public void setItemWidth(int width)
	{
		this.itemWidth = width;
	}
	public void setTitleBGColor(int color)
	{
		this.title_bgColor = color;
	}
	/**
	 * 设置Item右边图像id
	 * @param resId
	 */
	public void setArrowIconId(int resId){this.arrowIconId=resId;}
	
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
	
}

package com.tjbaobao.ledhelper.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tjbaobao.ledhelper.R;
import com.tjbaobao.ledhelper.entity.ui.Tab;

public class ImgListLayout extends BaseLinearLayout {

	public ImgListLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public ImgListLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public ImgListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}
	
	public void addTab(Tab tab)
	{
		LinearLayout ll_box= (LinearLayout) inflate(context,R.layout.app_img_list_item_layout, this);
		ImageView iv_item =(ImageView) ll_box.findViewById(R.id.iv_item);
		LinearLayout.LayoutParams lParams = (LayoutParams) ll_box.getLayoutParams();
		lParams.weight  = 1;
		iv_item.setImageResource(tab.getImgOffId());
	}

}

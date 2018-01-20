package com.tjbaobao.ledhelper.ui;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.tjbaobao.ledhelper.R;

public class ImageOnOffView extends AppCompatImageView implements OnClickListener{

	private boolean checked = false;
	private int imageOffId = R.drawable.app_setting_switch_off;
	private int imageOnId = R.drawable.app_setting_switch_on ;
	private OnCheckedLinstener onCheckedLinstener ;
	
	public ImageOnOffView(Context context) {
		super(context);
		initView();
	}
	public ImageOnOffView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	private void initView()
	{
		this.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		checked = !checked;
		setChecked(checked);
		if(onCheckedLinstener!=null)
		{
			onCheckedLinstener.onChecked(checked);
		}
	}
	
	public interface OnCheckedLinstener
	{
		public void onChecked(boolean checked);
	}

	public OnCheckedLinstener getOnCheckedLinstener() {
		return onCheckedLinstener;
	}
	public void setOnCheckedLinstener(OnCheckedLinstener onCheckedLinstener) {
		this.onCheckedLinstener = onCheckedLinstener;
	}
	public void setChecked(boolean checked)
	{
		if(checked)
		{
			this.setImageResource(imageOnId);
		}
		else
		{
			this.setImageResource(imageOffId);
		}
	}
}

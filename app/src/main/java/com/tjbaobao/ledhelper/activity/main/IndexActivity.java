package com.tjbaobao.ledhelper.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tjbaobao.framework.entity.ui.TitleBarInfo;
import com.tjbaobao.framework.tjbase.TJActivity;
import com.tjbaobao.framework.ui.BaseTitleBar;
import com.tjbaobao.framework.util.DeviceUtil;
import com.tjbaobao.ledhelper.R;
import com.tjbaobao.ledhelper.activity.device.DeviceListActivity;
import com.tjbaobao.ledhelper.entity.enums.MenuEnum;
import com.tjbaobao.ledhelper.ui.MenuListITTView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TJbaobao on 2018/1/4.
 */

public class IndexActivity extends TJActivity {

    private ImageView iv_bluetooth,iv_setting ;
    private DrawerLayout drawerLayout ;
    private View index_left ;
    private MenuListITTView menuList;
    private TextView tv_version ;
    @Override
    protected void onInitValues(Bundle savedInstanceState) {
        isOnclickTwoExit();
    }

    @Override
    protected void onInitView() {
        setContentView(R.layout.index_activity_layout);
        this.findViewById(R.id.ll_image).setOnClickListener(this);
        this.findViewById(R.id.ll_text).setOnClickListener(this);
        this.findViewById(R.id.ll_anim).setOnClickListener(this);
        tv_version = this.findViewById(R.id.tv_version);
        index_left = this.findViewById(R.id.index_left);
        drawerLayout = this.findViewById(R.id.drawerLayout);
        iv_bluetooth = this.findViewById(R.id.iv_bluetooth);
        iv_setting = this.findViewById(R.id.iv_setting);
        iv_bluetooth.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        menuList = this.findViewById(R.id.menuList);
        menuList.setOnMenuItemClickListener(new MyOnMenuItemClickListener());
        tv_version.setText("V"+DeviceUtil.getAppVersion());
    }

    @Override
    protected void onInitTitleBar(BaseTitleBar titleBar) {
        titleBar.addImageToLeft(R.drawable.ic_menu);
        titleBar.setTitle(getStringById(R.string.index_title));
    }

    @Override
    public <V extends TitleBarInfo.BaseView> void onTitleBarClick(int layoutType, int position, V info) {
        if(layoutType==BaseTitleBar.LAYOUT_LEFT&&position==0)
        {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    protected void onLoadData() {
        List<MenuListITTView.MenuItemInfo> list = new ArrayList<>();
        for(MenuEnum menuEnum:MenuEnum.values())
        {
            MenuListITTView.MenuItemInfo menuItemInfo = menuList.new MenuItemInfo();
            menuItemInfo.setIconId(menuEnum.imageId);
            menuItemInfo.setName(menuEnum.name);
            list.add(menuItemInfo);
        }
        menuList.setData(list);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.ll_image:
                toIndexSetActivity(SET_TYPE_IMAGE);
                break;
            case R.id.ll_text:
                toIndexSetActivity(SET_TYPE_TEXT);
                break;
            case R.id.ll_anim:
                toIndexSetActivity(SET_TYPE_ANIM);
                break;
            case R.id.iv_bluetooth:
                startActivity(DeviceListActivity.class);
                break;
            case R.id.iv_setting:
                startActivity(SettingActivity.class);
                break;
        }
    }

    private class MyOnMenuItemClickListener implements MenuListITTView.OnMenuItemClickListener
    {
        @Override
        public void onClick(int position) {
            if(position==0)
            {
                startActivity(DeviceListActivity.class);
            }
            else if(position==1)
            {
                startActivity(SettingActivity.class);
            }
        }
    }

    public static final int SET_TYPE_IMAGE = 0 ;
    public static final int SET_TYPE_TEXT = 1;
    public static final int SET_TYPE_ANIM = 2;

    private void toIndexSetActivity(int type)
    {
        Intent intent = new Intent(this,IndexSetActivity.class);
        intent.putExtra("ledHelperType",type);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.LEFT))
        {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
        else
        {
            super.onBackPressed();
        }

    }

    private boolean isStart = false;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus&&!isStart)
        {
            isStart = true;
            index_left.setPadding(0,getBarHeight(),0,0);
        }
    }
}

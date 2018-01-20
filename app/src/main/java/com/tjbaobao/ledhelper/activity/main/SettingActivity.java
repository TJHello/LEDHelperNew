package com.tjbaobao.ledhelper.activity.main;

import android.os.Bundle;

import com.tjbaobao.framework.entity.ui.TitleBarInfo;
import com.tjbaobao.framework.tjbase.TJActivity;
import com.tjbaobao.framework.ui.BaseTitleBar;
import com.tjbaobao.ledhelper.R;

/**
 * Created by TJbaobao on 2018/1/15.
 */

public class SettingActivity extends TJActivity {

    @Override
    protected void onInitValues(Bundle savedInstanceState) {

    }

    @Override
    protected void onInitView() {
        setContentView(R.layout.setting_activity_layout);
    }

    @Override
    protected void onInitTitleBar(BaseTitleBar titleBar) {
        titleBar.addImageToLeft(R.drawable.fw_ic_back);
        titleBar.setTitle(getStringById(R.string.index_setting_activity_title));
    }

    @Override
    protected void onLoadData() {

    }

    @Override
    public <V extends TitleBarInfo.BaseView> void onTitleBarClick(int layoutType, int position, V info) {
        if(layoutType==BaseTitleBar.LAYOUT_LEFT&&position==0)
        {
            this.finish();
        }
    }
}

package com.tjbaobao.framework.tjbase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tjbaobao.framework.R;
import com.tjbaobao.framework.base.BaseFragmentActivity;
import com.tjbaobao.framework.entity.ui.TitleBarInfo;
import com.tjbaobao.framework.ui.BaseTitleBar;
import com.tjbaobao.framework.util.Tools;

/**
 * Created by TJbaobao on 2018/1/10.
 */

public abstract class TJFragmentActivity extends BaseFragmentActivity  implements BaseTitleBar.OnTitleBarClickListener {


    protected BaseTitleBar titleBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        immersiveStatusBar();
        initValues(savedInstanceState);
        initView();
        initTitleBar();
        onLoadData();
    }

    /**
     * 初始化值
     */
    private void initValues(Bundle savedInstanceState)
    {
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        onInitValues(savedInstanceState);
    }
    /**
     * 初始化组件与布局
     */
    private void initView()
    {
        onInitView();
    }

    private void initTitleBar()
    {
        titleBar = this.findViewById(R.id.titleBar);
        if(titleBar!=null)
        {
            titleBar.setOnTitleBarClickListener(this);
            titleBar.setBackgroundColor(getColorById(R.color.fw_theme_color));
            onInitTitleBar(titleBar);
        }
    }

    protected abstract void onInitValues(Bundle savedInstanceState);

    protected abstract void onInitView();

    protected abstract void onInitTitleBar(BaseTitleBar titleBar);

    protected abstract void onLoadData();

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public <V extends TitleBarInfo.BaseView> void onTitleBarClick(int layoutType, int position, V info) {

    }

    //region ***************双击退出应用开始*************
    private long startTime = 0;
    private boolean isOnclickTwoExit = false;
    private int exitTime = 2000;
    /**
     * 双击两次退出应用
     */
    public void isOnclickTwoExit()
    {
        setOnclickTwoExit(exitTime);
    }
    public void isOnclickTwoExit(boolean isExit)
    {
        isOnclickTwoExit = isExit;
        if(isExit)
        {
            setOnclickTwoExit(exitTime);
        }
    }
    public void setOnclickTwoExit(int time)
    {
        isOnclickTwoExit = true ;
        exitTime = time;
    }

    @Override
    public void onBackPressed() {
        long nowTime = System.currentTimeMillis();
        if (nowTime - startTime < exitTime)
        {
            super.onBackPressed();
        }
        else
        {
            startTime = System.currentTimeMillis();
            onClickTwoExit();
        }
    }

    public boolean onClickTwoExit()
    {
        Tools.showToast("再次点击退出程序");
        return true;
    }
    //endregion ***************双击退出应用结束*************
}

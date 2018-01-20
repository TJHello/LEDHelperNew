package com.tjbaobao.ledhelper.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tjbaobao.framework.base.BaseActivity;
import com.tjbaobao.framework.util.Tools;
import com.tjbaobao.ledhelper.R;

/**
 * Created by TJbaobao on 2017/11/15.
 */

public abstract class AppActivity extends BaseActivity {

    private TextView tvBaseTitle ;
    private ImageView baseTitleLeft ,baseTitleRight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        immersiveStatusBar();
        initValues(savedInstanceState);
        initView();
        initBaseView();
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

    private void initBaseView()
    {
        View templateTitleBar = this.findViewById(R.id.app_template_title);
        if(templateTitleBar!=null)
        {
            baseTitleLeft = templateTitleBar.findViewById(R.id.iv_template_left);
            baseTitleRight = templateTitleBar.findViewById(R.id.iv_template_right);
            tvBaseTitle =  (TextView) templateTitleBar.findViewById(R.id.tv_template_title);
            if(baseTitleLeft!=null)
            {
                baseTitleLeft.setOnClickListener(this);
            }
            if(baseTitleRight!=null)
            {
                baseTitleRight.setOnClickListener(this);
            }
        }
        onInitBaseView();
    }

    protected abstract void onInitValues(Bundle savedInstanceState);

    protected abstract void onInitView();

    protected abstract void onInitBaseView();

    protected abstract void onLoadData();

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.iv_template_left:
                onTemplateLeftClick();
                break;
            case R.id.iv_template_right:
                onTemplateRightClick();
                break;
        }
    }

    protected void onTemplateLeftClick()
    {

    }
    protected void onTemplateRightClick()
    {

    }
    protected void setTempleTitle(String text)
    {
        if(tvBaseTitle!=null)
        {
            tvBaseTitle.setText(text);
        }
    }

    protected void hideTempleLeft()
    {
        if(baseTitleLeft!=null)
        {
            baseTitleLeft.setVisibility(View.INVISIBLE);
        }
    }
    protected void hideTempleRight()
    {
        if(baseTitleRight!=null)
        {
            baseTitleRight.setVisibility(View.INVISIBLE);
        }
    }

    protected void setTempleLeftRes(int res)
    {
        if(baseTitleLeft!=null)
        {
            baseTitleLeft.setImageResource(res);
        }
    }

    protected void setTempleRightRes(int res)
    {
        if(baseTitleRight!=null)
        {
            baseTitleRight.setImageResource(res);
        }
    }

    // ***************双击退出应用开始*************
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&&isOnclickTwoExit)
        {
            long nowTime = System.currentTimeMillis();
            if (nowTime - startTime < exitTime)
            {
                this.finish();
            }
            else
            {
                startTime = System.currentTimeMillis();
                return onClickTwoExit();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onClickTwoExit()
    {
        Tools.showToast("再次点击退出程序");
        return true;
    }
    // ***************双击退出应用结束*************
}

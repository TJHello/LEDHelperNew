package com.tjbaobao.ledhelper.popwindow;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tjbaobao.framework.util.Tools;
import com.tjbaobao.ledhelper.R;


/**
 * Created by TJbaobao on 2017/7/7.
 */

public class BasePopupWindow extends PopupWindow implements View.OnClickListener{
    protected Context context ;
    protected View iv_close,bt_continue ;
    private TextView tv_help_title;

    protected View viewParent,view,ll_windows_index;
    private int windowAnimExitId = R.anim.fw_windows_anim_exit;
    private int windowAnimEnterId = R.anim.fw_windows_anim_enter;
    private int contentAnimEnterId = R.anim.fw_windows_content_anim_enter;
    private int contentAnimExitId = R.anim.fw_windows_content_anim_exit;
    private static final int Handler_What_Anim_Stop = 1001;

    private boolean canOutsideClose = true,canBackClose = true;
    private boolean canDismis = true;

    public BasePopupWindow(Context context, View viewParent, int layoutId)
    {
        this(context,viewParent,layoutId, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
    public BasePopupWindow(Context context, View viewParent, int layoutId, int width, int height)
    {
        super(width,height);
        this.context = context;
        this.viewParent = viewParent;
        view = LayoutInflater.from(context).inflate(layoutId,null);
        this.setContentView(view);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.app_transparent_bg));
        initView(view);
    }

    protected void initView(View view)
    {
        ll_windows_index = view.findViewById(R.id.ll_windows_index);
        iv_close = view.findViewById(R.id.bt_cancel);
        if(iv_close!=null){
            iv_close.setOnClickListener(this);
        }
        bt_continue = view.findViewById(R.id.bt_continue);
        if(bt_continue!=null){
            bt_continue.setOnClickListener(this);
        }
        view.setOnClickListener(new WindowOnclick());
        tv_help_title = (TextView) view.findViewById(R.id.tv_help_title);
        if(iv_close!=null)
        {
            Tools.setOnclickBackground(iv_close,false);
        }
    }

    public BasePopupWindow show()
    {
        return show(Gravity.CENTER);
    }
    public BasePopupWindow show(int gravity)
    {
        return show(gravity,0,0);
    }
    public BasePopupWindow show(int gravity,int x,int y)
    {
        if(isShowing())
        {
            return this;
        }
        if(ll_windows_index!=null)
        {
            Animation animation = AnimationUtils.loadAnimation(context,contentAnimEnterId);
            //animation.setInterpolator(new DecelerateInterpolator(2));
            long durationMillis =450;
            long delayMillis = 100;
            animation.setInterpolator(new DecelerateInterpolator(2));
            animation.setDuration(durationMillis);
            animation.setStartOffset(delayMillis);
            ll_windows_index.setAnimation(animation);
            ll_windows_index.startAnimation(animation);
        }
        Animation animationView = AnimationUtils.loadAnimation(context,windowAnimEnterId);
        view.setAnimation(animationView);
        view.startAnimation(animationView);
        if(!canBackClose)
        {
            canDismis = false;
        }
        else
        {
            canDismis = true;
        }
        try{
            this.showAtLocation(viewParent, gravity,x,y);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return this;
    }

    protected void setTitle(String title)
    {
        if(tv_help_title!=null)
        {
            tv_help_title.setText(title);
        }
    }

    @Override
    public void dismiss() {
        if(canDismis)
        {
            canDismis = false;
            final Animation animation = AnimationUtils.loadAnimation(context,contentAnimExitId);
            if(ll_windows_index!=null)
            {
                ll_windows_index.setAnimation(animation);
                ll_windows_index.startAnimation(animation);
            }
            final Animation animationView = AnimationUtils.loadAnimation(context,windowAnimExitId);
            view.setAnimation(animationView);
            view.startAnimation(animationView);
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(animationView.getDuration());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendMessage(Handler_What_Anim_Stop,null);
                }
            }.start();
        }
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            onHandlerMessage(msg,msg.what,msg.obj);
            super.handleMessage(msg);
        }
    };
    protected void sendMessage(int what,Object obj)
    {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }
    protected void onHandlerMessage(Message msg, int what, Object obj)
    {
        switch (what)
        {
            case Handler_What_Anim_Stop:
                super.dismiss();
                break;
        }
    }

    private class WindowOnclick implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            if(canOutsideClose)
            {
                dismiss();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bt_cancel:
                dismiss();
                onClose();
                break;
            case R.id.bt_continue:
                canDismis = true;
                dismiss();
                onContinueClick();
                break;
        }
    }

    public void onContinueClick()
    {

    }
    public void onClose()
    {

    }


    public int getWindowAnimExitId() {
        return windowAnimExitId;
    }

    public void setWindowAnimExitId(int windowAnimExitId) {
        this.windowAnimExitId = windowAnimExitId;
    }

    public int getWindowAnimEnterId() {
        return windowAnimEnterId;
    }

    public void setWindowAnimEnterId(int windowAnimEnterId) {
        this.windowAnimEnterId = windowAnimEnterId;
    }

    public int getContentAnimEnterId() {
        return contentAnimEnterId;
    }

    public void setContentAnimEnterId(int contentAnimEnterId) {
        this.contentAnimEnterId = contentAnimEnterId;
    }

    public int getContentAnimExitId() {
        return contentAnimExitId;
    }

    public void setContentAnimExitId(int contentAnimExitId) {
        this.contentAnimExitId = contentAnimExitId;
    }

    public boolean isCanOutsideClose() {
        return canOutsideClose;
    }

    public void setCanOutsideClose(boolean canOutsideClose) {
        this.canOutsideClose = canOutsideClose;
    }

    public boolean isCanDismis() {
        return canDismis;
    }

    public void setCanDismis(boolean canDismis) {
        this.canDismis = canDismis;
    }

    public boolean isCanBackClose() {
        return canBackClose;
    }

    public void setCanBackClose(boolean canBackClose) {
        this.canBackClose = canBackClose;
    }
}

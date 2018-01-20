package com.tjbaobao.ledhelper.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tjbaobao.ledhelper.R;

/**
 * Created by TJbaobao on 2018/1/6.
 */

public class SaveDialog extends BaseDialog {
    private TextView tv_tip ,tv_ok,tv_cancel;
    private EditText editText ;
    private boolean isShowEt = false;
    public SaveDialog(@NonNull Context context) {
        super(context, R.layout.dialog_save_layout);
        editText = baseView.findViewById(R.id.et_title);
    }

    public SaveDialog(Context context,String tip)
    {
        super(context, R.layout.dialog_save_layout);
        tv_tip = (TextView) baseView.findViewById(R.id.tv_tip);
        tv_tip.setText(tip);
        editText = baseView.findViewById(R.id.et_title);
    }

    public SaveDialog(@NonNull Context context, String tip, String ok, String cancel) {
        super(context, R.layout.dialog_save_layout);
        tv_tip = (TextView) baseView.findViewById(R.id.tv_tip);
        tv_ok = (TextView) baseView.findViewById(R.id.tv_ok);
        tv_cancel = (TextView) baseView.findViewById(R.id.tv_cancel);
        editText = baseView.findViewById(R.id.et_title);

        tv_tip.setText(tip);
        tv_ok.setText(ok);
        tv_cancel.setText(cancel);
    }

    @Override
    public void onContinueClick() {
        String text = editText.getText().toString();
        if(!text.equals("")||!isShowEt)
        {
            super.onContinueClick();
            onSave(text);
        }
    }


    public void setShowEt(boolean showEt) {
        isShowEt = showEt;
        if(isShowEt)
        {
            editText.setVisibility(View.VISIBLE);
        }
        else
        {
            editText.setVisibility(View.GONE);
        }
    }
    public void setText(String text)
    {
        editText.setText(text);
    }

    public void onSave(String text)
    {

    }
}

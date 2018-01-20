package com.tjbaobao.ledhelper.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.tjbaobao.ledhelper.R;


/**
 * Created by TJbaobao on 2017/10/11.
 */

public class YesNoDialog extends BaseDialog {
    private TextView tv_tip ,tv_ok,tv_cancel;
    public YesNoDialog(@NonNull Context context) {
        super(context, R.layout.dialog_yes_no_layout);
    }

    public YesNoDialog(Context context,String tip)
    {
        super(context, R.layout.dialog_yes_no_layout);
        tv_tip = (TextView) baseView.findViewById(R.id.tv_tip);
        tv_tip.setText(tip);
    }

    public YesNoDialog(@NonNull Context context, String tip, String ok, String cancel) {
        super(context, R.layout.dialog_yes_no_layout);
        tv_tip = (TextView) baseView.findViewById(R.id.tv_tip);
        tv_ok = (TextView) baseView.findViewById(R.id.tv_ok);
        tv_cancel = (TextView) baseView.findViewById(R.id.tv_cancel);

        tv_tip.setText(tip);
        tv_ok.setText(ok);
        tv_cancel.setText(cancel);
    }
}

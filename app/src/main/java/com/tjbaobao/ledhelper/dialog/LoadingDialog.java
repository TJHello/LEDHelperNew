package com.tjbaobao.ledhelper.dialog;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tjbaobao.ledhelper.R;


/**
 * Created by TJbaobao on 2017/12/2.
 */

public class LoadingDialog extends BaseDialog {
    public LoadingDialog(@NonNull Context context) {
        super(context, R.layout.dialog_loading_layout);
        setCanOutsideClose(false);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }
}

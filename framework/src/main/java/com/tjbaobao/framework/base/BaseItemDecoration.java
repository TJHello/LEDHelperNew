package com.tjbaobao.framework.base;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tjbaobao.framework.util.Tools;

/**
 * Created by TJbaobao on 2018/1/6.
 */

public class BaseItemDecoration extends RecyclerView.ItemDecoration {

    public static final int DEF_SPACING = Tools.dpToPx(3);

    public static final int TYPE_LINE_VERTICAL = 1;
    public static final int TYPE_LINE_HORIZONTAL = 2;
    private int type = TYPE_LINE_VERTICAL;

    public static BaseItemDecoration getLineVerticalDecoration(int spacing) {
        return new BaseItemDecoration(spacing,TYPE_LINE_VERTICAL);
    }
    public static BaseItemDecoration getLineHorizontalDecoration(int spacing) {
        return new BaseItemDecoration(spacing,TYPE_LINE_HORIZONTAL);
    }

    private int spacing =0;

    public BaseItemDecoration(int spacing,int type)
    {
        this.spacing = spacing;
        this.type = type;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(type==TYPE_LINE_VERTICAL)
        {
            outRect.bottom = spacing ;
        }
        else if(type==TYPE_LINE_HORIZONTAL)
        {
            outRect.right = spacing ;
        }
        super.getItemOffsets(outRect, view, parent, state);
    }
}

package com.tjbaobao.ledhelper.popwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tjbaobao.framework.base.BaseAdapter;
import com.tjbaobao.framework.util.DeviceUtil;
import com.tjbaobao.ledhelper.R;
import com.tjbaobao.ledhelper.entity.ui.PoPMenuListInfo;

import java.util.ArrayList;

/**
 * Created by TJbaobao on 2017/9/20.
 */

public class MenuPopupWindow extends BasePopupWindow {

    private ListView listView;
    private MyAdapter adapter ;
    private ArrayList<PoPMenuListInfo> menuInfoList = new ArrayList<>();

    public MenuPopupWindow(Context context, View viewParent) {
        super(context, viewParent, R.layout.app_menu_window_layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listView = (ListView) view.findViewById(R.id.listview);
        adapter = new MyAdapter(context,menuInfoList,R.layout.app_menu_window_item_layout);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MyOnItemClickListener());


    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        int[] offs = calculatePopWindowPos(anchor,view);
        super.showAtLocation(anchor, Gravity.TOP | Gravity.START,offs[0], offs[1]);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }

    public void setData(ArrayList<PoPMenuListInfo> menuInfoList)
    {
        this.menuInfoList.clear();
        for (PoPMenuListInfo info:menuInfoList)
        {
            this.menuInfoList.add(info);
        }
        adapter.notifyDataSetChanged();
    }

    private class MyAdapter extends BaseAdapter
    {
        public MyAdapter(Context context, ArrayList<?> arrayList, int resId) {
            super(context, arrayList, resId);
        }

        private class Holder
        {
            private ImageView iv_image ;
            private TextView tv_text ;
            private View ll_index ;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if(convertView==null)
            {
                convertView = LayoutInflater.from(parent.getContext()).inflate(resId,null);
                holder = new Holder() ;
                holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                holder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
                holder.ll_index = convertView.findViewById(R.id.ll_index);
                convertView.setTag(holder);
                //convertView.setBackgroundResource(R.drawable.app_ripple);
            }
            else
            {
                holder = (Holder) convertView.getTag();
            }
            PoPMenuListInfo info = (PoPMenuListInfo) arrayList.get(position);
            holder.iv_image.setImageResource(info.getImageId());
            holder.tv_text.setText(info.getText());

            return convertView;
        }
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView   window的内容布局
     * @return window显示的左上角的xOff,yOff坐标
     */
    private static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
         // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = DeviceUtil.getScreenHeight();
        final int screenWidth = DeviceUtil.getScreenWidth();
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = anchorLoc[0] +anchorView.getWidth();
            windowPos[1] = anchorLoc[1] - windowHeight-anchorView.getHeight();
        } else {
            windowPos[0] = anchorLoc[0]+anchorView.getWidth();
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(onMenuClickListener!=null)
            {
                onMenuClickListener.onMenuClick(position);
            }
            dismiss();
        }
    }


    private OnMenuClickListener onMenuClickListener ;
    public interface OnMenuClickListener
    {
        public void onMenuClick(int position);
    }

    public OnMenuClickListener getOnMenuClickListener() {
        return onMenuClickListener;
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }
}

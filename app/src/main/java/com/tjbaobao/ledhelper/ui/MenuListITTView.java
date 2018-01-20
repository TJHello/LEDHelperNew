package com.tjbaobao.ledhelper.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tjbaobao.framework.base.BaseAdapter;
import com.tjbaobao.framework.ui.base.BaseLinearLayout;
import com.tjbaobao.ledhelper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TJbaobao on 2017/12/30.
 */

public class MenuListITTView extends BaseLinearLayout {

    private ListView listView ;
    private MyAdapter adapter ;
    private ArrayList<MenuItemInfo> menuItemInfoList = new ArrayList<>();

    public MenuListITTView(Context context) {
        super(context);
    }

    public MenuListITTView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuListITTView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }

    public class MenuItemInfo
    {
        private int iconId ;
        private String name ;
        private boolean isShowRightIcon ;

        public int getIconId() {
            return iconId;
        }

        public void setIconId(int iconId) {
            this.iconId = iconId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isShowRightIcon() {
            return isShowRightIcon;
        }

        public void setShowRightIcon(boolean showRightIcon) {
            isShowRightIcon = showRightIcon;
        }
    }

    public void setData(List<MenuItemInfo> infoList)
    {
        listView = new ListView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listView.setLayoutParams(layoutParams);
        adapter = new MyAdapter(context,menuItemInfoList, R.layout.menu_list_itt_view_layout);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MyOnClickListener());
//        listView.setDivider(new ColorDrawable(Color.parseColor("#636363")));
//        listView.setDividerHeight(Tools.dpToPx(1));
        this.addView(listView);
        menuItemInfoList.clear();
        for(MenuItemInfo menuItemInfo:infoList)
        {
            menuItemInfoList.add(menuItemInfo);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    private class MyAdapter extends BaseAdapter
    {
        public MyAdapter(Context context, ArrayList<?> arrayList, int resId) {
            super(context, arrayList, resId);
        }

        private class Holder
        {
            private ImageView iv_image;
            private TextView tv_name ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if(convertView==null)
            {
                convertView = LayoutInflater.from(parent.getContext()).inflate(resId,null);
                holder = new Holder();
                holder.iv_image = convertView.findViewById(R.id.iv_image);
                holder.tv_name = convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            }
            else
            {
                holder = (Holder) convertView.getTag();
            }
            MenuItemInfo info = menuItemInfoList.get(position);
            holder.iv_image.setImageResource(info.getIconId());
            holder.tv_name.setText(info.getName());

            return super.getView(position, convertView, parent);
        }
    }


    public class MyOnClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(onMenuItemClickListener!=null)
            {
                onMenuItemClickListener.onClick(position);
            }
        }
    }

    private OnMenuItemClickListener onMenuItemClickListener ;

    public OnMenuItemClickListener getOnMenuItemClickListener() {
        return onMenuItemClickListener;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    public interface OnMenuItemClickListener
    {
        public void onClick(int position);
    }
}

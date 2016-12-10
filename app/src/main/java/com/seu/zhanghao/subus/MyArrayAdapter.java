package com.seu.zhanghao.subus;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by zhanghao7 on 2016/12/7.
 */

public class MyArrayAdapter extends BaseAdapter{
    private List<String> mlist;

    MyArrayAdapter(List<String> list) {
        this.mlist = list;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       //View view = Layout
        return null;
    }
}

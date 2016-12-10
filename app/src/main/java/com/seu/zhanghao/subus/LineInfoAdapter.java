package com.seu.zhanghao.subus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhanghao7 on 2016/12/7.
 */

public class LineInfoAdapter extends ArrayAdapter<LineInfo>{
    private int resourceId;
    public LineInfoAdapter(Context context, int textViewResourceId, List<LineInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LineInfo lineInfo = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView linename = (TextView) view.findViewById(R.id.linename);
        linename.setText(lineInfo.getLinename());

        return view;
    }
}

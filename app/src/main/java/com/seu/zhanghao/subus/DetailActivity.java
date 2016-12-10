package com.seu.zhanghao.subus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import android.util.Log;

/**
 * Created by zhanghao7 on 2016/12/7.
 */

public class DetailActivity extends Activity {
    private ExpandableListView listView;
    String detail_resonse = null;
    List<Map<String, Object>> listInfo=new LinkedList<Map<String,Object>>();
    private Map<String, List<String>> dataset = new LinkedHashMap<>();
    Map<String,List> totalMap = new LinkedHashMap<String, List>();
    List<String> parentList = new LinkedList<>();
    private MyExpandableListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.line_detail);
        Intent intent = getIntent();
        listView = (ExpandableListView) findViewById(R.id.expandablelistview);
        detail_resonse = intent.getStringExtra("detail_response");
        Log.d("debug",detail_resonse);
        initData(detail_resonse);
        adapter = new MyExpandableListViewAdapter();
        listView.setAdapter(adapter);
    }


    //这个函数是返回每个一级列表所对应的childrenList，之后会被initData()调用,
    // totalMap里的list存的是每个站点的childrenList，而childrenList里要存的是该站点之前的所有到站车次信息，也为list存储
    private Map<String, List> getChildenList(List<Map<String, Object>> listInformation) {
        Map<String,List> totalMap = new LinkedHashMap<String, List>();
        for (int i = 0; i < listInformation.size() ; i++) {
            List<Object> list = new LinkedList<>();
            totalMap.put("childrenList" + i, list);
        }

        for (int i=0;i<listInformation.size();i++){
            for(int j=0;j<=i;j++){
                Map<String, Object> eachMap = new HashMap<>();
                eachMap = listInformation.get(j);
                if(!eachMap.get("carNumber").equals("")){
                    int distance = i-j;
                    totalMap.get("childrenList"+i).add("车辆"+eachMap.get("carNumber")+"于 "+ eachMap.get("time")+" 到达"+
                            eachMap.get("station")+"距该站点还有"+distance+"站");
                }
            }
        }
        return totalMap;
    }

    //获取父级列表
    private List<String> getParentList(List<Map<String, Object>> listInformation) {
        for (int i = 0; i < listInformation.size() ; i++) {
            parentList.add((String) listInformation.get(i).get("station"));
        }
        return parentList;
    }


    private void initData(String resonse){
        List<String> parentList = new LinkedList<>();

        listInfo = ResolveUtil.resolveHtml(detail_resonse);
        parentList = getParentList(listInfo);
        totalMap = getChildenList(listInfo);

        for (int i=0;i<listInfo.size();i++){
            dataset.put(parentList.get(i),totalMap.get("childrenList"+i)) ;
        }

    }

    //自定义Adapte类，用于为ExpandableListView提供数据
    private class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        //  获得某个父项的某个子项
        @Override
        public Object getChild(int parentPos, int childPos) {
            return dataset.get(parentList.get(parentPos)).get(childPos);
        }

        //  获得父项的数量
        @Override
        public int getGroupCount() {
            return dataset.size();
        }

        //  获得某个父项的子项数目
        @Override
        public int getChildrenCount(int parentPos) {
            return dataset.get(parentList.get(parentPos)).size();
        }

        //  获得某个父项
        @Override
        public Object getGroup(int parentPos) {
            return dataset.get(parentList.get(parentPos));
        }

        //  获得某个父项的id
        @Override
        public long getGroupId(int parentPos) {
            return parentPos;
        }

        //  获得某个父项的某个子项的id
        @Override
        public long getChildId(int parentPos, int childPos) {
            return childPos;
        }

        //  按函数的名字来理解应该是是否具有稳定的id，这个方法目前一直都是返回false，没有去改动过
        @Override
        public boolean hasStableIds() {
            return false;
        }

        //  获得父项显示的view
        @Override
        public View getGroupView(int parentPos, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) DetailActivity
                        .this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.parent_item, null);
            }
            view.setTag(R.layout.parent_item, parentPos);
            view.setTag(R.layout.child_item, -1);
            TextView text = (TextView) view.findViewById(R.id.parent_title);
            text.setText(parentList.get(parentPos));
            return view;
        }

        //  获得子项显示的view
        @Override
        public View getChildView(int parentPos, int childPos, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) DetailActivity
                        .this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.child_item, null);
            }
            view.setTag(R.layout.parent_item, parentPos);
            view.setTag(R.layout.child_item, childPos);
            TextView text = (TextView) view.findViewById(R.id.child_title);
            text.setText(dataset.get(parentList.get(parentPos)).get(childPos));
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DetailActivity.this, "嘿嘿嘿，你点我干哈", Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }

        //  子项是否可选中，如果需要设置子项的点击事件，需要返回true
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }

    }


}

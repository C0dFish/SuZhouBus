package com.seu.zhanghao.subus;

import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private EditText editText;
    private Button searchButton;
    private TextView responseText;
    private ExpandableListView expandableListView;
    // private MyExpandableListViewAdapter adapter;
    private ListView listView;
    private List<String> dataList = new ArrayList<String>();
    private String[] data = new String[8];
    private static final String URL_LINK = "http://www.szjt.gov.cn/apts/APTSLine.aspx";
    public static final int SHOW_RESPONSE = 0;
    public static final int TRAN_RESPONSE = 1;
    private List<LineInfo> lineInfoList = new ArrayList<LineInfo>();

    Handler handler = new Handler(){
        //  List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    String response = (String) msg.obj;
                    List<Map<String, Object>> list = new LinkedList<>();
                    list = ResolveUtil.resolveLineHtml(response.toString());

                    List<String> mliist = new LinkedList<>();
                    for(Map map:list)
                    {
                       // Log.d("nihao",(String) map.get("direction"));
                       String string =  (String) map.get("direction");
                       System.out.println(string);
                       mliist.add(string);
//                      Log.d("debug",data[i].toString());
                    }

                    listView= (ListView) findViewById(R.id.list_view);
                    initLineInfo(list);
                    LineInfoAdapter arrayadapter = new LineInfoAdapter(MainActivity.this,R.layout.line_info,lineInfoList);
                   // ArrayAdapter<String> arrayadapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,mliist);

                    listView.setAdapter(arrayadapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            LineInfo lineInfo = lineInfoList.get(position);
                            sendRequestWithHttpURLConnection(lineInfo.getLinehref());

                        }
                    });
                    break;

                case  TRAN_RESPONSE:
                    String deatail_reaponse = (String) msg.obj;
                    Log.d("debug",deatail_reaponse);
                    Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                    intent.putExtra("detail_response",deatail_reaponse);
                    Log.d("debug","启动新页面");
                    startActivity(intent);


            }
        }
    };

    //初始化线路列表数据
    private void initLineInfo(List<Map<String, Object>> list){
        List<String> lineNameList = new LinkedList<>();
        List<String> lineHrefList = new LinkedList<>();
        for(Map map:list)
        {
            // Log.d("nihao",(String) map.get("direction"));
            String lineName =  (String) map.get("direction");
            System.out.println(lineName);
            lineNameList.add(lineName);
            String lineHref = (String) map.get("lineLink");
            System.out.println(lineHref);
            lineHrefList.add(lineHref);
//                      Log.d("debug",data[i].toString());
        }
        LineInfo line = new LineInfo(lineNameList.get(0),lineHrefList.get(0));
        lineInfoList.add(line);
        LineInfo reverseline = new LineInfo(lineNameList.get(1),lineHrefList.get(1));
        lineInfoList.add(reverseline);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  responseText = (TextView) findViewById(R.id.textView3);
        editText= (EditText) findViewById(R.id.input_text);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus){
                    editText.setHint("请输入您要查询的线路");
                }else {
                    editText.setHint(null);
                }
            }
        });

        //按钮响应
        searchButton= (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.search_button){
                    sendLineRequest(editText.getText().toString());
                    Log.d("debug","here");
                }
            }
        });

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            //  Intent intent =new Intent(MainActivity.this,BusAssisActivity.class);
        } else if (id == R.id.nav_gallery) {
            Intent intent =new Intent(MainActivity.this,InfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //调用queryRequest1(String lineName)后解析List内的链接，并将其传入到queryRequest2(String hrefString)中
    //返回查询结果
    public static List getBusInfo(String hrefString){
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        //  String htmlString = doGet(hrefString);
        // list=resolveHtml(htmlString);

        return list;
    }


    /*
    获取详细公交线路列表的子线程
  */
    private void sendLineRequest(final String lineNumber){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("debug","sendLineRequest");
                doPost(lineNumber);
            }
        }) .start();
    }

    /*
        获取详细公交线路信息的子线程
     */
    private void sendRequestWithHttpURLConnection(final String queryString){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("debug","run");
                doGet(queryString);
            }
        }).start();

    }


    /*
    获取用户输入线路号所查询到的所有线路信息实现
     */

    public  String doPost(String lineName) {

        URL get_url = null;
        String responseHtml =null;
        HttpURLConnection connection = null;
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        try {
            get_url = new URL(URL_LINK);
//            Log.d("debug",get_url.toString());
            connection = (HttpURLConnection) get_url.openConnection();

            connection.setRequestMethod("POST");
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes("ctl00$MainContent$SearchLine=搜索" +
                    "&__VIEWSTATEGENERATOR=964EC381" +
                    "&__VIEWSTATE=/wEPDwUJNDk3MjU2MjgyD2QWAmYPZBYCAgMPZBYCAgEPZBYCAgYPDxYCHgdWaXNpYmxlaGRkZArYd9NZeb6lYhNOScqHVvOmnKWkIejcJ7J2157Nz6l1" +
                    "&__EVENTVALIDATION=/wEWAwL5m9CTDgL88Oh8AqX89aoKFjHWxIvicIW2NoJRKPFu7zDvdWiw74UWlUePz1dAXk4=" +
                    "&ctl00$MainContent$LineName="+lineName);
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                response.append(line);
            }
            Log.d("debug",responseHtml=response.toString());
            //返回解析列表
          //  list= resolveLineHtml(response.toString());
            Message message =  new Message();
            message.what = SHOW_RESPONSE;
            //将服务器返回的结果存放到Message中
            message.obj = response.toString();
            handler.sendMessage(message);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }
        return responseHtml;

    }

    //执行一个HTTP GET请求，返回请求响应的HTML
    //url 请求的URL地址
    //queryString 请求的查询参数,可以为null
    //pretty是否美化
    //返回请求响应的HTML
    public String doGet(String queryString) {
        URL get_url = null;
        String responseHtml =null;
        HttpURLConnection connection = null;
        try {
            get_url = new URL(URL_LINK+queryString);
//            Log.d("debug",get_url.toString());
            connection = (HttpURLConnection) get_url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                response.append(line);
            }
//            Log.d("debug",responseHtml=response.toString());
            Message message =  new Message();
            message.what = TRAN_RESPONSE;
            //将服务器返回的结果存放到Message中
            message.obj = response.toString();
            handler.sendMessage(message);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }
        return responseHtml;
    }




/*
    private class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

        //  获得某个父项的某个子项
        @Override
        public Object getChild(int parentPos, int childPos) {
            return dataset.get(parentList[parentPos]).get(childPos);
        }

        //  获得父项的数量
        @Override
        public int getGroupCount() {
            if (dataset == null) {
                Toast.makeText(ExpandableListViewTestActivity.this, "dataset为空", Toast.LENGTH_SHORT).show();
                return 0;
            }
            return dataset.size();
        }

        //  获得某个父项的子项数目
        @Override
        public int getChildrenCount(int parentPos) {
            if (dataset.get(parentList[parentPos]) == null) {
                Toast.makeText(ExpandableListViewTestActivity.this, "\" + parentList[parentPos] + \" + 数据为空", Toast.LENGTH_SHORT).show();
                return 0;
            }
            return dataset.get(parentList[parentPos]).size();
        }

        //  获得某个父项
        @Override
        public Object getGroup(int parentPos) {
            return dataset.get(parentList[parentPos]);
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

        //  按函数的名字来理解应该是是否具有稳定的id，这个函数目前一直都是返回false，没有去改动过
        @Override
        public boolean hasStableIds() {
            return false;
        }

        //  获得父项显示的view
        @Override
        public View getGroupView(int parentPos, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) ExpandableListViewTestActivity
                        .this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.parent_item, null);
            }
            view.setTag(R.layout.parent_item, parentPos);
            view.setTag(R.layout.child_item, -1);
            TextView text = (TextView) view.findViewById(R.id.parent_title);
            text.setText(parentList[parentPos]);
            return view;
        }

        //  获得子项显示的view
        @Override
        public View getChildView(int parentPos, int childPos, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) ExpandableListViewTestActivity
                        .this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.child_item, null);
            }
            view.setTag(R.layout.parent_item, parentPos);
            view.setTag(R.layout.child_item, childPos);
            TextView text = (TextView) view.findViewById(R.id.child_title);
            text.setText(dataset.get(parentList[parentPos]).get(childPos));
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ExpandableListViewTestActivity.this, "点到了内置的textview",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }

        //  子项是否可选中，如果需要设置子项的点击事件，需要返回true
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }


    private void initialData(List<Map<String, Object>> list ) {

        childrenList1.add(parentList[0] + "-" + "first");
        childrenList1.add(parentList[0] + "-" + "second");
        childrenList1.add(parentList[0] + "-" + "third");
        childrenList2.add(parentList[1] + "-" + "first");
        childrenList2.add(parentList[1] + "-" + "second");
        childrenList2.add(parentList[1] + "-" + "third");
        childrenList3.add(parentList[2] + "-" + "first");
        childrenList3.add(parentList[2] + "-" + "second");
        childrenList3.add(parentList[2] + "-" + "third");
        dataset.put(parentList[0], childrenList1);
        dataset.put(parentList[1], childrenList2);
        dataset.put(parentList[2], childrenList3);
    }
*/
}

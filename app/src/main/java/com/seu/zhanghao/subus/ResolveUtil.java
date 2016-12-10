package com.seu.zhanghao.subus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanghao7 on 2016/12/7.
 */

public class ResolveUtil {
    /*
解析返回的方向和链接
 */
    public static List resolveLineHtml(String responseHtml) {
        List<Map<String, Object>> list=new LinkedList<>();
        Document document = Jsoup.parse(responseHtml);

        Elements trs=document.select("tr");

        int totalTrs=trs.size();
        if(totalTrs>2){
            for (int i = 0; i <2; i++) {
                Elements tds=trs.get(i+2).select("td");
                //获取每个tr标签中td的个数
                int totalTds=tds.size();
                //临时存放各个属性的map
                Map<String,Object> map=new LinkedHashMap<>();
                String subHerf;
                String repDirection;
                //	String
                for (int j = 0; j < totalTds; j++) {
                    switch (j) {
                        case 0:
                            subHerf=tds.get(j).select("a").attr("href").toString();
                            subHerf=subHerf.substring(13);
                            map.put("lineLink",subHerf);
                            System.out.println(subHerf);
                            break;
                        case 1:
                            repDirection=tds.get(j).html().toString();
                            repDirection=repDirection.replace("=&gt;", "→");
                            map.put("direction", repDirection);
                            System.out.println(repDirection);
                            break;
                        default:
                            break;
                    }
                }
                list.add(map);
            }
        }

        return list;
    }

    /*
    解析请求返回的公交详细信息，并将返回的信息存入到List<Map<String, Object>>中
     */

    public static List resolveHtml(String htmlString) {
        List<Map<String, Object>> list=new LinkedList<Map<String,Object>>();
        Document document = Jsoup.parse(htmlString);

        //将以“tr”开头的标签存入trs元素群中
        Elements trs=document.select("tr");

        int totalTrs=trs.size();
        if(totalTrs>2){
            for (int i = 0; i < totalTrs-2; i++) {
                Elements tds=trs.get(i+2).select("td");
                //获取每个tr标签中td的个数
                int totalTds=tds.size();
                //临时存放各个属性的map
                Map<String,Object> map=new HashMap<String, Object>();

                for (int j = 0; j < totalTds; j++) {
                    switch (j) {
                        case 0:
                            map.put("station", tds.get(j).select("a").html().toString());
                            System.out.println(tds.get(j).select("a").html().toString());
                            break;
                        case 1:
                            map.put("id", tds.get(j).html().toString());
                            System.out.println(tds.get(j).html().toString());

                            break;
                        case 2:
                            map.put("carNumber", tds.get(j).html().toString());
                            System.out.println(tds.get(j).html().toString());
                            break;
                        case 3:
                            map.put("time", tds.get(j).html().toString());
                            System.out.println(tds.get(j).html().toString());
                            System.out.println("------------------------------------------------------------------------");
                            break;
                        default:
                            break;
                    }
                }
                list.add(map);
            }
        }

        return list;

    }

}

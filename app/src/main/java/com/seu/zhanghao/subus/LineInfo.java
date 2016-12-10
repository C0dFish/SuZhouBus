package com.seu.zhanghao.subus;

/**
 * Created by zhanghao7 on 2016/12/7.
 */

public class LineInfo {
    private  String linename;
    private  String linehref;

    public LineInfo(String linename,String linehref) {
        this.linehref = linehref;
        this.linename = linename;
    }

    public String getLinename() {
        return linename;
    }

    public void setLinename(String linename) {
        this.linename = linename;
    }

    public String getLinehref() {
        return linehref;
    }

    public void setLinehref(String linehref) {
        this.linehref = linehref;
    }
}

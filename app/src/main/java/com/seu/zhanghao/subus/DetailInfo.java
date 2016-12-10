package com.seu.zhanghao.subus;

/**
 * Created by zhanghao7 on 2016/12/7.
 */

public class DetailInfo
{
    private String station;
    private String carNumber;
    private String time;

    public DetailInfo(String station, String carNumber, String time) {
        this.station = station;
        this.carNumber = carNumber;
        this.time = time;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

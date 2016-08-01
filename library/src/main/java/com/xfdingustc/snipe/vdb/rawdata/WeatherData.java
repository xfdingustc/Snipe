package com.xfdingustc.snipe.vdb.rawdata;

/**
 * Created by laina on 16/7/12.
 */
public class WeatherData {

    public int tempF;
    public int windSpeedMiles;
    public int pressure;
    public int humidity;
    public int weatherCode;

    public WeatherData(int tempF, int windSpeedMiles, int pressure, int humidity, int weatherCode) {
        this.tempF = tempF;
        this.windSpeedMiles = windSpeedMiles;
        this.pressure = pressure;
        this.humidity = humidity;
        this.weatherCode = weatherCode;
    }


}

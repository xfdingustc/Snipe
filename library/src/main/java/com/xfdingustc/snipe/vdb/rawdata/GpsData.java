package com.xfdingustc.snipe.vdb.rawdata;

import java.io.Serializable;

/**
 * Created by Xiaofei on 2016/4/12.
 */
public class GpsData implements Serializable {

    public static final int GPS_F_LATLON = (1 << 0);
    public static final int GPS_F_ALTITUDE = (1 << 1);
    public static final int GPS_F_SPEED = (1 << 2);
    public static final int GPS_F_TIME = (1 << 3);
    public static final int GPS_F_TRACK = (1 << 4);

    public static class Coord implements Serializable {
        public double lat;
        public double lng;
        public double lat_orig;
        public double lng_orig;

        public void set(Coord other) {
            this.lat = other.lat;
            this.lng = other.lng;
            this.lat_orig = other.lat_orig;
            this.lng_orig = other.lng_orig;
        }
    }

    public int flags;
    public double speed;
    public double altitude;

    public int utc_time;
    public float track;
    public float accuracy;

    public final Coord coord = new Coord();

    public final boolean hasLatLng() {
        return (flags & GPS_F_LATLON) != 0;
    }

    public final boolean hasAltitude() {
        return (flags & GPS_F_ALTITUDE) != 0;
    }

    public final boolean hasSpeed() {
        return (flags & GPS_F_SPEED) != 0;
    }

    public final boolean hasTime() {
        return (flags & GPS_F_TIME) != 0;
    }

    public final boolean hasTrack() {
        return (flags & GPS_F_TRACK) != 0;
    }

    static public GpsData fromBinary(byte[] data) {
        GpsData result = new GpsData();

        ByteStream stream = new ByteStream(data);

        result.flags = stream.readInt32();
        result.speed = stream.readFloat();
        result.coord.lat = result.coord.lat_orig = stream.readDouble();
        result.coord.lng = result.coord.lng_orig = stream.readDouble();
        result.altitude = stream.readDouble();

        result.utc_time = stream.readInt32();
        result.track = stream.readFloat();
        result.accuracy = stream.readFloat();

        result.GMS84ToGCJ02();

        return result;
    }

    // ===========================================================================

    public static final double ECa = 6378245.0;
    public static final double ECee = 0.00669342162296594323;
    public static final double pi = 3.14159265358979324;

    public static boolean outOfChina(double lat, double lng) {
        if (lng < 73.3 || lng > 135.17)
            return true;
        if (lat < 3.5 || lat > 53.6)
            return true;
        if (lat < 39.8 && lat > 124.3)// Korea & Japan
            return true;
        if (lat < 25.4 && lat > 120.3)// Taiwan
            return true;
        if (lat < 24 && lat > 119)// Taiwan
            return true;
        if (lat < 21 && lat < 108.1)// SouthEastAsia
            return true;
        if (lng < 108 && (lng + lat < 107))
            return true;
        if (lat < 26.8 && lat < 97)// India
            return true;
        return false;
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public static double transformLng(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

    public void GMS84ToGCJ02() {
        GMS84ToGCJ02(this.coord);
    }

    // coord.lat, lng -> new lat, lng
    public static void GMS84ToGCJ02(Coord coord) {
        double lat = coord.lat;
        double lng = coord.lng;

        if (outOfChina(lat, lng)) {
            return;
        }

        double x = lng - 105.0;
        double y = lat - 35.0;

        double dLat = transformLat(x, y);
        double dLng = transformLng(x, y);

        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ECee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((ECa * (1 - ECee)) / (magic * sqrtMagic) * pi);
        dLng = (dLng * 180.0) / (ECa / sqrtMagic * Math.cos(radLat) * pi);

        coord.lat = lat + dLat;
        coord.lng = lng + dLng;
    }

}

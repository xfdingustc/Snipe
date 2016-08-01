package com.xfdingustc.snipe.utils;

/**
 * Created by Xiaofei on 2016/7/19.
 */
public class BufferUtils {
    public static int readi32(byte[] data, int index) {
        int result = (int) data[index] & 0xFF;
        result |= ((int) data[index + 1] & 0xFF) << 8;
        result |= ((int) data[index + 2] & 0xFF) << 16;
        result |= ((int) data[index + 3] & 0xFF) << 24;
        return result;
    }

    public static int read16(byte[] data, int index) {
        int result = (int) data[index] & 0xFF;
        result |= ((int) data[index + 1] & 0xFF) << 8;
        return result;
    }
}

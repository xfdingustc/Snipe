package com.xfdingustc.snipe.vdb.rawdata;

import java.io.Serializable;

/**
 * Created by Xiaofei on 2016/4/12.
 */
public class IioData implements Serializable {
    public int accX;
    public int accY;
    public int accZ;

    //---------------------------------------------------
    public int version;   // IIO_VERSION
    int size;      // sizeof(iio_raw_data_s)
    public int flags;     // IIO_F_ACCEL etc
    //---------------------------------------------------

    // gyro : Dps x 1000 = mDps
    public int gyro_x;
    public int gyro_y;
    public int gyro_z;

    // magn : uT x 1000000
    public int magn_x;
    public int magn_y;
    public int magn_z;

    // Orientation
    // Euler : Degrees x 1000 = mDegrees
    public int euler_heading;
    public int euler_roll;
    public int euler_pitch;

    // Quaternion : Raw, no unit
    public int quaternion_w;
    public int quaternion_x;
    public int quaternion_y;
    public int quaternion_z;

    // Pressure: Pa x 1000
    public int pressure;

    public static final int ACC_DATA_LENGTH_V0 = 12;

    @Override
    public String toString() {
        return String.format("AccX[%d], AccY[%d], AccZ[%d], EulerRoll[%d]", accX, accY, accZ,
            euler_roll);
    }

    public static IioData fromBinary(byte[] data) {
        IioData accData = new IioData();
        accData.parseData(data);
        return accData;
    }

    private void parseData(byte[] data) {

        ByteStream stream = new ByteStream(data);
        accX = stream.readInt32();
        accY = stream.readInt32();
        accZ = stream.readInt32();

        if (data.length == ACC_DATA_LENGTH_V0) {
            return;
        }

        version = stream.readInt16();
        size = stream.readInt16();
        if (size != data.length) {
            version = 0;
            return;
        }
        flags = stream.readInt32();

        gyro_x = stream.readInt32();
        gyro_y = stream.readInt32();
        gyro_z = stream.readInt32();

        magn_x = stream.readInt32();
        magn_y = stream.readInt32();
        magn_z = stream.readInt32();

        euler_heading = stream.readInt32();
        euler_roll = stream.readInt32();
        euler_pitch = stream.readInt32();

        quaternion_w = stream.readInt32();
        quaternion_x = stream.readInt32();
        quaternion_y = stream.readInt32();
        quaternion_z = stream.readInt32();

        pressure = stream.readInt32();
    }


}

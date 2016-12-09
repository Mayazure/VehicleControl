package com.example.mayaz.vehiclecontrol.Util;

/**
 * Created by mayaz on 2016/12/9.
 */

public class Const {

    public static final byte[] R_Table = {
            -54,  -55,  -56,  -57,  -58,  -59,  -60,
            -61,  -62,  -63,  -127, -126, -125, -124,
            -123, -122, -121, -120, -119, -118, -128};//右侧控制指令表

    public static final byte[] L_Table = {
            74, 73, 72, 71, 70, 69, 68,
            67, 66, 65, 1,  2,  3,  4,
            5,  6,  7,  8,  9,  10, 0};//左侧控制指令表

    public static final String address = "20:16:05:30:97:82"; //要连接的蓝牙设备MAC地址（测试用）

    public static final boolean Debug = true;
}

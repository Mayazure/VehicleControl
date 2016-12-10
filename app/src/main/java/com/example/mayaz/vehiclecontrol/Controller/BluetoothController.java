package com.example.mayaz.vehiclecontrol.Controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by mayaz on 2016/12/9.
 */

public class BluetoothController {

    private BluetoothAdapter mBluetoothAdapter = null;//本地蓝牙适配器
    private BluetoothSocket btSocket = null;//蓝牙通讯套接字
    private OutputStream outStream = null;//输出流
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String address = null;// 要连接的蓝牙设备地址

    private static final String TAG = "BluetoothController";
    private Context context = null;

    public BluetoothController(){
        init(null, null);
    }

    public BluetoothController(String address){
        init(null, address);
    }

    public BluetoothController(Context context, String address){
        init(context, address);
    }

    private void init(Context context, String address){
        this.context = context;
        this.address = address;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//获取本地蓝牙适配器
        mBluetoothAdapter.enable();//开启蓝牙（非用户授权模式）
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);//生成远程蓝牙设备
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);//生成蓝牙套接字
        } catch (IOException e) {
            DisplayToast("无法创建套接字");
        }
    }

    public void setAddr(String addr){
        this.address = addr;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void connect(){
        try {
            btSocket.connect();//套接字连接
            DisplayToast("套接字连接成功");
        } catch (IOException e) {
            DisplayToast("无法连接到蓝牙设备");
            try {
                btSocket.close();//关闭套接字
            } catch (IOException e2) {
                DisplayToast("无法关闭套接字");
            }
        }
    }

    public void send(byte[] Ctr, int len){
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Bluetooth: Output stream creation failed.", e);
        }
        try {
            for (int i=0; i<len; i++){
                outStream.write(Ctr[i]);
            }
        } catch (IOException e) {
            Log.e(TAG, "Bluetooth: Exception during write.", e);
            DisplayToast("无法发送信息");
        }
    }

    public void close(){
        if(btSocket.isConnected()){
            try {
                btSocket.close();//关闭套接字
            } catch (IOException e) {
                e.printStackTrace();
                DisplayToast("套接字无法关闭");
            }
        }
    }

    private void DisplayToast(String str)
    {
        Toast toast=Toast.makeText(context.getApplicationContext(), str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 220);
        toast.show();
    }
}
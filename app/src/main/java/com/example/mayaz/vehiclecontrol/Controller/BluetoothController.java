package com.example.mayaz.vehiclecontrol.Controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.mayaz.vehiclecontrol.MainActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by mayaz on 2016/12/9.
 */

public class BluetoothController {

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String address = null;// 要连接的蓝牙设备地址

    private static final String TAG = "BluetoothController";
    private Context context = null;

    public BluetoothController(Context context, String address){
        this.context = context;
        this.address = address;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
    }

    public void connect(){

        DisplayToast("正在尝试连接");
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            DisplayToast("套接字创建失败！");
        }
        DisplayToast("连接成功");
        mBluetoothAdapter.cancelDiscovery();

        try {
            btSocket.connect();
            DisplayToast("套接字连接成功");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                DisplayToast("无法关闭套接字");
            }
        }
    }

    public void send(byte[] Ctr){
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
        }
        try {
            outStream.write(Ctr);
        } catch (IOException e) {
            Log.e(TAG, "ON RESUME: Exception during write.", e);
        }
    }

    private void DisplayToast(String str)
    {
        Toast toast=Toast.makeText(context, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 220);
        toast.show();
    }

}
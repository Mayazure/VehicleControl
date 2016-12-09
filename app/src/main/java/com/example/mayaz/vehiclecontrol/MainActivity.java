package com.example.mayaz.vehiclecontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private byte[] R_Table = {-54,-55,-56,-57,-58,-59,-60,-61,-62,-63,-127,-126,-125,-124,-123,-122,-121,-120,-119,-118,-128};
    private byte[] L_Table = {74,73,72,71,70,69,68,67,66,65,1,2,3,4,5,6,7,8,9,10,0};
    private boolean FLAG_SYNC = false;
    private boolean FLAG_VKEEP = false;

    private TextView LeftControlBarInfo;
    private TextView RightControlBarInfo;
    private TextView OutInfo;
    private SeekBar LeftControlBar;
    private SeekBar RightControlBar;
    private Button Clear;
    private ScrollView OutInfoScroll;
    private Switch Sync;

    private static final String TAG = "MainActiivity";
    private static final boolean D = true;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "20:16:05:30:97:82"; // <==要连接的蓝牙设备MAC地址


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initBluetooth();
    }

    private void initView(){
        LeftControlBarInfo = (TextView) findViewById(R.id.LeftControlBarInfo);
        RightControlBarInfo = (TextView) findViewById(R.id.RightControlBarInfo);
        LeftControlBar = (SeekBar) findViewById(R.id.LeftControlBar);
        RightControlBar = (SeekBar) findViewById(R.id.RightControlBar);
        OutInfo = (TextView) findViewById(R.id.OutInfo);
        Clear = (Button) findViewById(R.id.Clear);
        OutInfoScroll = (ScrollView) findViewById(R.id.OutInfoScroll);
        Sync = (Switch) findViewById(R.id.Sync);

        Sync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    FLAG_SYNC = true;
                }
                else{
                    FLAG_SYNC = false;
                }
            }
        });

        LeftControlBarInfo.setText("50");
        RightControlBarInfo.setText("50");

        LeftControlBar.setProgress(50);
        LeftControlBar.setOnSeekBarChangeListener(listener);

        RightControlBar.setProgress(50);
        RightControlBar.setOnSeekBarChangeListener(listener);

    }

    private void initBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
    }

    public void Clear(View v){
        OutInfo.setText("");
    }

    private SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int key = seekBar.getId();
            int code = seekBar.getProgress();//获取当前位置值
            int index;
            if (code == 50){
                index = 20;
            }
            else{
                index = (code/5 == 20)?19:code/5;
            }

            if (FLAG_SYNC == false){
                byte sendVal = 0;
                if(key==R.id.LeftControlBar){
                    sendVal = L_Table[index];
                    LeftControlBarInfo.setText(String.valueOf(code));
                }
                else if(key==R.id.RightControlBar){
                    sendVal = R_Table[index];
                    RightControlBarInfo.setText(String.valueOf(code));
                }
                OutInfo.append(sendVal + " ");
                OutInfoScroll.scrollTo(0, OutInfo.getMeasuredHeight() - OutInfoScroll.getHeight());

                try {
                    outStream = btSocket.getOutputStream();
                } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                }
                try {
                    outStream.write(sendVal);
                } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Exception during write.", e);
                }
            }
            else{
                byte sendVal[] = {L_Table[index],R_Table[index]};
                LeftControlBarInfo.setText(String.valueOf(code));
                RightControlBarInfo.setText(String.valueOf(code));
                OutInfo.append("(" + sendVal[0] + "," + sendVal[1] + ") ");
                OutInfoScroll.scrollTo(0, OutInfo.getMeasuredHeight() - OutInfoScroll.getHeight());
                try {
                    outStream = btSocket.getOutputStream();
                } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                }
                try {
                    outStream.write(sendVal);
                } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Exception during write.", e);
                }
                if(key==R.id.LeftControlBar){
                    RightControlBar.setProgress(code);
                }
                else if(key==R.id.RightControlBar){
                    LeftControlBar.setProgress(code);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int key = seekBar.getId();
            if(key==R.id.LeftControlBar){
                seekBar.setProgress(50);
                LeftControlBarInfo.setText(String.valueOf(seekBar.getProgress()));
            }
            if(key==R.id.RightControlBar){
                seekBar.setProgress(50);
                RightControlBarInfo.setText(String.valueOf(seekBar.getProgress()));
            }

        }
    };

    public void onResume(){

        super.onResume();

        if (D) {
            Log.e(TAG, "+ ON RESUME +");
            Log.e(TAG, "+ ABOUT TO ATTEMPT CLIENT CONNECT +");

        }
        DisplayToast("正在尝试连接智能小车，请稍后····");
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            DisplayToast("套接字创建失败！");
        }
        DisplayToast("成功连接智能小车！可以开始操控了~~~");
        mBluetoothAdapter.cancelDiscovery();

        try {
            btSocket.connect();
            DisplayToast("连接成功建立，数据连接打开！");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                DisplayToast("连接没有建立，无法关闭套接字！");
            }
        }
//         Create a data stream so we can talk to server.

        if (D)
            Log.e(TAG, "+ ABOUT TO SAY SOMETHING TO SERVER +");
    }

    public void DisplayToast(String str)
    {
        Toast toast=Toast.makeText(this, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 220);
        toast.show();
    }
}
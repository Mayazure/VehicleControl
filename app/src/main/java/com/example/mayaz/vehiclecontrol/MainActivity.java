package com.example.mayaz.vehiclecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mayaz.vehiclecontrol.Controller.BluetoothController;
import com.example.mayaz.vehiclecontrol.Util.*;

public class MainActivity extends AppCompatActivity {

    private TextView LeftControlBarInfo;
    private TextView RightControlBarInfo;
    private TextView OutInfo;
    private SeekBar LeftControlBar;
    private SeekBar RightControlBar;
    private Button Clear;
    private ScrollView OutInfoScroll;
    private Switch Sync;

    private boolean FLAG_SYNC = false;
    private boolean FLAG_VKEEP = false;

    private BluetoothController bluetoothController;

    private static final String TAG = "MainActiivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bluetoothController = new BluetoothController(getApplicationContext(),Const.address);
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
                byte sendVal[] = {0};
                if(key==R.id.LeftControlBar){
                    sendVal[0] = Const.L_Table[index];
                    LeftControlBarInfo.setText(String.valueOf(code));
                }
                else if(key==R.id.RightControlBar){
                    sendVal[0] = Const.R_Table[index];
                    RightControlBarInfo.setText(String.valueOf(code));
                }
                OutInfo.append(sendVal + " ");
                OutInfoScroll.scrollTo(0, OutInfo.getMeasuredHeight() - OutInfoScroll.getHeight());

                bluetoothController.send(sendVal);
            }
            else{
                byte sendVal[] = {Const.L_Table[index],Const.R_Table[index]};
                LeftControlBarInfo.setText(String.valueOf(code));
                RightControlBarInfo.setText(String.valueOf(code));
                OutInfo.append("(" + sendVal[0] + "," + sendVal[1] + ") ");
                OutInfoScroll.scrollTo(0, OutInfo.getMeasuredHeight() - OutInfoScroll.getHeight());

                bluetoothController.send(sendVal);

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

        if (Const.Debug) {
            Log.e(TAG, "ON RESUME");
            Log.e(TAG, "Before Bluetooth Connection Creation");

        }

        bluetoothController.connect();

        if (Const.Debug)
            Log.e(TAG, "After Bluetooth Connection Creation");
    }

}
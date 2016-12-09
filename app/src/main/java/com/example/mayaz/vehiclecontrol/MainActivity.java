package com.example.mayaz.vehiclecontrol;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mayaz.vehiclecontrol.Controller.BluetoothController;
import com.example.mayaz.vehiclecontrol.Util.*;

public class MainActivity extends Activity {

    private TextView LeftControlBarInfo;
    private TextView RightControlBarInfo;
    private TextView OutInfo;
    private SeekBar LeftControlBar;
    private SeekBar RightControlBar;
    private Button Clear;
    private Button Stop;
    private Button Reset;
    private ScrollView OutInfoScroll;
    private Switch Sync;
    private Switch VKeep;

    private boolean FLAG_SYNC = false;
    private boolean FLAG_VKEEP = false;

    private BluetoothController bluetoothController;

    private static final String TAG = "MainActiivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bluetoothController = new BluetoothController(MainActivity.this,Const.address);
    }

    private void initView(){
        LeftControlBarInfo = (TextView) findViewById(R.id.LeftControlBarInfo);
        RightControlBarInfo = (TextView) findViewById(R.id.RightControlBarInfo);
        LeftControlBar = (SeekBar) findViewById(R.id.LeftControlBar);
        RightControlBar = (SeekBar) findViewById(R.id.RightControlBar);
        OutInfo = (TextView) findViewById(R.id.OutInfo);
        Clear = (Button) findViewById(R.id.Clear);
        Stop = (Button) findViewById(R.id.Stop);
        Reset = (Button) findViewById(R.id.Reset);

        OutInfoScroll = (ScrollView) findViewById(R.id.OutInfoScroll);
        Sync = (Switch) findViewById(R.id.Sync);
        VKeep = (Switch) findViewById(R.id.VKeep);


        Sync.setOnCheckedChangeListener(buttonlistener);
        VKeep.setOnCheckedChangeListener(buttonlistener);

        LeftControlBarInfo.setText("50");
        LeftControlBar.setProgress(50);
        LeftControlBar.setOnSeekBarChangeListener(seekbarlistener);

        RightControlBarInfo.setText("50");
        RightControlBar.setProgress(50);
        RightControlBar.setOnSeekBarChangeListener(seekbarlistener);

    }

    public void Clear(View v){
        OutInfo.setText("");
    }

    public void Stop(View v){
        byte sendVal[] = {0,-128};
        bluetoothController.send(sendVal, 2);
        OutInfo.append("STOP SIGNAL SENT");
    }

    public void Reset(View v){
        LeftControlBar.setProgress(50);
        RightControlBar.setProgress(50);
    }

    private CompoundButton.OnCheckedChangeListener buttonlistener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            int key = buttonView.getId();
            if (key == R.id.Sync){
                if (isChecked){
                    FLAG_SYNC = true;
                    OutInfo.append("SYNC ON\n");
                }
                else{
                    FLAG_SYNC = false;
                    VKeep.setChecked(false);
                    OutInfo.append("SYNC OFF\n");
                }
            }
            else if (key == R.id.VKeep){
                if (isChecked){
                    FLAG_VKEEP = true;
                    Sync.setChecked(true);
                    OutInfo.append("VKEEP ON\n");
                }
                else{
                    FLAG_VKEEP = false;
                    OutInfo.append("VKEEP OFF\n");
                }
            }

        }
    };

    private SeekBar.OnSeekBarChangeListener seekbarlistener = new SeekBar.OnSeekBarChangeListener() {
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

                OutInfo.append("[" + sendVal[0] + "] ");
                OutInfoScroll.scrollTo(0, OutInfo.getMeasuredHeight() - OutInfoScroll.getHeight());
                bluetoothController.send(sendVal,1);
            }
            else{
                byte sendVal[] = {Const.L_Table[index],Const.R_Table[index]};
                LeftControlBarInfo.setText(String.valueOf(code));
                RightControlBarInfo.setText(String.valueOf(code));
                OutInfo.append("(" + sendVal[0] + "," + sendVal[1] + ") ");
                OutInfoScroll.scrollTo(0, OutInfo.getMeasuredHeight() - OutInfoScroll.getHeight());

                bluetoothController.send(sendVal,2);

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
            if (FLAG_VKEEP == false){
                if(key==R.id.LeftControlBar){
                    seekBar.setProgress(50);
                }
                if(key==R.id.RightControlBar){
                    seekBar.setProgress(50);
                }
            }
            OutInfo.append("\n");
        }
    };

//    private byte[] getInstruction(int code, Boolean isSync){
//        byte[] instruction = null;
//        if (isSync == false){
//            int index;
//            if (code == 50){
//                index = 20;
//            }
//            else{
//                index = (code/5 == 20)?19:code/5;
//            }
//            instruction[0] = Const.L_Table[index];
//        }
//        else if (isSync){
//
//        }
//
//        return instruction;
//
//    }

    public void onResume(){

        super.onResume();

//        Toast.makeText(MainActivity.this, "onResume start", Toast.LENGTH_SHORT).show();

        if (Const.Debug) {
            Log.e(TAG, "ON RESUME");
            Log.e(TAG, "Before Bluetooth Connection Creation");

        }

        bluetoothController.connect();

        if (Const.Debug)
            Log.e(TAG, "After Bluetooth Connection Creation");

//        Toast.makeText(MainActivity.this, "onResume end", Toast.LENGTH_SHORT).show();

    }

    public void onStop(){

        super.onStop();
        bluetoothController.close();

    }

}
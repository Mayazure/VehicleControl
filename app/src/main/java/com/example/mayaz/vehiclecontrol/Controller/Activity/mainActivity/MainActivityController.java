package com.example.mayaz.vehiclecontrol.controller.activity.mainActivity;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mayaz.vehiclecontrol.action.bluetooth.BluetoothAction;
import com.example.mayaz.vehiclecontrol.action.mainActivity.MainActivityAction;
import com.example.mayaz.vehiclecontrol.R;
import com.example.mayaz.vehiclecontrol.controller.activity.base.BaseController;
import com.example.mayaz.vehiclecontrol.util.Const;

/**
 * Created by mayaz on 2016/12/10.
 */
public class MainActivityController extends BaseController {

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
    private boolean isPressed_L = false;
    private boolean isPressed_R = false;

    private BluetoothAction bluetoothAction;
    private MainActivityAction mainActivityAction;

    public MainActivityController(Context context) {
        super(context);
    }

    public void init(){
        setViews();
        setViewsInitAttr();
        setViewsListener();
        setAction();
    }

    protected void setViews(){
        LeftControlBarInfo = (TextView) this.getView("LeftControlBarInfo");
        RightControlBarInfo = (TextView) this.getView("RightControlBarInfo");
        LeftControlBar = (SeekBar) this.getView("LeftControlBar");
        RightControlBar = (SeekBar) this.getView("RightControlBar");
        OutInfo = (TextView) this.getView("OutInfo");
        Clear = (Button) this.getView("Clear");
        Stop = (Button) this.getView("Stop");
        Reset = (Button) this.getView("Reset");
        OutInfoScroll = (ScrollView) this.getView("OutInfoScroll");
        Sync = (Switch) this.getView("Sync");
        VKeep = (Switch) this.getView("VKeep");
    }

    protected void setViewsInitAttr(){
        LeftControlBarInfo.setText("50");
        LeftControlBar.setProgress(50);

        RightControlBarInfo.setText("50");
        RightControlBar.setProgress(50);
    }

    protected void setViewsListener(){
        Sync.setOnCheckedChangeListener(switchButtonListener);
        VKeep.setOnCheckedChangeListener(switchButtonListener);
        LeftControlBar.setOnSeekBarChangeListener(seekBarListener);
        RightControlBar.setOnSeekBarChangeListener(seekBarListener);
        Clear.setOnClickListener(pushButtonListener);
        Stop.setOnClickListener(pushButtonListener);
        Reset.setOnClickListener(pushButtonListener);
        OutInfo.addTextChangedListener(textWatcher);
    }

    protected void setAction(){
        this.bluetoothAction  = new BluetoothAction(this.context,Const.address, this);
        this.mainActivityAction = new MainActivityAction();
    }

    private void println(String info){
        OutInfo.append(info + "\n");
    }

//    private void print(String info){
//        OutInfo.append(info);
//    }
//
//    private void send(byte[] sendVal){
//        bluetoothAction.send(sendVal);
//        for (int i=0; i<sendVal.length; i++){
//            print(String.valueOf(sendVal[i]));
//        }
//        println("");
//    }

    public void btConnect(){
        bluetoothAction.connect();
    }

    public void btClose(){
        bluetoothAction.close();
    }

    private TextWatcher textWatcher = new TextWatcher(){
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            OutInfoScroll.scrollTo(0, OutInfo.getMeasuredHeight() - OutInfoScroll.getHeight());
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
//            OutInfoScroll.scrollTo(0, OutInfo.getMeasuredHeight() - OutInfoScroll.getHeight());
        }
    };

    private View.OnClickListener pushButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                case R.id.Clear : {
                    OutInfo.setText("");
                    break;
                }
                case R.id.Reset : {
                    LeftControlBar.setProgress(50);
                    RightControlBar.setProgress(50);
                    break;
                }
                case R.id.Stop : {
                    byte sendVal[] = {0,-128};
                    bluetoothAction.send(sendVal);
                    println("STOP SIGNAL SENT");
                    break;
                }
                case R.id.Connect : {
                    break;
                }
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener switchButtonListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            int key = buttonView.getId();
            switch (buttonView.getId()){
                case R.id.Sync : {
                    if(buttonView.isChecked()){
                        mainActivityAction.setFlagStat("SYNC", true);
                        println("SYNC ON");
                    }
                    else{
                        VKeep.setChecked(false);
                        mainActivityAction.setFlagStat("SYNC", false);
                        mainActivityAction.setFlagStat("VKEEP", false);
                        println("SYNC OFF");
                    }
                    break;
                }
                case R.id.VKeep : {
                    if(buttonView.isChecked()){
                        Sync.setChecked(true);
                        mainActivityAction.setFlagStat("VKEEP", true);
                        mainActivityAction.setFlagStat("SYNC", true);
                        println("VKEEP ON");
                    }
                    else{
                        mainActivityAction.setFlagStat("VKEEP", false);
                        println("VKEEP OFF");
                    }
                    break;
                }
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            byte sendVal[] = null;
            int code = seekBar.getProgress();

            switch (seekBar.getId()){
                case R.id.LeftControlBar : {
                    sendVal = mainActivityAction.getInstruction("L", code);
                    if (sendVal.length == 1){
                        LeftControlBarInfo.setText(String.valueOf(code));
                        println(sendVal[0] + "");
                    }
                    else{
                        LeftControlBarInfo.setText(String.valueOf(code));
                        RightControlBarInfo.setText(String.valueOf(code));
                        RightControlBar.setProgress(code);
                        println("(" + sendVal[0] + "," + sendVal[1] + ")");
                    }
                    break;
                }
                case R.id.RightControlBar : {
                    sendVal = mainActivityAction.getInstruction("R", code);
                    if (sendVal.length == 1){
                        RightControlBarInfo.setText(String.valueOf(code));
                        println(sendVal[0] + "");
                    }
                    else{
                        LeftControlBarInfo.setText(String.valueOf(code));
                        RightControlBarInfo.setText(String.valueOf(code));
                        LeftControlBar.setProgress(code);
                        println("(" + sendVal[0] + "," + sendVal[1] + ")");
                    }
                    break;
                }
            }
            bluetoothAction.send(sendVal);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            int key = seekBar.getId();
            if (key == R.id.LeftControlBar){
                mainActivityAction.setFlagStat("LON", true);
            }
            else if (key == R.id.RightControlBar){
                mainActivityAction.setFlagStat("RON", true);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            switch (seekBar.getId()){
                case R.id.LeftControlBar : {
                    mainActivityAction.setFlagStat("LON",false);
                    break;
                }
                case R.id.RightControlBar : {
                    mainActivityAction.setFlagStat("RON",false);
                    break;
                }
            }
            if(!mainActivityAction.getFlagStat("VKEEP")){
                seekBar.setProgress(50);
            }
        }
    };
}
package com.example.mayaz.vehiclecontrol;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.mayaz.vehiclecontrol.controller.activity.mainActivity.MainActivityController;
import com.example.mayaz.vehiclecontrol.util.*;

public class MainActivity extends Activity {

    private static final String TAG = "MainActiivity";

    private MainActivityController mainActivityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initController();
    }

    private void initController(){
        mainActivityController = new MainActivityController(MainActivity.this);
        mainActivityController.putView("LeftControlBarInfo", findViewById(R.id.LeftControlBarInfo));
        mainActivityController.putView("RightControlBarInfo", findViewById(R.id.RightControlBarInfo));
        mainActivityController.putView("LeftControlBar", findViewById(R.id.LeftControlBar));
        mainActivityController.putView("RightControlBar", findViewById(R.id.RightControlBar));
        mainActivityController.putView("OutInfo", findViewById(R.id.OutInfo));
        mainActivityController.putView("Clear", findViewById(R.id.Clear));
        mainActivityController.putView("Stop", findViewById(R.id.Stop));
        mainActivityController.putView("Reset", findViewById(R.id.Reset));
        mainActivityController.putView("OutInfoScroll", findViewById(R.id.OutInfoScroll));
        mainActivityController.putView("Sync", findViewById(R.id.Sync));
        mainActivityController.putView("VKeep", findViewById(R.id.VKeep));
        mainActivityController.init();
    }

    public void onResume(){
        super.onResume();

        if (Const.Debug) {
            Log.e(TAG, "ON RESUME");
            Log.e(TAG, "Before Bluetooth Connection Creation");
        }

        mainActivityController.btConnect();

        if (Const.Debug){
            Log.e(TAG, "After Bluetooth Connection Creation");
        }
    }

    public void onStop(){
        super.onStop();

        mainActivityController.btClose();
    }
}
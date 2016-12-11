package com.example.mayaz.vehiclecontrol.action.mainActivity;

import com.example.mayaz.vehiclecontrol.util.Const;

/**
 * Created by mayaz on 2016/12/9.
 */

public class MainActivityAction {

    private boolean FLAG_SYNC = false;
    private boolean FLAG_VKEEP = false;
    private boolean FLAG_LBAR_ON = false;
    private boolean FLAG_RBAR_ON = false;

    public MainActivityAction() {

    }

    public void setFlagStat(String name, boolean STAT){
        switch (name){
            case "SYNC":{
                FLAG_SYNC = STAT;
                break;
            }
            case "VKEEP":{
                FLAG_VKEEP = STAT;
                break;
            }
            case "LON":{
                FLAG_LBAR_ON = STAT;
                break;
            }
            case "RON":{
                FLAG_RBAR_ON = STAT;
                break;
            }
        }
    }

    public boolean getFlagStat(String name){
        boolean STAT = false;
        switch (name){
            case "SYNC":{
                STAT =  FLAG_SYNC;
                break;
            }
            case "VKEEP":{
                STAT = FLAG_VKEEP;
                break;
            }
            case "LON":{
                STAT = FLAG_LBAR_ON;
                break;
            }
            case "RON":{
                STAT = FLAG_RBAR_ON;
                break;
            }
        }
        return STAT;
    }

    public byte[] getInstruction(String ID, int code){
        int index;
        byte[] sendVal = null;

        if (code == 50){
            index = 20;
        }
        else{
            index = (code/5 == 20)?19:code/5;
        }

        if(FLAG_SYNC == false){
            if(ID == "L"){
                byte[] tempVal = {Const.L_Table[index]};
                sendVal = tempVal;
            }
            else if(ID == "R"){
                byte[] tempVal = {Const.R_Table[index]};
                sendVal = tempVal;
            }
        }
        else{
            byte[] tempVal = {Const.L_Table[index],Const.R_Table[index]};
            sendVal = tempVal;
        }

        return sendVal;
    }

}
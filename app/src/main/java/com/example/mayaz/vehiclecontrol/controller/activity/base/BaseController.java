package com.example.mayaz.vehiclecontrol.controller.activity.base;

import android.content.Context;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by mayaz on 2016/12/10.
 */

public abstract class BaseController {

    protected Context context;
    protected HashMap<String, Object> views;
//    protected HashMap<String, Object> extras;

    public BaseController(Context context){
        views = new HashMap<String, Object>();
//        extras = new HashMap<String, Object>();
        putContext(context);
    }

    public void putContext(Context context){
        this.context = context;
    }

    public void putView(String name, Object object){
        views.put(name, object);
    }

    public Object getView(String name){
        return views.get(name);
    }

//    public void putExtra(String name, Object object){
//        extras.put(name, object);
//    }
//
//    public Object getExtra(String name){
//        return extras.get(name);
//    }

    protected abstract void setViews();
    protected abstract void setViewsInitAttr();
    protected abstract void setViewsListener();
    protected abstract void setAction();

}

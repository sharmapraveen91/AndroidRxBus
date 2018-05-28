package com.praveen.androidrxbus;

import com.praveen.androidrxbus.rx.RxBus;

import android.app.Application;

/**
 * @author Praveen.Sharma on 5/28/2018.
 */
public class RxBusApplication extends Application {

    private RxBus bus;
    @Override
    public void onCreate() {
        super.onCreate();
        initBus();

    }

    private void initBus() {
        bus = new RxBus();
    }

    public RxBus bus() {
        return bus;
    }

}

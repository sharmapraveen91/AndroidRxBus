package com.praveen.androidrxbus;

import com.praveen.androidrxbus.constant.Event;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements Consumer<Object> {
    private CompositeDisposable disposables = new CompositeDisposable();
    private TextView mTextMessage;
    private TextView mEventMessage;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    sendTestData(Event.EVENT_HOME);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    sendTestData(Event.EVENT_DASHBOARD);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    sendTestData(Event.EVENT_NOTIFICATION);
                    return true;
            }
            return false;
        }
    };

    private void sendTestData(int eventValue) {
        StringBuilder eventValueSB=new StringBuilder();

        eventValueSB.append("Event value is:"+eventValue);

        ((RxBusApplication)getApplication() ).bus().send(eventValueSB.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        mEventMessage = (TextView) findViewById(R.id.eventMessage);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        subscribeData(Event.EVENT_HOME);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // do not send event after activity has been destroyed
        disposables.clear();
    }


    public void addDisposable(Disposable d) {
        if(disposables==null || ( disposables.isDisposed())){
            disposables= new CompositeDisposable();
        }
        if (d != null && !disposables.isDisposed() && !d.isDisposed()) {
            disposables.add(d);
        }
    }

    public void removeDisposable(Disposable d) {
        if (d != null ) {
            d.dispose();
            disposables.remove(d);
            d=null;
        }
    }

    public void clearDisposables() {
        if (disposables != null ) {
            disposables.dispose();
            disposables.clear();
        }
    }


    Disposable disposable;
    protected void subscribeData(int event) {
        if(disposable!=null)
            disposable.dispose();
        if(disposable==null || disposable.isDisposed()){
            disposable = ((RxBusApplication)getApplication() )
                    .bus()
                    .toObservable()
                    .serialize()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            if (throwable != null) {
                                throwable.printStackTrace();
                            }
                        }
                    });
            Disposable d = ((RxBusApplication)getApplication() ).bus().getEvent(event);
            if(d!=null ) {
                removeDisposable(d);
            }
            ((RxBusApplication)getApplication() ).bus().subscribeEvent(disposable,event);
            addDisposable(disposable);
        }
    }

    @Override
    public void accept(Object o) throws Exception {
        if(o!=null){
            mEventMessage.setText(String.valueOf(o));
        }

    }
}

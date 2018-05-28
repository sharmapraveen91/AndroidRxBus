package com.praveen.androidrxbus.rx;

import android.util.SparseArray;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * @author Praveen.Sharma on 12/5/2017.
 */

public class RxBus {
    SparseArray<Disposable> mDisposableSparseArray= new SparseArray<>();

    public RxBus() {
    }

    private PublishSubject<Object> bus = PublishSubject.create();

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }

    public void subscribeEvent(Disposable d,int event){
        if(d!=null && !d.isDisposed()){
            mDisposableSparseArray.put(event,d);
        }
    }

    public Disposable getEvent(int event){
        return mDisposableSparseArray.get(event);
    }

}

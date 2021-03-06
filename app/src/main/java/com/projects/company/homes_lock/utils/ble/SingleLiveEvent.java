package com.projects.company.homes_lock.utils.ble;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public class SingleLiveEvent<T> extends MutableLiveData<T> {

    //region Declare Variables
    private static final String TAG = "SingleLiveEvent";
    //endregion Declare Objects
    //region Declare Objects
    private final AtomicBoolean mPending = new AtomicBoolean(false);
    //endregion Declare Variables

    //region Declare Methods
    @MainThread
    public void observe(@NonNull final LifecycleOwner owner, @NonNull final Observer<T> observer) {
        if (hasActiveObservers())
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.");

        super.observe(owner, t -> {
            if (mPending.compareAndSet(true, false))
                observer.onChanged(t);
        });
    }

    @MainThread
    public void setValue(@Nullable final T t) {
        mPending.set(true);
        super.setValue(t);
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    public void call() {
        setValue(null);
    }
    //endregion Declare Methods
}

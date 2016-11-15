package com.example.gabriel.vinyl;

import android.app.Application;
import android.util.Log;

import com.example.gabriel.vinyl.net.SongRestClient;
import com.example.gabriel.vinyl.service.SongManager;

/**
 * Created by Gabriel on 11/15/2016.
 */

public class VinylApp extends Application {
    public static final String TAG = VinylApp.class.getSimpleName();
    private SongManager mSongManager;
    private SongRestClient mSongRestManager;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        mSongManager = new SongManager(this);
        mSongRestManager = new SongRestClient(this);
        mSongManager.setmSongRestClient(mSongRestManager);
    }

    public SongManager getSongManager() {
        return mSongManager;
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }
}

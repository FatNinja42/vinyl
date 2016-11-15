package com.example.gabriel.vinyl.service;

import android.content.Context;

import com.example.gabriel.vinyl.content.User;
import com.example.gabriel.vinyl.content.database.VinylDatabase;
import com.example.gabriel.vinyl.net.ResourceException;
import com.example.gabriel.vinyl.net.SongRestClient;
import com.example.gabriel.vinyl.util.Cancelable;
import com.example.gabriel.vinyl.util.OnErrorListener;
import com.example.gabriel.vinyl.util.OnSuccessListener;

import java.util.Observable;

/**
 * Created by Gabriel on 11/15/2016.
 */
public class SongManager extends Observable {
    private final VinylDatabase mVD;
    private final Context mContext;
    private SongRestClient mSongRestClient;

    private String mToken;
    private User mCurrentUser;

    public SongManager(Context context) {
        mContext = context;
        mVD = new VinylDatabase(context);
    }

    public User getCurrentUser() {
        return mVD.getCurrentUser();
    }

    public Cancelable loginAsync(String username,
                                 String password,
                                 final OnSuccessListener<String> onSuccessListener,
                                 final OnErrorListener onErrorListener) {
        final User user = new User(username, password);
        return mSongRestClient.getToken(
            user, new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String token) {
                    mToken = token;
                    if(mToken != null) {
                        user.setToken(mToken);
                        setCurrentUser(user);
                        mVD.saveUser(user);
                        onSuccessListener.onSuccess(mToken);
                    } else {
                        onErrorListener.onError(new ResourceException(new IllegalArgumentException("Invalid credentials")));
                    }
                }
            },
            onErrorListener);
    }

    private void setCurrentUser(User user) {
        mCurrentUser = user;
        mSongRestClient.setUser(user);
    }

    public void setmSongRestClient(SongRestClient songRestClient) {
        mSongRestClient = songRestClient;
    }
}

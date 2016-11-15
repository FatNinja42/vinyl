package com.example.gabriel.vinyl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.gabriel.vinyl.content.User;
import com.example.gabriel.vinyl.service.SongManager;
import com.example.gabriel.vinyl.util.Cancelable;
import com.example.gabriel.vinyl.util.DialogUtils;
import com.example.gabriel.vinyl.util.OnErrorListener;
import com.example.gabriel.vinyl.util.OnSuccessListener;

/**
 * Created by Gabriel on 11/15/2016.
 */

public class LoginActivity extends AppCompatActivity {
    private Cancelable mCancelable;
    private SongManager mSongManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSongManager = ((VinylApp) getApplication()).getSongManager();
        User user = mSongManager.getCurrentUser();
        if(user != null) {
            startSongListActivity();
            finish();
        }
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.action_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
                Snackbar.make(view, "Autenthication in progress...", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null).show();
            }
        });
    }

    private void login() {
        EditText usernameEdit = (EditText) findViewById(R.id.username);
        EditText passwordEdit = (EditText) findViewById(R.id.password);

        mCancelable = mSongManager
                .loginAsync(
                        usernameEdit.getText().toString(), passwordEdit.getText().toString(),
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startSongListActivity();
                                    }
                                });
                            }
                        }, new OnErrorListener() {
                            @Override
                            public void onError(final Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogUtils.showError(LoginActivity.this, e);
                                    }
                                });
                            }
                        });
    }

    private void startSongListActivity() {
        startActivity(new Intent(this, SongListActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mCancellable != null) {
//            mCancellable.cancel();
//        }
    }

//    private void setupToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                login();
//                Snackbar.make(view, "Authenticating, please wait", Snackbar.LENGTH_INDEFINITE)
//                        .setAction("Action", null).show();
//            }
//        });
//    }
//
//    private void login() {
////        EditText usernameEditText = (EditText) findViewById(R.id.username);
////        EditText passwordEditText = (EditText) findViewById(R.id.password);
////        mCancellable = mNoteManager
////                .loginAsync(
////                        usernameEditText.getText().toString(), passwordEditText.getText().toString(),
////                        new OnSuccessListener<String>() {
////                            @Override
////                            public void onSuccess(String s) {
////                                runOnUiThread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        startNoteListActivity();
////                                    }
////                                });
////                            }
////                        }, new OnErrorListener() {
////                            @Override
////                            public void onError(final Exception e) {
////                                runOnUiThread(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        DialogUtils.showError(LoginActivity.this, e);
////                                    }
////                                });
////                            }
////                        });
//    }
////
////    private void startNoteListActivity() {
////        startActivity(new Intent(this, NoteListActivity.class));
////    }
}

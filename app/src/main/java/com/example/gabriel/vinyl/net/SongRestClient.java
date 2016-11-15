package com.example.gabriel.vinyl.net;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import com.example.gabriel.vinyl.R;
import com.example.gabriel.vinyl.content.User;
import com.example.gabriel.vinyl.net.mapping.CredentialsWriter;
import com.example.gabriel.vinyl.net.mapping.TokenReader;
import com.example.gabriel.vinyl.util.Cancelable;
import com.example.gabriel.vinyl.util.OnErrorListener;
import com.example.gabriel.vinyl.util.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Gabriel on 11/15/2016.
 */
public class SongRestClient {
    private static final String TAG = SongRestClient.class.getSimpleName();
    public static final String APPLICATION_JSON = "application/json";
    public static final String UTF_8 = "UTF-8";
    public static final String LAST_MODIFIED = "Last-Modified";

    private final OkHttpClient mOkHttpClient;
    private final String mApiUrl;
    private final String mNoteUrl;
    private final String mAuthUrl;

    public SongRestClient(Context context) {
        mOkHttpClient = new OkHttpClient();
        mApiUrl = context.getString(R.string.api_url);
        mNoteUrl = mApiUrl.concat("/api/note");
        mAuthUrl = mApiUrl.concat("/api/auth");
        Log.d(TAG, "NoteRestClient created");
    }
    public void setUser(User user) {

    }

    public CancellableOkHttpAsync<String> getToken(User user, OnSuccessListener<String> successListener, OnErrorListener errorListener) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter writer = null;
        
        try {
            writer = new JsonWriter(new OutputStreamWriter(baos, UTF_8));
            new CredentialsWriter().write(user, writer);
            writer.close();
        } catch (Exception e) {
            Log.e(TAG, "getToken failed", e);
            throw new ResourceException(e);
        }
        return new CancellableOkHttpAsync<String>(
                new Request.Builder()
                        .url(String.format("%s/session", mAuthUrl))
                        .post(RequestBody.create(MediaType.parse(APPLICATION_JSON), baos.toByteArray()))
                        .build(),
                new ResponseReader<String>() {
                    @Override
                    public String read(Response response) throws Exception {
                        JsonReader reader = new JsonReader(new InputStreamReader(response.body().byteStream(), UTF_8));
                        if(response.code() == 201) {//created
                            return new TokenReader().read(reader);
                        } else {
                            return null;
                        }
                    }
                } ,
                successListener ,
                errorListener
        );
    }

    private static interface ResponseReader<E> {
        E read(Response response) throws Exception;
    }

    private class CancellableOkHttpAsync<E> implements Cancelable {
        private Call mCall;

        public CancellableOkHttpAsync(
                final Request request,
                final ResponseReader<E> responseReader,
                final OnSuccessListener<E> successListener,
                final OnErrorListener errorListener) {
            try {
                mCall = mOkHttpClient.newCall(request);
                Log.d(TAG, String.format("started %s %s", request.method(), request.url()));
                //TODO retry 3x, renew token
                mCall.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        notifyFailure(e, request, errorListener);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            notifySuccess(response, request, successListener, responseReader);
                        } catch (Exception e) {
                            notifyFailure(e, request, errorListener);
                        }
                    }
                });
            } catch (Exception e) {
                notifyFailure(e, request, errorListener);
            }
        }

        @Override
        public void cancel() {
            if (mCall != null) {
                mCall.cancel();
            }
        }

        private void notifySuccess(Response response, Request request,
                                   OnSuccessListener<E> successListener, ResponseReader<E> responseReader) throws Exception {
            if (mCall.isCanceled()) {
                Log.d(TAG, String.format("completed, but cancelled %s %s", request.method(), request.url()));
            } else {
                Log.d(TAG, String.format("completed %s %s", request.method(), request.url()));
                successListener.onSuccess(responseReader.read(response));
            }
        }

        private void notifyFailure(Exception e, Request request, OnErrorListener errorListener) {
            if (mCall.isCanceled()) {
                Log.d(TAG, String.format("failed, but cancelled %s %s", request.method(), request.url()));
            } else {
                Log.e(TAG, String.format("failed %s %s", request.method(), request.url()), e);
                errorListener.onError(e instanceof ResourceException ? e : new ResourceException(e));
            }
        }
    }
}

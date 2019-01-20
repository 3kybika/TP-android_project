package alex.task_manager.services.NetworkServices;

import android.content.Context;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.util.Log;

import alex.task_manager.api.SyncApi;
import alex.task_manager.models.DefaultResponse;
import alex.task_manager.requests.SyncRequest;
import alex.task_manager.responses.SyncResponse;
import alex.task_manager.services.DbServices.CookieService;
import alex.task_manager.utils.ListenerHandler;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SyncNetworkService {
    //private static final String BASE_URL = "http://95.142.47.111:8081/api/users/";
    public static final String BASE_URL = "http://10.0.2.2:8081/api/service/";
    //public static final String BASE_URL = "https://android.4eburek.site/api/users/";
    private SyncApi api;
    private static final Gson GSON = new GsonBuilder().create();

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static SyncNetworkService mInstance;

    private SyncNetworkService(Context context) {

        OkHttpClient.Builder oktHttpClient = new OkHttpClient.Builder()
                .cookieJar(CookieService.getInstance(context));

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        oktHttpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(oktHttpClient.build())
                .build();
        api = retrofit.create(SyncApi.class);
    }

    public static synchronized SyncNetworkService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SyncNetworkService(context);
        }
        return mInstance;
    }

    private SyncResponse parseRequest(final String body) throws IOException {
        try {
            return GSON.fromJson(body, SyncResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    private DefaultResponse parseMessage(final String body) throws IOException {
        try {
            return GSON.fromJson(body, DefaultResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    private ListenerHandler<OnSyncListener> sendRequest(final Call<ResponseBody> request, final OnSyncListener listener) {
        final ListenerHandler<OnSyncListener> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = request.execute();
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        if (response.code() != HttpURLConnection.HTTP_OK  ) {
                            invokeErrorCode(handler, response.code(), parseMessage(body));
                        }
                        invokeSuccess(handler, parseRequest(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    private void invokeSuccess(final ListenerHandler<OnSyncListener> handler, final SyncResponse user) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnSyncListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onSuccess(user);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private void invokeErrorCode(final ListenerHandler<OnSyncListener> handler, final int httpCode, final DefaultResponse response) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnSyncListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    switch (httpCode) {
                        case (HttpURLConnection.HTTP_FORBIDDEN):
                            listener.onForbidden(response);
                        case (HttpURLConnection.HTTP_NOT_FOUND):
                            listener.onNotFound(response);
                    }
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private void invokeError(final ListenerHandler<OnSyncListener> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnSyncListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    public interface OnSyncListener {
        void onSuccess(final SyncResponse user);
        void onError(final Exception error);
        void onForbidden(final DefaultResponse response);
        void onNotFound(final DefaultResponse response);
    }

    public ListenerHandler<OnSyncListener> sync (final SyncRequest syncRequest, final OnSyncListener listener) {
        return sendRequest(api.sync(syncRequest), listener);
    }
}


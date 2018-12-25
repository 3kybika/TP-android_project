package alex.myapplication.services;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import alex.myapplication.Utils.ListenerHandler;
import alex.myapplication.api.Api;
import alex.myapplication.models.ChangeUserDataForm;
import alex.myapplication.models.LoginForm;
import alex.myapplication.models.SignUpForm;
import alex.myapplication.models.UserModel;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    //private static final String BASE_URL = "http://95.142.47.111:8081/api/users/";
    private static final String BASE_URL = "http://10.0.2.2:8081/api/users/";
    private Retrofit retrofit;
    private Api api;
    private static final Gson GSON = new GsonBuilder().create();

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static NetworkService mInstance;

    private NetworkService() {

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        OkHttpClient.Builder oktHttpClient = new OkHttpClient.Builder()
                .cookieJar(new SessionCookieJar());

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        oktHttpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(oktHttpClient.build())
                .build();
        api = retrofit.create(Api.class);
    }

    private static class SessionCookieJar implements CookieJar {

        private List<Cookie> cookies;

        @Override
        public void saveFromResponse(@NonNull HttpUrl url, List<Cookie> cookies) {
            if (cookies != null) {
                this.cookies = new ArrayList<>(cookies);
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            if (cookies != null) {
                return cookies;
            }
            return Collections.emptyList();
        }
    }


    public static synchronized NetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkService();
        }
        return mInstance;
    }

    private UserModel parseUser(final String body) throws IOException {
        try {
            return GSON.fromJson(body, UserModel.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    public ListenerHandler<OnUserGetListener> signin(final LoginForm loginForm, final OnUserGetListener listener) {
        final ListenerHandler<OnUserGetListener> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = api.signin(loginForm).execute();
                    try (final ResponseBody responseBody = response.body()) {
                        if (response.code() != HttpURLConnection.HTTP_OK  ) {
                            throw new IOException("HTTP code " + response.code());
                        }
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parseUser(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnUserGetListener> signup(final SignUpForm loginForm, final OnUserGetListener listener) {
        final ListenerHandler<OnUserGetListener> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = api.signup(loginForm).execute();
                    try (final ResponseBody responseBody = response.body()) {
                        if (response.code() != HttpURLConnection.HTTP_CREATED  ) {
                            throw new IOException("HTTP code " + response.code());
                        }
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parseUser(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnUserGetListener> getme(final OnUserGetListener listener) {
        final ListenerHandler<OnUserGetListener> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = api.getUser().execute();
                    try (final ResponseBody responseBody = response.body()) {
                        if (response.code() != HttpURLConnection.HTTP_OK  ) {
                            throw new IOException("HTTP code " + response.code());
                        }
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parseUser(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    private void invokeSuccess(final ListenerHandler<OnUserGetListener> handler, final UserModel user) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnUserGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onUserSuccess(user);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private void invokeError(final ListenerHandler<OnUserGetListener> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnUserGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onUserError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    public interface OnUserGetListener {
        void onUserSuccess(final UserModel user);
        void onUserError(final Exception error);
    }
}

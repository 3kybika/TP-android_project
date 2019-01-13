package alex.task_manager.services.NetworkServices;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import alex.task_manager.api.UsersApi;
import alex.task_manager.models.DefaultResponse;
import alex.task_manager.models.UserModel;
import alex.task_manager.requests.LoginForm;
import alex.task_manager.requests.SignUpForm;
import alex.task_manager.services.DbServices.CookieService;
import alex.task_manager.utils.ListenerHandler;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UserNetworkService {

    //private static final String BASE_URL = "http://95.142.47.111:8081/api/users/";
    public static final String BASE_URL = "http://10.0.2.2:8081/api/users/";
    private UsersApi api;
    private static final Gson GSON = new GsonBuilder().create();

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static UserNetworkService mInstance;

    private UserNetworkService(Context context) {

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
        api = retrofit.create(UsersApi.class);
    }

    public static synchronized UserNetworkService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserNetworkService(context);
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

    private DefaultResponse parseMessage(final String body) throws IOException {
        try {
            return GSON.fromJson(body, DefaultResponse.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    private ListenerHandler<OnUserGetListener> sendRequest(final Call<ResponseBody> request, final OnUserGetListener listener) {
        final ListenerHandler<OnUserGetListener> handler = new ListenerHandler<>(listener);
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

    private void invokeErrorCode(final ListenerHandler<OnUserGetListener> handler, final int httpCode, final DefaultResponse response) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnUserGetListener listener = handler.getListener();
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
        void onForbidden(final DefaultResponse response);
        void onNotFound(final DefaultResponse response);
    }

    public ListenerHandler<OnUserGetListener> signin (final LoginForm loginForm, final OnUserGetListener listener) {
        return sendRequest(api.signin(loginForm), listener);
    }

    public ListenerHandler<OnUserGetListener> signup (final SignUpForm signUpForm, final OnUserGetListener listener) {
        return sendRequest(api.signup(signUpForm), listener);
    }

    public ListenerHandler<OnUserGetListener> getme (final OnUserGetListener listener) {
        return sendRequest( api.getUser(), listener);
    }
}

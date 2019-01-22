package alex.task_manager.api;

import alex.task_manager.requests.ChangeUserDataForm;
import alex.task_manager.requests.LoginForm;
import alex.task_manager.requests.SignUpForm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UsersApi {

    @POST("signup")
    Call<ResponseBody> signup(
            @Body SignUpForm body
    );

    @POST("signin")
    Call<ResponseBody> signin(
            @Body LoginForm body
    );

    @POST("me/")
    Call<ResponseBody> updateUser(
            @Body ChangeUserDataForm updateForm
    );
    @POST("logout/")
    Call<ResponseBody> logOut();


    @GET("me/")
    Call<ResponseBody> getUser();

    @GET("tasks/")
    Call<ResponseBody> getTasks();
}

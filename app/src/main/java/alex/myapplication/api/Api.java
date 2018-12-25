package alex.myapplication.api;


import alex.myapplication.models.ChangeUserDataForm;
import alex.myapplication.models.DefaultResponse;
import alex.myapplication.models.IdForm;
import alex.myapplication.models.LoginForm;
import alex.myapplication.models.SignUpForm;
import alex.myapplication.models.UserModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {

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

    @GET("me/")
    Call<ResponseBody> getUser();

    @DELETE("deleteuser/")
    Call<ResponseBody> deleteUser();

}

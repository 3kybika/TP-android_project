package alex.myapplication.api;


import alex.myapplication.models.DefaultResponse;
import alex.myapplication.models.IdForm;
import alex.myapplication.models.LoginForm;
import alex.myapplication.models.SignUpForm;
import alex.myapplication.models.UserModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {

    @POST("signup")
    Call<UserModel> signup(
            @Body SignUpForm body
    );

    @POST("signin")
    Call<UserModel> signin(
            @Body LoginForm body
    );

    @POST("me/")
    Call<DefaultResponse> updateUser(
            @Field("password") String password,
            @Field("newEmail") String newEmail,
            @Field("newPassword") String newPassword,
            @Field("newLogin") String newNickname
    );

    @GET("me/")
    Call<UserModel> getUser();

    @DELETE("deleteuser/")
    Call<DefaultResponse> deleteUser();

}

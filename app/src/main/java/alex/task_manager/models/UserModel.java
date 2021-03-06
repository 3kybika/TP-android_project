package alex.task_manager.models;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import alex.task_manager.services.DbServices.UserDbService;

public class UserModel {
    @SerializedName("id")
    private final int id;
    @SerializedName("email")
    private String email;
    @SerializedName("login")
    private String login;

    public static final class Builder extends DbModelBuilder<UserModel> {

        @Override
        protected UserModel mapper(Cursor cursor) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(UserDbService.LOGIN_COLUMN));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(UserDbService.EMAI_COLUMN));
            String login = cursor.getString(cursor.getColumnIndexOrThrow(UserDbService.LOGIN_COLUMN));

            return new UserModel(id, login, email);
        }
    }


    public UserModel(int id, String login, String email) {
        this.id = id;
        this.login = login;
        this.email = email;
    }

    public UserModel(String login, String email) {
        this.id = 0;
        this.login = login;
        this.email = email;
    }


    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

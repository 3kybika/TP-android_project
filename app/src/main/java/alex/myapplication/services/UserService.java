package alex.myapplication.services;

import alex.myapplication.models.UserModel;

public class UserService {
    private static UserService mInstance;

    public static synchronized UserService getInstance() {
        if (mInstance == null) {
            mInstance = new UserService();
        }
        return mInstance;
    }

    public static UserModel getUserById(int id) {
        return new UserModel(id, "123", "123@123.ru");
    }

    public static UserModel getActiveUser() {
        return getUserById(1);
    }
}

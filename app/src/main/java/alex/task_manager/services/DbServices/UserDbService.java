package alex.task_manager.services.DbServices;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import alex.task_manager.models.UserModel;

import static alex.task_manager.services.DbServices.Mappers.getInt;
import static alex.task_manager.services.DbServices.Mappers.userModelMapper;

public class UserDbService {

    Context context;
    DatabaseManager dbManager;
    private static final UserDbService mInstance = new UserDbService();

    public static UserDbService getInstance(Context context) {
        mInstance.context = context;
        mInstance.dbManager = DatabaseManager.getInstance(context);
        return mInstance;
    }

    public static void createDatabase(SQLiteDatabase db) {
        Log.d("User Service", "onCreate database");
        // создаем таблицу с полями
        db.execSQL("CREATE TABLE Users (" +
                "_id INTEGER PRIMARY KEY," +
                "login TEXT," +
                "email TEXT" +
                ");"
        );
        db.execSQL("CREATE TABLE LastUser (" +
                "_id INTEGER PRIMARY KEY," +
                "user_id INTEGER" +
                ")"
        );
        db.execSQL("INSERT INTO LastUser(_id, user_id) VALUES(0, -1);");
    }

    public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Users;");
            db.execSQL("DROP TABLE IF EXISTS LastUser;");
            createDatabase(db);
        }
    }
    public void addUser(UserModel user) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.execSQL(String.format(
                "REPLACE INTO Users (_id, login, email) " +
                        "VALUES(%d, \"%s\", \"%s\");",
                user.getId(), user.getLogin(), user.getEmail()
        ));
    }

    public void setCurrentUser(UserModel user){
        SQLiteDatabase db = dbManager.getWritableDatabase();

        db.execSQL(String.format("UPDATE LastUser SET user_id = %d WHERE _id = 0;", user.getId()));
        addUser(user);
    }

    public Integer getCurrentUserId() {
        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT _id FROM LastUser LIMIT 1;", null);

        return getInt(cursor);
    }

    public UserModel getCurrentUser(){

        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                    "SELECT _id, login, email " +
                    "FROM Users " +
                    "WHERE Users._id = (SELECT _id FROM LastUser WHERE _id = 0);",
                null
                );
        return getUserModel(cursor);
    }

    public UserModel getUserModel(Cursor cursor){
        UserModel user;
        if(cursor.moveToFirst()) {
            try {
                user = userModelMapper(cursor);
            } finally {
                cursor.close();
            }
        }
        else{
            return null;
        }
        return user;
    }
}

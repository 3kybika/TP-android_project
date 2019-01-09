package alex.task_manager.services.DbServices;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import alex.task_manager.models.UserModel;

import static alex.task_manager.services.DbServices.Mappers.getInt;
import static alex.task_manager.services.DbServices.Mappers.userModelMapper;

public class UserDbService extends BaseDbService {

    private static UserDbService mInstance;

    public static UserDbService getInstance(Context context) {
        mInstance.context = context.getApplicationContext();
        return mInstance;
    }

    @Override
    public void createDatabase(SQLiteDatabase db) {
        Log.d("Tasks Service", "onCreate database");
        // создаем таблицу с полями
        db.execSQL("CREATE TABLE Users (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login TEXT," +
                "email TEXT" +
                ");"
        );
        db.execSQL("CREATE TABLE LastUser (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER" +
                ")"
        );
        db.execSQL("INSERT INTO LastUser(_id, user_id) VALUES(0, -1);");
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Users;");
            createDatabase(db);
        }
    }
    public void setLastUser(Integer UserId){
        checkInitialized();
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL(String.format("UPDATE Last_user SET user_id = %d WHERE _id = 0", UserId));
    }

    public Integer getCurrentUserId() {
        checkInitialized();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM Last_user LIMIT 1;", null);

        return getInt(cursor);
    }

    public UserModel getCurrentUser(){
        checkInitialized();

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                    "SELECT id, login, email " +
                    "FROM Users " +
                    "WHERE Users.id = (SELECT id FROM Last_user WHERE _id = 0);",
                null
                );
        return userModelMapper(cursor);
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

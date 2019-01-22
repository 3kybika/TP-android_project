package alex.task_manager.services.DbServices;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import alex.task_manager.models.UserModel;


public class UserDbService {

    public static final String
            USER_TABLE_NAME = "Users",
            USER_ID_COLUMN = "user_id",
            LOGIN_COLUMN = "login",
            EMAI_COLUMN = "email",

            CURRENT_USER_TABLE_NAME = "LastUser",
            CURRENT_USER_KEY_COLUMN = "_id",
            CURRENT_USER_ID_COLUMN = "user_id";
    public static final int CURRENT_USER_KEY= 0;

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

        db.execSQL("CREATE TABLE "+ USER_TABLE_NAME +" (" +
                USER_ID_COLUMN + " INTEGER PRIMARY KEY," +
                LOGIN_COLUMN   + " TEXT," +
                EMAI_COLUMN    + " TEXT" +
                ");"
        );
        String query = "CREATE TABLE " + CURRENT_USER_TABLE_NAME + " (" +
                CURRENT_USER_KEY_COLUMN + " INTEGER PRIMARY KEY," +
                CURRENT_USER_ID_COLUMN + " INTEGER" +
                ");";
        db.execSQL(query
        );
        Log.d("db", query);

        //db.execSQL("INSERT INTO " + CURRENT_USER_TABLE_NAME +
        //        "("+ CURRENT_USER_KEY_COLUMN+","  +CURRENT_USER_ID_COLUMN + " ) " +
        //        "VALUES(" + CURRENT_USER_KEY +", -1);");
    }

    public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            Log.d("UserDb", "Recreate");
            db.execSQL("DROP TABLE IF EXISTS "+ USER_TABLE_NAME +";");
            db.execSQL("DROP TABLE IF EXISTS " + CURRENT_USER_TABLE_NAME + ";");
            createDatabase(db);
        }
    }
    public void addUser(UserModel user) {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.execSQL(String.format(
                "REPLACE INTO " + USER_TABLE_NAME + " (" +
                        USER_ID_COLUMN +"," +
                        LOGIN_COLUMN + "," +
                        EMAI_COLUMN +") " +
                        "VALUES(%d, \"%s\", \"%s\");",
                user.getId(), user.getLogin(), user.getEmail()
        ));
    }

    public void setCurrentUser(UserModel user){
        SQLiteDatabase db = dbManager.getWritableDatabase();

        db.execSQL(String.format("REPLACE INTO " + CURRENT_USER_TABLE_NAME + "(" +
                CURRENT_USER_KEY_COLUMN + "," +
                CURRENT_USER_ID_COLUMN + ")" +
                " VALUES ( " + CURRENT_USER_KEY +", %d )",
                user.getId()));
        addUser(user);
    }

    public Integer getCurrentUserId() {
        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + CURRENT_USER_ID_COLUMN +
                " FROM " + CURRENT_USER_TABLE_NAME + " LIMIT 1;", null);

        return (new Mappers.IntegerMapper()).buildOneInstance(cursor);
    }

    public UserModel getCurrentUser(){

        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                    "SELECT * " +
                    " FROM "+ USER_TABLE_NAME +
                    " WHERE " + USER_ID_COLUMN + " = (SELECT " + CURRENT_USER_KEY_COLUMN + " FROM " + CURRENT_USER_TABLE_NAME + " LIMIT 1);",
                null
                );
        return (new UserModel.Builder()).buildOneInstance(cursor);
    }

    public void removeLastUser() {
        SQLiteDatabase db = dbManager.getWritableDatabase();
        db.execSQL("DELETE FROM " + CURRENT_USER_TABLE_NAME);

    }
}

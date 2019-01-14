package alex.task_manager.services.DbServices;

import android.database.Cursor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import alex.task_manager.models.TaskModel;
import alex.task_manager.models.TaskViewModel;
import alex.task_manager.models.UserModel;
import alex.task_manager.utils.TimestampUtils;
import okhttp3.Cookie;

public class Mappers {



    private static boolean toBoolean(int value) {
        /*if (value == 0 || value == 1) {
            throw new Exception("Cannot get body");
        }*/
        return value == 1;
    }

    private static boolean toBoolean(String value) {
        /*if (value == 0 || value == 1) {
            throw new Exception("Cannot get body");
        }*/
        return value.equals("True");
    }

    public static int boolToInt(boolean value) {
        return value ? 1 : 0 ;
    }



    public static UserModel userModelMapper(Cursor cursor) {
        return new UserModel(
                cursor.getInt(0),    //id;
                cursor.getString(1), //login
                cursor.getString(2)  //email
            );
    }

    public static TaskViewModel taskViewModelMapper(Cursor cursor) {
        return new TaskViewModel(
                cursor.getInt(0), //id;
                cursor.getString(1), //author;
                cursor.getString(2), //caption;
                cursor.getString(3), //about;
                toBoolean(cursor.getInt(4)), //checked;
                TimestampUtils.stringToTimestamp(cursor.getString(5)) //deadline;
        );
    }

    public static TaskModel getTaskModelMapper(Cursor cursor) {

        return new TaskModel(
                cursor.getInt(0), //_id
                cursor.getInt(1), //author_id
                cursor.getString(2), //caption
                cursor.getString(3), //about
                toBoolean(cursor.getInt(4)), //checked
                TimestampUtils.stringToTimestamp(cursor.getString(5)) //deadline
        );
    }


    public static Cookie cookieMapper(Cursor cursor) {

        String name = cursor.getString(0); // name,
        String  value = cursor.getString(1); // value,
        Long expiresAt = cursor.getLong(2); // expiresAt,
        String domain = cursor.getString(3); // domain,
        String path = cursor.getString(4); // path,
        Boolean secure = toBoolean(cursor.getString(5)); //secure,
        Boolean httpOnly = toBoolean(cursor.getString(6)); //httpOnly,
        Boolean hostOnly = toBoolean(cursor.getString(7)); //hostOnly,
        Boolean persistent = toBoolean(cursor.getString(8)); //persistent


        Cookie.Builder builder =  new Cookie.Builder();
        builder.name(name); // name,
        builder.value(value); // value,
        builder.expiresAt(expiresAt); // expiresAt
        builder.path(path); // path,

        if (hostOnly) {  //hostonly
            builder.hostOnlyDomain(domain); // domain,
        }
        else {
            builder.domain(domain); // domain,
        }

        return builder.build();
    }

    public static TaskModel getTaskModel(Cursor cursor) {
        TaskModel task;
        if(cursor.moveToFirst()) {
            try {
                task = getTaskModelMapper(cursor);
            } finally {
                cursor.close();
            }
        }
        else{
            return null;
        }
        return task;
    }

    public static  List<TaskViewModel> getTaskViewModelList(Cursor cursor) {
        final List<TaskViewModel> resultList = new ArrayList<>();
        if(cursor.moveToFirst()) {
            try {
                while (cursor.moveToNext()) {
                    resultList.add( taskViewModelMapper(cursor));
                }
            } finally {
                cursor.close();
            }
        }
        else{
            return Collections.emptyList();
        }
        return resultList;
    }

    public static  List<Cookie> getCookieslList(Cursor cursor) {
        final List<Cookie> resultList = new ArrayList<>();
        if(cursor.moveToFirst()) {
            try {
                while (cursor.moveToNext()) {
                    resultList.add( cookieMapper(cursor));
                }
            } finally {
                cursor.close();
            }
        }
        else{
            return Collections.emptyList();
        }
        return resultList;
    }

    public static Integer getInt(Cursor cursor) {
        Integer value;
        if(cursor.moveToFirst()) {
            try {
                value = cursor.getInt(0);
            } finally {
                cursor.close();
            }
        }
        else{
            return null;
        }
        return value;
    }

    public static <T>  List<T> getList(Cursor cursor) {
        final List<T> resultList = new ArrayList<>();
        if(cursor.moveToFirst()) {
            try {
                while (cursor.moveToNext()) {
                    //resultList.add( mapper(cursor));
                }
            } finally {
                cursor.close();
            }
        }
        else{
            return Collections.emptyList();
        }
        return resultList;
    }
}

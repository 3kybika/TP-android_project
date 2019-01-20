package alex.task_manager.services.DbServices;

import android.database.Cursor;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import alex.task_manager.models.DbModelBuilder;
import alex.task_manager.models.TaskModel;
import alex.task_manager.models.TaskViewModel;
import alex.task_manager.models.UserModel;
import alex.task_manager.utils.TimestampUtils;
import okhttp3.Cookie;

public class Mappers {

    public static final class StringMapper extends DbModelBuilder<String> {

        @Override
        protected String mapper(Cursor cursor) {
            return cursor.getString(0);
        }
    }

    public static final class IntegerMapper extends DbModelBuilder<Integer> {

        @Override
        protected Integer mapper(Cursor cursor) {
            return cursor.getInt(0);
        }
    }

    public static final class TimestampMapper extends DbModelBuilder<Timestamp> {

        @Override
        protected Timestamp mapper(Cursor cursor) {
            return TimestampUtils.stringToTimestamp(cursor.getString(0));
        }
    }
}

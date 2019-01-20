package alex.task_manager.models;

import android.database.Cursor;

import java.sql.Timestamp;

import alex.task_manager.services.DbServices.TasksDbService;
import alex.task_manager.utils.TimestampUtils;

public class TaskForm {

    public static final class Builder extends DbModelBuilder<TaskForm> {

        @Override
        protected TaskForm mapper(Cursor cursor) {

            int priority = cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.PRIORITY_COLUMN));

            String name = cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.TASK_NAME_COLUMN));
            String about = cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.TASK_ABOUT_COLUMN));

            Timestamp deadline = TimestampUtils.stringToTimestamp(
                    cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.DEADLINE_COLUMN))
            );
            Timestamp notificationTime = TimestampUtils.stringToTimestamp(
                    cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.NOTIFICATION_TIME_COLUMN))
            );

            return new TaskForm(
                    priority,

                    name,
                    about,

                    deadline,
                    notificationTime
            );
        }


    }

    private int priority;
    private String name;
    private String about;

    private Timestamp deadline;
    private Timestamp notificationTime;

    public TaskForm(
            int priority,
            String name,
            String about,
            Timestamp deadline,
            Timestamp notificationTime
    ) {
        this.priority = priority;
        this.name = name;
        this.about = about;
        this.deadline = deadline;
        this.notificationTime = notificationTime;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public String getAbout() {
        return about;
    }

    public String getStringDeadline() {
        return TimestampUtils.timestampToString(deadline, TimestampUtils.FULL_DATE_FORMAT);
    }

    public Timestamp getNotificationTime() {
        return notificationTime;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public String getStringNotificationTime() {
        return TimestampUtils.timestampToString(notificationTime, TimestampUtils.FULL_DATE_FORMAT);
    }

}


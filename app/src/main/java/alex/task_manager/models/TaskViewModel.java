package alex.task_manager.models;

import android.database.Cursor;
import android.util.Log;

import java.sql.Timestamp;

import alex.task_manager.services.DbServices.TasksDbService;
import alex.task_manager.services.DbServices.UserDbService;
import alex.task_manager.utils.TimestampUtils;

import static alex.task_manager.utils.TimestampUtils.timestampToString;

public class TaskViewModel {

    private int id;
    private String author;
    private String name;
    private String about;
    private boolean complited;
    private Timestamp deadline;
    private Timestamp notificationTime;
    private Timestamp lastChangeTime;

    public static final class Builder extends DbModelBuilder<TaskViewModel> {

        @Override
        protected TaskViewModel mapper(Cursor cursor) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.LOCAL_ID_COLUMN));
            String author = cursor.getString(cursor.getColumnIndexOrThrow(UserDbService.LOGIN_COLUMN));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.TASK_NAME_COLUMN));
            String about = cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.TASK_ABOUT_COLUMN));
            boolean complited = toBoolean(cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.COMPLITED_COLUMN)));
            Timestamp deadline = TimestampUtils.stringToTimestamp(cursor.getString(cursor.getColumnIndex(TasksDbService.DEADLINE_COLUMN)));

            int index = cursor.getColumnIndex(TasksDbService.NOTIFICATION_TIME_COLUMN);
            Timestamp notificationTime = index != -1 ? TimestampUtils.stringToTimestamp(cursor.getString(index)) : null;
            index = cursor.getColumnIndex(TasksDbService.LAST_UPDATE_TIME_COLUMN);
            Timestamp lastChangeTime = index != -1 ? TimestampUtils.stringToTimestamp(cursor.getString(index)) : null;

            return new TaskViewModel(id, author, name, about, complited, deadline, notificationTime, lastChangeTime);
        }
    }

    public TaskViewModel(
            int id,
            String author,
            String name,
            String about,
            boolean complited,
            Timestamp deadline,
            Timestamp notificationTime,
            Timestamp lastChangeTime
    ) {
        this.id = id;
        this.author = author;
        this.name = name;
        this.about = about;
        this.complited = complited;
        this.deadline = deadline;
        this.notificationTime = notificationTime;
        this.lastChangeTime = lastChangeTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthorId(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public boolean isComplited() {
        return complited;
    }

    public void setComplited(boolean checked) {
        this.complited = checked;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public String getStringDeadline() {
        return timestampToString(this.deadline, TimestampUtils.FULL_DATE_FORMAT);
    }

    public void setNotificationTime(Timestamp notificationTime) {
        this.notificationTime = notificationTime;
    }

    public Timestamp getNotificationTime() {
        return notificationTime;
    }

    public void setLastChangeTime(Timestamp lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public Timestamp getLastChangeTime() {
        return lastChangeTime;
    }
}

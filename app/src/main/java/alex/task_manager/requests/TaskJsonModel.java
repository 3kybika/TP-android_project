package alex.task_manager.requests;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

import alex.task_manager.models.DbModelBuilder;
import alex.task_manager.services.DbServices.TasksDbService;
import alex.task_manager.utils.TimestampUtils;

// This model uses for requests-responses
public class TaskJsonModel {
    @SerializedName("global_id")
    private int globalId;
    @SerializedName("local_id")
    private int localId;

    @SerializedName("author_id")
    private int authorId;
    @SerializedName("changed_by")
    private int changedBy;
    @SerializedName("priority")
    private int priority;

    @SerializedName("name")
    private String name;
    @SerializedName("about")
    private String about;

    @SerializedName("deadline")
    private Timestamp deadline;
    @SerializedName("notification_time")
    private Timestamp notificationTime;
    @SerializedName("last_change_time")
    private Timestamp lastChangeTime;

    @SerializedName("completed")
    private boolean completed;
    @SerializedName("deleted")
    private boolean deleted;

    public static final class Builder extends DbModelBuilder<TaskJsonModel> {

        @Override
        protected TaskJsonModel mapper(Cursor cursor) {
            int globalId = cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.GLOBAL_ID_COLUMN));
            int localId = cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.LOCAL_ID_COLUMN));

            int priority = cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.PRIORITY_COLUMN));
            int authorId = cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.AUTHOR_ID_COLUMN));
            int changedBy = cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.CHANGED_BY_COLUMN));

            String name = cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.TASK_NAME_COLUMN));
            String about = cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.TASK_ABOUT_COLUMN));

            Timestamp deadline = TimestampUtils.stringToTimestamp(
                    cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.DEADLINE_COLUMN))
            );
            Timestamp notificationTime = TimestampUtils.stringToTimestamp(
                    cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.NOTIFICATION_TIME_COLUMN))
            );

            Timestamp lastChangeTime = TimestampUtils.stringToTimestamp(
                    cursor.getString(cursor.getColumnIndexOrThrow(TasksDbService.LAST_UPDATE_TIME_COLUMN))
            );

            boolean completed = toBoolean(cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.COMPLITED_COLUMN)));
            boolean deleted = toBoolean(cursor.getInt(cursor.getColumnIndexOrThrow(TasksDbService.DELETED_COLUMN)));

            return new TaskJsonModel(

                    globalId,
                    localId,

                    authorId,
                    changedBy,

                    priority,
                    name,
                    about,

                    deadline,
                    notificationTime,
                    lastChangeTime,

                    completed,
                    deleted
            );
        }
    }

    public TaskJsonModel(
            int globalId,
            int localId,

            int authorId,
            int changedBy,

            int priority,
            String name,
            String about,

            Timestamp deadline,
            Timestamp notificationTime,
            Timestamp lastChangeTime,

            boolean completed,
            boolean deleted
    ) {
        this.globalId = globalId;
        this.localId = localId;

        this.authorId = authorId;
        this.changedBy = changedBy;
        this.priority = priority;

        this.name = name;
        this.about = about;

        this.deadline = deadline;
        this.notificationTime = notificationTime;
        this.lastChangeTime = lastChangeTime;

        this.completed = completed;
        this.deleted = deleted;
    }

    public int getGlobalId() {
        return globalId;
    }

    public int getLocalId() {
        return localId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getChangedBy() {
        return changedBy;
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

    public Timestamp getDeadline() {
        return deadline;
    }

    public Timestamp getNotificationTime() {
        return notificationTime;
    }

    public String getStringDeadline() {
        return TimestampUtils.timestampToString(deadline, TimestampUtils.FULL_DATE_FORMAT);
    }

    public String getStringNotificationTime() {
        return TimestampUtils.timestampToString(notificationTime, TimestampUtils.FULL_DATE_FORMAT);
    }

    public Timestamp getLastChangeTime() {
        return lastChangeTime;
    }

    public String getStringLastChangeTime() {
        return TimestampUtils.timestampToString(notificationTime, TimestampUtils.FULL_DATE_FORMAT);
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
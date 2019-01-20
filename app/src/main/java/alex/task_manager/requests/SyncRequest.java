package alex.task_manager.requests;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.List;

public class SyncRequest {

    @SerializedName("last_sync_time")
    Timestamp lastSyncTime;
    @SerializedName("current_tasks")
    List<TaskJsonModel> currentTasks;

    public SyncRequest(
            Timestamp lastSyncTime,
            List<TaskJsonModel> currentTasks
    ){
        this.lastSyncTime = lastSyncTime;
        this.currentTasks = currentTasks;
    }

    public SyncRequest(){}

    public void setLastSyncTime(Timestamp lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public void setCurrentTasks(List<TaskJsonModel> currentTasks) {
        this.currentTasks = currentTasks;
    }
}

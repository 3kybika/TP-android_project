package alex.task_manager.responses;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.List;


import alex.task_manager.requests.TaskJsonModel;

public class SyncResponse {

    @SerializedName("sync_time")
    Timestamp lastSyncTime;
    @SerializedName("updated_tasks")
    List<TaskJsonModel> currentTasks;


    public SyncResponse(
            Timestamp lastSyncTime,
            List<TaskJsonModel> currentTasks
    ){
        this.lastSyncTime = lastSyncTime;
        this.currentTasks = currentTasks;
    }

    public SyncResponse(){}


    public List<TaskJsonModel> getCurrentTasks() {
        return currentTasks;
    }

    public Timestamp getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Timestamp lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public void setCurrentTasks(List<TaskJsonModel> currentTasks) {
        this.currentTasks = currentTasks;
    }

}

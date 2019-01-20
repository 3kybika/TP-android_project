package alex.task_manager.services.SyncService;


import android.content.Context;

import java.sql.Timestamp;

import alex.task_manager.models.DefaultResponse;
import alex.task_manager.requests.SyncRequest;
import alex.task_manager.responses.SyncResponse;
import alex.task_manager.services.DbServices.DatabaseManager;
import alex.task_manager.services.DbServices.SyncDbService;
import alex.task_manager.services.DbServices.TasksDbService;
import alex.task_manager.services.DbServices.UserDbService;
import alex.task_manager.services.NetworkServices.SyncNetworkService;

public class SyncService {

    SyncNetworkService syncNetworkService;

    SyncDbService syncDbService;
    TasksDbService tasksDbService;

    private boolean online;

    private static SyncService mInstance;

    public static synchronized SyncService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SyncService(context);
        }
        return mInstance;
    }

    private SyncService(Context context) {
        syncDbService = SyncDbService.getInstance(context);
        tasksDbService = TasksDbService.getInstance(context);
        syncNetworkService = SyncNetworkService.getInstance(context);
    }

    SyncNetworkService.OnSyncListener syncListener = new SyncNetworkService.OnSyncListener() {
        @Override
        public void onSuccess(SyncResponse response) {
            tasksDbService.syncTasks(response.getCurrentTasks());
            syncDbService.setLastSyncTime(response.getLastSyncTime());
        }

        @Override
        public void onError(Exception error) {
            //ToDo
        }

        @Override
        public void onForbidden(DefaultResponse response) {
            //ToDo
        }

        @Override
        public void onNotFound(DefaultResponse response) {
            //ToDo
        }
    };

    private boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void synchronize() {
        SyncRequest request = new SyncRequest();

        Timestamp lastSyncTime = syncDbService.getLastSyncTime();
        request.setLastSyncTime(lastSyncTime);

        request.setCurrentTasks(
                (tasksDbService.getTasksForSync(lastSyncTime))
        );

        syncNetworkService.sync(request, syncListener);

    }
}

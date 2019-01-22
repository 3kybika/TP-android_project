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

    class SyncListener implements SyncNetworkService.OnSyncListener{

        private syncListenerInterface syncListener;

        SyncListener(syncListenerInterface syncListener) {
            this.syncListener = syncListener;
        }

        @Override
        public void onSuccess(SyncResponse response) {
            tasksDbService.syncTasks(response.getCurrentTasks());
            syncDbService.setLastSyncTime(response.getLastSyncTime());
            syncListener.onSuccess();
        }

        @Override
        public void onError(Exception error) {
            syncListener.onError(error);
        }

        @Override
        public void onForbidden(DefaultResponse response) {
            syncListener.onErrorResponse(response);
        }

        @Override
        public void onNotFound(DefaultResponse response) {
            syncListener.onErrorResponse(response);
        }
    };

    private boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void synchronize(syncListenerInterface listener) {
        SyncRequest request = new SyncRequest();

        Timestamp lastSyncTime = syncDbService.getLastSyncTime();

        request.setLastSyncTime(lastSyncTime);
        request.setCurrentTasks(
                (tasksDbService.getTasksForSync(lastSyncTime))
        );
        SyncListener syncListener = new SyncListener(listener);
        syncNetworkService.sync(request, syncListener);
    }

    public interface syncListenerInterface {
        void onSuccess();

        void onError(final Exception error);

        void onErrorResponse(final DefaultResponse response);
    }
}

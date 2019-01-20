package alex.task_manager.api;

import java.util.ArrayList;

import alex.task_manager.models.TaskModel;
import alex.task_manager.requests.SyncRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SyncApi {

    @POST("sync")
    Call<ResponseBody> sync(
            @Body SyncRequest body
    );
}

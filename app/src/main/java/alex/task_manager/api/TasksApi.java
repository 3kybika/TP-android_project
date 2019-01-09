package alex.task_manager.api;

import java.util.ArrayList;

import alex.task_manager.models.TaskModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TasksApi {

    @GET("tasks")
    Call<ResponseBody> getTasks();

    @POST("createtasks")
    Call<ResponseBody> sendTasks(
            @Body ArrayList<TaskModel> body
    );
}

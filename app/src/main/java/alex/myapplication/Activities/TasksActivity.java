package alex.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import alex.myapplication.Adapters.TasksAdapter;
import alex.myapplication.R;
import alex.myapplication.models.UserModel;
import alex.myapplication.services.NetworkService;
import alex.myapplication.services.TasksService;

public class TasksActivity extends AppCompatActivity {
    private NetworkService networkService = NetworkService.getInstance();
    private TasksService tasksService = TasksService.getInstance();

    private RecyclerView tasksRecyclerView;
    private TasksAdapter tasksAdapter;

    private NetworkService.OnUserGetListener  userListener = new NetworkService.OnUserGetListener() {
        @Override
        public void onUserSuccess(final UserModel user) {
            // Already Loggined or signed up
            Log.d("Task activity", "loggined as:" + user.getLogin());
            Toast.makeText(TasksActivity.this, "Loggined as" + user.getLogin(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onUserError(final Exception error) {
            //ToDo : network disabled! - offline work
            Toast.makeText(TasksActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        // is already signed in?
        Log.d("Tasks activity", "try to sign up...");
        // ToDo: storage service!
    }

    @Override
    protected void onStart() {
        super.onStart();
        initRecyclerView();
        loadTasks();
    }

    private void loadTasks(){
        tasksAdapter.setItems(tasksService.loadTasks());
    }

    private void initRecyclerView() {
        tasksRecyclerView = findViewById(R.id.tasks_recycler_view);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TasksAdapter();
        tasksRecyclerView.setAdapter(tasksAdapter);
    }
}

package alex.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import alex.myapplication.models.UserModel;
import alex.myapplication.services.NetworkService;

public class TasksActivity extends AppCompatActivity {
    private NetworkService networkService = NetworkService.getInstance();

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
        // is already signed in?
        Log.d("Tasks activity", "try to sign up...");
        // ToDo: storage service!
        networkService.getme(userListener);
    }
}

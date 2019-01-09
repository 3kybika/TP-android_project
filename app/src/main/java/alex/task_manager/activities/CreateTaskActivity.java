package alex.task_manager.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import java.sql.Timestamp;

import alex.task_manager.R;
import alex.task_manager.models.TaskModel;
import alex.task_manager.services.DbServices.TasksDbService;
import alex.task_manager.services.DbServices.UserDbService;

public class CreateTaskActivity  extends AppCompatActivity implements View.OnClickListener {

    private UserDbService userDbService;
    private TasksDbService tasksDbService;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private CalendarView calendar;

    public CreateTaskActivity(Context context) {
        userDbService = UserDbService.getInstance(context);
        tasksDbService = TasksDbService.getInstance(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        calendar = findViewById(R.id.editDate);

        findViewById(R.id.buttonLogin).setOnClickListener(this);
        findViewById(R.id.textViewRegister).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ToDo: Must it inspect: are already loggined in?
    }

    public void createTask(){
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty()) {
            editTextTitle.setError(getString(R.string.CreatingTaskActivity__err__nameEmpty));
            editTextDescription.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            editTextDescription.setError("Password required");
            editTextDescription.requestFocus();
            return;
        }

        //dateTextView.setText(calendar.get(Calendar.MONTH) + ":" + calendar.get(Calendar.DAY_OF_MONTH))

        tasksDbService.createTask(new TaskModel(
                userDbService.getCurrentUserId(),
                title,
                description
        ));
    }

    private static void hideKeyboard(final View input) {
        final InputMethodManager inputMethodManager = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createTaskButton:
                createTask();
                break;
            default:
                hideKeyboard(view);
        }
    }
}

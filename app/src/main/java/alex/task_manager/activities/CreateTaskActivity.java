package alex.task_manager.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Calendar;

import alex.task_manager.R;
import alex.task_manager.models.TaskModel;
import alex.task_manager.services.DbServices.TasksDbService;
import alex.task_manager.services.DbServices.UserDbService;

public class CreateTaskActivity  extends AppCompatActivity implements View.OnClickListener {

    private UserDbService userDbService;
    private TasksDbService tasksDbService;

    private EditText taskTitleEditText, taskDescriptionEditText;
    private TextView calendarTextView, alarmTimeTextView;

    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        taskTitleEditText = findViewById(R.id.editTextTitle);
        taskDescriptionEditText = findViewById(R.id.editTextDescription);
        calendarTextView = findViewById(R.id.dateTextTitle);
        alarmTimeTextView = findViewById(R.id.alarmText);

        findViewById(R.id.resetTimeDeadlineBtn).setOnClickListener(this);
        findViewById(R.id.todayBtn).setOnClickListener(this);
        findViewById(R.id.tomorrowBtn).setOnClickListener(this);
        findViewById(R.id.nextWeekBtn).setOnClickListener(this);
        findViewById(R.id.setDataTimeBtn).setOnClickListener(this);
        findViewById(R.id.resetTimeAlarmBtn).setOnClickListener(this);
        findViewById(R.id.setTimeAlarmBtn).setOnClickListener(this);
        findViewById(R.id.createTaskButton).setOnClickListener(this);

        userDbService = UserDbService.getInstance(this.getApplicationContext());
        tasksDbService = TasksDbService.getInstance(this.getApplicationContext());

        initCalendar();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void createTask(){
        String title = taskTitleEditText.getText().toString().trim();
        String description = taskDescriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            taskTitleEditText.setError(getString(R.string.CreatingTaskActivity__err__nameEmpty));
            taskTitleEditText.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            taskDescriptionEditText.setError("Password required");
            taskDescriptionEditText.requestFocus();
            return;
        }

        //dateTextView.setText(calendar.get(Calendar.MONTH) + ":" + calendar.get(Calendar.DAY_OF_MONTH))

        tasksDbService.createTask(new TaskModel(
                userDbService.getCurrentUserId(),
                title,
                description
        ));

        Toast.makeText(
                CreateTaskActivity.this,
                getResources().getText(R.string.CreatingTaskActivity__infoMsg__successCreating),
                Toast.LENGTH_LONG
        ).show();

        finish();
    }

    private static void hideKeyboard(final View input) {
        final InputMethodManager inputMethodManager = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

    private void startTimeAlarmDialog() {
        new TimePickerDialog(
                CreateTaskActivity.this,
                timeDialogListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true)
                .show();
    }

    private void startCalendarDialog() {
        new DatePickerDialog(
                CreateTaskActivity.this,
                calendarDialogListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    TimePickerDialog.OnTimeSetListener timeDialogListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            setTime();
        }
    };

    DatePickerDialog.OnDateSetListener calendarDialogListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateTextView();
        }
    };

    private void setTime() {
        alarmTimeTextView.setText(
                "Hours:" + calendar.get(Calendar.HOUR) +
                "Minutes:" + calendar.get(Calendar.MINUTE)
        );
    }

    private void  updateDateTextView() {
        //ToDo: unhardcode
        calendarTextView.setText(
                "Year:  " + calendar.get(Calendar.YEAR) +
                "Month: " + calendar.get(Calendar.MONTH) +
                "Date:  " + calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void initCalendar() {
        calendar.clear();
    }

    private void resetTimeAlarm() {
        calendar.set(Calendar.HOUR, -1);
        calendar.set(Calendar.MINUTE, -1);

        calendarTextView.setText("");
    }

    private void resetDateTime() {
        calendar.set(Calendar.YEAR, -1);
        calendar.set(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, -1);

        alarmTimeTextView.setText("");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.createTaskButton:
                createTask();
                break;
            case R.id.resetTimeAlarmBtn:
                resetDateTime();
                break;
            case R.id.resetTimeDeadlineBtn:
                resetTimeAlarm();
                break;
            case R.id.todayBtn:
                Calendar tmpCalendar = Calendar.getInstance();

                int date = tmpCalendar.get(Calendar.DAY_OF_MONTH);
                int month = tmpCalendar.get(Calendar.MONTH);
                int year = tmpCalendar.get(Calendar.YEAR);

                calendar.set(Calendar.YEAR, date);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, year);

                updateDateTextView();
                break;
            case R.id.tomorrowBtn:
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                updateDateTextView();
                break;
            case R.id.nextWeekBtn:
                calendar.add(Calendar.MONTH, 1);
                updateDateTextView();
                break;
            case R.id.setDataTimeBtn:
                startCalendarDialog();
                break;
            case R.id.setTimeAlarmBtn:
                startTimeAlarmDialog();
                break;
            default:
                hideKeyboard(view);
        }
    }
}

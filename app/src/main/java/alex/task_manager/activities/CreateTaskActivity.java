package alex.task_manager.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import alex.task_manager.utils.TimestampUtils;

public class CreateTaskActivity  extends AppCompatActivity {

    private UserDbService userDbService;
    private TasksDbService tasksDbService;

    private EditText taskTitleEditText, taskDescriptionEditText;
    private TextView calendarTextView, alarmTimeTextView;

    Calendar calendar = Calendar.getInstance();

    private boolean editing = false;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        taskTitleEditText = findViewById(R.id.editTextTitle);
        taskDescriptionEditText = findViewById(R.id.editTextDescription);
        calendarTextView = findViewById(R.id.dateTextTitle);
        alarmTimeTextView = findViewById(R.id.alarmText);
        Button createTaskButton = findViewById(R.id.createTaskButton);

                userDbService = UserDbService.getInstance(this.getApplicationContext());
        tasksDbService = TasksDbService.getInstance(this.getApplicationContext());

        initCalendar();
        initButtonHandlers();

        // recieving data
        Intent intent = getIntent();
        id = intent.getIntExtra("task_id", -1);
        if (id != -1) {
            editing = true;
            TaskModel task = TasksDbService.getInstance(getApplicationContext()).getTaskById(id);
            fillFieldsWithTask(task);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void fillFieldsWithTask(TaskModel task) {
        taskTitleEditText.setText(task.getCaption());
        taskDescriptionEditText.setText(task.getAbout());
        calendar.setTime(task.getTime());
        updateDateTextView();
    }

    public void createTask(){
        String title = taskTitleEditText.getText().toString().trim();
        String description = taskDescriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            taskTitleEditText.setError(getString(R.string.CreatingTaskActivity__err__nameEmpty));
            taskTitleEditText.requestFocus();
            return;
        }
        if ((calendar.isSet(Calendar.HOUR_OF_DAY)
                && calendar.isSet(Calendar.MINUTE))
                && !(calendar.isSet(Calendar.YEAR)
                && calendar.isSet(Calendar.MONTH)
                && calendar.isSet(Calendar.DAY_OF_MONTH))
         ) {
            //ToDo: check this
            calendarTextView.setError(getString(R.string.CreatingTaskActivity__err__timeWithoutDate));
            return;
        }
        Timestamp time = new Timestamp(calendar.getTime().getTime());
        Log.d("creating task", "Task's timestamp is" + time);

        TaskModel task = new TaskModel(
                userDbService.getCurrentUserId(),
                title,
                description,
                time
        );

        if (!editing) {
            tasksDbService.createTask(task);
            Toast.makeText(
                    CreateTaskActivity.this,
                    getResources().getText(R.string.CreatingTaskActivity__infoMsg__successCreating),
                    Toast.LENGTH_LONG
            ).show();
        } else {
            tasksDbService.updateTask(id, task);
            Toast.makeText(
                    CreateTaskActivity.this,
                    getResources().getText(R.string.CreatingTaskActivity__infoMsg__successEditing),
                    Toast.LENGTH_LONG
            ).show();
        }

        finish();
    }

    private static void hideKeyboard(final View input) {
        final InputMethodManager inputMethodManager = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
        }
    }

    private void startTimeAlarmDialog() {
        if (calendar.isSet(Calendar.HOUR_OF_DAY)
                && calendar.isSet(Calendar.MINUTE)
                ) {
            new TimePickerDialog(
                    CreateTaskActivity.this,
                    timeDialogListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            ).show();
        } else {
            Calendar tmpCalendar = Calendar.getInstance();
            new TimePickerDialog(
                    CreateTaskActivity.this,
                    timeDialogListener,
                    tmpCalendar.get(Calendar.HOUR_OF_DAY),
                    tmpCalendar.get(Calendar.MINUTE),
                    true
            ).show();
        }
    }

    private void startCalendarDialog() {
        if (calendar.isSet(Calendar.YEAR)
                && calendar.isSet(Calendar.MONTH)
                && calendar.isSet(Calendar.DAY_OF_MONTH)
        ) {
            new DatePickerDialog(
                    CreateTaskActivity.this,
                    calendarDialogListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        } else {
            Calendar tmpCalendar = Calendar.getInstance();
            new DatePickerDialog(
                    CreateTaskActivity.this,
                    calendarDialogListener,
                    tmpCalendar.get(Calendar.YEAR),
                    tmpCalendar.get(Calendar.MONTH),
                    tmpCalendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        }
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
        if (calendar.isSet(Calendar.HOUR_OF_DAY) && calendar.isSet(Calendar.MINUTE)) {
            alarmTimeTextView.setText(TimestampUtils.calendarToString(
                    calendar,
                    TimestampUtils.USER_FRIENDLY_TIME_FORMAT
            ));
        } else {
            alarmTimeTextView.setText("");
        }
    }

    private void  updateDateTextView() {
        if (calendar.isSet(Calendar.YEAR)
                && calendar.isSet(Calendar.MONTH)
                && calendar.isSet(Calendar.DAY_OF_MONTH)
        ) {
            calendarTextView.setText(TimestampUtils.calendarToString(
                    calendar,
                    TimestampUtils.USER_FRIENDLY_DATE_FORMAT
            ));
        } else {
            calendarTextView.setText("");
        }
    }

    private void initCalendar() {
        calendar.clear();
    }

    private void initButtonHandlers() {

        View createTaskButton = findViewById(R.id.createTaskButton);
        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });

        View resetTimeAlarmBtn = findViewById(R.id.resetTimeAlarmBtn);
        resetTimeAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDateTime();
            }
        });

        View resetTimeDeadlineBtn = findViewById(R.id.resetTimeDeadlineBtn);
        resetTimeDeadlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimeAlarm();
            }
        });

        View todayBtn = findViewById(R.id.todayBtn);
        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar tmpCalendar = Calendar.getInstance();

                int date = tmpCalendar.get(Calendar.DAY_OF_MONTH);
                int month = tmpCalendar.get(Calendar.MONTH);
                int year = tmpCalendar.get(Calendar.YEAR);

                calendar.set(Calendar.YEAR, date);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, year);

                updateDateTextView();
            }
        });

        View tomorrowBtn = findViewById(R.id.tomorrowBtn);
        tomorrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar tmpCalendar = Calendar.getInstance();
                tmpCalendar.add(Calendar.DAY_OF_YEAR, 1);

                int date = tmpCalendar.get(Calendar.DAY_OF_MONTH);
                int month = tmpCalendar.get(Calendar.MONTH);
                int year = tmpCalendar.get(Calendar.YEAR);

                calendar.set(Calendar.YEAR, date);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, year);

                updateDateTextView();
            }
        });

        View nextWeekBtn = findViewById(R.id.nextWeekBtn);
        nextWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar tmpCalendar = Calendar.getInstance();
                tmpCalendar.add(Calendar.DAY_OF_YEAR, 7);

                int date = tmpCalendar.get(Calendar.DAY_OF_MONTH);
                int month = tmpCalendar.get(Calendar.MONTH);
                int year = tmpCalendar.get(Calendar.YEAR);

                calendar.set(Calendar.YEAR, date);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, year);

                updateDateTextView();
            }
        });

        View setDateTimeBtn = findViewById(R.id.setDateBtn);
        setDateTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalendarDialog();
            }
        });

        View setTimeAlarmBtn = findViewById(R.id.setTimeAlarmBtn);
        setTimeAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeAlarmDialog();
            }
        });
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
}

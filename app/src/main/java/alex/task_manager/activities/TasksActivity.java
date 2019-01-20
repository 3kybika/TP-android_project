package alex.task_manager.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import alex.task_manager.R;
import alex.task_manager.adapters.TasksRvCursorAdapter;
import alex.task_manager.services.DbServices.TasksDbService;
import alex.task_manager.services.DbServices.UserDbService;
import alex.task_manager.adapters.SwipeToDeleteCallback;
import alex.task_manager.services.SyncService.SyncService;

public class TasksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView tasksRecyclerView;
    private TasksRvCursorAdapter tasksAdapter;

    private TasksDbService tasksDbService;
    private UserDbService usersDbService;
    private SyncService syncService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCreationTaskPage();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tasksDbService = TasksDbService.getInstance(this.getApplicationContext());
        usersDbService  = UserDbService.getInstance(this.getApplicationContext());
        syncService = SyncService.getInstance(this.getApplicationContext());

    }

    @Override
    public void onStart() {
        super.onStart();
        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        processUpdatingData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        tasksAdapter = new TasksRvCursorAdapter(
                this.getApplicationContext(),
                tasksDbService.getTaskModelCursorByPerformerId(usersDbService.getCurrentUserId()),
                TasksDbService.LOCAL_ID_COLUMN
        );

        processUpdatingData();

        tasksRecyclerView = findViewById(R.id.tasks_recycler_view);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(tasksAdapter));

        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);
    }

    public void updateList(Cursor cursor) {
        tasksAdapter.changeCursor(cursor, TasksDbService.LOCAL_ID_COLUMN);
    }

    public void processUpdatingData() {
        updateList(tasksDbService.getTaskModelCursorByPerformerId(usersDbService.getCurrentUserId()));
    }

    public void goToCreationTaskPage() {
        // Go to tasks creation page
        Intent intent = new Intent(TasksActivity.this, CreateTaskActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
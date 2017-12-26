package com.jwhh.jim.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mNotesLayoutManager;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private GridLayoutManager mCoursesLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.pref_main_inuse, false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    //    addMenuToNavView();

        initializeDisplayContent();
    }

    /*
    * Dynamically adding menus to navigation view
    * */
    private void addMenuToNavView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();
        Menu subMenu = menu.addSubMenu("New Sub Menu");

        subMenu.add("SubMenu Item1");
        subMenu.add("SubMenu Item2");
        subMenu.add("SubMenu Item3");

        navigationView.invalidate();
    }

    private void initializeDisplayContent() {
        mRecyclerView = (RecyclerView) findViewById(R.id.list_notes);
        mNotesLayoutManager = new LinearLayoutManager(this);
        mCoursesLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.course_grid_span));


        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mCourseRecyclerAdapter = new CourseRecyclerAdapter(this, courses);

        displayNotes();
    }

    private void displayNotes() {
        mRecyclerView.setLayoutManager(mNotesLayoutManager);
        mRecyclerView.setAdapter(mNoteRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_notes);
    }

    private void selectNavigationMenuItem(int id) {
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    private void displayCourses() {
        mRecyclerView.setAdapter(mCourseRecyclerAdapter);
        mRecyclerView.setLayoutManager(mCoursesLayoutManager);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        mAdapterNotes.notifyDataSetChanged();
        mNoteRecyclerAdapter.notifyDataSetChanged();
        updateNavHeader();
    }

    private void updateNavHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textDisplayName = (TextView) headerView.findViewById(R.id.text_display_name);
        TextView textEmail = (TextView) headerView.findViewById(R.id.text_email);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String displayName = preferences.getString(this.getString(R.string.key_display_name), "");
        String email = preferences.getString(this.getString(R.string.key_email), "");

        textDisplayName.setText(displayName);
        textEmail.setText(email);


    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_notes) {
            displayNotes();
        } else if (id == R.id.nav_courses) {
            displayCourses();
        } else if (id == R.id.nav_send) {
            handleSelection(getString(R.string.nav_send_alert));
        } else if (id == R.id.nav_share) {
            handleShare();
        } else if(id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleShare() {
        View view = findViewById(R.id.list_notes);
        String social = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.key_social_network), "");
        Snackbar.make(view, "Share to: " + social, Snackbar.LENGTH_SHORT).show();

    }

    private void handleSelection(String msg) {
        View view = findViewById(R.id.list_notes);
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }


}

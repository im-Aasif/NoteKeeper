package com.jwhh.jim.notekeeper.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.jwhh.jim.notekeeper.R;
import com.jwhh.jim.notekeeper.adapter.CourseRecyclerAdapter;
import com.jwhh.jim.notekeeper.adapter.NoteRecyclerAdapter;
import com.jwhh.jim.notekeeper.db.DataManager;
import com.jwhh.jim.notekeeper.db.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.jwhh.jim.notekeeper.db.NoteKeeperOpenHelper;
import com.jwhh.jim.notekeeper.model.CourseInfo;
import com.jwhh.jim.notekeeper.provider.NoteKeeperProviderContract.Notes;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_NOTES = 0;
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mNotesLayoutManager;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private GridLayoutManager mCoursesLayoutManager;
    private NoteKeeperOpenHelper mDbOpenHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new NoteKeeperOpenHelper(this);

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

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    /*
     * Dynamically adding menus to navigation view
     *
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
        DataManager.loadFromDatabase(mDbOpenHelper);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_notes);
        mNotesLayoutManager = new LinearLayoutManager(this);
        mCoursesLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.course_grid_span));

        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, null);

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
//        loadNotes();
        getLoaderManager().restartLoader(LOADER_NOTES, null, this);
        updateNavHeader();
    }

    private void loadNotes() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String[] notesColumns = {
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry._ID
        };
        String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + ", " + NoteInfoEntry.COLUMN_NOTE_TITLE;
        final Cursor notesCursor = db.query(NoteInfoEntry.TABLE_NAME, notesColumns, null, null, null, null, noteOrderBy);

        mNoteRecyclerAdapter.changeCursor(notesCursor);
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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
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
        if (id == R.id.nav_notes) {
            displayNotes();
        } else if (id == R.id.nav_courses) {
            displayCourses();
        } else if (id == R.id.nav_send) {
            handleSelection(getString(R.string.nav_send_alert));
        } else if (id == R.id.nav_share) {
            handleShare();
        } else if (id == R.id.nav_settings) {
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_NOTES) {

            String[] notesColumns = {
                    Notes._ID,
                    Notes.COLUMN_NOTE_TITLE,
                    Notes.COLUMN_COURSE_TITLE
            };
            String noteOrderBy = Notes.COLUMN_COURSE_TITLE + ", " + Notes.COLUMN_NOTE_TITLE;

            loader = new CursorLoader(this, Notes.CONTENT_EXPANDED_URI, notesColumns, null, null, noteOrderBy);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_NOTES) {
            mNoteRecyclerAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_NOTES) {
            mNoteRecyclerAdapter.changeCursor(null);
        }
    }
}

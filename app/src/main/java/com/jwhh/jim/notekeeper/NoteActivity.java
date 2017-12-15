package com.jwhh.jim.notekeeper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_POSITION = "com.jwhh.jim.notekeeper.NOTE_POSITION";
    private static final String ORIGINAL_NOTE_cID = "com.jwhh.jim.notekeeper.ORIGINAL_NOTE_cID";
    private static final String ORIGINAL_NOTE_TITLE = "com.jwhh.jim.notekeeper.ORIGINAL_NOTE_TITLE";
    private static final String ORIGINAL_NOTE_TEXT = "com.jwhh.jim.notekeeper.ORIGINAL_NOTE_TEXT";
    private static final String TAG = NoteActivity.class.getSimpleName();

    private static final int POSITION_NOT_SET = -1;
    private static final int SHOW_CAMERA = 23;

    private NoteInfo mNote;
    private boolean mIsNewNote, mIsCancelling;
    private EditText mTextNoteTitle, mTextNoteText;
    private Spinner mSpinnerCourses;
    private Button mButtonAttachment;
    private ImageView mImageView;
    private int mNewPosition;
    private String mOriginalNoteCourseId, mOriginalNoteTitle, mOriginalNoteText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerCourses = (Spinner) findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        if(savedInstanceState == null) {
            saveOriginalStateValues();
        } else {
            restoreOriginalStateValue(savedInstanceState);
        }


        mTextNoteTitle = (EditText) findViewById(R.id.text_note_title);
        mTextNoteText = (EditText) findViewById(R.id.text_note_text);
        mButtonAttachment = (Button) findViewById(R.id.btn_attach);
        mImageView = (ImageView) findViewById(R.id.img_view);
        setButtonListener();

        if (!mIsNewNote) {
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);
        }
    }

    private void restoreOriginalStateValue(Bundle _savedInstanceState) {
        mOriginalNoteCourseId = _savedInstanceState.getString(ORIGINAL_NOTE_cID);
        mOriginalNoteTitle = _savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = _savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ORIGINAL_NOTE_cID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
    }


    private void saveOriginalStateValues() {
        if(mIsNewNote)
            return;

        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteText = mNote.getText();
        mOriginalNoteTitle = mNote.getTitle();
    }

    private void setButtonListener() {
        mButtonAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String dir = Environment.getDataDirectory() + "/data/" + getPackageName();
                String filename = "img101";
                Uri dirUri = new Uri.Builder().appendEncodedPath(dir).build();
                Uri path = Uri.withAppendedPath(dirUri, filename);
                Log.d(TAG, "onClick: PATH: " + path);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, SHOW_CAMERA);
                }
            }
        });
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNote.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());
    }

    private void  readDisplayStateValues() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mIsNewNote = position == POSITION_NOT_SET;
        if (mIsNewNote) {
            createNewNote();
        } else {
            mNote = DataManager.getInstance().getNotes().get(position);
        }

    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNewPosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNewPosition);

    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = mTextNoteText.getText().toString();
        String body = "Checkout what I learned in the Pluralsight course \"" + course.getTitle() + "\"\n" + text;

        // Prepare implicit intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SHOW_CAMERA && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: Intent Data Received: " + data);
            Bitmap thumbnail = data.getParcelableExtra("data");
            Log.d(TAG, "onActivityResult: Bitmap: " + thumbnail);
            mImageView.setImageBitmap(thumbnail);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling) {
            if(mIsNewNote) {
                DataManager.getInstance().removeNote(mNewPosition);
            } else {
                storePreviousStateValues();
            }
        } else {
            saveNote();
        }

    }

    private void storePreviousStateValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mOriginalNoteTitle);
        mNote.setText(mOriginalNoteText);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setText(mTextNoteText.getText().toString());
        mNote.setTitle(mTextNoteTitle.getText().toString());

    }
}

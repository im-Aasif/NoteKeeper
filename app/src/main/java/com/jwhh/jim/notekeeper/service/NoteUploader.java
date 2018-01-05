package com.jwhh.jim.notekeeper.service;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import static com.jwhh.jim.notekeeper.provider.NoteKeeperProviderContract.Notes;

/**
 * Created by Bohiyaanam on 05/01/18.
 */

public class NoteUploader {
    private final String TAG = getClass().getSimpleName();
    private final Context mContext;
    private boolean mCanceled;

    public NoteUploader(Context context) {
        mContext = context;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public void performUpload(Uri dataUri) {
        String[] columns = {
                Notes.COLUMN_COURSE_ID,
                Notes.COLUMN_NOTE_TITLE,
                Notes.COLUMN_NOTE_TEXT
        };

        Cursor cursor = mContext.getContentResolver().query(dataUri, columns, null, null, null);
        if (cursor == null) {
            return;
        }
        int courseIdPos = cursor.getColumnIndex(Notes.COLUMN_COURSE_ID);
        int noteTitlePos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TITLE);
        int noteTextPos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TEXT);

        Log.i(TAG, "performUpload: Upload STARTED: Thread-" + Thread.currentThread().getId());

        while (cursor.moveToNext()) {
            String courseId = cursor.getString(courseIdPos);
            String noteTitle = cursor.getString(noteTitlePos);
            String noteText = cursor.getString(noteTextPos);

            if (!noteTitle.equals("")) {
                Log.i(TAG, "performUpload: Uploading note: " + courseId + "  || " + noteTitle + " || " + noteText);
                simulateLongRunningTask();
            }
        }

        Log.i(TAG, "performUpload: UPLOAD COMPLETE");
        cursor.close();
    }

    private static void simulateLongRunningTask() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

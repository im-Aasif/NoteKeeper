package com.jwhh.jim.notekeeper.service;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import static com.jwhh.jim.notekeeper.provider.NoteKeeperProviderContract.Notes;

/**
 * Created by Bohiyaanam on 04/01/18.
 */

public class NotesBackup {
    public static final String ALL_COURSES = "ALL_COURSES";
    private static final String TAG = NotesBackup.class.getSimpleName();

    public static void doBackup(Context context, String backupCourseId) {
        String[] columns = {
                Notes.COLUMN_COURSE_ID,
                Notes.COLUMN_NOTE_TITLE,
                Notes.COLUMN_NOTE_TEXT,
        };

        String selection = null;
        String[] selectionArgs = null;

        if (!backupCourseId.equals(ALL_COURSES)) {
            selection = Notes.COLUMN_COURSE_ID + " = ? ";
            selectionArgs = new String[]{backupCourseId};
        }

        Cursor cursor = context.getContentResolver().query(Notes.CONTENT_URI, columns, selection, selectionArgs, null);
        if (cursor == null) {
            Log.i(TAG, "doBackup: Cursor is null");
            return;
        }

        int courseIdPos = cursor.getColumnIndex(Notes.COLUMN_COURSE_ID);
        int noteTitlePos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TITLE);
        int noteTextPos = cursor.getColumnIndex(Notes.COLUMN_NOTE_TEXT);

        Log.i(TAG, "doBackup: BACKUP STARTED: Thread-" + Thread.currentThread().getId());

        while (cursor.moveToNext()) {
            String courseId = cursor.getString(courseIdPos);
            String noteTitle = cursor.getString(noteTitlePos);
            String noteText = cursor.getString(noteTextPos);

            if (!noteTitle.equals("")) {
                Log.i(TAG, "doBackup: Backing up note: " + courseId + "  || " + noteTitle + " || " + noteText);
                simulateLongRunningTask();
            }
        }

        Log.i(TAG, "doBackup: BACKUP COMPLETE");
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

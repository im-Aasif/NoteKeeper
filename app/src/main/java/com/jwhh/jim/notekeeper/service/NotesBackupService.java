package com.jwhh.jim.notekeeper.service;

import android.app.IntentService;
import android.content.Intent;

public class NotesBackupService extends IntentService {

    public static final String EXTRA_COURSE_ID = "course_id";

    public NotesBackupService() {
        super("NotesBackupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String backupCourseId = intent.getStringExtra(EXTRA_COURSE_ID);
            NotesBackup.doBackup(this, backupCourseId);
        }
    }
}

package com.jwhh.jim.notekeeper.service;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.jwhh.jim.notekeeper.BuildConfig;

public class NoteUploaderJobService extends JobService {

    public static final String EXTRA_DATA_URI = BuildConfig.APPLICATION_ID + ".DATA_URI";

    public NoteUploaderJobService() {}

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}

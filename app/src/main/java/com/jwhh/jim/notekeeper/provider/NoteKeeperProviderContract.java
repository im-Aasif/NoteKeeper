package com.jwhh.jim.notekeeper.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import static com.jwhh.jim.notekeeper.BuildConfig.APPLICATION_ID;

/**
 * Created by Bohiyaanam on 28/12/17.
 */

public final class NoteKeeperProviderContract {
    private NoteKeeperProviderContract() {
    }
    protected  interface CoursesIdColumns {
        public static final String COLUMN_COURSE_ID = "course_id";
    }
    protected interface CoursesColumns {
        public static final String COLUMN_COURSE_TITLE = "course_title";
    }
    protected interface NotesColumns {
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
    }

    public static final String AUTHORITY = APPLICATION_ID + ".provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Courses implements BaseColumns, CoursesColumns, CoursesIdColumns {
        static final String PATH = "courses";
        // content://pkg_name.provider/courses
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Notes implements BaseColumns, NotesColumns, CoursesIdColumns, CoursesColumns {
        static final String PATH = "notes";
        // content://pkg_name.provider/notes
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
        static final String PATH_EXPANDED = "notes_expanded";
        public static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED);

    }

}

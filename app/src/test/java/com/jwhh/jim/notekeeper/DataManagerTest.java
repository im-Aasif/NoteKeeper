package com.jwhh.jim.notekeeper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Bohiyaanam on 18/12/17.
 */
public class DataManagerTest {
    private static DataManager sInstance;

    @BeforeClass
    public static void classSetup() throws Exception {
        sInstance = DataManager.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        sInstance.getNotes().clear();
        sInstance.initializeExampleNotes();
    }

    @Test
    public void createNewNote() throws Exception {
        final CourseInfo course = sInstance.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText = "This is the body";

        int index = sInstance.createNewNote();
        NoteInfo note = sInstance.getNotes().get(index);
        note.setCourse(course);
        note.setTitle(noteTitle);
        note.setText(noteText);

        NoteInfo compareNote = sInstance.getNotes().get(index);
        assertEquals(noteText, compareNote.getText());
        assertEquals(noteTitle, compareNote.getTitle());
        assertEquals(course, compareNote.getCourse());
    }

    @Test
    public void findSimilarNotes() throws Exception {
        final CourseInfo course = sInstance.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText = "This is the body";
        final String noteText2 = "This is another text body";

        int index1 = sInstance.createNewNote();
        NoteInfo note = sInstance.getNotes().get(index1);
        note.setCourse(course);
        note.setTitle(noteTitle);
        note.setText(noteText);

        int index2= sInstance.createNewNote();
        NoteInfo note2 = sInstance.getNotes().get(index2);
        note2.setCourse(course);
        note2.setTitle(noteTitle);
        note2.setText(noteText2);

        int foundIndex1 = sInstance.findNote(note);
        assertEquals(index1, foundIndex1);

        int foundIndex2 = sInstance.findNote(note2);
        assertEquals(index2, foundIndex2);
    }

    @Test
    public void createNewNoteOneStepCreation() {
        final CourseInfo course = sInstance.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText = "This is the body";

        int index = sInstance.createNewNote(course, noteTitle, noteText);

        NoteInfo compareNote = sInstance.getNotes().get(index);
        assertEquals(course, compareNote.getCourse());
        assertEquals(noteTitle, compareNote.getTitle());
        assertEquals(noteText,compareNote.getText());
    }

}
package com.jwhh.jim.notekeeper;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.jwhh.jim.notekeeper.activity.NoteListActivity;
import com.jwhh.jim.notekeeper.model.CourseInfo;
import com.jwhh.jim.notekeeper.db.DataManager;
import com.jwhh.jim.notekeeper.model.NoteInfo;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;


/**
 * Created by Bohiyaanam on 18/12/17.
 *
 */
@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {
    private static DataManager sInstance;
    @BeforeClass
    public static void classSetup() throws Exception {
        sInstance = DataManager.getInstance();
    }
    /*
    * Create a new ActivityTestRule for the activity which is to be tested.
    * */
    @Rule
    public ActivityTestRule<NoteListActivity> mNoteListActivityRule = new ActivityTestRule<>(NoteListActivity.class);

    @Test
    public void createNewNote() {
        final CourseInfo course = sInstance.getCourse("java_lang");
        final String noteTitle = "Test note title";
        final String noteText = "This is the body";

        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class), equalTo(course))).perform(click());
        onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(containsString(course.getTitle()))));
        onView(withId(R.id.text_note_title)).perform(typeText(noteTitle));
        onView(withId(R.id.text_note_text)).perform(typeText(noteText), closeSoftKeyboard());

        pressBack();

        int noteIndex = sInstance.getNotes().size() - 1;
        NoteInfo note = sInstance.getNotes().get(noteIndex);
        assertEquals(course, note.getCourse());
        assertEquals(noteTitle, note.getTitle());
        assertEquals(noteText, note.getText());

    }

    @Test
    public void openAndSendMail() throws Exception {
        onView(withId(R.id.fab)).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(anyOf(withText(R.string.action_cancel), withId(R.id.action_cancel))).perform(click());

    }

}
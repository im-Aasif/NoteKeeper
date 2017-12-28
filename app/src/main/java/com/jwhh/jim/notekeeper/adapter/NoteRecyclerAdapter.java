package com.jwhh.jim.notekeeper.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jwhh.jim.notekeeper.R;
import com.jwhh.jim.notekeeper.activity.NoteActivity;
import com.jwhh.jim.notekeeper.db.NoteKeeperDatabaseContract.NoteInfoEntry;

import static com.jwhh.jim.notekeeper.db.NoteKeeperDatabaseContract.CourseInfoEntry;

/**
 * Created by Bohiyaanam on 19/12/17.
 */

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.MyVH> {

    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mCoursePos;
    private int mNoteTitlePos;
    private int mIdPos;

    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        mCursor = cursor;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if (mCursor == null)
            return;
        // Get column indexes from cursor
        mCoursePos = mCursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE);
        mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mIdPos = mCursor.getColumnIndex(NoteInfoEntry._ID);


    }

    public void changeCursor(Cursor cursor) {
        if (mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @Override
    public MyVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list, parent, false);
        return new MyVH(itemView);
    }

    @Override
    public void onBindViewHolder(MyVH holder, int position) {
        mCursor.moveToPosition(position);

        String course = mCursor.getString(mCoursePos);
        String noteTitle = mCursor.getString(mNoteTitlePos);
        int id = mCursor.getInt(mIdPos);

        holder.mTextCourse.setText(course);
        holder.mTextTitle.setText(noteTitle);
        holder.mId = id;
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public class MyVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTextCourse;
        public final TextView mTextTitle;
        public int mId;


        MyVH(View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, NoteActivity.class);
            intent.putExtra(NoteActivity.NOTE_ID, mId);
            mContext.startActivity(intent);
        }
    }
}

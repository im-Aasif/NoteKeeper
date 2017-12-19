package com.jwhh.jim.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 *
 * Created by Bohiyaanam on 19/12/17.
 */

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.MyVH> {

    private final List<NoteInfo> mNotes;
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;

    NoteRecyclerAdapter(Context context, List<NoteInfo> notes) {
        mNotes = notes;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_note_list, parent, false);
        return new MyVH(itemView);
    }

    @Override
    public void onBindViewHolder(MyVH holder, int position) {
        NoteInfo note = mNotes.get(position);
        if(null != note) {
            holder.mTextCourse.setText(note.getCourse().getTitle());
            holder.mTextTitle.setText(note.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class MyVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mTextCourse;
        final TextView mTextTitle;

        MyVH(View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, NoteActivity.class);
            intent.putExtra(NoteActivity.NOTE_POSITION, getAdapterPosition());
            mContext.startActivity(intent);
        }
    }
}

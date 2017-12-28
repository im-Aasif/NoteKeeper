package com.jwhh.jim.notekeeper.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jwhh.jim.notekeeper.R;
import com.jwhh.jim.notekeeper.model.CourseInfo;

import java.util.List;

/**
 *
 * Created by Bohiyaanam on 19/12/17.
 */

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.MyVH> {

    private final List<CourseInfo> mCourses;
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> courses) {
        mCourses = courses;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_course_list, parent, false);
        return new MyVH(itemView);
    }

    @Override
    public void onBindViewHolder(MyVH holder, int position) {
        CourseInfo course = mCourses.get(position);
        if(null != course) {
            holder.mTextCourse.setText(course.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class MyVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mTextCourse;

        MyVH(View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Snackbar.make(v, mCourses.get(getAdapterPosition()).getTitle(), Snackbar.LENGTH_SHORT).show();
        }
    }
}

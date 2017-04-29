package com.example.keynes.rollcall.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.keynes.rollcall.R;
import com.example.keynes.rollcall.data.SchoolContract.CourseEntry;

import org.w3c.dom.Text;

/**
 * Created by Keynes on 2017/4/29.
 */

public class CourseCursorAdapter extends CursorAdapter {

    public CourseCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.course_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView)view.findViewById(R.id.course_name);

        int nameColumnIndex = cursor.getColumnIndex(CourseEntry.COLUMN_COURSE_NAME);

        String courseName = cursor.getString(nameColumnIndex);

        nameTextView.setText(courseName);
    }
}

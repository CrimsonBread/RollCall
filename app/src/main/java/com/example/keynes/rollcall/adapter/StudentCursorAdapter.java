package com.example.keynes.rollcall.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.keynes.rollcall.R;
import com.example.keynes.rollcall.data.SchoolContract;
import com.example.keynes.rollcall.data.SchoolContract.StudentEntry;

import org.w3c.dom.Text;

/**
 * Created by Keynes on 2017/5/1.
 */

public class StudentCursorAdapter extends CursorAdapter {
    public StudentCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.student_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView)view.findViewById(R.id.student_name);

        int nameColumnIndex = cursor.getColumnIndex(StudentEntry.COLUMN_STUDENT_NAME);

        String studentName = cursor.getString(nameColumnIndex);

        nameTextView.setText(studentName);
    }
}

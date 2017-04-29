package com.example.keynes.rollcall;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.keynes.rollcall.adapter.CourseCursorAdapter;
import com.example.keynes.rollcall.data.SchoolContract;
import com.example.keynes.rollcall.data.SchoolContract.CourseEntry;

public class TeacherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_added:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayCourseInfo() {
        String[] projection = {
                CourseEntry._ID,
                CourseEntry.COLUMN_COURSE_NAME
        };

        Cursor cursor = getContentResolver().query(
                CourseEntry.CONTENT_URI_COURSE,
                projection,
                null,
                null,
                null
        );

        ListView courseListView = (ListView)findViewById(R.id.course_list);

        CourseCursorAdapter adapter = new CourseCursorAdapter(this, cursor);

        courseListView.setAdapter(adapter);
    }
}

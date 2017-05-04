package com.example.keynes.rollcall;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keynes.rollcall.adapter.CourseCursorAdapter;
import com.example.keynes.rollcall.data.SchoolContract.CourseEntry;

import java.util.List;

import static android.R.attr.id;

public class TeacherActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int COURSE_LOADER = 0;

    private ListView mCourseListView;

    private CourseCursorAdapter mCourseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        mCourseListView = (ListView)findViewById(R.id.course_list);

        mCourseAdapter = new CourseCursorAdapter(this, null);
        mCourseListView.setAdapter(mCourseAdapter);

        mCourseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView courseName = (TextView)view.findViewById(R.id.course_name);

                Intent intent = new Intent(TeacherActivity.this, CourseActivity.class);
                intent.putExtra("course", courseName.getText().toString());
                startActivity(intent);
            }
        });

        mCourseListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(TeacherActivity.this, CourseEditorActivity.class);

                Uri currentCourseUri = ContentUris.withAppendedId(CourseEntry.CONTENT_URI_COURSE, id);

                intent.setData(currentCourseUri);

                startActivity(intent);

                return true;
            }
        });

        // Kick off the loader
        getSupportLoaderManager().initLoader(COURSE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_teacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_added:
                Intent intent = new Intent(TeacherActivity.this, CourseEditorActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_set_teacher_name:
                return true;
            case R.id.action_delected_all:
                showDeleteConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg_all_course);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAllCourses();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllCourses() {
        int rowsDeleted = getContentResolver().delete(CourseEntry.CONTENT_URI_COURSE, null, null);
        Log.v("TeacherActivity", rowsDeleted + " rows deleted from pet database");
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                CourseEntry._ID,
                CourseEntry.COLUMN_COURSE_NAME
        };


        return new CursorLoader(this,
                CourseEntry.CONTENT_URI_COURSE,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCourseAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCourseAdapter.swapCursor(null);
    }
}

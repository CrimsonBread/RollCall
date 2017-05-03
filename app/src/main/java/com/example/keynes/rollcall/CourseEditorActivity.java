package com.example.keynes.rollcall;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keynes.rollcall.data.SchoolContract.StudentEntry;
import com.example.keynes.rollcall.data.SchoolContract.CourseEntry;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class CourseEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_COURSE_LOADER = 0;
    private static final int ACTIVITY_CHOOSE_FILE = 401;

    private static final String LOG_TAG = "CourseEditor";

    private File mfile = null;

    private EditText mNameEditText;
    private ImageButton mCsvImportButton;

    private Uri mCurrentCourseUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);

        Intent intent = getIntent();
        mCurrentCourseUri = intent.getData();

        if(mCurrentCourseUri == null) {
            setTitle(R.string.add_course);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_course);

            mCsvImportButton = (ImageButton)findViewById(R.id.csv_import_button);
            mCsvImportButton.setVisibility(View.INVISIBLE);
            // Kick off the loader
            getSupportLoaderManager().initLoader(EXISTING_COURSE_LOADER, null, this);
        }

        if(mCsvImportButton == null) {
            mCsvImportButton = (ImageButton)findViewById(R.id.csv_import_button);
        }
        mNameEditText = (EditText)findViewById(R.id.edit_course_name);

        mCsvImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCsvFile();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentCourseUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save course to database
                saveCourse();
                // Import student list from csv
                if(mfile != null) {
                    try {
                        importCsv(mNameEditText.getText().toString().trim());
                    } catch (IOException e) {
                        Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                break;
            case android.R.id.home:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch (requestCode) {
            case ACTIVITY_CHOOSE_FILE:
                if(resultCode == RESULT_OK) {
                    Toast.makeText(this, "學生名單匯入成功", Toast.LENGTH_SHORT).show();
                    mfile =  new File(data.getData().getPath());
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveCourse() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentCourseUri == null && TextUtils.isEmpty(nameString)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(CourseEntry.COLUMN_COURSE_NAME, nameString);

        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (mCurrentCourseUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(CourseEntry.CONTENT_URI_COURSE, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_course_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_course_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentCourseUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_course_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_course_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg_course);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteCourse();
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

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteCourse() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentCourseUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentCourseUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_course_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_course_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    private void chooseCsvFile() {

        isStoragePermissionGranted();

        Intent it = new Intent(Intent.ACTION_GET_CONTENT);
        it.addCategory(Intent.CATEGORY_OPENABLE);
        it.setType("text/csv");
        try {
            startActivityForResult(
                    Intent.createChooser(it, "Select a File to Upload"),
                    ACTIVITY_CHOOSE_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void importCsv(String courseName) throws IOException {
        ContentValues values = new ContentValues();
        CSVReader csvReader = new CSVReader(new InputStreamReader(
                new FileInputStream(mfile.getPath()), Charset.forName("UTF-8")));

        String[] nextLine;
        while((nextLine = csvReader.readNext()) != null) {
            values.put(StudentEntry.COLUMN_STUDENT_COURSE_ID, courseName);
            values.put(StudentEntry.COLUMN_STUDENT_NO, nextLine[0]);
            values.put(StudentEntry.COLUMN_STUDENT_NAME, nextLine[1]);

            Uri newUri = getContentResolver().insert(StudentEntry.CONTENT_URI_STUDENT, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Log.e(LOG_TAG, "Failed to insert student");
            }
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG,"Permission is granted");
                return true;
            } else {

                Log.v(LOG_TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                CourseEntry._ID,
                CourseEntry.COLUMN_COURSE_NAME
        };


        return new CursorLoader(this,
                mCurrentCourseUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (data.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(CourseEntry.COLUMN_COURSE_NAME);

            // Extract out the value from the Cursor for the given column index
            String name = data.getString(nameColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
    }
}

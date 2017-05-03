package com.example.keynes.rollcall.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.regex.Matcher;

import com.example.keynes.rollcall.data.SchoolContract.CourseEntry;
import com.example.keynes.rollcall.data.SchoolContract.StudentEntry;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by Keynes on 2017/4/28.
 */

public class SchoolProvider extends ContentProvider {

    private static final String LOG_TAG = "SchoolProvider";

    /** URI matcher code for the content URI */
    private static final int COURSES = 100;
    private static final int COURSE_ID = 101;
    private static final int STUDENTS = 110;
    private static final int STUDENT_ID = 111;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(SchoolContract.CONTENT_AUTHORITY, SchoolContract.PATH_COURSE, COURSES);
        sUriMatcher.addURI(SchoolContract.CONTENT_AUTHORITY, SchoolContract.PATH_COURSE + "/#", COURSE_ID);
        sUriMatcher.addURI(SchoolContract.CONTENT_AUTHORITY, SchoolContract.PATH_STUDENT, STUDENTS);
        sUriMatcher.addURI(SchoolContract.CONTENT_AUTHORITY, SchoolContract.PATH_STUDENT+ "/#", STUDENT_ID);
    }

    /** Database helper object */
    private SchoolDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new SchoolDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COURSES:
                return CourseEntry.CONTENT_LIST_TYPE;
            case COURSE_ID:
                return CourseEntry.CONTENT_ITEM_TYPE;
            case STUDENTS:
                return StudentEntry.CONTENT_LIST_TYPE;
            case STUDENT_ID:
                return StudentEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case COURSES:
                cursor = database.query(CourseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COURSE_ID:
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(CourseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case STUDENTS:
                cursor = database.query(StudentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case STUDENT_ID:
                selection = StudentEntry._ID + "=?";
                selectionArgs = new String[] {String .valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StudentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknow URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case COURSES:
                // Check that the name is not null
                checkNull(CourseEntry.COLUMN_COURSE_NAME, contentValues);
                return insertData(CourseEntry.TABLE_NAME, uri, contentValues);
            case STUDENTS:
                // Check that the values is not null
                checkNull(StudentEntry.COLUMN_STUDENT_NAME, contentValues);
                checkNull(StudentEntry.COLUMN_STUDENT_NO, contentValues);
                checkNull(StudentEntry.COLUMN_STUDENT_COURSE_ID, contentValues);
                return  insertData(StudentEntry.TABLE_NAME, uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COURSES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(CourseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSE_ID:
                // Delete a single row given by the ID in the URI
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(CourseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STUDENTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(StudentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STUDENT_ID:
                // Delete a single row given by the ID in the URI
                selection = StudentEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(StudentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case COURSES:
                return updateData(CourseEntry.TABLE_NAME, uri, contentValues, selection,
                        selectionArgs);
            case COURSE_ID:
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateData(CourseEntry.TABLE_NAME, uri, contentValues, selection,
                        selectionArgs);
            case STUDENTS:
                return updateData(StudentEntry.TABLE_NAME, uri, contentValues, selection,
                        selectionArgs);
            case STUDENT_ID:
                selection = StudentEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateData(StudentEntry.TABLE_NAME, uri, contentValues, selection,
                        selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private void checkNull(String columnName, ContentValues values) {
        String str = values.getAsString(columnName);
        if(str.isEmpty()) {
            throw new IllegalArgumentException("requires " + columnName);
        }
    }

    private Uri insertData(String tableName, Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(tableName, null, contentValues);
        // If the insertion failed, return null.
        if(id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private int updateData(String tableName, Uri uri, ContentValues contentValues, String selection,
            String[] selectionArgs) {
        // check that the name value is not null.
        if (contentValues.containsKey(CourseEntry.COLUMN_COURSE_NAME)) {
            String name = contentValues.getAsString(CourseEntry.COLUMN_COURSE_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Course requires a name");
            }
        }

        if (contentValues.containsKey(StudentEntry.COLUMN_STUDENT_NAME)) {
            String name = contentValues.getAsString(StudentEntry.COLUMN_STUDENT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Student requires a name");
            }
        }

        if (contentValues.containsKey(StudentEntry.COLUMN_STUDENT_NO)) {
            String name = contentValues.getAsString(StudentEntry.COLUMN_STUDENT_NO);
            if (name == null) {
                throw new IllegalArgumentException("Student requires a number");
            }
        }

        if (contentValues.containsKey(StudentEntry.COLUMN_STUDENT_COURSE_ID)) {
            String name = contentValues.getAsString(StudentEntry.COLUMN_STUDENT_COURSE_ID);
            if (name == null) {
                throw new IllegalArgumentException("Students requires a course id");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(tableName, contentValues, selection,
                selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}

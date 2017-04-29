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

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by Keynes on 2017/4/28.
 */

public class SchoolProvider extends ContentProvider {

    private static final String LOG_TAG = "SchoolProvider";

    /** URI matcher code for the content URI */
    private static final int COURSES = 100;
    private static final int COURSE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(SchoolContract.CONTENT_AUTHORITY, SchoolContract.PATH_COURSE, COURSES);
        sUriMatcher.addURI(SchoolContract.CONTENT_AUTHORITY, SchoolContract.PATH_COURSE, COURSE_ID);
    }

    /** Database helper object */
    private SchoolDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new SchoolDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case COURSES:
                cursor = database.query(CourseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COURSE_ID:
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(CourseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknow URI " + uri);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COURSES:
                return CourseEntry.CONTENT_LIST_TYPE;
            case COURSE_ID:
                return CourseEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case COURSES:
                // Check that the name is not null
                checkNull(CourseEntry.COLUMN_COURSE_NAME, contentValues);
                return insertData(CourseEntry.TABLE_NAME, uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COURSES:
                // Delete all rows that match the selection and selection args
                return database.delete(CourseEntry.TABLE_NAME, selection, selectionArgs);
            case COURSE_ID:
                // Delete a single row given by the ID in the URI
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(CourseEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
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
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private void checkNull(String columnName, ContentValues values) {
        String str = values.getAsString(columnName);
        if(str.isEmpty()) {
            throw new IllegalArgumentException("Course requires a name");
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

        return ContentUris.withAppendedId(uri, id);
    }

    private int updateData(String tableName, Uri uir, ContentValues contentValues, String selection,
                           String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        return database.update(tableName, contentValues, selection, selectionArgs);
    }
}

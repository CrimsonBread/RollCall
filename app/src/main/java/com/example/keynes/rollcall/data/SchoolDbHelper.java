package com.example.keynes.rollcall.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.keynes.rollcall.data.SchoolContract.CourseEntry;
import com.example.keynes.rollcall.data.SchoolContract.StudentEntry;

/**
 * Created by salong on 2017/2/14.
 */

public class SchoolDbHelper extends SQLiteOpenHelper {

    /** Name of database */
    public static final String DATABASE_NAME = "school.db";

    private static final int DATABASE_VERSION = 1;

    /** String that contains the SQL statement to create the table */
    String SQL_CREATE_COURSE_TABLE_COURSES = "CREATE TABLE " + CourseEntry.TABLE_NAME + " ("
            + CourseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CourseEntry.COLUMN_COURSE_NAME + " TEXT NOT NULL);";

    String SQL_CREATE_COURSE_TABLE_STUDENTS = "CREATE TABLE " + StudentEntry.TABLE_NAME + " ("
            + StudentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + StudentEntry.COLUMN_STUDENT_NAME + " TEXT NOT NULL, "
            + StudentEntry.COLUMN_STUDENT_NO + " TEXT NOT NULL, "
            + StudentEntry.COLUMN_STUDENT_COURSE_ID + " TEXT NOT NULL, "
            + StudentEntry.COLUMN_STUDENT_ADDRESS + " TEXT);";


    public SchoolDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute SQL statament
        db.execSQL(SQL_CREATE_COURSE_TABLE_COURSES);
        db.execSQL(SQL_CREATE_COURSE_TABLE_STUDENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

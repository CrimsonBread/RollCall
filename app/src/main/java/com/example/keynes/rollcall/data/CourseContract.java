package com.example.keynes.rollcall.data;

import android.provider.BaseColumns;

/**
 * Created by salong on 2017/2/14.
 */

public final class CourseContract {

    private CourseContract() {}

    public static final class CourseEntry {

        /** Name of database table for courses */
        public static final String TABLE_NAME = "courses";

        /** Name of database column for courses */
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_COURSE_NAME = "name";
    }
}

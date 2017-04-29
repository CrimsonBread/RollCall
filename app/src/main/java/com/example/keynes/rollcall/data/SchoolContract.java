package com.example.keynes.rollcall.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by salong on 2017/2/14.
 */

public final class SchoolContract {

    public static final String CONTENT_AUTHORITY = "com.example.keynes.rollcall";
    public static final Uri BASE_COLUMN_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** paht of table */
    public static final String PATH_COURSE = "courses";


    private SchoolContract() {}

    public static final class CourseEntry {
        /** The content URI to access the course data in the provider */
        public static final Uri CONTENT_URI_COURSE = Uri.withAppendedPath(BASE_COLUMN_URI, PATH_COURSE);

        /** Name of database table for courses */
        public static final String TABLE_NAME = "courses";

        /** Name of database column for courses */
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_COURSE_NAME = "name";

        /**
         * The MIME type for a list of courses.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;

        /**
         * The MIME type for a single course.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;
    }

    public static final class StudentEntry {

    }
}

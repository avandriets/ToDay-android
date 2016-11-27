package com.gcgamecore.today.Data;


import android.content.ContentResolver;
import android.net.Uri;

public class TODAYContract {

    public static final String CONTENT_AUTHORITY = "com.gcgamecore.today";

    public static final String PATH_QUESTIONS               = "questions";
    public static final String PATH_THEME                   = "theme";
    public static final String PATH_THEME_QUESTIONS                   = "theme_questions";

    public static final String QUESTIONS_CONTENT_TYPE         = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUESTIONS;
    public static final String QUESTIONS_CONTENT_ITEM_TYPE    = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUESTIONS;

    public static final String THEME_CONTENT_TYPE         = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_THEME;
    public static final String THEME_CONTENT_ITEM_TYPE    = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_THEME;

    public static final String THEME_QUESTIONS_CONTENT_TYPE         = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_THEME_QUESTIONS;
    public static final String THEME_QUESTIONS_CONTENT_ITEM_TYPE    = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_THEME_QUESTIONS;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final Uri QUESTIONS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUESTIONS).build();
    public static final Uri THEME_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_THEME).build();
    public static final Uri THEME_QUESTIONS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_THEME_QUESTIONS).build();

}

package com.gcgamecore.today.Data;


import android.content.ContentResolver;
import android.net.Uri;

import com.gcgamecore.today.BuildConfig;

public class TODAYContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;//"com.gcgamecore.today";

    public static final String PATH_QUESTIONS               = "questions";
    public static final String PATH_THEME                   = "theme";
    public static final String PATH_THEME_QUESTIONS                   = "theme_questions";

    public static final String QUESTIONS_CONTENT_TYPE         = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BuildConfig.APPLICATION_ID + "/" + PATH_QUESTIONS;
    public static final String QUESTIONS_CONTENT_ITEM_TYPE    = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BuildConfig.APPLICATION_ID + "/" + PATH_QUESTIONS;

    public static final String THEME_CONTENT_TYPE         = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BuildConfig.APPLICATION_ID + "/" + PATH_THEME;
    public static final String THEME_CONTENT_ITEM_TYPE    = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BuildConfig.APPLICATION_ID + "/" + PATH_THEME;

    public static final String THEME_QUESTIONS_CONTENT_TYPE         = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BuildConfig.APPLICATION_ID + "/" + PATH_THEME_QUESTIONS;
    public static final String THEME_QUESTIONS_CONTENT_ITEM_TYPE    = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BuildConfig.APPLICATION_ID + "/" + PATH_THEME_QUESTIONS;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + BuildConfig.APPLICATION_ID);

    public static final Uri QUESTIONS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUESTIONS).build();
    public static final Uri THEME_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_THEME).build();
    public static final Uri THEME_QUESTIONS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_THEME_QUESTIONS).build();

}

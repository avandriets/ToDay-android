package com.gcgamecore.today.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gcgamecore.today.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


import java.sql.SQLException;


/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "ToDayQuiz.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 5;

    // the DAO object we use to access the SimpleData table

    private Dao<DB_Questions, Long> QuestionsDao = null;
    private RuntimeExceptionDao<DB_Questions, Long> QuestionsRuntimeDao = null;

    private Dao<DB_ThemeQuiz, Long> ThemeQuizDao = null;
    private RuntimeExceptionDao<DB_ThemeQuiz, Long> ThemeQuizRuntimeDao = null;

    private Dao<DB_ThemeQuestion, Long> ThemeQuestionDao = null;
    private RuntimeExceptionDao<DB_ThemeQuestion, Long> ThemeQuestionRuntimeDao = null;

    private Dao<DB_Answers, Long> AnswersDao = null;
    private RuntimeExceptionDao<DB_Answers, Long> AnswersRuntimeDao = null;

    private Dao<DB_FavoriteThemeQuestions, Long> FavoriteDao = null;
    private RuntimeExceptionDao<DB_FavoriteThemeQuestions, Long> FavoriteRuntimeDao = null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, DB_Questions.class);
            TableUtils.createTable(connectionSource, DB_Answers.class);
            TableUtils.createTable(connectionSource, DB_FavoriteThemeQuestions.class);
            TableUtils.createTable(connectionSource, DB_ThemeQuestion.class);
            TableUtils.createTable(connectionSource, DB_ThemeQuiz.class);


        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");

            TableUtils.dropTable(connectionSource, DB_Questions.class, true);

            TableUtils.dropTable(connectionSource, DB_Answers.class, true);
            TableUtils.dropTable(connectionSource, DB_FavoriteThemeQuestions.class, true);
            TableUtils.dropTable(connectionSource, DB_ThemeQuestion.class, true);
            TableUtils.dropTable(connectionSource, DB_ThemeQuiz.class, true);


            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }



    public Dao<DB_Questions, Long> getQuestionDao() throws SQLException {
        if (QuestionsDao == null) {
            QuestionsDao = getDao(DB_Questions.class);
        }
        return QuestionsDao;
    }

    public RuntimeExceptionDao<DB_Questions, Long> getQuestionDataDao() {
        if (QuestionsRuntimeDao == null) {
            QuestionsRuntimeDao = getRuntimeExceptionDao(DB_Questions.class);
        }
        return QuestionsRuntimeDao;
    }

    public Dao<DB_ThemeQuiz, Long> getThemeQuizDao() throws SQLException {
        if (ThemeQuizDao == null) {
            ThemeQuizDao = getDao(DB_ThemeQuiz.class);
        }
        return ThemeQuizDao;
    }

    public RuntimeExceptionDao<DB_ThemeQuiz, Long> getThemeQuizDataDao() {
        if (ThemeQuizRuntimeDao == null) {
            ThemeQuizRuntimeDao = getRuntimeExceptionDao(DB_ThemeQuiz.class);
        }
        return ThemeQuizRuntimeDao;
    }

    public Dao<DB_ThemeQuestion, Long> getThemeQuizQuestionsDao() throws SQLException {
        if (ThemeQuestionDao == null) {
            ThemeQuestionDao = getDao(DB_ThemeQuestion.class);
        }
        return ThemeQuestionDao;
    }

    public RuntimeExceptionDao<DB_ThemeQuestion, Long> getThemeQuizQuestionsDataDao() {
        if (ThemeQuestionRuntimeDao == null) {
            ThemeQuestionRuntimeDao = getRuntimeExceptionDao(DB_ThemeQuestion.class);
        }
        return ThemeQuestionRuntimeDao;
    }

    public Dao<DB_Answers, Long> getAnswerDao() throws SQLException {
        if (AnswersDao == null) {
            AnswersDao = getDao(DB_Answers.class);
        }
        return AnswersDao;
    }

    public RuntimeExceptionDao<DB_Answers, Long> getAnswerDataDao() {
        if (AnswersRuntimeDao == null) {
            AnswersRuntimeDao = getRuntimeExceptionDao(DB_Answers.class);
        }
        return AnswersRuntimeDao;
    }

    public Dao<DB_FavoriteThemeQuestions, Long> getFavoriteDao() throws SQLException {
        if (FavoriteDao == null) {
            FavoriteDao = getDao(DB_FavoriteThemeQuestions.class);
        }
        return FavoriteDao;
    }

    public RuntimeExceptionDao<DB_FavoriteThemeQuestions, Long> getFavoriteDataDao() {
        if (FavoriteRuntimeDao == null) {
            FavoriteRuntimeDao = getRuntimeExceptionDao(DB_FavoriteThemeQuestions.class);
        }
        return FavoriteRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();

        QuestionsDao = null;
        QuestionsRuntimeDao = null;

        ThemeQuizDao = null;
        ThemeQuizRuntimeDao = null;

        ThemeQuestionDao = null;
        ThemeQuestionRuntimeDao = null;

        AnswersDao = null;
        AnswersRuntimeDao = null;

        FavoriteDao = null;
        FavoriteRuntimeDao = null;
    }
}

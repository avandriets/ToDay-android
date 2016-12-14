package com.gcgamecore.today.Utility;


import com.gcgamecore.today.Data.DB_SentNotification;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Data.DatabaseHelper;

import java.sql.SQLException;
import java.util.Calendar;

public class DB_Utility {

    public static DB_ThemeQuiz getCurrentTheme(DatabaseHelper mDatabaseHelper){

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);

        DB_ThemeQuiz main_theme = null;

        try {
            main_theme = mDatabaseHelper.getThemeQuizDataDao().queryBuilder()
                    .orderBy(DB_ThemeQuiz.TARGET_DATE, false)
                    .where().eq(DB_ThemeQuiz.MAIN_THEME, true)
                    .queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return main_theme;
    }

    public static DB_SentNotification getNotificationByTheme(DatabaseHelper mDatabaseHelper, DB_ThemeQuiz theme){
        DB_SentNotification notification = null;

        try {
            notification = mDatabaseHelper.getNotificationsDataDao().queryBuilder()
                    .where()
                    .eq(DB_SentNotification.THEME, theme.getId())
                    .queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notification;
    }

    public static void notificationWasSentByTheme(DatabaseHelper mDatabaseHelper, DB_ThemeQuiz main_theme) {

        DB_SentNotification notification = DB_Utility.getNotificationByTheme(mDatabaseHelper, main_theme);

        if(notification == null){
            notification = new DB_SentNotification();
            notification.setTheme_id(main_theme.getId());
            mDatabaseHelper.getNotificationsDataDao().createIfNotExists(notification);
        }
    }
}

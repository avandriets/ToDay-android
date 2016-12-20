package com.gcgamecore.today.Utility;

import com.gcgamecore.today.Data.DB_SentNotification;
import com.gcgamecore.today.Data.DB_ThemeQuestion;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DB_Utility {

    public static DB_ThemeQuiz getCurrentTheme(DatabaseHelper mDatabaseHelper, String pLanguage) {

        Calendar cur_date = Calendar.getInstance();
        cur_date.set(Calendar.MILLISECOND, 0);
        cur_date.set(Calendar.SECOND, 0);
        cur_date.set(Calendar.MINUTE, 0);
        cur_date.set(Calendar.HOUR_OF_DAY, 0);

        DB_ThemeQuiz main_theme = null;

        try {
            main_theme = mDatabaseHelper.getThemeQuizDataDao().queryBuilder()
                    .orderBy(DB_ThemeQuiz.TARGET_DATE, false)
                    .where()
                    .eq(DB_ThemeQuiz.MAIN_THEME, true)
                    .and()
                    .le(DB_ThemeQuiz.TARGET_DATE, cur_date.getTime())
                    .and()
                    .eq(DB_ThemeQuiz.LANGUAGE, pLanguage)
                    .queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return main_theme;
    }

    public static DB_SentNotification getNotificationByTheme(DatabaseHelper mDatabaseHelper, DB_ThemeQuiz theme) {
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

        if (notification == null) {
            notification = new DB_SentNotification();
            notification.setTheme_id(main_theme.getId());
            mDatabaseHelper.getNotificationsDataDao().createIfNotExists(notification);
        }
    }

    public static DB_ThemeQuiz getLastTheme(DatabaseHelper mDatabaseHelper, String pLanguage) {

        DB_ThemeQuiz main_theme = null;

        try {
            main_theme = mDatabaseHelper.getThemeQuizDataDao().queryBuilder()
                    .orderBy(DB_ThemeQuiz.TARGET_DATE, false)
                    .where()
                    .eq(DB_ThemeQuiz.LANGUAGE, pLanguage)
                    .queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return main_theme;
    }

    public static List<ThemeWithQuestion> parseThemeArray(String jsonString) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder
                .registerTypeAdapter(Date.class, new JsonDateDeserializer())
                //.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ")
                .create();

        List<ThemeWithQuestion> listThemeWithQuestions = new ArrayList<>();

        try {
            JSONArray themesArray = new JSONArray(jsonString);

            for (int i = 0; i < themesArray.length(); i++) {
                JSONObject jsonObj = themesArray.getJSONObject(i);

                String theme = jsonObj.getString("theme");
                String theme_questions = jsonObj.getString("questions");
                DB_ThemeQuiz theme_quiz = gson.fromJson(theme, DB_ThemeQuiz.class);

                Type listType = new TypeToken<ArrayList<DB_ThemeQuestion>>() {
                }.getType();
                List<DB_ThemeQuestion> theme_questions_list = gson.fromJson(theme_questions, listType);

                listThemeWithQuestions.add(new ThemeWithQuestion(theme_quiz, theme_questions_list));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listThemeWithQuestions;
    }

    public static void updateThemesWithQuestions(DatabaseHelper mDatabaseHelper, List<ThemeWithQuestion> theme_questions_list) {

        for (ThemeWithQuestion t_and_q : theme_questions_list) {

            DB_ThemeQuiz theme_quiz_from_db = mDatabaseHelper.getThemeQuizDataDao().createIfNotExists(t_and_q.getTheme());

            if (t_and_q.getTheme().getUpdated_at().after(theme_quiz_from_db.getUpdated_at())) {
                mDatabaseHelper.getThemeQuizDataDao().update(t_and_q.getTheme());
            }

            DB_ThemeQuestion theme_quest = null;
            for (DB_ThemeQuestion t_quest : t_and_q.getTheme_questions_list()) {

                t_quest.setTheme(theme_quiz_from_db.getId());
                theme_quest = mDatabaseHelper.getThemeQuizQuestionsDataDao().createIfNotExists(t_quest);
                if (t_quest.getUpdated_at().after(theme_quest.getUpdated_at())) {
                    mDatabaseHelper.getThemeQuizQuestionsDataDao().update(t_quest);
                }
            }
        }
    }

    public static DB_ThemeQuiz getMaxUpdateTheme(DatabaseHelper mDatabaseHelper, String pLanguage) {

        DB_ThemeQuiz main_theme = null;

        try {
            main_theme = mDatabaseHelper.getThemeQuizDataDao().queryBuilder()
                    .orderBy(DB_ThemeQuiz.UPDATED_AT, false)
                    .where()
                    .eq(DB_ThemeQuiz.LANGUAGE, pLanguage)
                    .queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return main_theme;
    }
}

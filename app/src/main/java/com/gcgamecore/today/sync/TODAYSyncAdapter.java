package com.gcgamecore.today.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gcgamecore.today.Data.DB_Questions;
import com.gcgamecore.today.Data.DB_SentNotification;
import com.gcgamecore.today.Data.DB_ThemeQuestion;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.gcgamecore.today.Data.QuizService;
import com.gcgamecore.today.Data.TODAYContract;
import com.gcgamecore.today.R;
import com.gcgamecore.today.Utility.DB_Utility;
import com.gcgamecore.today.Utility.ThemeWithQuestion;
import com.gcgamecore.today.Utility.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TODAYSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final int HOUR_FROM_SYNC = 10;
    private static final int HOUR_BEFORE_SYNC = 12;
    public final String LOG_TAG = TODAYSyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED = "com.digitallifelab.environmentmonitor.ACTION_DATA_UPDATED";
    // Interval at which to sync, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 15;
    public static final int SYNC_FLEXTIME = 150;
    public static final int NOTIFICATION_ID = 1;

    private DatabaseHelper mDatabaseHelper = null;
    private Context mContext;

    public TODAYSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    private String[] column_one;
    private String[] column_two;

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(LOG_TAG, "Starting sync");

        if (!Utility.isNetworkAvailable(getContext())) {
            Log.e(LOG_TAG, "No internet connection.");
            return;
        }

        Resources res = getContext().getResources();
        column_one = res.getStringArray(R.array.column_one);
        column_two = res.getStringArray(R.array.column_two);

        getHelper();

        //GET DATA from server
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utility.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QuizService service = retrofit.create(QuizService.class);

        String pLanguage = Utility.getLangCode(getContext());
        loadThemeChangedFromServer(service, pLanguage);
        loadThemeFromServerByInterval(service, pLanguage);

        LoadQuestions(service);

        ShowNewThemeNotification(pLanguage);

        getContext().getContentResolver().insert(TODAYContract.QUESTIONS_CONTENT_URI, null);

        if (mDatabaseHelper != null) {
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }
    }

    private DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(mContext, DatabaseHelper.class);
        }
        return mDatabaseHelper;
    }

    private void LoadQuestions(QuizService service){

        try {
            DB_Questions check_records = mDatabaseHelper.getQuestionDataDao()
                    .queryBuilder()
                    .orderBy(DB_Questions.UPDATED_AT,false)
                    .queryForFirst();

            if(check_records == null){
                loadQuestionsFirstly(service);
            }else{
                loadQuestionsChanges(service, check_records);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadQuestionsFirstly(QuizService service) {

        String lang_code = Utility.getLangCode(mContext);

        Call<List<DB_Questions>> retGetQuestions = service.getQuestions(lang_code);

        try {
            Response<List<DB_Questions>> response = retGetQuestions.execute();

            if (response.isSuccessful()) {
                List<DB_Questions> questions = response.body();

                for (DB_Questions question : questions) {
                    mDatabaseHelper.getQuestionDataDao().createIfNotExists(question);
                }
                Log.d(LOG_TAG, "Get questions successful");
            } else {
                Log.d(LOG_TAG, "Get error.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Get error.");
        }
    }

    private void loadQuestionsChanges(QuizService service, DB_Questions check_records) {

        String lang_code = Utility.getLangCode(mContext);

        Call<List<DB_Questions>> retGetQuestions = service.getQuestionsGetChanges(lang_code, check_records.getUpdated_at().getTime());

        try {
            Response<List<DB_Questions>> response = retGetQuestions.execute();

            if (response.isSuccessful()) {
                List<DB_Questions> questions = response.body();

                for (DB_Questions question : questions) {
                    mDatabaseHelper.getQuestionDataDao().createOrUpdate(question);
                }
                Log.d(LOG_TAG, "Get questions successful");
            } else {
                Log.d(LOG_TAG, "Get error.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Get error.");
        }
    }

    private void loadThemeFromServer(QuizService service) {

        String lang_code = Utility.getLangCode(mContext);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);

        Call<ResponseBody> retGetTheme = service.getTheme(lang_code, day, month, year);

        try {
            Response<ResponseBody> response = retGetTheme.execute();

            if (response.isSuccessful()) {

                Log.d(LOG_TAG, "CallBack response is success " + response);

                String jsonString = Utility.ReadRetrofitResponseToString(response);
                JSONObject jsonObj = new JSONObject(jsonString);

                String theme = jsonObj.getString("theme");
                String theme_questions = jsonObj.getString("questions");
                DB_ThemeQuiz theme_quiz = gson.fromJson(theme, DB_ThemeQuiz.class);

                //DB_ThemeQuiz theme_quiz_from_db = mDatabaseHelper.getThemeQuizDataDao().queryForId(theme_quiz.getId());
                DB_ThemeQuiz theme_quiz_from_db = mDatabaseHelper.getThemeQuizDataDao().createIfNotExists(theme_quiz);

                if (theme_quiz.getUpdated_at().after(theme_quiz_from_db.getUpdated_at())) {
                    mDatabaseHelper.getThemeQuizDataDao().update(theme_quiz);
                }

                Type listType = new TypeToken<ArrayList<DB_ThemeQuestion>>() {
                }.getType();
                List<DB_ThemeQuestion> theme_questions_list = new Gson().fromJson(theme_questions, listType);

                DB_ThemeQuestion theme_quest = null;
                for (DB_ThemeQuestion t_quest : theme_questions_list) {
                    t_quest.setTheme(theme_quiz_from_db.getId());
                    theme_quest = mDatabaseHelper.getThemeQuizQuestionsDataDao().createIfNotExists(t_quest);
                    if (t_quest.getUpdated_at().after(theme_quest.getUpdated_at())) {
                        mDatabaseHelper.getThemeQuizQuestionsDataDao().update(t_quest);
                    }
                }

            } else {
                //JSONObject error_obj = Utility.ReadRetrofitResponseToJsonObj(response);
                Log.d(LOG_TAG, "Error. " + response.code() + " " + response.message());
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadThemeFromServerByInterval(QuizService service, String pLanguage) {

        String lang_code = Utility.getLangCode(mContext);

        Calendar cur_date = Calendar.getInstance();

        DB_ThemeQuiz last_theme = DB_Utility.getLastTheme(mDatabaseHelper, pLanguage);

        if (last_theme != null) {
            if (last_theme.getTarget_date().after(cur_date.getTime())) {
                return;
            } else {
                Calendar last_theme_date = Calendar.getInstance();
                last_theme_date.setTime(last_theme.getTarget_date());

                loadThemeByInterval(service, lang_code, last_theme_date, cur_date);
            }
        } else {
            Calendar last_theme_date = Calendar.getInstance();
            last_theme_date.setTimeInMillis(0);

            loadThemeByInterval(service, lang_code, last_theme_date, cur_date);
        }
    }

    private void loadThemeChangedFromServer(QuizService service, String pLanguage) {

        String lang_code = Utility.getLangCode(mContext);

        DB_ThemeQuiz last_theme = DB_Utility.getLastTheme(mDatabaseHelper, pLanguage);
        DB_ThemeQuiz max_update_theme = DB_Utility.getMaxUpdateTheme(mDatabaseHelper, pLanguage);

        if (last_theme != null) {

            Calendar last_update_date = Calendar.getInstance();
            last_update_date.setTimeZone(TimeZone.getTimeZone("UTC"));

            Calendar last_create_date = Calendar.getInstance();

            Calendar test_date = Calendar.getInstance();
            test_date.setTime(max_update_theme.getUpdated_at());

            last_update_date.setTime(max_update_theme.getUpdated_at());

            last_create_date.setTime(last_theme.getTarget_date());

            loadThemeChanges(service, lang_code, last_update_date, last_create_date);
        }
    }

    private void loadThemeByInterval(QuizService service, String lang_code, Calendar last_theme_date, Calendar cur_date) {

        int cur_day = cur_date.get(Calendar.DAY_OF_MONTH);
        int cur_month = cur_date.get(Calendar.MONTH) + 1;
        int cur_year = cur_date.get(Calendar.YEAR);

        int last_day = last_theme_date.get(Calendar.DAY_OF_MONTH);
        int last_month = last_theme_date.get(Calendar.MONTH) + 1;
        int last_year = last_theme_date.get(Calendar.YEAR);


        Call<ResponseBody> retGetThemeByInterval = service.getThemeByInterval(lang_code, last_day, last_month, last_year, cur_day, cur_month, cur_year);
        parseAndUpdate(retGetThemeByInterval);
    }

    private void loadThemeChanges(QuizService service, String lang_code, Calendar last_update_date, Calendar last_create_date) {

        int create_day = last_create_date.get(Calendar.DAY_OF_MONTH);
        int create_month = last_create_date.get(Calendar.MONTH) + 1;
        int create_year = last_create_date.get(Calendar.YEAR);

//        int last_update_day = last_update_date.get(Calendar.DAY_OF_MONTH);
//        int last_update_month = last_update_date.get(Calendar.MONTH) + 1;
//        int last_update_year = last_update_date.get(Calendar.YEAR);

        Call<ResponseBody> retGetThemeChanges = service.getThemeGetChanges(
                lang_code
                , create_day
                , create_month
                , create_year
                //, Utility.toUTC(last_update_date.getTimeInMillis(), TimeZone.getDefault())
                , last_update_date.getTimeInMillis()
        );
//        Utility.toUTC(current_theme.getUpdated_at().getTime(), TimeZone.getDefault())
        parseAndUpdate(retGetThemeChanges);
    }

    private void parseAndUpdate(Call<ResponseBody> pResponse) {
        try {
            Response<ResponseBody> response = pResponse.execute();

            if (response.isSuccessful()) {

                Log.d(LOG_TAG, "CallBack response is success " + response);

                String jsonString = Utility.ReadRetrofitResponseToString(response);

                List<ThemeWithQuestion> theme_questions_list = DB_Utility.parseThemeArray(jsonString);

                DB_Utility.updateThemesWithQuestions(mDatabaseHelper, theme_questions_list);

            } else {
                //JSONObject error_obj = Utility.ReadRetrofitResponseToJsonObj(response);
                Log.d(LOG_TAG, "Error. " + response.code() + " " + response.message());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ShowNewThemeNotification(String pLanguage) {

        DB_ThemeQuiz main_theme = DB_Utility.getThemeForToday(mDatabaseHelper, pLanguage);

        if (main_theme != null) {
            DB_SentNotification notification = DB_Utility.getNotificationByTheme(mDatabaseHelper, main_theme);

            if (notification == null) {

                Calendar ten_o_clock = Calendar.getInstance();
                ten_o_clock.set(Calendar.HOUR_OF_DAY, HOUR_FROM_SYNC);
                Calendar twelve_o_clock = Calendar.getInstance();
                twelve_o_clock.set(Calendar.HOUR_OF_DAY, HOUR_BEFORE_SYNC);
                Calendar now = Calendar.getInstance();

                if (now.after(ten_o_clock) && now.before(twelve_o_clock)) {
                    NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                    Bitmap largeIcon = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setLargeIcon(largeIcon)
                                    .setContentTitle(getContext().getString(R.string.app_name))
                                    //.setStyle(new NotificationCompat.BigTextStyle().bigText("You have " + newMessages.newAmount + " messages"))
                                    .setContentText(getNotificationString(main_theme))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

                    DB_Utility.notificationWasSentByTheme(mDatabaseHelper, main_theme);
                }
            }
        }
    }

    private String getNotificationString(DB_ThemeQuiz theme) {
        Random r = new Random();

        int first_word = r.nextInt(column_one.length);
        int second_word = r.nextInt(column_two.length);

        String notification = "";

        notification = theme.getName() + ". " + column_one[first_word] + " " + column_two[second_word];

        return notification;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {

        Log.d("Sync Adapter", "Starting sync getSyncAccount()");

        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        Log.d("Sync Adapter", "Starting sync onAccountCreated()");
        TODAYSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
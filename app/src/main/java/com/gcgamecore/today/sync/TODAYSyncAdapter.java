package com.gcgamecore.today.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.gcgamecore.today.Data.DB_Questions;
import com.gcgamecore.today.Data.DB_ThemeQuestion;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.gcgamecore.today.Data.QuizService;
import com.gcgamecore.today.Data.TODAYContract;
import com.gcgamecore.today.R;
import com.gcgamecore.today.Utility.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TODAYSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = TODAYSyncAdapter.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED = "com.digitallifelab.environmentmonitor.ACTION_DATA_UPDATED";
    // Interval at which to sync, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private DatabaseHelper mDatabaseHelper = null;
    private Context mContext;

    public TODAYSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(LOG_TAG, "Starting sync");

        if(!Utility.isNetworkAvailable(getContext())){
            Log.e(LOG_TAG, "No internet connection.");
            return;
        }

        getHelper();

        //GET DATA from server
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utility.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QuizService service = retrofit.create(QuizService.class);

        loadQuestionsFromServer(service);
        loadThemeFromServer(service);

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

    private void loadQuestionsFromServer(QuizService service) {

        Call<List<DB_Questions>> retGetQuestions = service.getQuestions();

        try {
            Response<List<DB_Questions>> response = retGetQuestions.execute();

            if (response.isSuccessful()) {
                List<DB_Questions> questions = response.body();

                for (DB_Questions question :questions) {
                    mDatabaseHelper.getQuestionDataDao().createIfNotExists(question);
                }
                Log.d(LOG_TAG, "Get questions successful");
            } else {
                Log.d(LOG_TAG, "Get error.");
            }

        } catch ( Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Get error.");
        }

    }

    private void loadThemeFromServer(QuizService service) {

        //Locale current = mContext.getResources().getConfiguration().locale;
        //String lang = current.getLanguage();

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        String lang = mContext.getResources().getString(R.string.locale);
        String lang_code = "";

        if (lang.equals("rus")){
            lang_code = "R";
        }else{
            lang_code = "E";
        }

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

                if(theme_quiz.getUpdated_at().after(theme_quiz_from_db.getUpdated_at())) {
                    mDatabaseHelper.getThemeQuizDataDao().update(theme_quiz);
                }

                Type listType = new TypeToken<ArrayList<DB_ThemeQuestion>>(){}.getType();
                List<DB_ThemeQuestion> theme_questions_list = new Gson().fromJson(theme_questions, listType);

                DB_ThemeQuestion theme_quest = null;
                for (DB_ThemeQuestion t_quest : theme_questions_list) {
                    t_quest.setTheme(theme_quiz_from_db.getId());
                    theme_quest = mDatabaseHelper.getThemeQuizQuestionsDataDao().createIfNotExists(t_quest);
                    if(t_quest.getUpdated_at().after(theme_quest.getUpdated_at())){
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
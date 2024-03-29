package com.gcgamecore.today.Utility;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gcgamecore.today.BuildConfig;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.gcgamecore.today.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static final String BASE_URL = BuildConfig.HOST;//"http://today2401.cloudapp.net";
    public static final String KEY_THEME_NAME = "key_theme_name";
    public static final String KEY_QUESTION = "key_question";
    public static final String KEY_BACKGROUND_IMAGE_URL = "key_back_ground_url";
    public static final String KEY_THEME_ID = "key_theme_id";
    public static final String KEY_QUESTION_ID = "key_question_id";


    public static OkHttpClient mClientOkHttp;
    public static final String QuestionsURL = "/rest/marathon-header/get-questions/";
    public static final String QuestionsURLGetChanges = "/rest/marathon-header/get-changed-questions/";

    public static final String ThemeURLbyDay = "/rest/theme-questions/get-day-theme-questions/";
    public static final String ThemeURLbyInterval = "/rest/theme-questions/get-theme-questions-interval/";
    public static final String ThemeURLGetChanges = "/rest/theme-questions/get-changed-theme-questions/";

    public static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");
    public static String[] randomBackGround = {"background1.jpg", "background2.jpg", "background3.jpg", "background4.jpg", "background5.jpg"};

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static JSONObject ReadRetrofitResponseToJsonObj(retrofit2.Response<ResponseBody> response) throws IOException {

        //if (!response.isSuccess()) throw new IOException("Unexpected code " + response);

        BufferedReader reader = null;
        InputStream inputStream;

        if (response.isSuccessful()) {
            inputStream = response.body().byteStream();
        } else {
            inputStream = response.errorBody().byteStream();
        }

        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            // Nothing to do.
            //return null;
            Log.d(LOG_TAG, "inputStream == null");
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
            // But it does make debugging a *lot* easier if you print out the completed
            // buffer for debugging.
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            Log.d(LOG_TAG, "buffer.length() == 0");
            return null;
        }

        try {
            String jsonStr = buffer.toString();
            JSONObject json_obj = new JSONObject(jsonStr);
            //JSONArray pollutionArray = json_obj.getJSONArray("result");

            Log.d(LOG_TAG, "Object was successfully parse");
            return json_obj;
        } catch (JSONException e) {

            Log.e(LOG_TAG, "Something went wrong " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static String ReadRetrofitResponseToString(retrofit2.Response<ResponseBody> response) throws IOException {

        BufferedReader reader = null;
        InputStream inputStream;

        if (response.isSuccessful()) {
            inputStream = response.body().byteStream();
        } else {
            inputStream = response.errorBody().byteStream();
        }

        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            // Nothing to do.
            //return null;
            Log.d(LOG_TAG, "inputStream == null");
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
            // But it does make debugging a *lot* easier if you print out the completed
            // buffer for debugging.
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            Log.d(LOG_TAG, "buffer.length() == 0");
            return null;
        }

        return buffer.toString();
    }

    public static JSONObject ReadHTTPOkResponse(Response response) throws IOException {

        //if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        BufferedReader reader = null;
        InputStream inputStream;
        inputStream = response.body().byteStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            // Nothing to do.
            //return null;
            Log.d(LOG_TAG, "inputStream == null");
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
            // But it does make debugging a *lot* easier if you print out the completed
            // buffer for debugging.
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            Log.d(LOG_TAG, "buffer.length() == 0");
            return null;
        }
        try {
            JSONObject json_obj = new JSONObject(buffer.toString());

            Log.d(LOG_TAG, "Object was successfully parse");
            return json_obj;
        } catch (JSONException e) {

            Log.d(LOG_TAG, "Somthing went wrong ");
            e.printStackTrace();
        }
        return null;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit.equals("K") ) {
            dist = dist * 1.609344;
        } else if (unit.equals("N")) {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static DatabaseHelper getDBHelper(Context context) {

        return OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static String getDayOfWeek(Context context, int day) {
        Resources res = context.getResources();
        String day_of_week = "";

        switch (day) {
            case Calendar.SUNDAY:
                day_of_week = res.getString(R.string.SUNDAY);
                break;
            case Calendar.MONDAY:
                day_of_week = res.getString(R.string.MONDAY);
                break;
            case Calendar.TUESDAY:
                day_of_week = res.getString(R.string.TUESDAY);
                break;
            case Calendar.WEDNESDAY:
                day_of_week = res.getString(R.string.WEDNESDAY);
                break;
            case Calendar.THURSDAY:
                day_of_week = res.getString(R.string.THURSDAY);
                break;
            case Calendar.FRIDAY:
                day_of_week = res.getString(R.string.FRIDAY);
                break;
            case Calendar.SATURDAY:
                day_of_week = res.getString(R.string.SATURDAY);
        }

        return day_of_week;
    }

    public static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month;
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static File store(Bitmap bm, String fileName) {
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public static void shareImage(File file, Context context) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            context.startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getLangCode(Context mContext) {

        //TODO CHANE IT TO E for english version
        //return "R";
        return BuildConfig.QUESTIONS_LANGUAGE;

//        String lang = mContext.getResources().getString(R.string.locale);
//        String lang_code = "";
//
//        if (lang.equals("rus")) {
//            lang_code = "R";
//        } else {
//            lang_code = "E";
//        }
//        return lang_code;
    }

    public static long toLocalTime(long time, TimeZone to) {
        return convertTime(time, utcTZ, to);
    }

    public static long toUTC(long time, TimeZone from) {
        return convertTime(time, from, utcTZ);
    }

    public static long convertTime(long time, TimeZone from, TimeZone to) {
        return time + getTimeZoneOffset(time, from, to);
    }

    private static long getTimeZoneOffset(long time, TimeZone from, TimeZone to) {
        int fromOffset = from.getOffset(time);
        int toOffset = to.getOffset(time);
        int diff = 0;

        if (fromOffset >= 0) {
            if (toOffset > 0) {
                toOffset = -1 * toOffset;
            } else {
                toOffset = Math.abs(toOffset);
            }
            diff = (fromOffset + toOffset) * -1;
        } else {
            if (toOffset <= 0) {
                toOffset = -1 * Math.abs(toOffset);
            }
            diff = (Math.abs(fromOffset) + toOffset);
        }
        return diff;
    }

    public static String loadJSONFromAsset(Context pContext, String file_name) {
        String json = null;
        try {
            InputStream is = pContext.getAssets().open(file_name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static Drawable getBackGroundPlaceholderFromAsset(String imageFile, Context pContext) {

        Drawable d = null;
        try {
            // get input stream
            InputStream ims = pContext.getAssets().open(imageFile);
            // load image as Drawable
            d = Drawable.createFromStream(ims, null);
        } catch (IOException ex) {
            Drawable mPlaceholderDrawable = ResourcesCompat.getDrawable(
                    pContext.getResources(),
                    R.drawable.ic_oval_placeholder, null);

            return mPlaceholderDrawable;
        }
        return d;
    }

    public static Drawable getImageFromAsset(String sFileName, Context pContext) {

        Drawable d = null;
        try {
            InputStream ims = null;
            // get input stream
            if(sFileName.substring(0,1).equals("/")){
                ims = pContext.getAssets().open(sFileName.substring(1,sFileName.length()));
            }else{
                ims = pContext.getAssets().open(sFileName);
            }

            // load image as Drawable
            d = Drawable.createFromStream(ims, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return d;
    }
}

package com.gcgamecore.today.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static final String BASE_URL             = "http://today1077.cloudapp.net";

    public static final String Questions    = "/rest/marathon-questions/";

    public static OkHttpClient mClientOkHttp;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static JSONObject ReadRetrofitResponseToJsonObj(retrofit2.Response<ResponseBody> response) throws IOException {

        //if (!response.isSuccess()) throw new IOException("Unexpected code " + response);

        BufferedReader reader = null;
        InputStream inputStream;

        if(response.isSuccessful()){
            inputStream = response.body().byteStream();
        }else{
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
            Log.d(LOG_TAG,"buffer.length() == 0");
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

        if(response.isSuccessful()){
            inputStream = response.body().byteStream();
        }else{
            inputStream = response.errorBody().byteStream();
        }

        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            // Nothing to do.
            //return null;
            Log.d(LOG_TAG,"inputStream == null");
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
            Log.d(LOG_TAG,"buffer.length() == 0");
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
            Log.d(LOG_TAG,"inputStream == null");
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
            Log.d(LOG_TAG,"buffer.length() == 0");
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
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
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
}

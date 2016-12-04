package com.gcgamecore.today;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.gcgamecore.today.Utility.Utility;
import com.j256.ormlite.android.apptools.OpenHelperManager;


public class BaseFragment extends Fragment {

    private static final String LOG_TAG = BaseFragment.class.getSimpleName();

    protected DatabaseHelper mDatabaseHelper  = null;
    protected Typeface custom_font_regular;
    protected Typeface custom_font_bold;
    protected Typeface custom_font_times;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "Get dataBase helper");
        mDatabaseHelper = Utility.getDBHelper(getActivity());

        //Init fonts
        custom_font_regular = Typeface.createFromAsset(getContext().getAssets(), "fonts/Book Antiqua Regular.ttf");
        custom_font_bold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Book Antiqua Bold.ttf");
        custom_font_times = Typeface.createFromAsset(getContext().getAssets(), "fonts/Times New Roman Cyr Italic.ttf");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(LOG_TAG, "On destroy helper && GApiClient.");
        if (mDatabaseHelper != null) {
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }
    }
}

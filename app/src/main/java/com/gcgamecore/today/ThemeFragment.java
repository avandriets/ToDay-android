package com.gcgamecore.today;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcgamecore.today.Data.DB_FavoriteThemeQuestions;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Utility.DB_Utility;
import com.gcgamecore.today.Utility.Utility;
import com.j256.ormlite.android.apptools.support.OrmLiteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ThemeFragment extends BaseFragmentWithHeader implements LoaderManager.LoaderCallbacks<Cursor>{

    @BindView(R.id.background_image)
    ImageView background_image;

    @BindView(R.id.headLine)
    TextView headLine;

    @BindView(R.id.leadText)
    TextView leadText;

    @BindView(R.id.themeIntroduction)
    View themeIntroduction;

    @BindView(R.id.emptyTheme)
    View emptyTheme;

    @BindView(R.id.textViewEmpty)
    TextView textViewEmpty;

    protected PreparedQuery<DB_ThemeQuiz> preparedQuery;
    protected Dao<DB_ThemeQuiz, Long> mOrderDao;

    private static final int ARCHIVE_LIST_ID_LOADER = 2100;

    public interface Callback {
        void onStartGame(Long themeId, boolean isFavorite);
    }

    private static final String LOG_TAG = ThemeFragment.class.getSimpleName();
    private long theme_id;
    private DB_ThemeQuiz current_theme = null;

    public ThemeFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.theme_preview_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);

        headLine.setTypeface(custom_font_bold);
        leadText.setTypeface(custom_font_times);
        textViewEmpty.setTypeface(custom_font_regular);

        Bundle arguments = getArguments();
        theme_id = -1;

        if (arguments != null) {
            theme_id = arguments.getLong(MainActivity.KEY_THEME_ID);
            current_theme = mDatabaseHelper.getThemeQuizDataDao().queryForId(theme_id);
        } else {
            initViewByCurrentDay();
        }

        initTheme();
        InitHeader();

        return rootView;
    }

    public void initViewByCurrentDay(){

        if(theme_id == -1) {
            DB_ThemeQuiz main_theme = DB_Utility.getCurrentTheme(mDatabaseHelper, pLanguage);

            if (main_theme != null) {
                current_theme = main_theme;
            }

            if (current_theme != null)
                theme_id = current_theme.getId();
        }
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private void initTheme() {

        if(current_theme != null) {
            headLine.setText(current_theme.getName());
            leadText.setText(current_theme.getDescription());

            Random r = new Random();
            int backGroundIndex = r.nextInt(Utility.randomBackGround.length);
            Drawable mPlaceholderDrawable = Utility.getBackGroundPlaceholderFromAsset(Utility.randomBackGround[backGroundIndex], getContext());

            if (current_theme.getTheme_image() != null) {

                Drawable targetImage = Utility.getImageFromAsset(current_theme.getTheme_image(), getContext());

                if(targetImage != null){
                    background_image.setImageDrawable(targetImage);
                }else{
                    Picasso.with(getContext()).load(Utility.BASE_URL + current_theme.getTheme_image())
                            .placeholder(mPlaceholderDrawable)
                            .error(mPlaceholderDrawable)
                            .into(background_image);
                }
            }else{
                background_image.setImageDrawable(mPlaceholderDrawable);
            }

            themeIntroduction.setVisibility(View.VISIBLE);
            emptyTheme.setVisibility(View.GONE);
        }else{
            themeIntroduction.setVisibility(View.GONE);
            emptyTheme.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.start_game_button)
    public void startGame(ImageButton button) {
        ((Callback) getActivity()).onStartGame(theme_id, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        android.support.v4.content.Loader<Object> loader = getLoaderManager().getLoader(ARCHIVE_LIST_ID_LOADER);

        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(ARCHIVE_LIST_ID_LOADER, null, this);
        } else {
            getLoaderManager().initLoader(ARCHIVE_LIST_ID_LOADER, null, this);
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG,"Create loader");

        try {

            mOrderDao = mDatabaseHelper.getThemeQuizDao();
            RuntimeExceptionDao<DB_FavoriteThemeQuestions, Long> questions_favorite_dao = mDatabaseHelper.getFavoriteDataDao();

            List<DB_FavoriteThemeQuestions> listOfFavorite = questions_favorite_dao.queryForAll();
            ArrayList<Long> listFoIds = new ArrayList<>();

            for (DB_FavoriteThemeQuestions cc :
                    listOfFavorite) {
                listFoIds.add(cc.getTheme_id());
            }

            QueryBuilder<DB_ThemeQuiz, Long> queryBuilder = mOrderDao.queryBuilder();
            Where<DB_ThemeQuiz, Long> where = queryBuilder.where().in(DB_ThemeQuiz.ID,listFoIds);
            preparedQuery = where.prepare();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new OrmLiteCursorLoader(getActivity(), mOrderDao, preparedQuery);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG,"Finish loading");

        initViewByCurrentDay();
        initTheme();
        InitHeader();

        //mAdapter.changeCursor(data, preparedQuery);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //mAdapter.swapCursor(null);
    }
}

package com.gcgamecore.today;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gcgamecore.today.Adapters.ArchiveRecyclerViewAdapter;
import com.gcgamecore.today.Data.DB_FavoriteThemeQuestions;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class FavoritesListFragment extends ThemeListFragment {

    public FavoritesListFragment(){
    }

    @Override
    public int getFragmentID() {
        return R.layout.favorite_fragment_layout;
    }

    @Override
    public int getEmptyListTextID() {
        return R.id.list_empty;
    }

    @Override
    public int getListViewID() {
        return R.id.idFavoriteListView;
    }

    @Override
    protected String getCaption(){
        return getContext().getResources().getString(R.string.string_favorite);
    }

    @Override
    public void initDataAdapter(View rootView) {

        View emptyView = rootView.findViewById(getEmptyListTextID());

        mAdapter = new ArchiveRecyclerViewAdapter(getActivity(),
                new ArchiveRecyclerViewAdapter.ArchiveAdapterOnClickHandler() {
                    @Override
                    public void onClick(Long date, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh) {
                        ((ThemeListFragment.Callback) getActivity()).onItemFavoriteSelected(date, vh);
                    }
                }, emptyView,  mDatabaseHelper, 2);
    }

    @Override
    public void initQuery() {
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
            Where<DB_ThemeQuiz, Long> where = queryBuilder.where().in(DB_ThemeQuiz.ID,listFoIds);// .eq(DB_ThemeQuiz.TARGET_DATE, today_date);
            preparedQuery = where.prepare();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

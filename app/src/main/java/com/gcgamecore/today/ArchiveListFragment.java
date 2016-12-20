package com.gcgamecore.today;

import android.view.View;

import com.gcgamecore.today.Adapters.ArchiveRecyclerViewAdapter;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;


public class ArchiveListFragment extends ThemeListFragment {

    @Override
    public int getFragmentID(){
        return R.layout.archive_fragment_layout;
    }

    @Override
    public int getEmptyListTextID(){
        return R.id.orders_list_empty;
    }

    @Override
    public int getListViewID(){
        return R.id.idOrdersListView;
    }

    @Override
    protected String getCaption(){
        return getContext().getResources().getString(R.string.string_archive);
    }

    @Override
    public void initDataAdapter(View rootView) {

        View emptyView = rootView.findViewById(getEmptyListTextID());

        mAdapter = new ArchiveRecyclerViewAdapter(getActivity(),
                new ArchiveRecyclerViewAdapter.ArchiveAdapterOnClickHandler() {
                    @Override
                    public void onClick(Long date, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh) {
                        ((Callback) getActivity()).onItemArchiveSelected(date, vh);
                    }
                }, emptyView,  mDatabaseHelper, 1);
    }

    @Override
    public void initQuery(){

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);

        Date today_date = c.getTime();

        try {
            mOrderDao = mDatabaseHelper.getThemeQuizDao();

            SelectArg selectArgDeleted =new SelectArg();
            selectArgDeleted.setValue(0);

            QueryBuilder<DB_ThemeQuiz, Long> queryBuilder = mOrderDao.queryBuilder();

            Where<DB_ThemeQuiz, Long> where = queryBuilder
                    .orderBy(DB_ThemeQuiz.TARGET_DATE, false)
                    .where()
                    .le(DB_ThemeQuiz.TARGET_DATE, today_date)
                    .and()
                    .eq(DB_ThemeQuiz.MAIN_THEME,true)
                    .and()
                    .eq(DB_ThemeQuiz.LANGUAGE, pLanguage);

            preparedQuery = queryBuilder.prepare();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

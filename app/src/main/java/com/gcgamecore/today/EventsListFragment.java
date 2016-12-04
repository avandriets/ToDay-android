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


public class EventsListFragment extends ThemeListFragment {
    @Override
    public int getFragmentID() {
        return R.layout.event_fragment_layout;
    }

    @Override
    public int getEmptyListTextID() {
        return R.id.list_empty;
    }

    @Override
    public int getListViewID() {
        return R.id.idToDayThemeListView;
    }

    @Override
    protected String getCaption(){
        return getContext().getResources().getString(R.string.string_action);
    }

    @Override
    public void initDataAdapter(View rootView) {

        View emptyView = rootView.findViewById(getEmptyListTextID());

        mAdapter = new ArchiveRecyclerViewAdapter(getActivity(),
                new ArchiveRecyclerViewAdapter.ArchiveAdapterOnClickHandler() {
                    @Override
                    public void onClick(Long date, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh) {
                        ((Callback) getActivity()).onItemThemeSelected(date, vh);
                    }
                }, emptyView,  mDatabaseHelper, 3);
    }

    @Override
    public void initQuery() {
        try {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.HOUR_OF_DAY, 0);


            Date today_date = c.getTime();

            mOrderDao = mDatabaseHelper.getThemeQuizDao();

            SelectArg selectArgDeleted =new SelectArg();
            selectArgDeleted.setValue(0);

            QueryBuilder<DB_ThemeQuiz, Long> queryBuilder = mOrderDao.queryBuilder();
            Where<DB_ThemeQuiz, Long> where = queryBuilder.where().eq(DB_ThemeQuiz.TARGET_DATE, today_date);
            preparedQuery = where.prepare();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

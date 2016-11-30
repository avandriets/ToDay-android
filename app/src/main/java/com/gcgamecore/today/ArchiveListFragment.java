package com.gcgamecore.today;

import android.view.View;

import com.gcgamecore.today.Adapters.ArchiveRecyclerViewAdapter;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import java.sql.SQLException;


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
    public void initDataAdapter(View rootView) {

        View emptyView = rootView.findViewById(getEmptyListTextID());

        mAdapter = new ArchiveRecyclerViewAdapter(getActivity(),
                new ArchiveRecyclerViewAdapter.ArchiveAdapterOnClickHandler() {
                    @Override
                    public void onClick(Long date, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh) {
                        ((Callback) getActivity()).onItemArchiveSelected(date, vh);
                    }
                }, emptyView,  mDatabaseHelper);
    }

    @Override
    public void initQuery(){
        try {
            mOrderDao = mDatabaseHelper.getThemeQuizDao();

            SelectArg selectArgDeleted =new SelectArg();
            selectArgDeleted.setValue(0);

            QueryBuilder<DB_ThemeQuiz, Long> queryBuilder = mOrderDao.queryBuilder();
            //Where<DB_ThemeQuiz, Long> where = queryBuilder.where().ge(Order.ID_LOCAL, 0);
            preparedQuery = queryBuilder.prepare();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package com.gcgamecore.today;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gcgamecore.today.Adapters.ArchiveRecyclerViewAdapter;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.gcgamecore.today.Utility.Utility;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.support.OrmLiteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import java.sql.SQLException;


public class ArchiveFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = ArchiveFragment.class.getSimpleName();

    private static final int ARCHIVE_LIST_ID_LOADER = 1100;
    private ArchiveRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private PreparedQuery<DB_ThemeQuiz> preparedQuery;
    private Dao<DB_ThemeQuiz, Long> mOrderDao;

    protected DatabaseHelper mDatabaseHelper  = null;

    public interface Callback {
        void onItemCustomerOrderSelected(Long pId, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh);
    }

    public ArchiveFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "Get dataBase helper");
        mDatabaseHelper = Utility.getDBHelper(getActivity());
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.archive_fragment_layout, container, false);

        View emptyView = rootView.findViewById(R.id.orders_list_empty);

        mAdapter = new ArchiveRecyclerViewAdapter(getActivity(),
                new ArchiveRecyclerViewAdapter.ArchiveAdapterOnClickHandler() {
                    @Override
                    public void onClick(Long date, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh) {
                        ((Callback) getActivity()).onItemCustomerOrderSelected(date, vh);
                    }
                }, emptyView,  mDatabaseHelper);

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.idOrdersListView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

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
        return new OrmLiteCursorLoader(getActivity(), mOrderDao, preparedQuery);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG,"Finish loading");
        mAdapter.changeCursor(data, preparedQuery);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}

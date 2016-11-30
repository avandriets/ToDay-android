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


public abstract class ThemeListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = ThemeListFragment.class.getSimpleName();
    private static final int ARCHIVE_LIST_ID_LOADER = 1100;

    protected ArchiveRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    protected PreparedQuery<DB_ThemeQuiz> preparedQuery;
    protected Dao<DB_ThemeQuiz, Long> mOrderDao;


    public interface Callback {
        void onItemArchiveSelected(Long pId, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh);
        void onItemThemeSelected(Long pId, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh);
    }

    public ThemeListFragment(){
    }

    public abstract int getFragmentID();

    public abstract int getEmptyListTextID();

    public abstract int getListViewID();

    public abstract void initDataAdapter(View rootView);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(getFragmentID(), container, false);

        initDataAdapter(rootView);

        mRecyclerView = (RecyclerView)rootView.findViewById(getListViewID());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public abstract void initQuery();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        initQuery();

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
package com.gcgamecore.today;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gcgamecore.today.Adapters.ArchiveRecyclerViewAdapter;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.gcgamecore.today.Utility.Utility;
import com.gcgamecore.today.sync.TODAYSyncAdapter;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements ArchiveListFragment.Callback {

    private static final String EVENT_FRAGMENT_TAG = "EVENT_FRAGMENT";
    public static final String KEY_POINT_ID = "key_theme_id";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private DatabaseHelper mDatabaseHelper  = null;

    @BindView(R.id.btn_archive)
    Button ArchiveButton;
    @BindView(R.id.btn_favorite)
    Button FavoriteButton;
    @BindView(R.id.btn_quiz)
    Button QuizButton;
    @BindView(R.id.btn_action)
    Button EventButton;

    @BindView(R.id.rl_action)
    View relativeLayout_Event;
    @BindView(R.id.rl_archive)
    View relativeLayout_Archive;
    @BindView(R.id.rl_favorite)
    View relativeLayout_Favorite;
    @BindView(R.id.rl_quiz)
    View relativeLayout_Quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mDatabaseHelper = Utility.getDBHelper(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new EventsListFragment(), EVENT_FRAGMENT_TAG).commit();

        selectButton(relativeLayout_Event);
    }

    public void selectButton(View currentView){
        relativeLayout_Event.setBackgroundColor(Utility.getColor(this, R.color.colorBlack));
        relativeLayout_Archive.setBackgroundColor(Utility.getColor(this, R.color.colorBlack));
        relativeLayout_Favorite.setBackgroundColor(Utility.getColor(this, R.color.colorBlack));
        relativeLayout_Quiz.setBackgroundColor(Utility.getColor(this, R.color.colorBlack));

        currentView.setBackgroundColor(Utility.getColor(this, R.color.colorRed));

        TODAYSyncAdapter.syncImmediately(this);
    }

    @OnClick(R.id.btn_archive)
    public void showArchive(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new ArchiveListFragment(), EVENT_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Archive);
    }

    @OnClick(R.id.btn_favorite)
    public void showFavorite(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new FavoritesListFragment(), EVENT_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Favorite);
    }

    @OnClick(R.id.btn_quiz)
    public void showQuiz(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new QuizFragment(), EVENT_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Quiz);
    }

    @OnClick(R.id.btn_action)
    public void showAction(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new EventsListFragment(), EVENT_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Event);
    }

    @Override
    public void onItemArchiveSelected(Long pId, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh) {
        Toast.makeText(this, "Archive", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemThemeSelected(Long pId, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh) {

        Bundle arguments = new Bundle();
        arguments.putLong(MainActivity.KEY_POINT_ID, getIntent().getLongExtra(MainActivity.KEY_POINT_ID, pId));

        ThemeQuestionsFragment fragment = new ThemeQuestionsFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list_container, fragment)
                .commit();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        Log.d(LOG_TAG, "On destroy helper && GApiClient.");
        if (mDatabaseHelper != null) {
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }

    }
}

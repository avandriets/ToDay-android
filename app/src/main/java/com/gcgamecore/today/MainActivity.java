package com.gcgamecore.today;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gcgamecore.today.Adapters.ArchiveRecyclerViewAdapter;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.gcgamecore.today.Utility.Utility;
import com.gcgamecore.today.sync.TODAYSyncAdapter;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements ArchiveListFragment.Callback, ThemeFragment.Callback, ThemeQuestionsFragment.Callback {

    private static final String EVENT_FRAGMENT_TAG = "EVENT_FRAGMENT";
    public static final String KEY_POINT_ID = "key_theme_id";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT";
    private static final String ARCHIVE_FRAGMENT_TAG = "ARCHIVE_FRAGMENT";
    private static final String FAVORITE_FRAGMENT_TAG = "FAVORITE_FRAGMENT";
    private static final String QUIZ_FRAGMENT_TAG = "QUIZ_FRAGMENT";
    private DatabaseHelper mDatabaseHelper = null;

    @BindView(R.id.btn_main)
    Button MainButton;
    @BindView(R.id.btn_archive)
    Button ArchiveButton;
    @BindView(R.id.btn_favorite)
    Button FavoriteButton;
    @BindView(R.id.btn_quiz)
    Button QuizButton;
    @BindView(R.id.btn_action)
    Button EventButton;

    @BindView(R.id.rl_main)
    View relativeLayout_Main;
    @BindView(R.id.rl_action)
    View relativeLayout_Event;
    @BindView(R.id.rl_archive)
    View relativeLayout_Archive;
    @BindView(R.id.rl_favorite)
    View relativeLayout_Favorite;
    @BindView(R.id.rl_quiz)
    View relativeLayout_Quiz;

    @BindView(R.id.currentDate)
    TextView tv_currentDate;
    @BindView(R.id.currentTime)
    TextView tv_currentTime;

    private CountDownTimer new_timer = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //new_timer.cancel();

        Log.d(LOG_TAG, "On destroy helper && GApiClient.");
        if (mDatabaseHelper != null) {
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mDatabaseHelper = Utility.getDBHelper(this);

        goToMainPage();
    }

    private void initInterface() {

        new_timer = new CountDownTimer(1000000000, 1000) {

            public void onTick(long millisUntilFinished) {
                Calendar c = Calendar.getInstance();

                tv_currentTime.setText(String.format("%02d:%02d",c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE) ));
            }

            public void onFinish() {

            }
        };
        //new_timer.start();

        Calendar c = Calendar.getInstance();

        String str_cur_date = String.format("%s, %s %d, %d  ",
                Utility.getDayOfWeek(this, c.get(Calendar.DAY_OF_WEEK)),
                Utility.getMonthForInt(c.get(Calendar.MONTH)),
                c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.YEAR));

        tv_currentDate.setText(str_cur_date);
    }

    public void selectButton(View currentView) {
        relativeLayout_Event.setBackgroundColor(Utility.getColor(this, R.color.ToDayColorBlack));
        relativeLayout_Archive.setBackgroundColor(Utility.getColor(this, R.color.ToDayColorBlack));
        relativeLayout_Favorite.setBackgroundColor(Utility.getColor(this, R.color.ToDayColorBlack));
        relativeLayout_Quiz.setBackgroundColor(Utility.getColor(this, R.color.ToDayColorBlack));
        relativeLayout_Main.setBackgroundColor(Utility.getColor(this, R.color.ToDayColorBlack));

        currentView.setBackgroundColor(Utility.getColor(this, R.color.ToDayColorRed));

        TODAYSyncAdapter.syncImmediately(this);
    }

    @OnClick(R.id.btn_main)
    public void showMain(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new ThemeFragment(), MAIN_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Main);
    }

    @OnClick(R.id.btn_archive)
    public void showArchive(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new ArchiveListFragment(), ARCHIVE_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Archive);
    }

    @OnClick(R.id.btn_favorite)
    public void showFavorite(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new FavoritesListFragment(), FAVORITE_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Favorite);
    }

    @OnClick(R.id.btn_quiz)
    public void showQuiz(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new QuizFragment(), QUIZ_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Quiz);
    }

    @OnClick(R.id.btn_action)
    public void showAction(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new EventsListFragment(), EVENT_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Event);
    }

    @Override
    public void onItemArchiveSelected(Long pId, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh) {
        StartThemePreview(pId);
    }

    @Override
    public void onItemThemeSelected(Long pId, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh) {
        StartThemePreview(pId);
    }

    @Override
    public void onItemFavoriteSelected(Long pId, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh) {
        StartThemePreview(pId);
    }

    private void StartThemePreview(Long pId) {
        Bundle arguments = new Bundle();
        arguments.putLong(MainActivity.KEY_POINT_ID, getIntent().getLongExtra(MainActivity.KEY_POINT_ID, pId));

        ThemeFragment fragment = new ThemeFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list_container, fragment)
                .commit();
    }

    @Override
    public void onStartGame(Long themeId) {
        Bundle arguments = new Bundle();
        arguments.putLong(MainActivity.KEY_POINT_ID, getIntent().getLongExtra(MainActivity.KEY_POINT_ID, themeId));

        ThemeQuestionsFragment fragment = new ThemeQuestionsFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list_container, fragment)
                .commit();
    }

    @Override
    public void onFinishGame() {
        goToMainPage();
    }

    private void goToMainPage(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new ThemeFragment(), MAIN_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Main);
        initInterface();
    }
}

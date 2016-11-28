package com.gcgamecore.today;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gcgamecore.today.Adapters.ArchiveRecyclerViewAdapter;
import com.gcgamecore.today.Data.DatabaseHelper;
import com.gcgamecore.today.Utility.Utility;
import com.gcgamecore.today.sync.TODAYSyncAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements ArchiveFragment.Callback {

    private static final String EVENT_FRAGMENT_TAG = "EVENT_FRAGMENT";
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

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new EventFragment(), EVENT_FRAGMENT_TAG).commit();

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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new ArchiveFragment(), EVENT_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Archive);
    }

    @OnClick(R.id.btn_favorite)
    public void showFavorite(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new FavoriteFragment(), EVENT_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Favorite);
    }

    @OnClick(R.id.btn_quiz)
    public void showQuiz(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new QuizFragment(), EVENT_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Quiz);
    }

    @OnClick(R.id.btn_action)
    public void showAction(Button button) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_container, new EventFragment(), EVENT_FRAGMENT_TAG).commit();
        selectButton(relativeLayout_Event);
    }

    @Override
    public void onItemCustomerOrderSelected(Long pId, ArchiveRecyclerViewAdapter.ArchiveViewHolder vh) {

        Toast.makeText(this, vh.tvHeader.getText(), Toast.LENGTH_SHORT).show();

    }
}

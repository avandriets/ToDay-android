package com.gcgamecore.today;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcgamecore.today.Adapters.ArchiveRecyclerViewAdapter;
import com.gcgamecore.today.Data.DB_Answers;
import com.gcgamecore.today.Data.DB_FavoriteThemeQuestions;
import com.gcgamecore.today.Data.DB_ThemeQuestion;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Utility.Utility;
import com.j256.ormlite.stmt.QueryBuilder;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ThemeQuestionsFragment extends BaseFragment implements View.OnTouchListener {

    private static final String LOG_TAG = ThemeQuestionsFragment.class.getSimpleName();
    private long theme_id;
    private List<DB_ThemeQuestion> question_list;
    private DB_ThemeQuiz current_theme;

    private int current_question_index = 0;

    public long currentAnswer = -1;

    static final int MIN_DISTANCE = 70;// TODO change this runtime based on screen resolution. for 1920x1080 is to small the 100 distance
    private float downX, downY, upX, upY;

    @BindView(R.id.textTheme)
    TextView headLine;

    @BindView(R.id.textQuestion)
    TextView question_text;

    @BindView(R.id.textAnswerOne)
    TextView one_answer_text;

    @BindView(R.id.textAnswerTwo)
    TextView two_answer_text;

    @BindView(R.id.answerDescription)
    TextView answerDescription;

    @BindView(R.id.layout_answer_one)
    LinearLayout one_layout_answer;

    @BindView(R.id.layout_answer_two)
    LinearLayout two_layout_answer;

    @BindView(R.id.imageAnswerOne)
    ImageView one_image_answer;
    @BindView(R.id.imageAnswerTwo)
    ImageView two_image_answer;

    @BindView(R.id.imgButtonFavorite)
    ImageButton imgButtonFavorite;

    @BindView(R.id.imgButtonShare)
    ImageButton imgButtonShare;

    @BindView(R.id.finishGameDescription)
    LinearLayout finishGameDescription;

    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    @BindView(R.id.gameLayout)
    ScrollView gameLayout;

    @BindView(R.id.finishLayout)
    RelativeLayout finishLayout;

    @BindView(R.id.textFinishMessage)
    TextView textFinishMessage;


    Drawable drw_answerOneOriginal;
    Drawable drw_answerOneLoser;
    Drawable drw_answerOneWinner;

    Drawable drw_answerTwoOriginal;
    Drawable drw_answerTwoLoser;
    Drawable drw_answerTwoWinner;

    Drawable drw_redStar;
    Drawable drw_greenStar;

    Drawable drw_redShare;
    Drawable drw_greenShare;

    public interface Callback {
        void onFinishGame();
    }

    public ThemeQuestionsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.theme_questions_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();

        headLine.setTypeface(custom_font_bold);
        question_text.setTypeface(custom_font_regular);
        one_answer_text.setTypeface(custom_font_regular);
        two_answer_text.setTypeface(custom_font_regular);
        answerDescription.setTypeface(custom_font_times);
        textFinishMessage.setTypeface(custom_font_regular);

        drw_answerOneOriginal = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_original);
        drw_answerOneLoser = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_loser);
        drw_answerOneWinner = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_winner);
        drw_answerTwoOriginal = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_original);
        drw_answerTwoLoser = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_loser);
        drw_answerTwoWinner = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_winner);

        drw_redStar = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_red_star);
        drw_greenStar = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_green_star);
        drw_redShare = ContextCompat.getDrawable(getContext(), R.drawable.ic_share_red);
        drw_greenShare = ContextCompat.getDrawable(getContext(), R.drawable.ic_share_green);

        if (arguments != null) {
            theme_id = arguments.getLong(MainActivity.KEY_POINT_ID);
            current_theme = mDatabaseHelper.getThemeQuizDataDao().queryForId(theme_id);
        } else {
            theme_id = -1;
        }

        question_list = mDatabaseHelper.getThemeQuizQuestionsDataDao().queryForEq(DB_ThemeQuestion.THEME, theme_id);

        if (question_list.size() > 0) {
            current_question_index = 0;
        }

        initHeadLine();
        initQuestions();

        mainLayout.setOnTouchListener(this);

        gameLayout.setVisibility(View.VISIBLE);
        finishLayout.setVisibility(View.GONE);

        return rootView;
    }

    private void initHeadLine() {
        if (theme_id == -1) {
            return;
        }

        headLine.setText(current_theme.getName());
    }

    private void initQuestions() {
        if (theme_id == -1) {
            return;
        }

        DB_ThemeQuestion current_question = question_list.get(current_question_index);

        question_text.setText(current_question.getQuestion());
        one_answer_text.setText(current_question.getAnswer1());
        two_answer_text.setText(current_question.getAnswer2());
        answerDescription.setText(current_question.getDescription());

        finishGameDescription.setVisibility(View.GONE);

        one_layout_answer.setVisibility(View.VISIBLE);
        two_layout_answer.setVisibility(View.VISIBLE);

        one_layout_answer.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGray));
        two_layout_answer.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGray));

        one_image_answer.setImageDrawable(drw_answerOneOriginal);
        two_image_answer.setImageDrawable(drw_answerTwoOriginal);

        one_answer_text.setTextColor(Utility.getColor(getContext(),R.color.ToDayColorTextGray));
        two_answer_text.setTextColor(Utility.getColor(getContext(),R.color.ToDayColorTextGray));
    }

    @OnClick(R.id.layout_answer_one)
    public void oneQuestionClick() {
        if (currentAnswer == -1) {
            currentAnswer = 1;
            applyAnswer();
        }
    }

    @OnClick(R.id.layout_answer_two)
    public void twoQuestionClick() {
        if (currentAnswer == -1) {
            currentAnswer = 2;
            applyAnswer();
        }
    }

    public void applyAnswer() {

        DB_ThemeQuestion current_question = question_list.get(current_question_index);
        boolean flag_winner = false;
        if (currentAnswer == current_question.getRight_answer()) {
            flag_winner = true;
        }

        // Save answer
        DB_Answers current_answer;

        //ask if exist
        QueryBuilder<DB_Answers, Long> qb = mDatabaseHelper.getAnswerDataDao().queryBuilder();
        try {
            qb.where().eq(DB_Answers.THEME_ID, theme_id).and().eq(DB_Answers.QUESTION_ID, current_question.getId());
            current_answer = qb.queryForFirst();
            if (current_answer != null) {
                // tell the user to enter unique values
                if (flag_winner)
                    current_answer.setAnswer(1);
                else
                    current_answer.setAnswer(0);

                mDatabaseHelper.getAnswerDataDao().update(current_answer);
            } else {
                current_answer = new DB_Answers();
                current_answer.setTheme_id(theme_id);
                current_answer.setQuestion_id(current_question.getId());

                if (flag_winner)
                    current_answer.setAnswer(1);
                else
                    current_answer.setAnswer(0);

                mDatabaseHelper.getAnswerDataDao().create(current_answer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Show right answer
        switch ((int) currentAnswer) {
            case 1:
                setAnswerResult(one_layout_answer, one_image_answer, drw_answerOneLoser, drw_answerOneWinner, one_answer_text, flag_winner);
                two_layout_answer.setVisibility(View.GONE);
                break;
            case 2:
                setAnswerResult(two_layout_answer, two_image_answer, drw_answerTwoLoser, drw_answerTwoWinner, two_answer_text, flag_winner);
                one_layout_answer.setVisibility(View.GONE);
                break;
        }
        finishGameDescription.setVisibility(View.VISIBLE);
    }

    public void setAnswerResult(View view, ImageView img_view, Drawable drw_loser, Drawable drw_winner, TextView caption, boolean winner) {

        caption.setTextColor(Utility.getColor(getContext(),R.color.ToDayColorWhite));
        if (winner) {
            view.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGreen));
            img_view.setImageDrawable(drw_winner);
            imgButtonFavorite.setImageDrawable(drw_greenStar);
            imgButtonShare.setImageDrawable(drw_greenShare);
        } else {
            view.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorRed));
            img_view.setImageDrawable(drw_loser);
            imgButtonFavorite.setImageDrawable(drw_redStar);
            imgButtonShare.setImageDrawable(drw_redShare);
        }
    }

    @OnClick(R.id.imgButtonFavorite)
    public void onClickAddFavorite() {
        DB_ThemeQuestion current_question = question_list.get(current_question_index);
        DB_FavoriteThemeQuestions favorite_item;

        QueryBuilder<DB_FavoriteThemeQuestions, Long> qb = mDatabaseHelper.getFavoriteDataDao().queryBuilder();
        try {
            qb.where().eq(DB_FavoriteThemeQuestions.THEME_ID, theme_id).and().eq(DB_FavoriteThemeQuestions.QUESTION_ID, current_question.getId());
            favorite_item = qb.queryForFirst();

            if (favorite_item == null) {

                favorite_item = new DB_FavoriteThemeQuestions();
                favorite_item.setTheme_id(theme_id);
                favorite_item.setQuestion_id(current_question.getId());

                mDatabaseHelper.getFavoriteDataDao().createIfNotExists(favorite_item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.imgButtonShare)
    public void onClickShare() {
        DB_ThemeQuestion current_question = question_list.get(current_question_index);
        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        String file_name = String.format("ToDay theme-%d q-%d.jpg", theme_id, current_question.getId());

        Bitmap bmp = Utility.getScreenShot(rootView);
        File file = Utility.store(bmp, file_name);
        Utility.shareImage(file, getActivity());
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal?
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe();
                        return true;
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe();
                        return true;
                    }
                } else {
                    Log.i(LOG_TAG, "Swipe was only " + Math.abs(deltaX) + " long horizontally, need at least " + MIN_DISTANCE);
                    // return false; // We don't consume the event
                }

                // swipe vertical?
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    // top or down
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe();
                        return true;
                    }
                    if (deltaY > 0) {
                        this.onBottomToTopSwipe();
                        return true;
                    }
                } else {
                    Log.i(LOG_TAG, "Swipe was only " + Math.abs(deltaX) + " long vertically, need at least " + MIN_DISTANCE);
                    // return false; // We don't consume the event
                }

                return false; // no swipe horizontally and no swipe vertically
            }// case MotionEvent.ACTION_UP:
        }
        return false;
    }

    public void onRightToLeftSwipe() {
        Log.i(LOG_TAG, "RightToLeftSwipe!");

        if (currentAnswer == -1)
            return;

        if(current_question_index == question_list.size()-1){
            ShowFinishScreen();
        }else{
            current_question_index +=1;
            currentAnswer = -1;
            initQuestions();
        }
    }

    public void onTopToBottomSwipe() {
        Log.i(LOG_TAG, "onTopToBottomSwipe!");
    }

    public void onBottomToTopSwipe() {
        Log.i(LOG_TAG, "onBottomToTopSwipe!");
    }

    public void onLeftToRightSwipe() {
        Log.i(LOG_TAG, "LeftToRightSwipe!");
        //Toast.makeText(getContext(), "LeftToRightSwipe", Toast.LENGTH_SHORT).show();
    }

    private void ShowFinishScreen() {
        gameLayout.setVisibility(View.GONE);
        finishLayout.setVisibility(View.VISIBLE);

        textFinishMessage.setText(getEndGameMessage());
    }

    private String getEndGameMessage() {
        return "Вы дали правильные ответы на 67% вопросов.";
    }

    @OnClick(R.id.btnFinish)
    public void onClickFinishGame(){
        ((ThemeQuestionsFragment.Callback)getActivity()).onFinishGame();
    }

}

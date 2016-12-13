package com.gcgamecore.today;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gcgamecore.today.Data.DB_Answers;
import com.gcgamecore.today.Data.DB_FavoriteThemeQuestions;
import com.gcgamecore.today.Data.DB_LastQuestionInTheme;
import com.gcgamecore.today.Data.DB_ThemeQuestion;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Utility.Utility;
import com.j256.ormlite.stmt.QueryBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FavoriteQuestionsFragment extends BaseFragment {

    private static final String LOG_TAG = FavoriteQuestionsFragment.class.getSimpleName();

    private long theme_id;
    private List<DB_ThemeQuestion> question_list;
    private DB_ThemeQuiz current_theme;

    private long current_question_index = 0;


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

    @BindView(R.id.ImageBackground)
    ImageView ImageBackground;

    @BindView(R.id.textResultView)
    TextView textResultView;

    @BindView(R.id.imageButtonBACK)
    Button imageButtonBACK;

    @BindView(R.id.imageButtonNEXT)
    Button imageButtonNEXT;

    Drawable drw_answerOneOriginal;
    Drawable drw_answerOneLoser;
    Drawable drw_answerOneWinner;

    Drawable drw_answerTwoOriginal;
    Drawable drw_answerTwoLoser;
    Drawable drw_answerTwoWinner;

    Drawable drw_favorite_on;
    Drawable drw_favorite_off;


    public interface Callback {
        void onFinishGame();
    }

    public FavoriteQuestionsFragment() {
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
        textResultView.setTypeface(custom_font_bold);

        drw_answerOneOriginal = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_original);
        drw_answerOneLoser = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_loser);
        drw_answerOneWinner = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_winner);
        drw_answerTwoOriginal = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_original);
        drw_answerTwoLoser = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_loser);
        drw_answerTwoWinner = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_winner);

        drw_favorite_on = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_on);
        drw_favorite_off = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_off);

        imageButtonNEXT.setCompoundDrawablesWithIntrinsicBounds(
                null
                , null
                , ContextCompat.getDrawable(getContext(), R.drawable.ic_right)
                , null);

        imageButtonBACK.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_left)
                , null
                , null
                , null);

        if (arguments != null) {
            theme_id = arguments.getLong(MainActivity.KEY_THEME_ID);

            current_theme = mDatabaseHelper.getThemeQuizDataDao().queryForId(theme_id);
        }

        try {
            List<DB_FavoriteThemeQuestions> listOfFavorite = mDatabaseHelper.getFavoriteDataDao().queryBuilder().where()
                    .eq(DB_FavoriteThemeQuestions.THEME_ID, theme_id).query();

            ArrayList<Long> listFoIds = new ArrayList<>();
            for (DB_FavoriteThemeQuestions cc : listOfFavorite) {
                listFoIds.add(cc.getQuestion_id());
            }

            question_list = mDatabaseHelper.getThemeQuizQuestionsDataDao().queryBuilder()
                    .orderBy(DB_ThemeQuestion.ID, true)
                    .where().in(DB_ThemeQuestion.ID, listFoIds)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        current_question_index = 0;

        current_theme = mDatabaseHelper.getThemeQuizDataDao().queryForId(theme_id);


        initHeadLine();
        initQuestion((int) current_question_index);

        gameLayout.setVisibility(View.VISIBLE);
        finishLayout.setVisibility(View.GONE);

        return rootView;
    }

    private void initHeadLine() {

        textResultView.setVisibility(View.GONE);

        headLine.setText(current_theme.getName());

        if (current_theme.getTheme_image() != null) {
            Picasso.with(getContext()).load(Utility.BASE_URL + current_theme.getTheme_background_image())
                    .into(ImageBackground);
        }

        imgButtonFavorite.setVisibility(View.VISIBLE);
        imgButtonShare.setVisibility(View.VISIBLE);
    }

    private void initNextPrevButtons(long position) {
        if (position == 0)
            imageButtonBACK.setVisibility(View.GONE);
        else
            imageButtonBACK.setVisibility(View.VISIBLE);

        if (position == question_list.size() - 1)
            imageButtonNEXT.setVisibility(View.GONE);
        else
            imageButtonNEXT.setVisibility(View.VISIBLE);
    }


    private void initQuestion(int pQuestionNumber) {

        initNextPrevButtons(pQuestionNumber);

        DB_ThemeQuestion current_question = question_list.get(pQuestionNumber);

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

        one_answer_text.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorTextGray));
        two_answer_text.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorTextGray));

        imgButtonFavorite.setVisibility(View.INVISIBLE);

        boolean flag_winner;

        if(current_question.getRight_answer() == 1){
            flag_winner = true;
        }else{
            flag_winner = false;
        }

        hideResultPanels(one_layout_answer,
                one_image_answer,
                drw_answerOneLoser,
                drw_answerOneWinner,
                one_answer_text,
                flag_winner);

        hideResultPanels(two_layout_answer,
                two_image_answer,
                drw_answerTwoLoser,
                drw_answerTwoWinner,
                two_answer_text,
                !flag_winner);

    }


    public void hideResultPanels(View view, ImageView img_view, Drawable drw_loser, Drawable drw_winner, TextView caption, boolean winner) {

        caption.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorWhite));
        if (winner) {
            view.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGreen));
            img_view.setImageDrawable(drw_winner);
            textResultView.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorGreen));
        } else {
            view.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorRed));
            img_view.setImageDrawable(drw_loser);
            textResultView.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorRed));
        }

        finishGameDescription.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.imgButtonShare)
    public void onClickShare() {
        DB_ThemeQuestion current_question = question_list.get((int) current_question_index);
        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        String file_name = String.format("ToDay theme-%d q-%d.jpg", theme_id, current_question.getId());

        Bitmap bmp = Utility.getScreenShot(rootView);
        File file = Utility.store(bmp, file_name);
        Utility.shareImage(file, getActivity());
    }

    @OnClick(R.id.imageButtonBACK)
    protected void OnBACKClick() {

        gameLayout.scrollTo(0, 0);

        current_question_index -= 1;
        initQuestion((int) current_question_index);
    }

    @OnClick(R.id.imageButtonNEXT)
    protected void OnNEXTClick() {
        gameLayout.scrollTo(0, 0);

        current_question_index += 1;
        initQuestion((int) current_question_index);
    }
}

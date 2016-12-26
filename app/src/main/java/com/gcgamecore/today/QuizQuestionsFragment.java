package com.gcgamecore.today;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gcgamecore.today.Data.DB_Questions;
import com.gcgamecore.today.Data.DB_ThemeQuiz;
import com.gcgamecore.today.Utility.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class QuizQuestionsFragment extends BaseFragment {

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

    @BindView(R.id.imageButtonNEXT)
    Button imageButtonNEXT;

    @BindView(R.id.time_indicator)
    View topLine;

    @BindView(R.id.currentTime)
    TextView tv_currentTime;


    Drawable drw_answerOneOriginal;
    Drawable drw_answerOneLoser;
    Drawable drw_answerOneWinner;

    Drawable drw_answerTwoOriginal;
    Drawable drw_answerTwoLoser;
    Drawable drw_answerTwoWinner;

    private static final String LOG_TAG = QuizQuestionsFragment.class.getSimpleName();
    private List<AnswersClass> question_list;
    public long currentAnswer = -1;
    private long time_for_game;
    private DB_Questions current_question;
    private CountDownTimer game_timer;

    private class AnswersClass {

        AnswersClass(DB_Questions questions, int answer) {
            this.questions = questions;
            this.answer = answer;
        }

        public DB_Questions questions;
        public int answer;
    }

    public interface Callback {
        void onFinishQuiz();
    }

    public QuizQuestionsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.quiz_questions_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();

        question_text.setTypeface(custom_font_regular);
        one_answer_text.setTypeface(custom_font_regular);
        two_answer_text.setTypeface(custom_font_regular);
        answerDescription.setTypeface(custom_font_times);
        textFinishMessage.setTypeface(custom_font_regular);
        textResultView.setTypeface(custom_font_regular);

        drw_answerOneOriginal = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_original);
        drw_answerOneLoser = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_loser);
        drw_answerOneWinner = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_one_winner);
        drw_answerTwoOriginal = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_original);
        drw_answerTwoLoser = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_loser);
        drw_answerTwoWinner = ContextCompat.getDrawable(getContext(), R.drawable.ic_answer_two_winner);

        imageButtonNEXT.setCompoundDrawablesWithIntrinsicBounds(
                null
                , null
                , ContextCompat.getDrawable(getContext(), R.drawable.ic_right)
                , null);

        question_list = new LinkedList<>();

        if (arguments != null) {
            time_for_game = arguments.getInt(MainActivity.KEY_TIME);
        }

        try {
            current_question = mDatabaseHelper.getQuestionDataDao()
                    .queryBuilder()
                    .orderByRaw("RANDOM()")
                    .where()
                    .eq(DB_ThemeQuiz.LANGUAGE, pLanguage)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        initNexQuestion();

        gameLayout.setVisibility(View.VISIBLE);
        finishLayout.setVisibility(View.GONE);

        game_timer = new CountDownTimer(time_for_game * 60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                //Calendar c = Calendar.getInstance();
                //tv_currentTime.setText(String.format("%02d:%02d",c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE) ));

                long min = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long sec = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                tv_currentTime.setText(String.format("%02d:%02d", min, sec));
            }

            public void onFinish() {
                ShowFinishScreen();
            }
        };
        game_timer.start();

        // load image
        try {
            // get input stream
            InputStream ims = getActivity().getAssets().open("quiz_background.jpg");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            ImageBackground.setImageDrawable(d);
        }
        catch(IOException ex) {
        }

        return rootView;
    }

    private void initNexQuestion() {

        question_text.setText(current_question.getQuestion());
        one_answer_text.setText(current_question.getAnswer1());
        two_answer_text.setText(current_question.getAnswer2());


        finishGameDescription.setVisibility(View.GONE);

        one_layout_answer.setVisibility(View.VISIBLE);
        two_layout_answer.setVisibility(View.VISIBLE);

        one_layout_answer.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGray));
        two_layout_answer.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGray));

        one_image_answer.setImageDrawable(drw_answerOneOriginal);
        two_image_answer.setImageDrawable(drw_answerTwoOriginal);

        one_answer_text.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorTextGray));
        two_answer_text.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorTextGray));

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

        boolean flag_winner = false;
        if (currentAnswer == current_question.getRight_answer()) {
            flag_winner = true;
        }

        question_list.add(new AnswersClass(current_question, flag_winner ? 1 : 0));

        ShowResult(flag_winner);
    }

    public void ShowResult(boolean flag_winner) {
        // Show right answer
        switch ((int) currentAnswer) {
            case 1:
                hideResultPanels(one_layout_answer, one_image_answer, drw_answerOneLoser, drw_answerOneWinner, one_answer_text, flag_winner);
                two_layout_answer.setVisibility(View.GONE);
                break;
            case 2:
                hideResultPanels(two_layout_answer, two_image_answer, drw_answerTwoLoser, drw_answerTwoWinner, two_answer_text, flag_winner);
                one_layout_answer.setVisibility(View.GONE);
                break;
        }
    }

    public void hideResultPanels(View view, ImageView img_view, Drawable drw_loser, Drawable drw_winner, TextView caption, boolean winner) {

        caption.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorWhite));
        if (winner) {
            view.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorGreen));
            img_view.setImageDrawable(drw_winner);
            textResultView.setText(getString(R.string.winner_text));
            textResultView.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorGreen));
        } else {
            view.setBackgroundColor(Utility.getColor(getContext(), R.color.ToDayColorRed));
            img_view.setImageDrawable(drw_loser);
            textResultView.setText(getString(R.string.looser_text));
            textResultView.setTextColor(Utility.getColor(getContext(), R.color.ToDayColorRed));
        }

        finishGameDescription.setVisibility(View.VISIBLE);
    }


    private void ShowFinishScreen() {
        gameLayout.setVisibility(View.GONE);
        finishLayout.setVisibility(View.VISIBLE);

        long numQuestions = 0;
        long numRightAnswers = 0;

        numQuestions = question_list.size();

        long res = 0;

        for (AnswersClass cc : question_list) {
            numRightAnswers += cc.answer;
        }

        if (numQuestions != 0)
            res = (long) (((float) numRightAnswers / numQuestions) * 100);

        String finish_message = String.format(getEndGameMessage(), String.valueOf(numRightAnswers));

        textFinishMessage.setText(finish_message);
    }

    private String getEndGameMessage() {

        return getString(R.string.finish_round_description);
    }

    @OnClick(R.id.btnFinish)
    public void onClickFinishGame() {
        ((QuizQuestionsFragment.Callback) getActivity()).onFinishQuiz();
    }

    @OnClick(R.id.imageButtonNEXT)
    protected void OnNEXTClick() {
        if (currentAnswer == -1)
            return;

        try {
            ArrayList<Long> listFoIds = new ArrayList<>();
            for (AnswersClass cc : question_list) {
                listFoIds.add(cc.questions.getId());
            }

            current_question = mDatabaseHelper.getQuestionDataDao().queryBuilder()
                    .orderByRaw("RANDOM()")
                    .where().notIn(DB_Questions.ID, listFoIds)
                    .queryForFirst();

            if (current_question == null) {
                current_question = mDatabaseHelper.getQuestionDataDao().queryBuilder()
                        .orderByRaw("RANDOM()")
                        .queryForFirst();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        currentAnswer = -1;
        initNexQuestion();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        game_timer.cancel();
    }
}

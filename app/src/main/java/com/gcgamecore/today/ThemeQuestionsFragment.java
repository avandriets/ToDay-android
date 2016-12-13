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
import android.widget.Toast;
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


public class ThemeQuestionsFragment extends BaseFragment {

    private static final String LOG_TAG = ThemeQuestionsFragment.class.getSimpleName();
    private long theme_id;
    private List<DB_ThemeQuestion> question_list;
    private DB_ThemeQuiz current_theme;

    private long current_question_index = 0;
    private long last_question_index = 0;

    public long currentAnswer = -1;

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
        } else {
            theme_id = -1;
        }

        try {
            question_list = mDatabaseHelper.getThemeQuizQuestionsDataDao().queryBuilder()
                    .orderBy(DB_ThemeQuestion.ID, true)
                    .where()
                    .eq(DB_ThemeQuestion.THEME, theme_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        last_question_index = 0;

        if(theme_id != -1) {
            try {
                DB_LastQuestionInTheme last_q = mDatabaseHelper.getLastQuestionDataDao().queryBuilder()
                        .where()
                        .eq(DB_LastQuestionInTheme.THEME_ID, theme_id).queryForFirst();

                if(last_q != null){

                    for (int i = 0; i < question_list.size(); i++) {
                        if(question_list.get(i).getId() == last_q.getQuestion_id()){
                            last_question_index = i;
                        }
                    }
                } else{
                    last_question_index = -1;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (question_list.size() > 0) {
            if(last_question_index> 0) {
                if(last_question_index < question_list.size() -1)
                    current_question_index = last_question_index + 1;
                else
                    current_question_index = last_question_index;

            }
            else
            {
                last_question_index = 0;
                current_question_index = last_question_index;
            }
        }

        initHeadLine();
        initNexQuestion();


        gameLayout.setVisibility(View.VISIBLE);
        finishLayout.setVisibility(View.GONE);

        return rootView;
    }

    private void initHeadLine() {
        if (theme_id == -1) {
            return;
        }

        headLine.setText(current_theme.getName());

        if (current_theme.getTheme_image() != null) {
            Picasso.with(getContext()).load(Utility.BASE_URL + current_theme.getTheme_background_image())
                    .into(ImageBackground);
        }

        imgButtonFavorite.setVisibility(View.VISIBLE);
        imgButtonShare.setVisibility(View.VISIBLE);
    }

    private void initNextPrevButtons(long position){
        if(position == 0)
            imageButtonBACK.setVisibility(View.GONE);
        else
            imageButtonBACK.setVisibility(View.VISIBLE);

        if(position == question_list.size()-1)
            imageButtonNEXT.setText(getString(R.string.finish));
        else
            imageButtonNEXT.setText(getString(R.string.next));
    }

    private void initNexQuestion() {
        if (theme_id == -1) {
            return;
        }

        initNextPrevButtons(current_question_index);

        DB_ThemeQuestion current_question = question_list.get((int) current_question_index);

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

        DB_FavoriteThemeQuestions isFavorite = null;
        try {
            isFavorite = mDatabaseHelper.getFavoriteDataDao().queryBuilder().where()
                    .eq(DB_FavoriteThemeQuestions.THEME_ID, theme_id)
                    .and()
                    .eq(DB_FavoriteThemeQuestions.QUESTION_ID, current_question.getId())
                    .queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(isFavorite != null)
            imgButtonFavorite.setImageDrawable(drw_favorite_on);
        else
            imgButtonFavorite.setImageDrawable(drw_favorite_off);
    }

    private void initOpenQuestion(int pQuestionNumber) {
        if (theme_id == -1) {
            return;
        }

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

        one_answer_text.setTextColor(Utility.getColor(getContext(),R.color.ToDayColorTextGray));
        two_answer_text.setTextColor(Utility.getColor(getContext(),R.color.ToDayColorTextGray));

        DB_FavoriteThemeQuestions isFavorite = null;
        try {
            isFavorite = mDatabaseHelper.getFavoriteDataDao().queryBuilder().where()
                    .eq(DB_FavoriteThemeQuestions.THEME_ID, theme_id)
                    .and()
                    .eq(DB_FavoriteThemeQuestions.QUESTION_ID, current_question.getId())
                    .queryForFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(isFavorite != null)
            imgButtonFavorite.setImageDrawable(drw_favorite_on);
        else
            imgButtonFavorite.setImageDrawable(drw_favorite_off);


        try {
            DB_Answers answer_state = mDatabaseHelper.getAnswerDataDao().queryBuilder().where()
                    .eq(DB_Answers.THEME_ID, theme_id)
                    .and().eq(DB_Answers.QUESTION_ID, current_question.getId())
                    .queryForFirst();

            if(answer_state.getAnswer() == 1)
                ShowResult(true);
            else
                ShowResult(false);

        } catch (SQLException e) {
            e.printStackTrace();
        }

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

        DB_ThemeQuestion current_question = question_list.get((int) current_question_index);
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

        ShowResult(flag_winner);

        // Save last question
        try {
            DB_LastQuestionInTheme lastQuestion = mDatabaseHelper.getLastQuestionDataDao().queryBuilder()
                    .where()
                    .eq(DB_LastQuestionInTheme.THEME_ID, theme_id)
                    .queryForFirst();

            long q = current_question.getId();
            if(current_question_index == question_list.size() - 1){
                q =0;
            }

            if(lastQuestion != null){
                lastQuestion.setQuestion_id(q);
                mDatabaseHelper.getLastQuestionDataDao().createOrUpdate(lastQuestion);
            }else{
                lastQuestion = new DB_LastQuestionInTheme();
                lastQuestion.setTheme_id(theme_id);
                lastQuestion.setQuestion_id(q);
                mDatabaseHelper.getLastQuestionDataDao().createIfNotExists(lastQuestion);
            }

            last_question_index = question_list.indexOf(current_question);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ShowResult(boolean flag_winner){
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

        caption.setTextColor(Utility.getColor(getContext(),R.color.ToDayColorWhite));
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

    @OnClick(R.id.imgButtonFavorite)
    public void onClickAddFavorite() {
        DB_ThemeQuestion current_question = question_list.get((int) current_question_index);
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
                imgButtonFavorite.setImageDrawable(drw_favorite_on);
                Toast.makeText(getActivity(), R.string.success_add_to_fav, Toast.LENGTH_SHORT).show();
            }else{
                mDatabaseHelper.getFavoriteDataDao().delete(favorite_item);
                imgButtonFavorite.setImageDrawable(drw_favorite_off);
                Toast.makeText(getActivity(), R.string.delete_from_favorite, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void ShowFinishScreen() {
        gameLayout.setVisibility(View.GONE);
        finishLayout.setVisibility(View.VISIBLE);

        imgButtonFavorite.setVisibility(View.INVISIBLE);
        imgButtonShare.setVisibility(View.INVISIBLE);

        long numQuestions = 0;
        long numRightAnswers = 0;

        numQuestions = question_list.size();

        long res = 0;
        try {
            numRightAnswers = mDatabaseHelper.getAnswerDataDao().queryBuilder()
                    .where()
                    .eq(DB_Answers.ANSWER, 1)
                    .and()
                    .eq(DB_Answers.THEME_ID, theme_id)
                    .countOf();

            if(numQuestions != 0)
                res = (long)(((float)numRightAnswers / numQuestions) * 100);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String finish_message = String.format(getEndGameMessage(), String.valueOf(res)+ " %");

        textFinishMessage.setText(finish_message);
    }

    private String getEndGameMessage() {

        return getString(R.string.finish_round_description);
    }

    @OnClick(R.id.btnFinish)
    public void onClickFinishGame(){
        ((ThemeQuestionsFragment.Callback)getActivity()).onFinishGame();
    }

    @OnClick(R.id.imageButtonBACK)
    protected void OnBACKClick(){

        gameLayout.scrollTo(0,0);

        if(current_question_index > 0)
            current_question_index -= 1;

        initOpenQuestion((int) current_question_index);
    }

    @OnClick(R.id.imageButtonNEXT)
    protected void OnNEXTClick(){
        if (currentAnswer == -1)
            return;

        gameLayout.scrollTo(0,0);

        if(current_question_index == question_list.size()-1){
            ShowFinishScreen();
        }else if(last_question_index > current_question_index){
            current_question_index +=1;
            initOpenQuestion((int) current_question_index);
        }
        else{
            current_question_index +=1;
            currentAnswer = -1;
            initNexQuestion();
        }
    }
}

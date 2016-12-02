package com.gcgamecore.today;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcgamecore.today.Data.DB_ThemeQuestion;
import com.gcgamecore.today.Data.DB_ThemeQuiz;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ThemeQuestionsFragment extends BaseFragment {

    private static final String LOG_TAG = ThemeQuestionsFragment.class.getSimpleName();
    private long theme_id;
    private List<DB_ThemeQuestion> question_list;
    private DB_ThemeQuiz current_theme;

    private int current_question_index = 0;

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

    public ThemeQuestionsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.theme_questions_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();

        Typeface custom_font_regular = Typeface.createFromAsset(getContext().getAssets(), "fonts/Book Antiqua Regular.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Book Antiqua Bold.ttf");
        Typeface custom_font_times = Typeface.createFromAsset(getContext().getAssets(), "fonts/Times New Roman Cyr Italic.ttf");

        headLine.setTypeface(custom_font_bold);
        question_text.setTypeface(custom_font_regular);
        one_answer_text.setTypeface(custom_font_regular);
        two_answer_text.setTypeface(custom_font_regular);
        answerDescription.setTypeface(custom_font_times);

        if (arguments != null) {
            theme_id = arguments.getLong(MainActivity.KEY_POINT_ID);
            current_theme = mDatabaseHelper.getThemeQuizDataDao().queryForId(theme_id);
        } else {
            theme_id = -1;
        }

        question_list = mDatabaseHelper.getThemeQuizQuestionsDataDao().queryForEq(DB_ThemeQuestion.THEME, theme_id);

        if(question_list.size()>0){
            current_question_index = 0;
        }

        initHeadLine();
        initQuestions();

        return rootView;
    }

    private void initHeadLine() {
        if(theme_id == -1){
            return;
        }

        headLine.setText(current_theme.getName());
    }

    private void initQuestions() {
        if(theme_id == -1){
            return;
        }

        DB_ThemeQuestion current_question = question_list.get(current_question_index);

        question_text.setText(current_question.getQuestion());
        one_answer_text.setText(current_question.getAnswer1());
        two_answer_text.setText(current_question.getAnswer2());
    }

    @OnClick(R.id.layout_answer_one)
    public void oneQuestionClick(){
        Toast.makeText(getContext(), "One", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.layout_answer_two)
    public void twoQuestionClick(){
        Toast.makeText(getContext(), "Two", Toast.LENGTH_SHORT).show();
    }
}

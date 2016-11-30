package com.gcgamecore.today;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gcgamecore.today.Data.DB_ThemeQuestion;

import java.util.List;

public class ThemeQuestionsFragment extends BaseFragment {

    private static final String LOG_TAG = ThemeQuestionsFragment.class.getSimpleName();
    private long theme_id;
    private List<DB_ThemeQuestion> question_list;
    private int current_question_index = 0;

    public ThemeQuestionsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.theme_questions_fragment_layout, container, false);

        Bundle arguments = getArguments();

        if (arguments != null) {
            theme_id = arguments.getLong(MainActivity.KEY_POINT_ID);
        } else {
            theme_id = -1;
        }

        question_list = mDatabaseHelper.getThemeQuizQuestionsDataDao().queryForEq(DB_ThemeQuestion.THEME, theme_id);

        if(question_list.size()>0){
            current_question_index = -1;
        }

        initQuestions();

        return rootView;
    }

    private void initQuestions() {

    }
}

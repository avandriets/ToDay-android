package com.gcgamecore.today;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class QuizChoiceFragment extends BaseFragmentWithHeader {

    @BindView(R.id.textViewHeadline)
    TextView headLine;


    public interface Callback {
        void onStartQuiz(int min);
    }

    public QuizChoiceFragment(){
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.quiz_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);

        headLine.setTypeface(custom_font_bold);

        InitHeader();

        return rootView;
    }

    @OnClick(R.id.FIVEimageButton)
    public void startGame5(ImageButton button) {

        if(mDatabaseHelper.getQuestionDataDao().countOf() >0)
            ((QuizChoiceFragment.Callback) getActivity()).onStartQuiz(5);
        else
            Toast.makeText(getActivity(), R.string.no_questions_string, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.TENimageButton)
    public void startGame10(ImageButton button) {
        if(mDatabaseHelper.getQuestionDataDao().countOf() >0)
            ((QuizChoiceFragment.Callback) getActivity()).onStartQuiz(10);
        else
            Toast.makeText(getActivity(), R.string.no_questions_string, Toast.LENGTH_SHORT).show();

    }

    @OnClick(R.id.THYRTYimageButton)
    public void startGame30(ImageButton button) {
        if(mDatabaseHelper.getQuestionDataDao().countOf() >0)
            ((QuizChoiceFragment.Callback) getActivity()).onStartQuiz(30);
        else
            Toast.makeText(getActivity(), R.string.no_questions_string, Toast.LENGTH_SHORT).show();
    }

}

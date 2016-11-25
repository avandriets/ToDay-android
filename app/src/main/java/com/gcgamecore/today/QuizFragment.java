package com.gcgamecore.today;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class QuizFragment extends Fragment {

    public QuizFragment(){
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.quiz_fragment_layout, container, false);

        return rootView;
    }
}

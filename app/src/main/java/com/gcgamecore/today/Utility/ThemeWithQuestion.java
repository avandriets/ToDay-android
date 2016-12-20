package com.gcgamecore.today.Utility;

import com.gcgamecore.today.Data.DB_ThemeQuestion;
import com.gcgamecore.today.Data.DB_ThemeQuiz;

import java.util.List;


public class ThemeWithQuestion {
    private DB_ThemeQuiz theme;
    private List<DB_ThemeQuestion> theme_questions_list;

    public ThemeWithQuestion(DB_ThemeQuiz theme, List<DB_ThemeQuestion> theme_questions_list) {
        this.theme = theme;
        this.theme_questions_list = theme_questions_list;
    }

    public DB_ThemeQuiz getTheme() {
        return theme;
    }

    public void setTheme(DB_ThemeQuiz theme) {
        this.theme = theme;
    }

    public List<DB_ThemeQuestion> getTheme_questions_list() {
        return theme_questions_list;
    }

    public void setTheme_questions_list(List<DB_ThemeQuestion> theme_questions_list) {
        this.theme_questions_list = theme_questions_list;
    }
}

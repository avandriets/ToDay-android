package com.gcgamecore.today.Data;


import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "answers")
public class DB_Answers {

    public static final String ID               = "_id";
    public static final String THEME_ID         = "theme_id";
    public static final String QUESTION_ID      = "question_id";
    public static final String ANSWER            = "answer";

    @Expose
    @DatabaseField(id = true, canBeNull = false, columnName = ID)
    private long id;

    @Expose
    @DatabaseField(columnName = THEME_ID)
    private long theme_id;

    @Expose
    @DatabaseField(columnName = QUESTION_ID)
    private long question_id;

    @Expose
    @DatabaseField(columnName = ANSWER)
    private long answer;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTheme_id() {
        return theme_id;
    }

    public void setTheme_id(long theme_id) {
        this.theme_id = theme_id;
    }

    public long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(long question_id) {
        this.question_id = question_id;
    }

    public long getAnswer() {
        return answer;
    }

    public void setAnswer(long answer) {
        this.answer = answer;
    }
}

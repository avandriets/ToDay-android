package com.gcgamecore.today.Data;


import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "last_questions")
public class DB_LastQuestionInTheme {

    public static final String ID               = "_id";
    public static final String THEME_ID         = "theme_id";
    public static final String QUESTION_ID      = "question_id";

    @Expose
    @DatabaseField(canBeNull = false, columnName = ID, generatedId = true)
    private long id;

    @Expose
    @DatabaseField(columnName = THEME_ID, uniqueCombo = true)
    private long theme_id;

    @Expose
    @DatabaseField(columnName = QUESTION_ID)
    private long question_id;

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
}

package com.gcgamecore.today.Data;


import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "theme_questions")
public class DB_ThemeQuestion {

    public static final String ID               = "_id";
    public static final String HEADER           = "header";
    public static final String THEME            = "theme";
    public static final String LANGUAGE         = "language";
    public static final String QUESTION         = "question";
    public static final String ANSWER1          = "answer1";
    public static final String ANSWER2          = "answer2";
    public static final String RIGHT_ANSWER     = "right_answer";
    public static final String DESCRIPTION      = "description";

    public static final String CREATED_AT       = "created_at";
    public static final String UPDATED_AT       = "updated_at";

    @Expose
    @DatabaseField(id = true, canBeNull = false, columnName = ID)
    private long id;

    @Expose
    @DatabaseField(columnName = THEME)
    private long theme;

    @Expose
    @DatabaseField(columnName = HEADER)
    private long header;

    @Expose
    @DatabaseField(columnName = LANGUAGE)
    private String language;

    @Expose
    @DatabaseField(columnName = QUESTION)
    private String question;

    @Expose
    @DatabaseField(columnName = ANSWER1)
    private String answer1;

    @Expose
    @DatabaseField(columnName = ANSWER2)
    private String answer2;

    @Expose
    @DatabaseField(columnName = RIGHT_ANSWER)
    private long right_answer;

    @Expose
    @DatabaseField(columnName = DESCRIPTION)
    private String description;

    @Expose
    @DatabaseField(canBeNull = true, columnName = CREATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date created_at;

    @Expose
    @DatabaseField(canBeNull = true, columnName = UPDATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date updated_at;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTheme() {
        return theme;
    }

    public void setTheme(long theme) {
        this.theme = theme;
    }

    public long getHeader() {
        return header;
    }

    public void setHeader(long header) {
        this.header = header;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public long getRight_answer() {
        return right_answer;
    }

    public void setRight_answer(long right_answer) {
        this.right_answer = right_answer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}

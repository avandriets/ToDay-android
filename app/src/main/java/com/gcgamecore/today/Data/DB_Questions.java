package com.gcgamecore.today.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "questions")
public class DB_Questions {

    public static final String ID               = "_id";
    public static final String HEADER               = "HEADER";
    public static final String LANGUAGE         = "LANGUAGE";
    public static final String QUESTION         = "QUESTION";
    public static final String ANSWER1          = "ANSWER1";
    public static final String ANSWER2          = "ANSWER2";
    public static final String RIGHT_ANSWER     = "RIGHT_ANSWER";

    public static final String CREATED_AT       = "CREATED_AT";
    public static final String UPDATED_AT       = "UPDATED_AT";

    @Expose
    @DatabaseField(id = true, canBeNull = false, columnName = ID)
    private long id;

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
    @DatabaseField(canBeNull = true, columnName = CREATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date created_at;

    @Expose
    @DatabaseField(canBeNull = true, columnName = UPDATED_AT, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date updated_at;

    public long getHeader() {
        return header;
    }

    public void setHeader(long header) {
        this.header = header;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

package com.gcgamecore.today.Data;


import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "theme_quiz")
public class DB_ThemeQuiz {

    public static final String ID               = "_id";
    public static final String LANGUAGE         = "language";
    public static final String THEME_IMAGE      = "theme_image";
    public static final String TARGET_DATE      = "target_date";
    public static final String NAME             = "name";
    public static final String DESCRIPTION      = "description";
    public static final String CREATED_AT       = "created_at";
    public static final String UPDATED_AT       = "updated_at";


    @DatabaseField(id = true, canBeNull = false, columnName = ID)
    private long id;

    @Expose
    @DatabaseField(columnName = LANGUAGE)
    private String language;

    @Expose
    @DatabaseField(columnName = THEME_IMAGE)
    private String theme_image;

    @Expose
    @DatabaseField(canBeNull = true, columnName = TARGET_DATE, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd")
    private Date target_date;

    @Expose
    @DatabaseField(columnName = NAME)
    private String name;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme_image() {
        return theme_image;
    }

    public void setTheme_image(String theme_image) {
        this.theme_image = theme_image;
    }

    public Date getTarget_date() {
        return target_date;
    }

    public void setTarget_date(Date target_date) {
        this.target_date = target_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

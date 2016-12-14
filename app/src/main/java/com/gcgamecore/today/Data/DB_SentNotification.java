package com.gcgamecore.today.Data;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "notifications")
public class DB_SentNotification {

    public static final String ID = "_id";
    public static final String THEME = "theme";


    @Expose
    @DatabaseField(canBeNull = false, columnName = ID, generatedId = true)
    private long id;

    @Expose
    @DatabaseField(columnName = THEME, uniqueCombo = true)
    private long theme_id;

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
}

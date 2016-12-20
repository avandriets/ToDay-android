package com.gcgamecore.today.Utility;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class JsonDateDeserializer implements JsonDeserializer<Date> {
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String s = json.getAsJsonPrimitive().getAsString();
        SimpleDateFormat sdf_time_stamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");

        Date date_full = null;
        try {
            sdf_time_stamp.parse(s);

            String microseconds = s.substring(s.length()-7, s.length()-1);
            String milliseconds = microseconds.substring(microseconds.length()-3, microseconds.length());
            String time_with_milliseconds = s.substring(0, s.length()-7) + milliseconds + "Z";
            String microseconds_without_mill = microseconds.substring(0,microseconds.length() - 3) + "000";
            long micro_sec = Long.valueOf(microseconds_without_mill);

            date_full = sdf_time_stamp.parse(time_with_milliseconds);
            date_full = new Date(Utility.toLocalTime(date_full.getTime() + micro_sec, TimeZone.getDefault()));

            return date_full;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date date = null;
        try {
            date = sdf_date.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}

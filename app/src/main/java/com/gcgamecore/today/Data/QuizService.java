package com.gcgamecore.today.Data;


import com.gcgamecore.today.Utility.Utility;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface QuizService {

    @Headers({"Content-Type: application/json"})
    @GET(Utility.QuestionsURL + "{lang}/")
    Call<List<DB_Questions>> getQuestions(@Path("lang") String lang);

    @Headers({"Content-Type: application/json"})
    @GET(Utility.QuestionsURLGetChanges + "{lang}/")
    Call<List<DB_Questions>> getQuestionsGetChanges(@Path("lang") String lang,
                                                    @Query("update_time_stamp") long update_time_stamp);

    @Headers({"Content-Type: application/json"})
    @GET(Utility.ThemeURLbyDay + "{lang}/")
    Call<ResponseBody> getTheme(@Path("lang") String lang,
                                @Query("day") int day,
                                @Query("month") int month,
                                @Query("year") int year);

    //Передается поле max theme_date и current_date
    @Headers({"Content-Type: application/json"})
    @GET(Utility.ThemeURLbyInterval + "{lang}/")
    Call<ResponseBody> getThemeByInterval(@Path("lang") String lang,
                                          @Query("last_day") int last_day,
                                          @Query("last_month") int last_month,
                                          @Query("last_year") int last_year,
                                          @Query("current_day") int current_day,
                                          @Query("current_month") int current_month,
                                          @Query("current_year") int current_year
                                          );

    //Передается поле max create_at и max update_at
    @Headers({"Content-Type: application/json"})
    @GET(Utility.ThemeURLGetChanges + "{lang}/")
    Call<ResponseBody> getThemeGetChanges(@Path("lang") String lang,
                                          @Query("create_day") int create_day,
                                          @Query("create_month") int create_month,
                                          @Query("create_year") int create_year,
                                          @Query("update_time_stamp") long update_time_stamp);
}

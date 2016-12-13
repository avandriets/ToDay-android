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
    @GET(Utility.QuestionsURL+"{lang}/")
    Call<List<DB_Questions>> getQuestions(@Path("lang") String lang);

    @Headers({"Content-Type: application/json"})
    @GET(Utility.ThemeURL+"{lang}/")
    Call<ResponseBody> getTheme(@Path("lang") String lang,
                                @Query("day") int day,
                                @Query("month") int month,
                                @Query("year") int year);
}

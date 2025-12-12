package com.imp.data.remote.api

import com.imp.data.util.BaseResponse
import com.imp.data.util.HttpConstants
import com.imp.domain.model.DailyLifePatternInferenceModel
import com.imp.domain.model.DailyLifePatternModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

@JvmSuppressWildcards
interface ApiDailyLifePattern {

    /**
     * Daily Life Pattern Data
     */
    @Headers("Content-Type: application/json")
    @GET(HttpConstants.API_DAILY_LIFE_PATTERN)
    fun dailyLifePattern(
        @Query("id") id: String,
        @Query("date") date: String
    ): Observable<BaseResponse<DailyLifePatternModel>>

    /**
     * Daily Life Pattern Inference
     */
    @Headers("Content-Type: application/json")
    @GET(HttpConstants.API_DAILY_LIFE_PATTERN_INFERENCE)
    fun inferDailyLifePattern(
        @Query("id") id: String,
        @Query("date") date: String
    ): Observable<BaseResponse<DailyLifePatternInferenceModel>>

    /**
     * Daily Life Pattern List (all dates)
     */
    @Headers("Content-Type: application/json")
    @GET(HttpConstants.API_DAILY_LIFE_PATTERN_LIST)
    fun dailyLifePatternList(
        @Query("id") id: String
    ): Observable<BaseResponse<List<DailyLifePatternModel>>>

    /**
     * Save Analysis Result
     */
    @Headers("Content-Type: application/json")
    @POST(HttpConstants.API_ANALYSIS_RESULT)
    fun saveAnalysisResult(
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): Observable<BaseResponse<Boolean>>
}

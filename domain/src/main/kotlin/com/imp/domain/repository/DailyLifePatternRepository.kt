package com.imp.domain.repository

import com.imp.domain.model.DailyLifePatternInferenceModel
import com.imp.domain.model.DailyLifePatternModel
import com.imp.domain.model.ErrorCallbackModel

/**
 * Daily Life Pattern Repository Interface
 */
interface DailyLifePatternRepository {

    /**
     * Load Daily Life Pattern Data
     */
    suspend fun loadDailyLifePattern(
        id: String,
        date: String,
        successCallback: (DailyLifePatternModel) -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    )

    /**
     * Infer Daily Life Pattern
     */
    suspend fun inferDailyLifePattern(
        id: String,
        date: String,
        successCallback: (DailyLifePatternInferenceModel) -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    )

    /**
     * Load Daily Life Pattern List (all dates)
     */
    suspend fun loadDailyLifePatternList(
        id: String,
        successCallback: (List<DailyLifePatternModel>) -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    )

    /**
     * Save Analysis Result
     */
    suspend fun saveAnalysisResult(
        result: com.imp.domain.model.AnalysisResultModel,
        successCallback: () -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    )
}

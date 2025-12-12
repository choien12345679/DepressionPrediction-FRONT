package com.imp.domain.usecase

import com.imp.domain.model.DailyLifePatternInferenceModel
import com.imp.domain.model.DailyLifePatternModel
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.repository.DailyLifePatternRepository
import javax.inject.Inject

/**
 * Daily Life Pattern UseCase
 */
class DailyLifePatternUseCase @Inject constructor(private val repository: DailyLifePatternRepository) {

    /**
     * Load Daily Life Pattern Data
     */
    suspend fun loadDailyLifePattern(
        id: String,
        date: String,
        successCallback: (DailyLifePatternModel) -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    ) {
        repository.loadDailyLifePattern(id, date, successCallback, errorCallback)
    }

    /**
     * Infer Daily Life Pattern
     */
    suspend fun inferDailyLifePattern(
        id: String,
        date: String,
        successCallback: (DailyLifePatternInferenceModel) -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    ) {
        repository.inferDailyLifePattern(id, date, successCallback, errorCallback)
    }

    /**
     * Load Daily Life Pattern List
     */
    suspend fun loadDailyLifePatternList(
        id: String,
        successCallback: (List<DailyLifePatternModel>) -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    ) {
        repository.loadDailyLifePatternList(id, successCallback, errorCallback)
    }

    /**
     * Save Analysis Result
     */
    suspend fun saveAnalysisResult(
        result: com.imp.domain.model.AnalysisResultModel,
        successCallback: () -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    ) {
        repository.saveAnalysisResult(result, successCallback, errorCallback)
    }
}

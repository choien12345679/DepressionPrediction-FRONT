package com.imp.data.repository

import android.annotation.SuppressLint
import com.imp.data.mapper.CommonMapper
import com.imp.data.remote.api.ApiDailyLifePattern
import com.imp.data.util.ApiClient
import com.imp.data.util.extension.isSuccess
import com.imp.domain.model.DailyLifePatternInferenceModel
import com.imp.domain.model.DailyLifePatternModel
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.repository.DailyLifePatternRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Daily Life Pattern Repository Implementation
 */
class DailyLifePatternRepositoryImpl @Inject constructor() : DailyLifePatternRepository {

    /**
     * Load Daily Life Pattern Data
     */
    @SuppressLint("CheckResult")
    override suspend fun loadDailyLifePattern(
        id: String,
        date: String,
        successCallback: (DailyLifePatternModel) -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    ) {
        ApiClient.getClient().create(ApiDailyLifePattern::class.java).dailyLifePattern(id, date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let { successCallback.invoke(it) }
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }

    /**
     * Infer Daily Life Pattern
     */
    @SuppressLint("CheckResult")
    override suspend fun inferDailyLifePattern(
        id: String,
        date: String,
        successCallback: (DailyLifePatternInferenceModel) -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    ) {
        ApiClient.getClient().create(ApiDailyLifePattern::class.java).inferDailyLifePattern(id, date)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let { successCallback.invoke(it) }
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }

    /**
     * Load Daily Life Pattern List (all dates)
     */
    @SuppressLint("CheckResult")
    override suspend fun loadDailyLifePatternList(
        id: String,
        successCallback: (List<DailyLifePatternModel>) -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    ) {
        ApiClient.getClient().create(ApiDailyLifePattern::class.java).dailyLifePatternList(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    response.data?.let { successCallback.invoke(it) }
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }

    /**
     * Save Analysis Result
     */
    @SuppressLint("CheckResult")
    override suspend fun saveAnalysisResult(
        result: com.imp.domain.model.AnalysisResultModel,
        successCallback: () -> Unit,
        errorCallback: (ErrorCallbackModel?) -> Unit
    ) {

        val body: MutableMap<String, Any?> = HashMap()
        body["analysis_result_id"] = result.analysis_result_id
        body["user_id"] = result.user_id
        body["date"] = result.date
        body["analysis_type"] = result.analysis_type
        body["score"] = result.score
        body["label"] = result.label
        body["confidence"] = result.confidence
        body["comment"] = result.comment
        body["model_version"] = result.model_version

        ApiClient.getClient().create(ApiDailyLifePattern::class.java).saveAnalysisResult(body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->

                if (response.isSuccess()) {
                    successCallback.invoke()
                } else {
                    errorCallback.invoke(CommonMapper.mappingErrorCallbackData(response))
                }

            }, { error ->
                errorCallback.invoke(CommonMapper.mappingErrorData(error))
            })
    }
}

package com.imp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imp.domain.model.DailyLifePatternInferenceModel
import com.imp.domain.model.DailyLifePatternModel
import com.imp.domain.model.ErrorCallbackModel
import com.imp.domain.usecase.DailyLifePatternUseCase
import com.imp.presentation.widget.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Daily Life Pattern ViewModel
 */
@HiltViewModel
class DailyLifePatternViewModel @Inject constructor(private val useCase: DailyLifePatternUseCase) : ViewModel() {

    /** Pattern data */
    private var _patternData: MutableLiveData<DailyLifePatternModel> = MutableLiveData()
    val patternData: LiveData<DailyLifePatternModel> get() = _patternData

    /** Inference result */
    private var _inferenceData: MutableLiveData<DailyLifePatternInferenceModel> = MutableLiveData()
    val inferenceData: LiveData<DailyLifePatternInferenceModel> get() = _inferenceData

    /** Pattern list */
    private var _patternList: MutableLiveData<List<DailyLifePatternModel>> = MutableLiveData()
    val patternList: LiveData<List<DailyLifePatternModel>> get() = _patternList

    /** Error Callback */
    private var _errorCallback: MutableLiveData<Event<ErrorCallbackModel?>> = MutableLiveData()
    val errorCallback: LiveData<Event<ErrorCallbackModel?>> get() = _errorCallback

    /** Save result callback */
    private var _saveResultSuccess: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val saveResultSuccess: LiveData<Event<Boolean>> get() = _saveResultSuccess

    /**
     * Load Daily Life Pattern
     */
    fun loadPattern(id: String, date: String) = viewModelScope.launch {

        useCase.loadDailyLifePattern(
            id = id,
            date = date,
            successCallback = { _patternData.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Request inference
     */
    fun inferPattern(id: String, date: String) = viewModelScope.launch {

        useCase.inferDailyLifePattern(
            id = id,
            date = date,
            successCallback = { _inferenceData.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Load Daily Life Pattern List (all dates)
     */
    fun loadPatternList(id: String) = viewModelScope.launch {

        useCase.loadDailyLifePatternList(
            id = id,
            successCallback = { _patternList.value = it },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Save analysis result
     */
    fun saveAnalysisResult(result: com.imp.domain.model.AnalysisResultModel) = viewModelScope.launch {

        useCase.saveAnalysisResult(
            result = result,
            successCallback = { _saveResultSuccess.value = Event(true) },
            errorCallback = { _errorCallback.value = Event(it) }
        )
    }

    /**
     * Set inference from local model
     */
    fun setLocalInference(data: DailyLifePatternInferenceModel) {
        _inferenceData.value = data
    }

    /**
     * Fallback: set list with single item when list api is empty
     */
    fun setSinglePatternAsList(data: DailyLifePatternModel) {
        _patternList.value = listOf(data)
    }

    /**
     * Reset data
     */
    fun reset() {

        _patternData = MutableLiveData()
        _inferenceData = MutableLiveData()
        _patternList = MutableLiveData()
        _errorCallback = MutableLiveData()
        _saveResultSuccess = MutableLiveData()
    }
}

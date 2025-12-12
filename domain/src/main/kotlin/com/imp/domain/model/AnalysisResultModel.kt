package com.imp.domain.model

/**
 * Analysis Result payload for saving inference outcome
 */
data class AnalysisResultModel(
    var analysis_result_id: Long? = null,
    var user_id: String,
    var date: String,
    var analysis_type: String,
    var score: Double? = null,
    var label: String? = null,
    var confidence: Double? = null,
    var comment: String? = null,
    var model_version: String? = null
)

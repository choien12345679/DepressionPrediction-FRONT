package com.imp.domain.model

/**
 * Daily Life Pattern Inference Model
 */
data class DailyLifePatternInferenceModel(

    var id: String? = null,

    var date: String? = null,

    // Model generated summary for the user's day
    var summary: String? = null,

    // Recommendations or insights derived from the pattern
    var recommendations: ArrayList<String> = ArrayList(),

    // Depression label and confidence (from backend or local inference)
    var label: String? = null,
    var confidence: Double = 0.0,
    var probabilities: List<Double>? = null,

    var analysis_type: String? = null,
    var model_version: String? = null,
    var comment: String? = null,
    var score: Double? = null
)

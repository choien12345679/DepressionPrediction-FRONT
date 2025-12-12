package com.imp.domain.model

/**
 * Daily Life Pattern Model
 */
data class DailyLifePatternModel(

    var id: String? = null,

    var date: String? = null,

    // Features for depression inference
    var place_diversity: Double = 0.0,
    var home_stay_percentage: Double = 0.0,
    var life_routine_consistency: Double = 0.0,
    var day_phone_use_frequency: Double = 0.0,
    var night_phone_use_frequency: Double = 0.0,
    var day_phone_use_duration: Double = 0.0,
    var night_phone_use_duration: Double = 0.0,
    var sleeptime_screen_duration: Double = 0.0,
    var day_call_frequency: Double = 0.0,
    var night_call_frequency: Double = 0.0,
    var day_call_duration: Double = 0.0,
    var night_call_duration: Double = 0.0,
    var day_light_exposure: Double = 0.0,
    var night_light_exposure: Double = 0.0,
    var day_step_count: Double = 0.0,
    var night_step_count: Double = 0.0,

    // Optional label (0/1) if backend returns it
    var label: Int? = null
)

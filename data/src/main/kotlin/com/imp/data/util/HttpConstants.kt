package com.imp.data.util

import com.imp.data.BuildConfig

/**
 * Http Constants
 */
class HttpConstants {

    companion object {

        // Server define
        private const val SERVICE = 0
        private const val DEV = 1

        // Current
        //--------------------------------------------------
        private const val SERVER: Int = DEV

        @JvmStatic
        fun getHost(): String = when (BuildConfig.DEBUG) {

            true -> when(SERVER) {
                SERVICE -> SERVICE_SERVER_HOST
                else -> DEV_SERVER_HOST
            }
            false -> SERVICE_SERVER_HOST
        }

        @JvmStatic
        fun getChatHost(): String = CHAT_SERVER_HOST

        // Server Url
        //--------------------------------------------------
        private val SERVICE_SERVER_HOST = BuildConfig.SERVICE_SERVER_HOST
        private val DEV_SERVER_HOST = BuildConfig.DEV_SERVER_HOST
        private val CHAT_SERVER_HOST = BuildConfig.CHAT_SERVER_HOST

        // Api url
        const val API_LOGIN = "/api/login"                                    // 로그인
        const val API_REGISTER = "/api/register"                              // 회원 가입
        const val API_REGISTER_VALIDATION = "/api/register/validation"        // 이메일 중복 검사
        const val API_HOME = "/api/home"                                      // 홈 데이터
        const val API_MOOD_INSERT = "/api/mood/insert"                        // 기분 데이터 저장
        const val API_SENSOR_INSERT = "/api/sensor/insert"                    // 센싱 데이터 저장
        const val API_LOG = "/api/log"                                        // 로그 데이터
        const val API_ANALYSIS = "/api/analysis"                              // 분석 데이터
        const val API_DAILY_LIFE_PATTERN = "/api/daily-life-pattern"          // 일상 패턴 조회
        const val API_DAILY_LIFE_PATTERN_INFERENCE = "/api/daily-life-pattern/inference" // 일상 패턴 추론
        const val API_DAILY_LIFE_PATTERN_LIST = "/api/daily-life-pattern/list" // 일상 패턴 전체 목록
        const val API_CHAT_LIST = "/chat/list"                                // 채팅 목록
        const val API_CHAT_CREATE = "/chat/create"                            // 채팅 생성
        const val API_CHAT_DELETE = "/chat/delete"                            // 채팅 삭제
        const val API_CHAT_QUESTION = "/chat/question"                        // 채팅 전송
        const val API_CHANGE = "/api/change/{id}"                             // 회원 정보 변경
        const val API_ANALYSIS_RESULT = "/api/analysis-result"                // 추론 결과 저장

        const val KAKAO_BASE_HOST = "https://dapi.kakao.com/"
        const val API_KAKAO_ADDRESS_SEARCH = "v2/local/search/address.json"
        const val API_KAKAO_ADDRESS_REGION_CODE = "v2/local/geo/coord2regioncode.json"
    }
}

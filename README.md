# DepressionPrediction-FRONT

해당 프로젝트는 https://github.com/IMP-FINAL-PROJECT/FINAL-FRONT에서 작업한 내용의 후속 연구활동입니다.

## 개요
  - 일상 패턴(`daily_life_pattern`)을 기반으로 **Smile** 라이브러리의 RandomForest로 단말에서
  우울 확률을 추론하고, 결과를 백엔드 `/api/analysis-result`에 저장합니다.
  - 주요 화면: “어제의 우울 점수” + “이전 우울 점수 확인하기” 카드 리스트.
  - 추론/훈련에 사용하는 라벨링 CSV는 APK 자산(`presentation/src/main/assets/dlp_labeled.csv`)
  에 두며, Git에는 포함하지 않습니다.

  ## 아키텍처
  - 멀티모듈: `app`(런처/DI), `presentation`(UI), `domain`(모델/유즈케이스), `data`(API/리포지
  토리/Smile 모델).
  - DI: Hilt.
  - 네트워크: Retrofit2 + RxJava.
  - ML: **Smile RandomForest** — 자산 CSV를 읽어 단말에서 학습 후 메모리 캐시, 추론 시 확률(정
  상/우울 posterior)을 반환.

  ## 주요 로직
  1. **데이터 조회**
     - 어제 날짜(`DateUtil.getYesterdayDate()`, `yyyy-MM-dd`)로 `GET /api/daily-life-pattern?
  id=...&date=...`.
     - 전체 리스트: `GET /api/daily-life-pattern/list?id=...` (어제 제외 후 “이전 우울 점수”
  카드 목록에 사용).

  2. **로컬 추론 (on-device RandomForest)**
     - 자산 `dlp_labeled.csv`를 읽어 Smile RF 학습 → 모델 캐시.
     - 패턴 데이터를 `double[]`로 매핑 후 `predictWithProb` 호출.
     - pred=0 → “정상”, pred=1 → “우울”; 우울 확률 = posterior[1] * 100을 점수로 표시.

  3. **결과 표시**
     - 상단: “어제의 우울 점수는 NN점이에요!” + “어제 날짜 기준”.
     - 하단: RecyclerView 카드로 과거 우울 점수(어제 제외) 표시.

  4. **결과 저장**
     - 추론 직후 `POST /api/analysis-result`로 `analysis_result` 테이블에 저장.
     - 전송 필드: user_id, date, analysis_type(기본 `depression_rf_v1`), score(우울 확률),
  label(“정상/우울”), confidence, comment, model_version.

  ## 파일 구조 (주요)
  - `presentation/src/main/kotlin/com/imp/presentation/view/pattern/FrgDailyLifePattern.kt`
    어제 패턴 조회 → 로컬 RF 추론 → UI 갱신 → 결과 저장 → 이전 점수 리스트 구성.
  - `presentation/src/main/kotlin/com/imp/presentation/view/pattern/DepressionScoreAdapter.kt`
    이전 점수 카드 리스트 어댑터.
  - `presentation/src/main/res/layout/frg_daily_life_pattern.xml`
    어제 점수 + 이전 점수 UI.
  - `presentation/src/main/res/layout/item_depression_score.xml`
    카드 아이템 UI.
  - `presentation/src/main/res/layout/act_main.xml`
    하단 탭 아이콘/텍스트 겹침 해소(높이/패딩/아이콘 크기 조정).
  - `presentation/src/main/kotlin/com/imp/presentation/widget/utils/DateUtil.kt`
    `getYesterdayDate()` 추가.
  - `data/src/main/kotlin/com/imp/data/ml/DailyLifePatternTrainer.kt`
    CSV 로드, Smile RF 학습/예측/확률 반환, 모델 캐시.
  - `data/src/main/kotlin/com/imp/data/remote/api/ApiDailyLifePattern.kt`
    패턴 단건/목록, 추론, 분석결과 저장 API.
  - `data/src/main/kotlin/com/imp/data/repository/DailyLifePatternRepositoryImpl.kt`
    Retrofit 호출 래핑 및 콜백 전달.
  - `domain/src/main/kotlin/com/imp/domain/model/...`
    `DailyLifePatternModel`, `DailyLifePatternInferenceModel`, `AnalysisResultModel`.
  - `domain/src/main/kotlin/com/imp/domain/usecase/DailyLifePatternUseCase.kt`
    리포지토리 유즈케이스 집합.

  ## API 요약
  - `GET /api/daily-life-pattern?id={id}&date=yyyy-MM-dd` : 어제 패턴 조회.
  - `GET /api/daily-life-pattern/list?id={id}` : 전체 패턴 리스트(날짜 내림차순).
  - `GET /api/daily-life-pattern/inference?id={id}&date=yyyy-MM-dd` : 백엔드 추론(현재 더미/
  옵션).
  - `POST /api/analysis-result` : 프런트 추론 결과 저장.

  ## 데이터/모델
  - 자산 CSV 경로: `presentation/src/main/assets/dlp_labeled.csv` (Git에 올리지 말 것).
  - 특성 컬럼: place_diversity, home_stay_percentage, life_routine_consistency, day/
  night_phone_use_frequency, day/night_phone_use_duration, sleeptime_screen_duration,
  day/night_call_frequency, day/night_call_duration, day/night_light_exposure, day/
  night_step_count, label.
  - 라벨 규칙: 0=정상, 1=우울.

  ## 빌드/실행
  - Android 스튜디오에서 `app` 모듈 실행 또는 CLI `./gradlew :app:assembleDebug`.
  - 네트워크/호스트: `HttpConstants`가 BuildConfig 호스트 사용; `local.properties`에 서버 호스
  트 키 필요(비공개).

  ## 화면 흐름
  1) 앱 진입 → 어제 패턴 자동 조회 → 로컬 RF 추론 → “어제의 우울 점수” 표시.
  2) 추론 결과를 백엔드에 저장.
  3) 패턴 리스트에서 어제 제외 후 과거 우울 점수를 카드로 표시.

  ## 주의/남은 작업
  - CSV는 저장소에 포함하지 말고 자산 폴더에만 배치.
  - 백엔드 추론을 사용하려면 `inferPattern` 호출과 응답 매핑을 필요에 따라 조정.
  - 데모 중 에러 토스트를 숨기려면 `FrgDailyLifePattern`의 `errorCallback` 주석 유지, 필요 시
  해제.

package com.imp.presentation.view.pattern

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.imp.data.ml.DailyLifePatternTrainer
import com.imp.domain.model.DailyLifePatternInferenceModel
import com.imp.domain.model.DailyLifePatternModel
import com.imp.presentation.R
import com.imp.presentation.base.BaseFragment
import com.imp.presentation.constants.BaseConstants
import com.imp.presentation.databinding.FrgDailyLifePatternBinding
import com.imp.presentation.viewmodel.MemberViewModel
import com.imp.presentation.viewmodel.DailyLifePatternViewModel
import com.imp.presentation.view.main.activity.ActMain
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Daily Life Pattern Fragment
 */
@AndroidEntryPoint
class FrgDailyLifePattern : BaseFragment<FrgDailyLifePatternBinding>() {

    /** Daily Life Pattern ViewModel */
    private val viewModel: DailyLifePatternViewModel by viewModels()
    private val memberViewModel: MemberViewModel by viewModels()
    private var trainingJob: Job? = null
    private val scoreAdapter = DepressionScoreAdapter()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FrgDailyLifePatternBinding.inflate(inflater, container, false)

    override fun initData() {
        viewModel.reset()
        memberViewModel.getMember()
    }

    override fun initView() {
        initDisplay()
        initObserver()
        initRecycler()
        callPatternApi()

        activity?.let { if (it is ActMain) it.setCurrentStatusBarColor(BaseConstants.MAIN_NAV_LABEL_LOG) }
    }

    /**
     * Initialize text and placeholders
     */
    private fun initDisplay() = with(mBinding) {

        tvTodayTitle.text = getString(R.string.today_depression_title)
        tvTodayScore.text = getString(R.string.today_depression_score_placeholder)
        tvTodayDate.text = getString(
            R.string.today_depression_date_fmt,
            com.imp.presentation.widget.utils.DateUtil.getYesterdayDate()
        )
        tvPreviousTitle.text = getString(R.string.previous_depression_title)
        tvEmptyPrevious.text = getString(R.string.depression_score_empty)
    }

    private fun initRecycler() = with(mBinding) {
        rvPreviousScores.layoutManager = LinearLayoutManager(context)
        rvPreviousScores.adapter = scoreAdapter
    }

    /**
     * Initialize observer
     */
    private fun initObserver() {

        viewModel.patternData.observe(viewLifecycleOwner) { data ->
            runLocalInference(data)
            viewModel.setSinglePatternAsList(data)
        }

        viewModel.inferenceData.observe(viewLifecycleOwner) { data ->
            updateTodayScore(data)
            saveInferenceResult(data)
        }

        viewModel.patternList.observe(viewLifecycleOwner) { list ->
            runInferenceForList(list)
        }

        viewModel.errorCallback.observe(viewLifecycleOwner) { event ->

            event.getContentIfNotHandled()?.let { error ->
                // Demo 용으로 서버 오류 토스트를 숨깁니다.
                // 필요 시 아래 한 줄을 복원하세요.
                // context?.let { Toast.makeText(it, error?.message, Toast.LENGTH_SHORT).show() }
            }
        }

        viewModel.saveResultSuccess.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                context?.let { Toast.makeText(it, getString(R.string.daily_life_pattern_saved), Toast.LENGTH_SHORT).show() }
            }
        }

        memberViewModel.memberData.observe(viewLifecycleOwner) { member ->
            callPatternApi(member.id)
            callPatternListApi(member.id)
        }
    }

    private fun callPatternApi(forceId: String? = null) {

        val id = forceId ?: memberViewModel.memberData.value?.id ?: return
        val date = com.imp.presentation.widget.utils.DateUtil.getYesterdayDate() // "yyyy-MM-dd"

        viewModel.loadPattern(id, date)
    }

    private fun callPatternListApi(forceId: String? = null) {

        val id = forceId ?: memberViewModel.memberData.value?.id ?: return
        viewModel.loadPatternList(id)
    }

    /**
     * Run on-device inference using cached RF model
     */
    private fun runLocalInference(data: DailyLifePatternModel) {

        val ctx = context ?: return
        viewLifecycleOwner.lifecycleScope.launch {

            val modelReady = ensureModelReady(ctx)

            if (!modelReady) return@launch

            val features = buildFeatureVector(data)
            val (pred, confidence, posterior) = DailyLifePatternTrainer.predictWithProb(features)

            val labelText = if (pred == 1) "우울" else "정상"
            val inference = DailyLifePatternInferenceModel(
                id = data.id,
                date = data.date,
                summary = "Local RF inference",
                recommendations = arrayListOf(),
                label = labelText,
                confidence = confidence,
                probabilities = posterior.toList(),
                analysis_type = "depression_rf_v1",
                model_version = "rf_v1",
                comment = "daily life pattern inference",
                score = confidence
            )
            viewModel.setLocalInference(inference)
        }
    }

    private suspend fun ensureModelReady(ctx: android.content.Context): Boolean = withContext(Dispatchers.Default) {
        if (DailyLifePatternTrainer.hasCachedModel()) return@withContext true
        runCatching {
            ctx.assets.open("dlp_labeled.csv").use { input ->
                DailyLifePatternTrainer.trainFromStream(
                    inputStream = input,
                    iterations = 10,
                    testRatio = 0.2
                )
            }
        }.isSuccess
    }

    private fun buildFeatureVector(data: DailyLifePatternModel): DoubleArray {

        return doubleArrayOf(
            data.place_diversity,
            data.home_stay_percentage,
            data.life_routine_consistency,
            data.day_phone_use_frequency,
            data.night_phone_use_frequency,
            data.day_phone_use_duration,
            data.night_phone_use_duration,
            data.sleeptime_screen_duration,
            data.day_call_frequency,
            data.night_call_frequency,
            data.day_call_duration,
            data.night_call_duration,
            data.day_light_exposure,
            data.night_light_exposure,
            data.day_step_count,
            data.night_step_count
        )
    }

    override fun onDestroyView() {
        trainingJob?.cancel()
        trainingJob = null
        super.onDestroyView()
    }

    private fun updateTodayScore(data: DailyLifePatternInferenceModel) {

        val score = (data.probabilities?.getOrNull(1) ?: data.confidence).coerceAtLeast(0.0) * 100
        mBinding.tvTodayScore.text = getString(R.string.today_depression_score_fmt, score)
        mBinding.tvTodayDate.text = getString(
            R.string.today_depression_date_fmt,
            data.date ?: com.imp.presentation.widget.utils.DateUtil.getYesterdayDate()
        )
    }

    private fun saveInferenceResult(data: DailyLifePatternInferenceModel) {

        val userId = memberViewModel.memberData.value?.id ?: return
        val date = data.date ?: com.imp.presentation.widget.utils.DateUtil.getYesterdayDate()

        val result = com.imp.domain.model.AnalysisResultModel(
            analysis_result_id = null,
            user_id = userId,
            date = date,
            analysis_type = data.analysis_type ?: "depression_rf_v1",
            score = data.score ?: data.confidence,
            label = data.label ?: "unknown",
            confidence = data.confidence,
            comment = data.comment ?: "daily life pattern inference",
            model_version = data.model_version ?: "rf_v1"
        )

        viewModel.saveAnalysisResult(result)
    }

    private fun runInferenceForList(list: List<DailyLifePatternModel>?) {

        val yesterday = com.imp.presentation.widget.utils.DateUtil.getYesterdayDate()
        val filtered = list?.filter { it.date != yesterday }

        if (filtered.isNullOrEmpty()) {
            mBinding.tvEmptyPrevious.isVisible = true
            scoreAdapter.submitList(emptyList())
            return
        }

        mBinding.tvEmptyPrevious.isVisible = false

        val ctx = context ?: return
        viewLifecycleOwner.lifecycleScope.launch {

            val modelReady = ensureModelReady(ctx)
            if (!modelReady) return@launch

            val scores = withContext(Dispatchers.Default) {
                filtered.mapNotNull { item ->
                    val prob = runCatching {
                        val (_, confidence, posterior) = DailyLifePatternTrainer.predictWithProb(buildFeatureVector(item))
                        (posterior.getOrNull(1) ?: confidence) * 100
                    }.getOrNull()
                    prob?.let { DepressionScoreUi(item.date ?: "-", it) }
                }
            }
            scoreAdapter.submitList(scores)
        }
    }
}

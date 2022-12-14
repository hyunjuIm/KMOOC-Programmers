package com.programmers.kmooc.activities.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.databinding.ActivityKmookDetailBinding
import com.programmers.kmooc.network.ImageLoader
import com.programmers.kmooc.utils.DateUtil
import com.programmers.kmooc.viewmodels.KmoocDetailViewModel
import com.programmers.kmooc.viewmodels.KmoocDetailViewModelFactory
import com.programmers.kmooc.viewmodels.state.KmoocDetailState

class KmoocDetailActivity : AppCompatActivity() {

    companion object {
        const val INTENT_PARAM_COURSE_ID = "param_course_id"
    }

    private val courseId by lazy { intent.getStringExtra(INTENT_PARAM_COURSE_ID) }

    private val binding by lazy { ActivityKmookDetailBinding.inflate(layoutInflater) }
    private lateinit var viewModel: KmoocDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocDetailViewModelFactory(kmoocRepository)).get(
            KmoocDetailViewModel::class.java
        )

        setContentView(binding.root)

        if (courseId == null || courseId.isEmpty()) {
            finish()
            return
        }

        observeData()
        initViews()

        viewModel.detail(courseId)
    }

    private fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun observeData() = viewModel.kmoocDetailLiveData.observe(this) {
        when (it) {
            is KmoocDetailState.Loading -> {
                handleLoadingState()
            }
            is KmoocDetailState.Success -> {
                handleSuccessState(it)
            }
            else -> Unit
        }
    }

    private fun handleLoadingState() = with(binding) {
        progressBar.isVisible = true
    }

    private fun handleSuccessState(state: KmoocDetailState.Success) = with(binding) {
        progressBar.isGone = true

        toolbar.title = state.lecture.name

        ImageLoader.loadImage(state.lecture.courseImageLarge) { bitmap ->
            bitmap?.let {
                lectureImage.setImageBitmap(bitmap)
            }
        }

        lectureNumber.setDescription("??? ????????????", state.lecture.number)
        lectureType.setDescription("??? ??????", state.lecture.classfyName)
        lectureOrg.setDescription("??? ????????????", state.lecture.orgName)
        lectureTeachers.setDescription("??? ?????????", state.lecture.teachers ?: "")
        lectureDue.setDescription(
            "??? ????????????",
            DateUtil.dueString(state.lecture.start, state.lecture.end)
        )

        if (!state.lecture.overview.isNullOrEmpty()) {
            webView.loadDataWithBaseURL(
                null,
                state.lecture.overview,
                "text/html; charset=utf-8",
                "utf-8",
                null
            )
            webView.isVisible = true
        }

    }

}
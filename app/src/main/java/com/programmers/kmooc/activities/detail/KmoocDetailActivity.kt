package com.programmers.kmooc.activities.detail

import android.annotation.SuppressLint
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

    private lateinit var binding: ActivityKmookDetailBinding
    private lateinit var viewModel: KmoocDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocDetailViewModelFactory(kmoocRepository)).get(
            KmoocDetailViewModel::class.java
        )

        binding = ActivityKmookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (courseId == null || courseId.isEmpty()) {
            finish()
            return
        }

        observeData()
        initViews()

        viewModel.detail(courseId)
    }

    @SuppressLint("ClickableViewAccessibility")
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

        lectureNumber.setDescription("• 강좌번호", state.lecture.number)
        lectureType.setDescription("• 분류", state.lecture.classfyName)
        lectureOrg.setDescription("• 운영기관", state.lecture.orgName)
        lectureTeachers.setDescription("• 교수진", state.lecture.teachers ?: "")
        lectureDue.setDescription(
            "• 운영기간",
            DateUtil.dueString(state.lecture.start, state.lecture.end)
        )

        if (!state.lecture.overview.isNullOrBlank()) {
            webView.loadDataWithBaseURL(null, state.lecture.overview, "text/html; charset=utf-8", "utf-8",null)
            webView.isVisible = true
        }

    }

}
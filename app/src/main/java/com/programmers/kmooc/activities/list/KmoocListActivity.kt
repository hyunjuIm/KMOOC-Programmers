package com.programmers.kmooc.activities.list

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.programmers.kmooc.KmoocApplication
import com.programmers.kmooc.activities.detail.KmoocDetailActivity
import com.programmers.kmooc.databinding.ActivityKmookListBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.utils.toVisibility
import com.programmers.kmooc.viewmodels.KmoocListViewModel
import com.programmers.kmooc.viewmodels.KmoocListViewModelFactory
import com.programmers.kmooc.viewmodels.state.KmoocListState

class KmoocListActivity : AppCompatActivity() {

    private val binding by lazy { ActivityKmookListBinding.inflate(layoutInflater) }
    private lateinit var viewModel: KmoocListViewModel

    private val adapter by lazy {
        LecturesAdapter().apply { onClick = this@KmoocListActivity::startDetailActivity }
    }

    private lateinit var hasNextPage: LectureList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val kmoocRepository = (application as KmoocApplication).kmoocRepository
        viewModel = ViewModelProvider(this, KmoocListViewModelFactory(kmoocRepository)).get(
            KmoocListViewModel::class.java
        )

        setContentView(binding.root)

        observeData()
        initViews()
    }

    private fun initViews() = with(binding) {
        pullToRefresh.setOnRefreshListener {
            viewModel.list()
        }

        lectureList.adapter = adapter
        lectureList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = lectureList.layoutManager

                if (hasNextPage.next.isNotEmpty()) {
                    val lastVisibleItem = (layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()

                    if (layoutManager.itemCount <= lastVisibleItem + 3) {
                        viewModel.next(hasNextPage)
                    }
                }
            }
        })

        viewModel.list()
    }

    private fun observeData() = viewModel.kmoocListLiveData.observe(this) {
        when (it) {
            is KmoocListState.Loading -> {
                handleLoadingState()
            }
            is KmoocListState.Success -> {
                handleSuccessState(it)
            }
            else -> Unit
        }
    }

    private fun handleLoadingState() = with(binding) {
        progressBar.isVisible = true
    }

    private fun handleSuccessState(state: KmoocListState.Success) = with(binding) {
        progressBar.isGone = true
        pullToRefresh.isRefreshing = false

        adapter.updateLectures(state.result.lectures)
        hasNextPage = state.result
    }

    private fun startDetailActivity(lecture: Lecture) {
        startActivity(
            Intent(this, KmoocDetailActivity::class.java)
                .apply { putExtra(KmoocDetailActivity.INTENT_PARAM_COURSE_ID, lecture.id) }
        )
    }
}

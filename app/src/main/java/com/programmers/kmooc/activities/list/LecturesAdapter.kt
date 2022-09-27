package com.programmers.kmooc.activities.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmers.kmooc.R
import com.programmers.kmooc.databinding.ViewKmookListItemBinding
import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.network.ImageLoader
import com.programmers.kmooc.utils.DateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LecturesAdapter : RecyclerView.Adapter<LectureViewHolder>() {

    private val lectures = mutableListOf<Lecture>()
    var onClick: (Lecture) -> Unit = {}

    fun updateLectures(lectures: List<Lecture>) {
        this.lectures.clear()
        this.lectures.addAll(lectures)
        notifyDataSetChanged()
    }

    fun addLectures(lectures: List<Lecture>) {
        this.lectures.addAll(lectures)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return lectures.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_kmook_list_item, parent, false)
        val binding = ViewKmookListItemBinding.bind(view)
        return LectureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        val lecture = lectures[position]
        holder.bindData(lecture)
        holder.itemView.setOnClickListener { onClick(lecture) }
    }
}

class LectureViewHolder(
    private val binding: ViewKmookListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindData(lecture: Lecture) = with(binding) {

        ImageLoader.loadImage(lecture.courseImage) { bitmap ->
            bitmap?.let {
                lectureImage.setImageBitmap(bitmap)
            }
        }

        lectureTitle.text = lecture.name
        lectureFrom.text = lecture.orgName
        lectureDuration.text = DateUtil.dueString(lecture.start, lecture.end)
    }
}
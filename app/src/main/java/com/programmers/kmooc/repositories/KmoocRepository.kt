package com.programmers.kmooc.repositories

import com.programmers.kmooc.models.Lecture
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.network.HttpClient
import com.programmers.kmooc.utils.DateUtil
import org.json.JSONObject

class KmoocRepository {

    /**
     * 국가평생교육진흥원_K-MOOC_강좌정보API
     * https://www.data.go.kr/data/15042355/openapi.do
     */

    private val httpClient = HttpClient("http://apis.data.go.kr/B552881/kmooc")
    private val serviceKey =
        "LwG%2BoHC0C5JRfLyvNtKkR94KYuT2QYNXOT5ONKk65iVxzMXLHF7SMWcuDqKMnT%2BfSMP61nqqh6Nj7cloXRQXLA%3D%3D"

    fun list(completed: (LectureList) -> Unit) {
        httpClient.getJson(
            "/courseList",
            mapOf("serviceKey" to serviceKey, "Mobile" to 1)
        ) { result ->
            result.onSuccess {
                completed(parseLectureList(JSONObject(it)))
            }
            result.onFailure {
                completed(LectureList.EMPTY)
            }
        }
    }

    fun next(currentPage: LectureList, completed: (LectureList) -> Unit) {
        val nextPageUrl = currentPage.next
        httpClient.getJson(nextPageUrl, emptyMap()) { result ->
            result.onSuccess {
                completed(parseLectureList(JSONObject(it)))
            }
            result.onFailure {
                completed(LectureList.EMPTY)
            }
        }
    }

    fun detail(courseId: String, completed: (Lecture) -> Unit) {
        httpClient.getJson(
            "/courseDetail",
            mapOf("CourseId" to courseId, "serviceKey" to serviceKey)
        ) { result ->
            result.onSuccess {
                completed(parseLecture(JSONObject(it)))
            }
            result.onFailure {
                completed(Lecture.EMPTY)
            }
        }
    }

    private fun parseLectureList(jsonObject: JSONObject): LectureList {
        return jsonObject.getJSONObject("pagination").run {
            LectureList(
                count = getInt("count"),
                numPages = getInt("num_pages"),
                previous = getString("previous"),
                next = getString("next"),
                lectures = jsonObject.getJSONArray("results").run {
                    mutableListOf<Lecture>().apply {
                        for (i in 0 until length()) {
                            add(parseLecture(getJSONObject(i)))
                        }
                    }
                }
            )
        }
    }

    private fun parseLecture(jsonObject: JSONObject): Lecture {
        return jsonObject.run {
            Lecture(
                id = getString("id"),
                number = getString("number"),
                name = getString("name"),
                classfyName = getString("classfy_name"),
                middleClassfyName = getString("middle_classfy_name"),
                courseImage = getJSONObject("media").getJSONObject("image").getString("small"),
                courseImageLarge = getJSONObject("media").getJSONObject("image").getString("large"),
                shortDescription = getString("short_description"),
                orgName = getString("org_name"),
                start = DateUtil.parseDate(getString("start")),
                end = DateUtil.parseDate(getString("end")),
                teachers = if (has("teachers")) getString("teachers") else null,
                overview = if (has("overview")) getString("overview") else null
            )
        }
    }
}
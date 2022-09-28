# K-MOOC 강좌정보 서비스 (프로그래머스 과제 테스트)

<img src="https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=Android&logoColor=white"/> <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=Kotlin&logoColor=white"/>

<br>

> ## 📋 문제

K-MOOC에서 제공하는 강좌의 목록과 상세 내용을 제공하는 서비스를 완성하세요.

**요구사항**
1. 강좌 목록을 표시하세요
    * 각 강좌는 썸네일, 운영기관, 운영기간 정보로 구성됩니다.
    * Pull to refresh 가 가능합니다.
    * 무한 스크롤방식으로 목록이 추가로 보여집니다.
2. 강좌 상세를 표시하세요
    * 강좌 이미지를 표시합니다.
    * 강좌 번호, 분류, 운영기관, 교수정보, 운영기간, 추가 상세정보로 구성됩니다.
    * 추가 상세정보는 웹뷰로 표현됩니다.
3. 제한사항
    * Android : org.jetbrains.* , androidx.* 외에 다른 라이브러리는 추가하지 않습니다.
4. 기타
    * 원본 데이터는 https://www.data.go.kr/data/15042355/openapi.do 에서 제공됩니다.

**베이스코드 설명**
1. KmoocListActivity, KmoocDetailActivity로 구성됩니다.
2. 각 Activity는 해당 ViewModel이 제공됩니다.
3. data fetch는 구현된 상태로 제공됩니다.
    * KmoocRepository::list() : 첫 목록 불러오기
    * KmoocRepository::next() : 다음 페이지 불러오기
    * KmoocRepository::detail() : 상세 정보 불러오기
    * fetch된 데이터는 Lecture, LectureList 모델을 사용하세요
    * Lecture 모델의 필드 설명은 주석으로 작성되어 있습니다.
4. java.util.Date 타입을 파싱하고 포맷팅할 때 .utils.DateUtil 을 사용하세요.
5. url로 이미지를 로딩할 때 .network.ImageLoader 를 사용하세요.

<br>

> ## 💡 해결

기본적으로 구글에서 제시하는 앱 아키텍쳐 가이드를 따랐으며 LiveData를 사용하는 부분에서는 State 패턴을 적용해 작성해보았다.
<br>

### 강좌 리스트 페이지 (KmoocListActivity)
- ViewModel LiveData에서 fetching 된 데이터를 adapter를 통해 표시, pull to refresh 구현
- RecyclerView 무한 스크롤 구현
  * addOnScrollListener의 `onScrolled()` 에서 스크롤이 바닥에 닿을 때 쯤 페이지 단위로 자동 로딩
  * 다음 페이지의 존재 유무, 현재 로딩 중인지 확인해 불필요한 요청이 생기지 않도록 조건문을 통해 처리

### 강좌 상세보기 페이지 (KmoocDetailActivity)
- 상세 데이터를 조회하여 기본 정보 적용 및 webView를 사용한 표시 기능 구현 

### 이미지 로딩 처리 (ImageLoader)
- UI Thread(= Main Thread) 와 IO Thread 를 나누어 코루틴을 통해 비동기 처리 (ANR 에러 방지)
  * IO Thread : Bitmap 이미지 관련 처리
  * UI Thread : UI에 이미지 전달할 때
- 이미지 캐시 처리 (LruCache 사용)

[📝 기술 블로그에 작성한 관련 포스팅 : 안드로이드 Glide 없이 LruCache로 이미지 캐싱하기 (Bitmap Cache)](https://velog.io/@dear_jjwim/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-Glide-%EC%97%86%EC%9D%B4-LruCache%EB%A1%9C-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%BA%90%EC%8B%B1%ED%95%98%EA%B8%B0-Bitmap-Cache)

### KmoocRepository
- json을 파싱하여 Model 객체 구현 (`org.json.JSONObject` 사용)

<br>

> ## 📝 후기
외부 라이브러리를 사용하지 않고 기본 기능으로만 작성할 경우가 흔치 않아 더 와닿았던 과제였다.<br>
제한사항을 보고 사실 조금 당황하기도 했지만 결국 내가 사용하는 메이저 라이브러리들도 전부 이러한 기능들의 조합인데❗️<br>
앞으로도 "왜 사용하는가?", "이 라이브러리가 왜 편한가?" 생각하며 중요한 것을 놓치지 않기 위해 기본을 충실히 해야겠다.


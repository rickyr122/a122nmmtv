package com.projects.a122mmtv.dataclass

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiHomeCodesService {
    @GET("gethomecodes")
    suspend fun getHomeCodes(
        @Query("type") type: String
    ): List<Int>
}

//sealed class Section  {
//    object Continue : Section()
//    data class Category(val code: Int) : Section()
//    object TopContent : Section()
//}
sealed class Section(open val code: Int) {

    object Continue : Section(code = -1)

    data class Category(
        override val code: Int
    ) : Section(code)

    object TopContent : Section(code = -2)
}



sealed class ProfileSection {
    object Continue : ProfileSection()
    object RecentWatch : ProfileSection()
    data class Category(val code: Int) : ProfileSection()
}

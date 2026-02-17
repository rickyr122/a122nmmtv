package com.projects.a122mmtv.auth

import com.projects.a122mmtv.components.ContinueWatchingResponse
import com.projects.a122mmtv.components.HomeMenuResponse
import com.projects.a122mmtv.components.TopContentItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    @POST("login.php")
    suspend fun login(@Body body: LoginReq): Response<TokenRes>

    data class LoginResponse(
        val status: String,
        val access_token: String,
        val refresh_token: String,
        val token_type: String,
        val expires_in: Int
    )

    @POST("signup.php")
    suspend fun signup(@Body body: SignUpReq): Response<Map<String, Any>>

    @POST("refresh.php")
    suspend fun refresh(@Body body: Map<String, String>): Response<TokenRes>

    data class RefreshRes(
        val status: String?,
        val access_token: String,
        val refresh_token: String,
        val token_type: String?,
        val expires_in: Int?
    )

    @POST("device_upsert.php")
    suspend fun upsertDevice(
        @Body body: Map<String, String>
    ): Response<Map<String, Any>>

    @GET("profile.php")
    suspend fun profile(): Response<ProfileRes>

    @GET("get_profilepic.php")
    suspend fun getProfilePic(): Response<ProfilePicRes>

    data class ProfilePicRes(
        val pp_link: String,
        val username: String
    )

    // AuthApiService.kt
    @GET("devices.php")
    suspend fun listDevices(
        @Query("tz_offset_minutes") tzOffsetMinutes: Int
    ): Response<List<DeviceDto>>


    @GET("me.php")
    suspend fun me(): Response<Map<String, Any>>

    @GET("me.php")
    @Headers("X-No-Refresh: 1")
    suspend fun meNoRefresh(): Response<Map<String, Any>>

    @POST("logout_device.php")
    suspend fun logoutDevice(@Body body: Map<String, String>): Response<Map<String, Any>>

    @POST("logout_others.php")
    suspend fun logoutOthers(@Body body: Map<String, String>): Response<Map<String, Any>>

    data class DeviceDto(
        val device_id: String,
        val device_name: String,
        val device_type: String,
        val last_active: String // e.g. "Today", "Yesterday", or "2025-10-18"
    )

    data class LoginReq(
        val email: String,
        val password: String,
        val device_id: String,
        val device_name: String,
        val device_type: String,   // "phone" | "tablet" | "tv"
        val client_time: String,        // "yyyy-MM-dd HH:mm:ss" (LOCAL device time)
        val tz_offset_minutes: Int      // e.g., +420 for UTC+7 (Jakarta)
    )

    @POST("change_password.php")
    suspend fun changePassword(@Body body: ChangePasswordReq): Response<BasicRes>

    // request/response models
    data class ChangePasswordReq(
        val current_password: String,
        val new_password: String,
        val sign_out_all: Boolean
    )

    data class BasicRes(
        val status: String?,        // "ok"
        val logout: Boolean? = null // server can ask client to log out
    )

    // ---------- Profile Icon Picker ----------
    data class IconItem(
        val icon_id: Long,
        val title: String,
        val img_url: String
    )
    data class IconSection(
        val section_code: String,
        val title: String,
        val icons: List<IconItem>
    )
    data class SetProfileRes(
        val ok: Boolean? = null,
        val pp_link: String? = null,
        val error: String? = null
    )

    @GET("get_iconsections.php")
    suspend fun iconSections(
        @Query("user_id") userId: Int
    ): Response<List<IconSection>>

    @FormUrlEncoded
    @POST("set_profilepicture.php")
    suspend fun setProfilePicture(
        @Field("user_id") userId: Int,
        @Field("icon_id") iconId: Long?,      // nullable: some pics might be URL-only
        @Field("img_url") imgUrl: String
    ): Response<SetProfileRes>

    data class GenericRes(val ok: Boolean? = null, val message: String? = null, val error: String? = null)

    @FormUrlEncoded
    @POST("update_profile.php")
    suspend fun updateProfile(
        @Field("user_id") userId: Int,
        @Field("username") username: String
    ): Response<GenericRes>


    data class TvUserDto(
        val user_id: Int,
        val email: String,
        val username: String,
        val pp_link: String
    )

    @GET("gettvdevices")
    suspend fun getTvDevices(
        @Header("X-No-Refresh") noRefresh: String = "1",
        @Query("device_id") deviceId: String,
        @Query("tz_offset_minutes") tzOffsetMinutes: Int
    ): Response<List<TvUserDto>>

//    @GET("gettvusers_device.php")
//    suspend fun getTvUsersByDeviceToken(
//        @Query("device_id") deviceId: String,
//        @Query("device_token") deviceToken: String,
//        @Query("tz_offset_minutes") tzOffsetMinutes: Int
//    ): retrofit2.Response<List<TvUserDto>>

    @GET("gettvusers_device.php")
    suspend fun getTvUsersByDeviceId(
        @Query("device_id") deviceId: String
    ): Response<List<TvUserDto>>



    data class TvPairStartDto(
        val pair_code: String,
        val poll_token: String,
        val expires_in: Int,
        val verify_url: String
    )

    @POST("tv_pair_start.php")
    @FormUrlEncoded
    suspend fun tvPairStart(
        @Field("device_id") deviceId: String
    ): Response<TvPairStartDto>

//    data class TvPairStatusDto(
//        val status: String,
//        val access_token: String? = null,
//        val refresh_token: String? = null,
//        val expires_in: Int? = null,
//        val error: String? = null
//    )

    data class TvPairStatusDto(
        val status: String,

        val access_token: String? = null,
        val device_token: String? = null,
        val expires_in: Int? = null,

        val user_id: Int? = null,
        val username: String? = null,
        val pp_link: String? = null,

        val error: String? = null
    )


    @GET("tv_pair_status.php")
    suspend fun tvPairStatus(
        @Query("device_id") deviceId: String,
        @Query("poll_token") pollToken: String,
        @Query("device_name") deviceName: String
    ): Response<TvPairStatusDto>

    data class BannerDto(
        val mId: String,
        val bdropUrl: String,
        val logoUrl: String,
        val mGenre: String,
        val m_year: String,
        val m_duration: String,
        val m_content: String,
        val mDescription: String,
        val playId: String,
        val cProgress: Int,
        val cFlareVid: String,
        val cFlareSrt: String,
        val gDriveVid: String,
        val gDriveSrt: String
    )

    @GET("getbanner")
    suspend fun getBanner(
        @Query("type") type: String
    ): Response<BannerDto>

    @GET("gethomemenu")
    suspend fun getHomeMenu(
        @Query("code") code: Int,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("user_id") userId: Int
    ): HomeMenuResponse

    @GET("getcontinuewatching")
    suspend fun getContinueWatching(
        @Query("type") type: String,
        @Query("user_id") userId: Int
    ): List<ContinueWatchingResponse>

    @GET("gettopcontent")
    suspend fun getTopContent(
        @Query("type") type: String
    ): List<TopContentItem>  // ✅ API returns array

    @GET("getmoviedetail")
    suspend fun getMovieDetail(
        @Query("code") code: String,
        @Query("user_id") userId: Int
    ): Response<MovieDetailDto>

    data class MovieDetailDto(
        val m_id: String,
        val m_title: String,
        val m_year: String,
        val m_rating: String,
        val m_content: String,
        val m_release_date: String,
        val m_duration: Int,
        val m_description: String,
        val mGenre: String,
        val m_starring: String,
        val m_director: String,
        val bdropUrl: String,
        val logoUrl: String,
        val rt_state: String,
        val rt_score: Int,
        val audience_state: String,
        val audience_score: Int,
        val c_remaining: Int,
        val c_percent : Double?,
        val gId : String,
        val gName: String,
        val totalSeason: Int,
        val totalEps: Int,
        val activeSeason: Int,
        val activeEps : Int,
        val inList: String,
        val hasCollection: String,
        val hasTrailer: String,
        val hasRated: Int,
        val playId: String,
        val pTitle: String,
        val cProgress: Int,
        val cFlareVid: String,
        val cFlareSrt: String,
        val gDriveVid: String,
        val gDriveSrt: String
    )

    @GET("getFranchise")
    suspend fun getFranchise(
        @Query("code") code: String
    ): Response<FranchiseDto>

    data class FranchiseDto(
        val gId: String,
        val gName: String,
        val items: List<FranchiseItemDto>
    )

    data class FranchiseItemDto(
        val mId: String,
        val cvrUrl: String,
        val gOrder: Int
    )

    @GET("gettvseasoncount")
    suspend fun getTvSeasonCount(
        @Query("code") code: String
    ): TvSeasonCountResponse

    data class TvSeasonCountResponse(
        val t_id: String,
        val logoUrl: String,
        val m_year: String,
        val total_season: String,
        val seasons: List<SeasonItem>
    )

    data class SeasonItem(
        val season: Int,
        val episodes: Int
    )

}

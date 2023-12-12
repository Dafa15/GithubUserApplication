package com.example.githubresubmission.data.retrofit

import com.example.githubresubmission.data.response.DetailUserResponse
import com.example.githubresubmission.data.response.ItemsItem
import com.example.githubresubmission.data.response.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("search/users")
    @Headers("Authorization: token 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")
    fun searchUser (
        @Query("q") q : String
    ): Call<UserResponse>

    @GET("users/{username}")
    @Headers("Authorization: token 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")
    fun detailUser (
        @Path("username") username : String
    ): Call<DetailUserResponse>

    @GET("users/{username}/followers")
    @Headers("Authorization: token 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")
    fun getFollowers(@Path("username") username: String): Call<List<ItemsItem>>

    @GET("users/{username}/following")
    @Headers("Authorization: token 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")
    fun getFollowing(@Path("username") username: String): Call<List<ItemsItem>>
}


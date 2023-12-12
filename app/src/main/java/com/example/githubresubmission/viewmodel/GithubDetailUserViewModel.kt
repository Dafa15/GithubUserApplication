package com.example.githubresubmission.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.githubresubmission.data.response.DetailUserResponse
import com.example.githubresubmission.data.retrofit.ApiConfig
import com.example.githubresubmission.entity.UserFavorite
import com.example.githubresubmission.room.FavoriteDatabase
import com.example.githubresubmission.room.FavoriteList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GithubDetailUserViewModel(application: Application): AndroidViewModel(application) {
    private val _detailUser= MutableLiveData<DetailUserResponse>()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object{
        const val TAG = "GithubDetailUserViewModel"

    }
    private var userDao: FavoriteList?
    private var userDB: FavoriteDatabase?

    init {
        userDB = FavoriteDatabase.getDatabase(application)
        userDao = userDB?.favoriteDao()
    }


    fun detailUser(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().detailUser(username)
        client.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _detailUser.value = response.body()
                    }
                } else {
                    Log.d(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }
    fun getDetailUser(): LiveData<DetailUserResponse> {
        return _detailUser
    }

    fun rmvFavorite(usrname: String) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao?.rmvFavorite(usrname)
        }
    }

    fun getFavoriteUser(): LiveData<List<UserFavorite>>?{
        return userDao?.getUserFavorite()
    }

    fun addUserFavorite(usrname: String, avaFav: String ){
        CoroutineScope(Dispatchers.IO).launch {
            val user = UserFavorite(
                usrname,
                avaFav
            )
            userDao?.addFavorite(user)
        }
    }
    suspend fun checkUser(username: String): Int? = userDao?.checkUser(username)

}

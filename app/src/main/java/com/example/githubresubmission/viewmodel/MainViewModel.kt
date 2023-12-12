package com.example.githubresubmission.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubresubmission.data.response.ItemsItem
import com.example.githubresubmission.data.response.UserResponse
import com.example.githubresubmission.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    private val _userlist= MutableLiveData<List<ItemsItem>>()
    val userList: LiveData<List<ItemsItem>> = _userlist
    private val _listFollowers= MutableLiveData<List<ItemsItem>>()
    val listFollowers: LiveData<List<ItemsItem>> = _listFollowers
    private val _listFollowing= MutableLiveData<List<ItemsItem>>()
    val listFollowing: LiveData<List<ItemsItem>> = _listFollowing
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    companion object{
         const val TAG = "MainViewModel"
    }

     fun searchUser(query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchUser(query)
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _userlist.value = responseBody.items
                    }
                } else {
                    Log.d(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun followers(username: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowers(username)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listFollowers.value = response.body()
                    }
                } else {
                    Log.d(GithubDetailUserViewModel.TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                Log.d(GithubDetailUserViewModel.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun following(username: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowing(username)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listFollowing.value = response.body()
                    }
                } else {
                    Log.d(GithubDetailUserViewModel.TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                Log.d(GithubDetailUserViewModel.TAG, "onFailure: ${t.message}")
            }
        })
    }
}
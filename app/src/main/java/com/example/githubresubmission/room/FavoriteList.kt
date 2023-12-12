package com.example.githubresubmission.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubresubmission.entity.UserFavorite

@Dao
interface FavoriteList{
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun addFavorite(favorite: UserFavorite)

        @Query("SELECT * FROM UserFavorite")
        fun getUserFavorite(): LiveData<List<UserFavorite>>

        @Query("SELECT COUNT(*) FROM UserFavorite WHERE username = :username")
        suspend fun checkUser(username: String): Int

        @Query("DELETE FROM UserFavorite WHERE username = :username")
        suspend fun rmvFavorite(username: String)
}
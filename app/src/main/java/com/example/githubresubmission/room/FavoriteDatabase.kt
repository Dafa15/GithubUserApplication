package com.example.githubresubmission.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.githubresubmission.entity.UserFavorite

@Database(entities = [UserFavorite::class], version = 1)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteList
    companion object {
        @Volatile
        private var INSTANCE: FavoriteDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): FavoriteDatabase {
            if (INSTANCE == null) {
                synchronized(FavoriteDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        FavoriteDatabase::class.java, "favorite_database")
                        .build()
                }
            }
            return INSTANCE as FavoriteDatabase
        }
    }

}
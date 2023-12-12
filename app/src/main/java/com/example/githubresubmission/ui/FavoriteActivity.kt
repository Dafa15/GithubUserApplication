package com.example.githubresubmission.ui

import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubresubmission.R
import com.example.githubresubmission.adapter.UserAdapter
import com.example.githubresubmission.data.response.ItemsItem
import com.example.githubresubmission.databinding.ActivityFavoriteBinding
import com.example.githubresubmission.setting.SettingPreferences
import com.example.githubresubmission.setting.dataStore
import com.example.githubresubmission.viewmodel.GithubDetailUserViewModel
import com.example.githubresubmission.viewmodel.ThemeViewModel
import com.example.githubresubmission.viewmodel.ViewModelFactory

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: UserAdapter
    private lateinit var viewModel: GithubDetailUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pref = SettingPreferences.getInstance(application.dataStore)
        val themeViewModel = ViewModelProvider(this, ViewModelFactory(pref))[ThemeViewModel::class.java]
        themeViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            val nightMode = if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(nightMode)

            val actionBarColorRes = if (isDarkModeActive) R.color.black else R.color.blue

            supportActionBar?.apply {
                setTitleAndColor(actionBarColorRes)
            }
        }
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[GithubDetailUserViewModel::class.java]

        adapter = UserAdapter()

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                val intent = Intent(this@FavoriteActivity, UserDetailActivity::class.java)
                intent.putExtra("user", data)
                startActivity(intent)
            }
        })

        binding.apply {
            rvFavorite.setHasFixedSize(true)
            rvFavorite.layoutManager = LinearLayoutManager(this@FavoriteActivity)
            rvFavorite.adapter = adapter
        }

        viewModel.getFavoriteUser()?.observe(this) { users ->
            val items = arrayListOf<ItemsItem>()
            users.map {
                val item = ItemsItem(login = it.username, avatarUrl = it.avatarUrl)
                items.add(item)
            }
            adapter.submitList(items)
        }

    }

    private fun setTitleAndColor(actionBarColorRes: Int) {
        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@FavoriteActivity, actionBarColorRes)))
            val title = SpannableString("Favorite User")
            title.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@FavoriteActivity, R.color.white)), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            title.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            title.setSpan(AbsoluteSizeSpan(24, true), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            this.title = title
        }
    }
}
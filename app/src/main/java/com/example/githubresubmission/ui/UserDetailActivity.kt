package com.example.githubresubmission.ui

import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.githubresubmission.R
import com.example.githubresubmission.adapter.SectionsPagerAdapter
import com.example.githubresubmission.data.response.ItemsItem
import com.example.githubresubmission.databinding.ActivityUserDetailBinding
import com.example.githubresubmission.setting.SettingPreferences
import com.example.githubresubmission.setting.dataStore
import com.example.githubresubmission.viewmodel.GithubDetailUserViewModel
import com.example.githubresubmission.viewmodel.ThemeViewModel
import com.example.githubresubmission.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private lateinit var viewModel: GithubDetailUserViewModel

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.followings,
        )
    }

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
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val user = intent.getParcelableExtra<ItemsItem>("user")
        val usrname = user?.login.toString()
        viewModel = ViewModelProvider(this)[GithubDetailUserViewModel::class.java]

        if (user != null) {
            viewModel.detailUser(usrname)
            viewModel.getDetailUser().observe(this) { data ->
                if (data != null) {
                    Glide.with(this)
                        .load(user.avatarUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .circleCrop()
                        .into(binding.detailAva)
                    binding.detailUsername.text = usrname
                    binding.detailName.text = data.name
                    binding.detailFollowing.text =
                        resources.getString(R.string.followings_label, data.following)
                    binding.detailFollowers.text =
                        resources.getString(R.string.followers_label, data.followers)
                }
            }
        }

        val viewPager: ViewPager2 = binding.viewPager
        val sectionsPagerAdapter = SectionsPagerAdapter(this, user?.login ?: "")
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        var isChecked = true
        CoroutineScope(Dispatchers.IO).launch {
            val count = viewModel.checkUser(usrname)
            withContext(Dispatchers.Main) {
                if (count != null) {
                    isChecked = count > 0
                    binding.toggleFavorite.isChecked = isChecked
                }
            }
        }

        binding.toggleFavorite.setOnClickListener {
            isChecked = !isChecked
            binding.toggleFavorite.isChecked = isChecked
            val message = if (isChecked) "Added $usrname to favorite" else "Removed $usrname from favorite"

            CoroutineScope(Dispatchers.IO).launch {
                if (isChecked) {
                    viewModel.addUserFavorite(usrname, user?.avatarUrl.toString())
                } else {
                    viewModel.rmvFavorite(usrname)
                }

                withContext(Dispatchers.Main) {
                    showToast(message)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun setTitleAndColor(actionBarColorRes: Int) {
        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@UserDetailActivity, actionBarColorRes)))
            val titleBar = SpannableString("User Detail")
            titleBar.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@UserDetailActivity, R.color.white)), 0, titleBar.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            titleBar.setSpan(StyleSpan(Typeface.BOLD), 0, titleBar.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            titleBar.setSpan(AbsoluteSizeSpan(24, true), 0, titleBar.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            this.title = titleBar
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}

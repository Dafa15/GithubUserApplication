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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubresubmission.R
import com.example.githubresubmission.adapter.UserAdapter
import com.example.githubresubmission.data.response.ItemsItem
import com.example.githubresubmission.databinding.ActivityMainBinding
import com.example.githubresubmission.setting.SettingPreferences
import com.example.githubresubmission.setting.dataStore
import com.example.githubresubmission.viewmodel.MainViewModel
import com.example.githubresubmission.viewmodel.ThemeViewModel
import com.example.githubresubmission.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
        mainViewModel.userList.observe(this) { user ->
            val hasUser = user.isNotEmpty()
            binding.apply {
                rvUser.visibility = if (hasUser) View.VISIBLE else View.GONE
                nfIc.visibility = if (!hasUser) View.VISIBLE else View.GONE
                nfText.visibility = if (!hasUser) View.VISIBLE else View.GONE
            }
            if (hasUser) {
                setUserList(user)
            }
        }
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUser.addItemDecoration(itemDecoration)

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText
                .setOnEditorActionListener { _, _, _ ->
                    mainViewModel.searchUser(searchView.text.toString())
                    searchView.hide()
                    false
                }
        }
    }

    private fun setTitleAndColor(actionBarColorRes: Int) {
        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@MainActivity, actionBarColorRes)))
            val titleBar = SpannableString("GitHubUser")
            titleBar.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.white)), 0, titleBar.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            titleBar.setSpan(StyleSpan(Typeface.BOLD), 0, titleBar.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            titleBar.setSpan(AbsoluteSizeSpan(24, true), 0, titleBar.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            this.title = titleBar
        }
    }

   private fun setUserList(user: List<ItemsItem>) {
       val userAdapter = UserAdapter()
       userAdapter.submitList(user)
       binding.rvUser.adapter = userAdapter

       userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
           override fun onItemClicked(data: ItemsItem) {
               val intent = Intent(this@MainActivity, UserDetailActivity::class.java)
               intent.putExtra("user", data)
               startActivity(intent)
           }
       })

   }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.favorite_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_favorite -> {
                Intent(this, FavoriteActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.switch_theme -> {
                Intent(this, SwitchThemeActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
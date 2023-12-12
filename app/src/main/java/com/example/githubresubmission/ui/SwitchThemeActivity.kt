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
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.githubresubmission.R
import com.example.githubresubmission.databinding.ActivitySwitchThemeBinding
import com.example.githubresubmission.setting.SettingPreferences
import com.example.githubresubmission.setting.dataStore
import com.example.githubresubmission.viewmodel.ThemeViewModel
import com.example.githubresubmission.viewmodel.ViewModelFactory

class SwitchThemeActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySwitchThemeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwitchThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val switchTheme = binding.switchTheme

        val pref = SettingPreferences.getInstance(application.dataStore)
        val viewModel = ViewModelProvider(this, ViewModelFactory(pref))[ThemeViewModel::class.java]
        viewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            val nightMode = if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(nightMode)

            val actionBarColorRes = if (isDarkModeActive) R.color.black else R.color.blue

            supportActionBar?.apply {
                setTitleAndColor(actionBarColorRes)
            }
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }
    }
    private fun setTitleAndColor(actionBarColorRes: Int) {
        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@SwitchThemeActivity, actionBarColorRes)))
            val tittleBar = SpannableString("Favorite User")
            tittleBar.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@SwitchThemeActivity, R.color.white)), 0, tittleBar.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            tittleBar.setSpan(StyleSpan(Typeface.BOLD), 0, tittleBar.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            tittleBar.setSpan(AbsoluteSizeSpan(24, true), 0, tittleBar.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            this.title = tittleBar
        }
    }
}
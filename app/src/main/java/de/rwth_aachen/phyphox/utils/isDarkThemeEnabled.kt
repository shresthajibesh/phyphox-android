package de.rwth_aachen.phyphox.utils

import android.content.Context
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.SettingsActivity.SettingsFragment

//temp till fully migrated to compose

fun Context.isDarkThemeEnabled(): Boolean {
    val themePreference = getDefaultSharedPreferences(this)
        .getString(getString(R.string.setting_dark_mode_key), SettingsFragment.DARK_MODE_ON)

    return themePreference != SettingsFragment.DARK_MODE_OFF

}

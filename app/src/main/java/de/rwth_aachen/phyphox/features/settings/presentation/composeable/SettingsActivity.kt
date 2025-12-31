package de.rwth_aachen.phyphox.features.settings.presentation.composeable

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import de.rwth_aachen.phyphox.Helper.WindowInsetHelper
import de.rwth_aachen.phyphox.R

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var settingsFrame: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        inflateViews()
        setSupportActionBar(toolbar)
        updateActionBar()
        setFrameInsets()
        setToolBarInsets()
        showSettings()
    }

    private fun inflateViews() {
        toolbar = findViewById(R.id.settingsToolbar)
        settingsFrame = findViewById(R.id.settingsFrame)
    }

    private fun updateActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
    }

    private fun showSettings() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settingsFrame, SettingsFragment())
            .commit()
    }

    private fun setFrameInsets() {
        WindowInsetHelper.setInsets(
            settingsFrame,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.IGNORE,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.MARGIN,
        )
    }

    private fun setToolBarInsets() {
        WindowInsetHelper.setInsets(
            toolbar,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.IGNORE,
        )
    }
}

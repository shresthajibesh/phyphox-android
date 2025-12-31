package de.rwth_aachen.phyphox.features.settings.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import de.rwth_aachen.phyphox.features.settings.presentation.compose.SettingsRoot
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.SettingsViewModel
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContent {
            PhyphoxTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                Surface {
                    SettingsRoot(
                        uiState = uiState,
                    )
                }

            }
        }
//        setContentView(R.layout.activity_settings)
//        inflateViews()
//        setSupportActionBar(toolbar)
//        updateActionBar()
//        setFrameInsets()
//        setToolBarInsets()
//        showSettings()
    }

//    private fun inflateViews() {
//        toolbar = findViewById(R.id.settingsToolbar)
//        settingsFrame = findViewById(R.id.settingsFrame)
//    }
//
//    private fun updateActionBar() {
//        supportActionBar?.apply {
//            setDisplayHomeAsUpEnabled(true)
//            setDisplayShowTitleEnabled(true)
//        }
//    }
//
//    private fun showSettings() {
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.settingsFrame, SettingsFragment())
//            .commit()
//    }
//
//    private fun setFrameInsets() {
//        WindowInsetHelper.setInsets(
//            settingsFrame,
//            WindowInsetHelper.ApplyTo.PADDING,
//            WindowInsetHelper.ApplyTo.IGNORE,
//            WindowInsetHelper.ApplyTo.PADDING,
//            WindowInsetHelper.ApplyTo.MARGIN,
//        )
//    }
//
//    private fun setToolBarInsets() {
//        WindowInsetHelper.setInsets(
//            toolbar,
//            WindowInsetHelper.ApplyTo.PADDING,
//            WindowInsetHelper.ApplyTo.PADDING,
//            WindowInsetHelper.ApplyTo.PADDING,
//            WindowInsetHelper.ApplyTo.IGNORE,
//        )
//    }
}

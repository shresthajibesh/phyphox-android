package de.rwth_aachen.phyphox

import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import de.rwth_aachen.phyphox.appdelegate.LanguageDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

//This extension to application is only used to store measured data in memory as this may easily exceed the amount of data allowed on the transaction stack
@HiltAndroidApp
class App : MultiDexApplication() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var languageDelegate: LanguageDelegate


    //Need to get rid off of this ASAP
    @JvmField
    var experiment: PhyphoxExperiment? = null

    override fun onCreate() {
        super.onCreate()
        languageDelegate.start(applicationScope)
    }
}

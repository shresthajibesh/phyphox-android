package de.rwth_aachen.phyphox.ExperimentList.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.rwth_aachen.phyphox.ExperimentList.data.model.PhyphoxExperimentX
import de.rwth_aachen.phyphox.libs.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML
import javax.inject.Inject

@HiltViewModel
class ExperimentListViewModel @Inject constructor(
    private val dataSource: ExperimentListDataSource,
) : ViewModel() {

    init {
        loadData()
    }

    fun ld() {}

    private fun loadData() = viewModelScope.launch {
        dataSource.getExperimentsFromAssets()
    }
}

interface ExperimentListDataSource {
    suspend fun getExperimentsFromFiles()
    suspend fun getExperimentsFromAssets()
}

class ExperimentListDataSourceImpl @Inject constructor(
    val context: Context,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
) : ExperimentListDataSource {
    override suspend fun getExperimentsFromFiles() {
        val directory = context.filesDir
        val phyphoxFiles = directory.listFiles { file ->
            file.isFile && file.name.endsWith(".phyphox")
        }
        Log.d("Files", "${phyphoxFiles.size} files found")
    }

    @OptIn(ExperimentalXmlUtilApi::class)
    override suspend fun getExperimentsFromAssets(): Unit = withContext(dispatcher) {
        val assetManager = context.assets
        val xml = XML {
            this.policy = DefaultXmlSerializationPolicy {
                unknownChildHandler =
                    UnknownChildHandler { input, inputKind, descriptor, name, candidates -> emptyList() }
                autoPolymorphic = true
            }
        }

        val streams = assetManager.list("experiments")?.filter { it.endsWith(".phyphox") }?.map { fileName ->
            val reader = assetManager.open("experiments/$fileName").reader()
            val text = reader.readText()
            try {
                xml.decodeFromString(PhyphoxExperimentX.serializer(), text)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } ?: emptyList()

        with(streams.filterNotNull()) {
            if (streams.isEmpty()) {
                Log.d("EXPERIMENTS", "It was empty")
            } else {
                Log.d("EXPERIMENTS", streams.first()?.translations?.translation?.map {
                    it.locale.trim() +"::"+it.localizedString
                }?.joinToString("\n") ?: "EMPTY")
            }
        }

    }
}

package de.rwth_aachen.phyphox.ExperimentList.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.rwth_aachen.phyphox.ExperimentList.data.model.PhyphoxExperiment
import de.rwth_aachen.phyphox.libs.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.decodeFromStream
import nl.adaptivity.xmlutil.xmlStreaming
import javax.inject.Inject
import kotlin.io.use
import kotlin.use

@HiltViewModel
class ExperimentListViewModel @Inject constructor(
    private val dataSource: ExperimentListDataSource,
) : ViewModel() {

    init {
        loadData()
    }

    fun ld() {

    }

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

    override suspend fun getExperimentsFromAssets(): Unit = withContext(dispatcher) {
        val assetManager = context.assets
        val xml = XML.v1
        val streams = assetManager.list("experiments")?.filter { it.endsWith(".phyphox") }?.map { fileName ->
            assetManager.open("experiments/$fileName").use { inputStream ->
                inputStream
            }
        } ?: emptyList()


        val files = streams.map {
            it.reader(Charsets.UTF_8).use { reader ->
                xml.decodeFromString<PhyphoxExperiment>(reader.readText())
            }
        }
        files.forEach {
            Log.d("Assetrs",it.locale)
        }

    }


}

package de.rwth_aachen.phyphox.features.experimentlist.data.localio

import android.content.Context
import de.rwth_aachen.phyphox.features.experimentlist.domain.data.FilesExperimentListDataSource
import java.io.File
import javax.inject.Inject

class FilesExperimentListDataSourceImpl @Inject constructor(
    val context: Context,
) : FilesExperimentListDataSource {
    override suspend fun getExperimentsFromFiles(): List<File> {
        val directory = context.filesDir
        val filesArray =  directory.listFiles { file ->
            file.isFile && file.name.endsWith(".phyphox")
        }
        return filesArray?.filterNotNull() ?: emptyList()
    }
}

package de.rwth_aachen.phyphox.features.experimentlist.domain.data

import java.io.File

interface FilesExperimentListDataSource {
    suspend fun getExperimentsFromFiles(): List<File>
}

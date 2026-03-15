package de.rwth_aachen.phyphox.features.experimentlist.domain.data

import java.io.InputStream

interface AssetsExperimentListDataSource {
    suspend fun getExperimentsFromAssets(): List<InputStream>
}


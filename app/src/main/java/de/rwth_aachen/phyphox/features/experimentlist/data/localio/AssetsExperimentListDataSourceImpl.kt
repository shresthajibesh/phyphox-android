package de.rwth_aachen.phyphox.features.experimentlist.data.localio

import android.content.Context
import de.rwth_aachen.phyphox.features.experimentlist.domain.data.AssetsExperimentListDataSource
import java.io.InputStream
import javax.inject.Inject

class AssetsExperimentListDataSourceImpl @Inject constructor(
    val context: Context,

    ) : AssetsExperimentListDataSource {
    override suspend fun getExperimentsFromAssets(): List<InputStream> {
        val assetManager = context.assets
        val streams = assetManager.list("experiments")?.filter { it.endsWith(".phyphox") }?.map { fileName ->
            assetManager.open("experiments/$fileName")
        } ?: emptyList()
        return streams
    }
}



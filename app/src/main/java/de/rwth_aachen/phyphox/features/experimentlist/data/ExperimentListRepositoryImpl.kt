package de.rwth_aachen.phyphox.features.experimentlist.data

import android.util.Log
import de.rwth_aachen.phyphox.features.experimentlist.data.parser.PhyphoxExperimentParser
import de.rwth_aachen.phyphox.features.experimentlist.domain.data.AssetsExperimentListDataSource
import de.rwth_aachen.phyphox.features.experimentlist.domain.data.ExperimentListRepository
import de.rwth_aachen.phyphox.features.experimentlist.domain.data.FilesExperimentListDataSource
import de.rwth_aachen.phyphox.libs.IoDispatcher
import de.rwth_aachen.phyphox.utils.parallelMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExperimentListRepositoryImpl @Inject constructor(
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val assetsExperimentListDataSource: AssetsExperimentListDataSource,
    private val filesExperimentListDataSource: FilesExperimentListDataSource,
    private val phyphoxExperimentParser: PhyphoxExperimentParser,
) : ExperimentListRepository {

    override suspend fun loadExperimentsFromFiles() = withContext(dispatcher) {

    }

    override suspend fun loadExperimentsFromAssets() = withContext(dispatcher) {
        val results = assetsExperimentListDataSource.getExperimentsFromAssets()
            .subList(0,1)
            .parallelMap(4) { inputStream ->
                inputStream.use {
                    phyphoxExperimentParser.parse(it)
                }
            }
        return@withContext results
    }
}




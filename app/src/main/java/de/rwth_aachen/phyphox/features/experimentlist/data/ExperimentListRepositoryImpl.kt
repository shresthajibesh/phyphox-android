package de.rwth_aachen.phyphox.features.experimentlist.data

import de.rwth_aachen.phyphox.features.experimentlist.data.parser.PhyphoxExperimentParser
import de.rwth_aachen.phyphox.features.experimentlist.domain.data.AssetsExperimentListDataSource
import de.rwth_aachen.phyphox.features.experimentlist.domain.data.ExperimentListRepository
import de.rwth_aachen.phyphox.features.experimentlist.domain.data.FilesExperimentListDataSource
import de.rwth_aachen.phyphox.libs.IoDispatcher
import de.rwth_aachen.phyphox.utils.parallelMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ExperimentListRepositoryImpl constructor(
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val assetsExperimentListDataSource: AssetsExperimentListDataSource,
    private val filesExperimentListDataSource: FilesExperimentListDataSource,
    private val phyphoxExperimentParser: PhyphoxExperimentParser,
) : ExperimentListRepository {

    override suspend fun loadExperimentsFromFiles() = withContext(dispatcher) {

    }

    override suspend fun loadExperimentsFromAssets() = withContext(dispatcher) {
        assetsExperimentListDataSource.getExperimentsFromAssets()
            .parallelMap(4) { inputStream ->
                inputStream.use { phyphoxExperimentParser.parse(it) }
            }
    }
}




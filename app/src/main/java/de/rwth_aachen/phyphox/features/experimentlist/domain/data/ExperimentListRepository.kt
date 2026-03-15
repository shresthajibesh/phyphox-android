package de.rwth_aachen.phyphox.features.experimentlist.domain.data

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX

interface ExperimentListRepository {

    suspend fun loadExperimentsFromFiles()
    suspend fun loadExperimentsFromAssets() : List<PhyphoxExperimentX>
}

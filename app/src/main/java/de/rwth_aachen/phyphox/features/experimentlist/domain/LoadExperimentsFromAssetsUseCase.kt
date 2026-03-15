package de.rwth_aachen.phyphox.features.experimentlist.domain

import de.rwth_aachen.phyphox.features.experimentlist.domain.data.ExperimentListRepository
import javax.inject.Inject

class LoadExperimentsFromAssetsUseCase @Inject constructor(
    val experimentListRepository: ExperimentListRepository,
){
    suspend operator fun invoke(){
        experimentListRepository.loadExperimentsFromAssets()
        //ADD Business logic and error handling
    }
}

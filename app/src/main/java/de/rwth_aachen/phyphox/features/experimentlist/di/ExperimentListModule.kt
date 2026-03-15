package de.rwth_aachen.phyphox.features.experimentlist.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.rwth_aachen.phyphox.features.experimentlist.data.ExperimentListRepositoryImpl
import de.rwth_aachen.phyphox.features.experimentlist.data.localio.AssetsExperimentListDataSourceImpl
import de.rwth_aachen.phyphox.features.experimentlist.data.localio.FilesExperimentListDataSourceImpl
import de.rwth_aachen.phyphox.features.experimentlist.domain.data.AssetsExperimentListDataSource
import de.rwth_aachen.phyphox.features.experimentlist.domain.data.ExperimentListRepository
import de.rwth_aachen.phyphox.features.experimentlist.domain.data.FilesExperimentListDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class ExperimentListModule {

    @Binds
    abstract fun bindAssetsExperimentListDataSource(
        source: AssetsExperimentListDataSourceImpl,
    ): AssetsExperimentListDataSource

    @Binds
    abstract fun bindFilesExperimentListDataSource(
        source: FilesExperimentListDataSourceImpl,
    ): FilesExperimentListDataSource

    @Binds
    abstract fun bindExperimentListRepository(
        source: ExperimentListRepositoryImpl,
    ): ExperimentListRepository
}

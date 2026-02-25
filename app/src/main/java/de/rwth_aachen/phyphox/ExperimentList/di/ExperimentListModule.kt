package de.rwth_aachen.phyphox.ExperimentList.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.rwth_aachen.phyphox.ExperimentList.viewmodel.ExperimentListDataSource
import de.rwth_aachen.phyphox.ExperimentList.viewmodel.ExperimentListDataSourceImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class ExperimentListModule {

    @Binds
    abstract fun bindExperimentListDataSource(source: ExperimentListDataSourceImpl): ExperimentListDataSource
}

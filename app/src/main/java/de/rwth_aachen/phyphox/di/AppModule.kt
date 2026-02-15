package de.rwth_aachen.phyphox.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.rwth_aachen.phyphox.common.camera.di.CommonCameraModule
import de.rwth_aachen.phyphox.libs.CoroutinesModule

@Module(
    includes = [
        CoroutinesModule::class,
        CommonCameraModule::class,
    ],
)
@InstallIn(SingletonComponent::class)
abstract class AppModule {}


package de.rwth_aachen.phyphox.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.rwth_aachen.phyphox.ExperimentList.di.ExperimentListModule
import de.rwth_aachen.phyphox.common.camera.di.CommonCameraModule
import de.rwth_aachen.phyphox.libs.CoroutinesModule

@Module(
    includes = [
        CoroutinesModule::class,
        CommonCameraModule::class,
        ExperimentListModule::class
    ],
)
@InstallIn(SingletonComponent::class)
abstract class AppModule {

   companion object {
       @Provides
       fun providesApplicationContext(@ApplicationContext context: Context): Context{
           return context
       }
   }
}

package com.cliomuseexperience.core.di

import com.cliomuseexperience.core.api.ApiService
import com.cliomuseexperience.feature.experience.data.ExperienceDataRepository
import com.cliomuseexperience.feature.experience.domain.ExperienceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {


    @Named("sdk")
    @Provides
    fun provideExploreDataRepository(
        api: ApiService,
    ): ExperienceRepository {
        return ExperienceDataRepository(
            apiService = api,
        )
    }

}